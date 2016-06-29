package it.unical.mat.embasp.platforms.desktop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

import it.unical.mat.embasp.asp.AnswerSets;
import it.unical.mat.embasp.base.Callback;
import it.unical.mat.embasp.base.InputProgram;
import it.unical.mat.embasp.base.OptionDescriptor;
import it.unical.mat.embasp.base.Output;
import it.unical.mat.embasp.base.Service;

/**is a specialization for a Desktop platform
 * @see Service*/

public abstract class DesktopService implements Service {
	/**Stores solver's executable path*/
	protected String exe_path;

	public DesktopService(String exe_path){
		this.exe_path = exe_path;
	}

	public String getExePath() {
		return exe_path;
	}

	/**set {@link #exe_path} to a new path*
	 * @param exe_path a string representing the path for the new solver
	 */
	public void setExePath(String exe_path) {
		this.exe_path = exe_path;
	}


	/**Start a new process for the {@link #exe_path} and starts solving
	 * @see it.unical.mat.embasp.base.Service#startAsync(Callback, List, List) */
	@Override
	public void startAsync(final Callback callback, final List<InputProgram> programs,
						   final List<OptionDescriptor> options) {


		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {

				try {


					String option = new String();
					for (OptionDescriptor o : options) {

						option += o.getOptions();

					}

					String files_paths = new String();
					String final_program = new String();

					for (InputProgram p : programs) {
						final_program += p.getPrograms();
						String program_file = p.getFilesPaths();
						if (program_file != null) {
							files_paths += program_file;
						}
					}


					long startTime = System.nanoTime();

					Process solver_process = Runtime.getRuntime().exec(exe_path + " " + option + files_paths + "--");

					PrintWriter writer = new PrintWriter(solver_process.getOutputStream());
					writer.println(final_program);
					writer.flush();
					writer.close();

					BufferedReader output_reader = new BufferedReader(new InputStreamReader(solver_process.getInputStream()));
					String output_line = new String();
					String output = new String();
					try {
						while ((output_line = output_reader.readLine()) != null) {
							output += output_line;
						}
					} catch (IOException e) {

						e.printStackTrace();
					}

					long stopTime = System.nanoTime();
					System.out.println("Total time : " + (stopTime - startTime));
					callback.callback(getAnswerSet(output));
				} catch (IOException e2) {
					e2.printStackTrace();
				}

			}
		});
		thread.start();


	}

	/**Start a new process for the {@link #exe_path} and starts solving
	 * @see it.unical.mat.embasp.base.Service#startSync(List, List)*/
	@Override
	public Output startSync(List<InputProgram> programs,
							List<OptionDescriptor> options) {

		Output to_return = null;

		try {


			String option = new String();
			for (OptionDescriptor o : options) {

				option += o.getOptions();

			}

			String files_paths = new String();
			String final_program = new String();

			for (InputProgram p : programs) {
				final_program += p.getPrograms();
				String program_file = p.getFilesPaths();
				if (program_file != null) {
					files_paths += program_file;
				}
			}


			long startTime = System.nanoTime();

			Process solver_process = Runtime.getRuntime().exec(exe_path + " " + option + files_paths + "--");

			PrintWriter writer = new PrintWriter(solver_process.getOutputStream());
			writer.println(final_program);
			writer.flush();
			writer.close();

			BufferedReader output_reader = new BufferedReader(new InputStreamReader(solver_process.getInputStream()));
			String output_line = new String();
			String output = new String();
			try {
				while ((output_line = output_reader.readLine()) != null) {
					output += output_line;
				}
			} catch (IOException e) {

				e.printStackTrace();
			}

			long stopTime = System.nanoTime();
			System.out.println("Total time : " + (stopTime - startTime));
			to_return = getAnswerSet(output);
		} catch (IOException e2) {
			e2.printStackTrace();
		}


		return to_return;

	}


	abstract protected AnswerSets getAnswerSet(String output);


}
