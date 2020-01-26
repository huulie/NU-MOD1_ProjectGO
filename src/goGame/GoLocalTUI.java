package goGame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

import exceptions.ExitProgram;
import goClient.GoClient; // TODO check for unused imports
import goUI.GoTUI;
import goUI.GoTUICommands;

/** Go Game Local TUI.
 * Can be used by a local player or locally on server //TODO thinks about this> maybe seperate?
 * @author huub.lievestro
 *
 */
public class GoLocalTUI extends GoTUI {

	//	/** Constructor. TODO: need a TUI to know which player it's connected to? 
	//	 * @param client corresponding client
	//	 */
	//	public GoLocalTUI(LocalPlayer player) {
	//		super();
	//		this.player = player; 
	//	}

	/** Creates a new local TUI, not bound to a player.
	 */
	public GoLocalTUI() {
		super();
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
			// Could be implemented: controller."help" TODO: add something about player?
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
			this.showMessage("I don't understand this command, try again");
			this.showMessage("Maybe this helps:");
			printHelpMenu();
		}
	}

	/**
	 * Prints the help menu with available input options.
	 */
	@Override
	public void printHelpMenu() {
		this.showMessage("Commands:");
		this.showMessage(GoTUICommands.HELP + " ................ help (this menu)");
		this.showMessage(GoTUICommands.EXIT + " ................ exit program");
		this.showMessage("TO BE IMPLEMTED FURHTER"); // TODO NEED TO ADD SOME THINGS? 
		
	}

}
