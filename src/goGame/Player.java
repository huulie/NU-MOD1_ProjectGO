package goGame;

import java.util.Arrays;

import exceptions.InvalidFieldException;
import exceptions.TimeOutException;
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
	 * GameController associated to this Player
	 */
	private GameController game;

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
	 * @throws TimeOutException 
	 */
	public abstract int determineMove(Board board) throws TimeOutException;
	
	/**
	 * Check if this Player has a (a field named) GUI
	 */
	public boolean hasGUI() {
		return Arrays.stream(this.getClass().getFields())
				.anyMatch(f -> f.getName().equals("GUI"));
	}
	
	public GameController getGame() {
		return game;
	}

	// -- Commands ---------------------------------------------------

	public void setGame(GameController game) {
		this.game = game;
	}
	
	/**
	 * Makes a move on the board. <br>
	 * @requires board is not null and not full
	 * @param board the current board
	 */
	public char makeMove(Board board, int choice) {
		//TODO check if this validation works
		if (choice == GoGameConstants.PASSint) {
			return GoGameConstants.PASS;
		} else {
			boolean validPlacement = board.isField(choice) && board.isEmptyField(choice);

			Board checkSamePrevious = board.deepCopy();
			try {
				checkSamePrevious.setField(choice, this.getColour()); 
				// NOTE: checking on clone of board, not actually placing stone
			} catch (InvalidFieldException e) {
				TUI.showMessage("Something went wrong when checking the stone: " 
						+ e.getLocalizedMessage()); // TODO TUI?
				e.printStackTrace();
			}

			boolean validPrevious = 
					board.checkSamePreviousState(checkSamePrevious.returnIntersectionArray());

			boolean valid = validPlacement;//&& validPrevious; //TODO: reanable previious 

			if (!valid) {
				return GoGameConstants.INVALID;
			} else {
				try {
					board.setField(choice, this.getColour());
				} catch (InvalidFieldException e) {
					TUI.showMessage("Weird: invalid field was not detected, but now: " 
							+ e.getLocalizedMessage());
					return GoGameConstants.INVALID;
				}
				return GoGameConstants.VALID;
			}
		}
	}

	/**
	 * Displays message on user interface.
	 */
	public void displayMessage(String message) {
		this.TUI.showMessage(message);
	}

	/**
	 * Updates the GUI of the Player with this board.
	 * @param board data to update GUI with
	 */
	public void updateGUI(Board board) {
	}
	
	/**
	 * Shows the result of the move to the Player.
	 * @param board data to update
	 */
	public void moveResult(char result, Board board) {
	}


	/**
	 * Ends game for player, showing result.
	 * @param board data to update
	 */
	public void endGame(char reason, char winner, double scoreBlack, double scoreWhite) {
		
	}
	
	/**
	 * Returns the colour of the player.
	 */
	public void setColour(Stone colour) {
		this.colour = colour;
	}

}
