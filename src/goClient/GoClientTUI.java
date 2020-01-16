package goClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

import ss.week7.hotel.exceptions.ExitProgram;
import ss.week7.hotel.exceptions.ServerUnavailableException;

/** Hotel Client TUI.
 * @author huub.lievestro
 *
 */
public class GoClientTUI implements HotelClientView {

	private GoClient client;
	
	static final char IN = 'i';
	static final char OUT = 'o';
	static final char ROOM = 'r';
	static final char HELP = 'h';
	static final char PRINT = 'p';
	static final char EXIT = 'x';
	static final char BILL = 'b';
	static final char ACTIVATESAFE = 'a';
	
	
	/** Constructor.
	 * @param client corresponding client
	 */
	public GoClientTUI(HotelClient client) {
		super();
		this.client = client;
	}

	/**
	 * Asks for user input continuously and handles communication accordingly using
	 * the {@link #handleUserInput(String input)} method.
	 * 
	 * If an ExitProgram exception is thrown, stop asking for input, send an exit
	 * message to the server according to the protocol and close the connection.
	 * 
	 * @throws ServerUnavailableException in case of IO exceptions.
	 */
	@Override
	public void start() throws ServerUnavailableException {
		String input;
		try {
			input = this.getString("Input your command or " + HELP + " for help:");
			while (input != null) {
				handleUserInput(input);
				input = this.getString(" "); // Input your command or " + HELP + " for help:
			}
		} catch (ExitProgram eExit) {
			client.sendExit();
			System.out.println("User requested exit: server-client session ended");
		}
	}

	/**
	 * Split the user input on a space and handle it accordingly. 
	 * - If the input is valid, take the corresponding action (for example, 
	 *   when "i Name" is called, send a checkIn request for Name) 
	 * - If the input is invalid, show a message to the user and print the help menu.
	 * 
	 * @param input The user input.
	 * @throws ExitProgram               	When the user has indicated to exit the
	 *                                    	program.
	 * @throws ServerUnavailableException 	if an IO error occurs in taking the
	 *                                    	corresponding actions.
	 */
	@Override
	public void handleUserInput(String input) throws ExitProgram, ServerUnavailableException {
		String [] split = input.split("\\s+");

		char command = split[0].charAt(0);
		String param = null;
		String param2 = null;

		if (split.length > 1) {
			param = split[1];
		}
		if (split.length > 2) {
			param2 = split[2];
		}

		switch (command) {

    		case IN:
    			if (param == null) {	
    				System.out.println("Cannot check-in without any data");
    			}
    			client.doIn(param);
    			break;
    
    		case OUT:
    			if (param == null) {	
    				System.out.println("Cannot check-out without any data");
    			} else {
                    client.doOut(param);
    				System.out.println(param + " is checked out of the room");
    			}
    			break;
    
    		case ROOM:
    			if (param == null) {	
    				System.out.println("Cannot search without any data");
    			}
    			client.doRoom(param);
    			break;
    			
    		case ACTIVATESAFE:
    			if (param2 != null) {
    				client.doAct(param, param2);
    			} else {
    				System.out.println("Whoops, only one parameter.. but I need two!");
    			}
    			break;
    			
    		case BILL:
    			if (param2 != null) {
    				client.doBill(param, param2);
    			} else {
    				System.out.println("Whoops, only one parameter.. but I need two!");
    			}
    			break;
    
    		case HELP:
    			System.out.println(" - Help for the hotel booking system - ");
    			client.doHelp();
    			printHelpMenu();
    			break;
    
    		case PRINT:
    			client.doPrint();
    			break;
    
    		case EXIT:
    			String confirmation = null;

    			while (confirmation == null) {
    				confirmation = this.getString("Are you sure? [Y]es / [N]o ");

    				if (confirmation.equalsIgnoreCase("Y")) {
    					System.out.println("Bye bye! ");
    					throw new ExitProgram("User requested EXIT");
    				} else if (confirmation.equalsIgnoreCase("N")) {
    					System.out.println("Okay, continue to run client ");
    				} else {
    					confirmation = null; // all other inputs are ignored
    				}
    			}
    			break;

    		default:
    			System.out.println("I don't understand this command, try again");
    			System.out.println("Maybe this helps:");
    			printHelpMenu();
		}
	}

	/**
	 * Writes the given message to standard output.
	 * 
	 * @param msg the message to write to the standard output.
	 */
	@Override
	public void showMessage(String message) {
		System.out.println(message);
	}

	/**
	 * Ask the user to input a valid IP. If it is not valid, show a message and ask
	 * again.
	 * 
	 * @return a valid IP
	 */
	@Override
	public InetAddress getIp(String question) {
		String answer = null;
		InetAddress answerIP = null;
		Boolean answerValid = false;

		while (!answerValid) {
			System.out.println(question); 
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				answer = in.readLine();
				answerIP = InetAddress.getByName(answer);
				answerValid = true;
			} catch (UnknownHostException eFormat) {
				System.out.println("ERROR> " + answer +  " is not found (" 
						+ eFormat.getLocalizedMessage() + ") try again!");
			} catch (IOException e) {
				System.out.println("IO Exception occurred");
			}
		}
		return answerIP;
	}

	/**
	 * Prints the question and asks the user to input a String.
	 * 
	 * @param question The question to show to the user
	 * @return The user input as a String
	 */
	@Override
	public String getString(String question) {
		System.out.print(question); // manual new line, for better layout (no extra white lines)
        String antw = null;
        try {
        	BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            antw = in.readLine();
        } catch (IOException e) {
        	System.out.println("IO exception: " + e.getLocalizedMessage());
        }
        return (antw == null) ? "" : antw;
	}

	/**
	 * Prints the question and asks the user to input an Integer.
	 * 
	 * @param question The question to show to the user
	 * @return The written Integer.
	 */
	@Override
	public int getInt(String question) {
		String answer = null;
		int answerInt = 0;
		Boolean answerValid = false;

		while (!answerValid) {
			System.out.println(question); 
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				answer = in.readLine();

				answerInt = Integer.parseInt(answer);
				answerValid = true;
			} catch (NumberFormatException eFormat) {
				System.out.println("ERROR> " + answer +  " is not an integer (" 
						+ eFormat.getLocalizedMessage() + ") try again!");
			} catch (IOException e) {
				System.out.println("IO Exception occurred");
			}
		}
        return answerInt;
	}

	/**
	 * Prints the question and asks the user for a yes/no answer.
	 * 
	 * @param question The question to show to the user
	 * @return The user input as boolean.
	 */
	@Override
	public boolean getBoolean(String question) {
		String answer = null;
		Boolean answerBool = false;
		Boolean answerValid = false;

		while (!answerValid) {
			System.out.println(question); 
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				answer = in.readLine();

				answerBool = Boolean.parseBoolean(answer);
				answerValid = true;
			} catch (NumberFormatException eFormat) {
				System.out.println("ERROR> " + answer +  " is not an boolean (" 
						+ eFormat.getLocalizedMessage() + ") try again!");
			} catch (IOException e) {
				System.out.println("IO Exception occurred");
			}
		}
        return answerBool;
	}

	/**
	 * Prints the help menu with available input options.
	 */
	@Override
	public void printHelpMenu() {
		System.out.println("Commands:");
		System.out.println(IN + " name ........... check in guest with name");
		System.out.println(OUT + " name ........... check out guest with name");
		System.out.println(ROOM + " name ........... request room of guest");
		System.out.println(ACTIVATESAFE + " name password... activate safe (password required for PricedSafe)");
		System.out.println(BILL + " name nights..... print bill for guest (name) and number of nights");
		System.out.println(HELP + " ................ help (this menu)");
		System.out.println(PRINT + " ................ print state of hotel");
		System.out.println(EXIT + " ................ exit program");
	}

}
