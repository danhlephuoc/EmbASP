package it.unical.mat.embasp.languages.asp;

import java.util.HashMap;

import it.unical.mat.embasp.languages.Mapper;
import it.unical.mat.embasp.languages.asp.parser.ASPGrammarBaseVisitorImplementation;

/**
 * Contains methods used to transform Objects into {@link it.unical.mat.embasp.base.InputProgram}
 */

public class ASPMapper extends Mapper {

	private static ASPMapper mapper;
	private ASPGrammarBaseVisitorImplementation parser;
	
	public static ASPMapper getInstance() {
		if (ASPMapper.mapper == null)
			ASPMapper.mapper = new ASPMapper();
		return ASPMapper.mapper;
	}

	private ASPMapper() {
		super();
	}

	@Override
	protected String getActualString(final String predicate, final HashMap<Integer, Object> parametersMap) throws IllegalTermException {
		if (parametersMap.isEmpty())
			return predicate;

		String atom = predicate + "(";
		for (int i = 0; i < parametersMap.size(); i++) {
			if (i != 0)
				atom += ",";
			final Object objectTerm = parametersMap.get(i);
			if (objectTerm == null)
				throw new IllegalTermException("Wrong term number of predicate " + predicate);
			if (objectTerm instanceof Integer)
				atom += objectTerm + "";
			else
				atom += "\"" + objectTerm.toString() + "\"";
		}
		atom += ")";
		return atom;

	}

	@Override
	protected void buildParseTree(final String atomsList) {
		parser = new ASPGrammarBaseVisitorImplementation(atomsList);
	}
	
	
	@Override
	protected String getId() {
		return parser.getIdentifier();
	}
	
	@Override
	protected String[] getParam() {
		return parser.getParameters();
	}
}
