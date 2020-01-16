package goServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import ss.week7.hotel.server.HotelServerView;


/**
 * GO Server TUI for user input and user messages.
 * 
 * @author Huub Lievestro
 */
public class GoServerTUI implements HotelServerView {
	
	/** The PrintWriter to write messages to. */
	private PrintWriter console;

	/**
	 * Constructs a new HotelServerTUI. Initializes the console.
	 */
	public GoServerTUI() {
		console = new PrintWriter(System.out, true);
	}

	@Override
	public void showMessage(String message) {
		console.println(message);
	}
	
	@Override
	public String getString(String question) {
		console.println(question); 
        String answer = null;
        try {
        	BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        	answer = in.readLine();
        } catch (IOException e) {
        	console.println("IO Exception occurred");
        }
        return (answer == null) ? "" : answer;
	}

	@Override
	public int getInt(String question) {
		String answer = null;
		int answerInt = 0;
		Boolean answerValid= false;

		while (!answerValid) {
			console.println(question); 
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				answer = in.readLine();

				answerInt = Integer.parseInt(answer);
				answerValid = true;
			} catch (NumberFormatException eFormat) {
				console.println("ERROR> " + answer +  " is not an integer (" 
						+ eFormat.getLocalizedMessage() + ") try again!");
			} catch (IOException e) {
				console.println("IO Exception occurred");
			}
		}
        return answerInt;
	}

	@Override
	public boolean getBoolean(String question) {
		String answer = null;
		Boolean answerBool = false;
		Boolean answerValid = false;

		while (!answerValid) {
			console.println(question); 
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				answer = in.readLine();

				answerBool = Boolean.parseBoolean(answer);
				answerValid = true;
			} catch (NumberFormatException eFormat) {
				console.println("ERROR> " + answer +  " is not an boolean (" 
						+ eFormat.getLocalizedMessage() + ") try again!");
			} catch (IOException e) {
				console.println("IO Exception occurred");
			}
		}
        return answerBool;
	}

}
