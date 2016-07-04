package it.unical.mat.embasp.asp;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


/**
 * Contains methods used to transform Objects into {@link it.unical.mat.embasp.base.InputProgram}
 */

public class ASPMapper {

    private static ASPMapper mapper;

    private Map<String, Class<?>> predicateClass;
    private Map<Class, Map<String, Method>> classSetterMethod;

    private ASPMapper() {
        predicateClass = new HashMap<>();
        classSetterMethod = new HashMap<>();
    }

    public static ASPMapper getInstance() {
        if (mapper == null) {
            mapper = new ASPMapper();
        }
        return mapper;
    }

    public Class<?> getClass(String predicate) {
        return predicateClass.get(predicate);
    }


    /**
     * insert an object into {@link #predicateClass} and {@link #classSetterMethod}
     *
     * @return String representing pairing key of {@link #predicateClass}
     */
    public String registerClass(Class<?> cl) throws PredicateNotValidException, IllegalAnnotationException {

        Annotation annotation = cl.getAnnotation(Predicate.class);

        if(annotation == null){
            throw new IllegalAnnotationException();
        }

        String predicate = ((Predicate) annotation).value();

        if(predicate.contains(" "))
            throw new PredicateNotValidException();

//		String predicate = cl.getAnnotation(Predicate.class).value();
        predicateClass.put(predicate, cl);
        Map<String, Method> namesMethods = new HashMap<>();
        for (Method method : cl.getMethods()) {
            if (method.getName().startsWith("set"))
                namesMethods.put(method.getName(), method);
        }
        classSetterMethod.put(cl, namesMethods);
        return predicate;
    }


    /**
     * Returns data for the given Object
     *
     * @param obj Object from witch data are extrapolated
     * @return String data for the given Object in a String format
     * @throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IllegalTermException
     */
    public String getString(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IllegalTermException, PredicateNotValidException, IllegalAnnotationException {
        String predicate = registerClass(obj.getClass());
        String atom = predicate + "(";
        HashMap<Integer, Object> mapTerm = new HashMap<>();
        for (Field field : obj.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Term.class)) {
                Object value = obj.getClass().getMethod("get" + Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1)).invoke(obj);
                mapTerm.put(field.getAnnotation(Term.class).value(), value);
            }else{
                throw new IllegalAnnotationException();
            }
        }
        for (int i = 0; i < mapTerm.size(); i++) {
            if (i != 0)
                atom += ",";
            Object objectTerm = mapTerm.get(i);
            if (objectTerm == null)
                throw new IllegalTermException("Wrong term number of class " + obj.getClass().getName());
            if (objectTerm instanceof Integer) {
                atom += objectTerm + "";
            } else
                atom += "\"" + objectTerm.toString() + "\"";

        }
        atom += ")";
        return atom;
    }

    /**
     * Returns an Object for the given atom
     *
     * @param atom String from witch data are extrapolated
     * @return Object for the given String data
     * @throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IllegalTermException
     */
    public Object getObject(String atom) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, InstantiationException { // TODO
        String predicate;

        int indexOf = atom.indexOf("(");
        if (indexOf == -1) {
            //Arity 0
            predicate = atom;
        } else
            predicate = atom.substring(0, (atom.indexOf("(")));
        Class<?> cl = getClass(predicate);
        // Not exist mapping between the predicate and the class
        if (cl == null) return null;
        Object obj = cl.newInstance();
        //Term with arity 0 return obj
        if (indexOf == -1)
            return obj;
        //FIXME Not work with "a("asd,"). fix the split
        String[] paramiter = atom.substring(atom.indexOf("(") + 1, atom.lastIndexOf(")")).split(",");
        for (Field field : cl.getDeclaredFields()) {

            if (field.isAnnotationPresent(Term.class)) {

                int term = field.getAnnotation(Term.class).value();
                String nameMethod = "set" + Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);
                Method method = classSetterMethod.get(cl).get(nameMethod);

                if (method.getParameterTypes()[0].getName().equals(int.class.getName()) || method.getParameterTypes()[0].getName().equals(Integer.class.getName()))
                    method.invoke(obj, Integer.valueOf(paramiter[term]));
                else
                    method.invoke(obj, paramiter[term]);

            }
        }

        return obj;
    }
}
