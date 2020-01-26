package goGame;

import java.util.Arrays;

import exceptions.InvalidFieldException;
import goUI.GoTUI;


/**
 * Abstract class for keeping a model of a player in the GO game. 
 * 
 * @author Huub Lievestro
 */
public abstract class Player {

	/**
	 * Name of this Player
	 */
	private String name;

	/**
	 * Stone colour of this Player
	 */
	private Stone colour;

	/**
	 * TUI associated to this Player
	 */
	private GoTUI TUI;


	// -- Constructor(s) ---------------------------------------------

	/**
	 * Creates a new Player object.
	 * @requires name is not null
	 * @requires colour is either GoGameConstants.BLACK or GoGameConstants.WHITE
	 * @ensures the Name of this player will be name
	 * @ensures the Stone of this player will be colour
	 */
	public Player(String name, Stone colour) {
		this.name = name;
		this.colour = colour;
	}


	// -- Queries ----------------------------------------------------

	/**
	 * Returns the name of the player.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the colour of the player.
	 */
	public Stone getColour() {
		return colour;
	}

	/**
	 * Determines the field for the next move.
	 * @requires board is not null and not full
	 * @ensures the returned in is a valid field index and that field is empty, or -1 to pass
	 * @param board the current game board
	 * @return the player's choice
	 */
	public abstract int determineMove(Board board);
	
	/**
	 * Check if this Player has a (a field named) GUI
	 */
	public boolean hasGUI() {
		return Arrays.stream(this.getClass().getFields())
				.anyMatch(f -> f.getName().equals("GUI"));
	}

	// -- Commands ---------------------------------------------------

	/**
	 * Makes a move on the board. <br>
	 * @requires board is not null and not full
	 * @param board the current board
	 */
	public char makeMove(Board board) {
		int choice = determineMove(board);

		if (choice == GoGameConstants.PASSint) {
			return GoGameConstants.PASS;
		} else {
			try {
				board.setField(choice, this.getColour());
				return GoGameConstants.VALID;
			} catch (InvalidFieldException e) {
				return GoGameConstants.INVALID;
			}
		}
	}

	/**
	 * Displays message on user interface
	 */
	public void displayMessage(String message) {
		this.TUI.showMessage(message);
	}

	public void updateGUI(Board board) {
	}

}
