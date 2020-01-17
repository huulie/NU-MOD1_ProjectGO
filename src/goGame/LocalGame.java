package goGame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;

public class LocalGame {
	
	GameController localGame;

	public static void main(String[] args) {
		assert (args.length <= 2) : "Sorry, two players maximum!";
		
		String name1;
		String name2;
		
		if (args.length == 0) {
			name1 = getString("Please enter name of the first player and press return: ");
		} else {
			showMessage("The first player is: " + args[0]);
			name1 = args[0];
		}
		
		if (args.length <= 1) {
			name2  = getString("Please enter name of the second player and press return: ");
			
		} else {
			showMessage("The second player is: " + args[1]);
			name2 = args[1];
		}

//		Player player1 = createNewPlayer(name1, Stone.BLACK); // TODO change to colors, changeable?
//		Player player2 = createNewPlayer(name2, Stone.WHITE); // TODO change to colors, changeable?
		
		int boardDim = 5;
		
		Player player1 = new LocalPlayer(name1, Stone.BLACK, boardDim); // TODO change to gamecontroller
		Player player2 = new LocalPlayer(name2, Stone.WHITE, boardDim); // TODO change to gamecontroller
		
		boolean continueGame = true;
		while (continueGame) {
		showMessage("\n -- Let the game begin! -- \n"); 
		(new GameController(19, player1, player2)).start();
		// localGame = new GameController(19, player1, player2);
		// localGame.start();
		continueGame = getBoolean("\n> Play another time? (y/n)?");
		}
		showMessage("Bye bye!");
		
	}
	
	
	/**
	 * Writes the given message to system output.
	 * 
	 * @param msg the message to write to the system output.
	 */
	public static void showMessage(String message) { // TODO implement in separate TUI?
		System.out.println(message);
	}

	/**
	 * Prints the question and asks the user to input a String.
	 * 
	 * @param question the question shown to the user, asking for input
	 * @return The user input as a String
	 */
	public static String getString(String question) { // TODO implement in separate TUI?
		showMessage(question); // manual new line, for better layout (no extra white lines)
        String antw = null;
        try {
        	BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            antw = in.readLine();
        } catch (IOException e) {
        	showMessage("IO exception: " + e.getLocalizedMessage());
        }
        return (antw == null) ? "" : antw;
	}
	
	/**
	 * Prints the question and asks the user for a yes/no answer.
	 * 
	 * @param question the question shown to the user, asking for input
	 * @return The user input as boolean.
	 */
	public static boolean getBoolean(String question) {
		String answer = null;
		Boolean answerBool = false;
		Boolean answerValid = false;

		while (!answerValid) {
			showMessage(question); 
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				answer = in.readLine();

				answerBool = Boolean.parseBoolean(answer);
				answerValid = true;
			} catch (NumberFormatException eFormat) {
				showMessage("ERROR> " + answer +  " is not an boolean (" 
						+ eFormat.getLocalizedMessage() + ") try again!");
			} catch (IOException e) {
				showMessage("IO Exception occurred");
			}
		}
        return answerBool;
	}

}
