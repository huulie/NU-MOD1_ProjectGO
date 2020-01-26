package goGame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * Starter for a local game of Go, between two local players
 * @author huub.lievestro
 *
 */
public class LocalGameStarter {

	/**
	 * GameController of the started local game.
	 */
	static GameController localGame;

	/**
	 * Main method, to start the local game
	 * @param args may input one or two players name 
	 */
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

		int boardDim = 5; // TODO fixed board dimensions

		LocalPlayer player1 = new LocalPlayer(name1, Stone.BLACK, boardDim, false); 
		LocalPlayer player2 = new LocalPlayer(name2, Stone.WHITE, boardDim, false); 

		boolean continueGame = true;
		while (continueGame) {
			showMessage("\n -- Let the game begin! -- \n"); 

			localGame = new GameController(boardDim, player1, player2, true);
			localGame.startGame();

			continueGame = getBoolean("\n> Play another time? (true/false)?"); // TODO convert this to yes/no?
		}
		showMessage("Bye bye!");

	}

// TODO: use a local TUI instance? Or make a subclass of local TUI?
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
