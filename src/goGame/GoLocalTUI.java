package goGame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

import exceptions.ExitProgram;
import goClient.GoClient;
import goUI.GoTUI;
import goUI.GoTUICommands;

/** Hotel Client TUI.
 * @author huub.lievestro
 *
 */
public class GoLocalTUI extends GoTUI {

private LocalPlayer player; // corresponding controller
	
	
	/** Constructor.
	 * @param client corresponding client
	 */
	public GoLocalTUI(LocalPlayer player) {
		super();
		this.player = player;
	}
	
	/** Constructor, not bound to a player.
	 */
	public GoLocalTUI() {
		GoLocalTUI(null);
	}

	/**
	 * Split the user input on a space and handle it accordingly. 
	 * - If the input is valid, take the corresponding action via the controller LocalPlayer
	 * - If the input is invalid, show a message to the user and print the help menu.
	 * 
	 * @param input The user input.
	 * @throws ExitProgram When the user has indicated to exit the program.
	 */
	@Override
	public void handleUserInput(String input) throws ExitProgram {
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

		case GoTUICommands.HELP:
			this.showMessage(" - Help for the GO game - ");
			// Could be implemented: controller."help"
			printHelpMenu();
			break;
			
			case GoTUICommands.EXIT:
    			String confirmation = null;

    			while (confirmation == null) {
    				confirmation = this.getString("Are you sure? [Y]es / [N]o ");

    				if (confirmation.equalsIgnoreCase("Y")) {
    					this.showMessage("Bye bye! ");
    					throw new ExitProgram("User requested EXIT");
    				} else if (confirmation.equalsIgnoreCase("N")) {
    					this.showMessage("Okay, continue to run client ");
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
