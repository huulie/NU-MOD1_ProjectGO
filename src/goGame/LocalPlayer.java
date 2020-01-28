package goGame;

import com.nedap.go.gui.GoGUIIntegrator;

import exceptions.InvalidFieldException;
import goUI.GoGuiUpdater;
import goUI.GoTUICommands;


/**
 * A local player in Go. // TODO consistent documentation (and formulation)
 * 
 * @author Huub Lievestro
- */
public class LocalPlayer extends Player {

	
	/** 
	 * Associated TUI of this local Player.
	 */
	private GoLocalTUI TUI;

	/** 
	 * Associated GUI of this local Player.
	 */
	private GoGUIIntegrator GUI;

	/** 
	 * To update the associated GUI of this local Player.
	 */
	private GoGuiUpdater GUIupdater;

	/** 
	 * Indicates if the board has to be sent to the TUI
	 * TODO what to do with this? 
	 */
	private boolean outputBoardToTUI;

	/**
	 * Creates a new local player object.
	 * @requires name is not null
	 * @requires Stone is either BLACK or WHITE
	 * @ensures the Name of this player will be name
	 * @ensures the Stone of this player will be colour
	 * @param name of this player
	 * @param colour of the stones of this player
	 * @param boardDim if using a GUI, the board dimensions have to be set // TODO ergens afleiden?
	 * @param GUI start a GUI (note: only one GUI can be started per VM)
	 */
	public LocalPlayer(String name, Stone colour, int boardDim, boolean GUI) {
		super(name, colour);
		this.TUI = new GoLocalTUI(); // TODO: also here, think about if the TUI needs to knwo about player

		if (GUI) {
			this.TUI.showMessage("Player " + name + " will use a GUI");
			this.GUI = new GoGUIIntegrator(true, true, boardDim);
			this.GUI.startGUI();
			this.GUI.setBoardSize(boardDim);
			this.GUIupdater = new GoGuiUpdater(this.GUI, boardDim);
			this.outputBoardToTUI = false;
		} else {
			this.TUI.showMessage("Player " + name + " will use a TUI");
			this.outputBoardToTUI = true;
		}
	}

	/**
	 * Asks the user to input the field where to place the next stone, via the associated TUI
	 * @requires board is not null
	 * @ensures the returned int is a valid and empty field index and not recreating previous board state (checked on board clone) 
	 * @param board the game board
	 * @return the player's chosen field
	 */
	public int determineMove(Board board) {
		String question = "> " + getName() + " (" + getColour() + ")"
				+ ", what is your move? (index or " + GoTUICommands.PASS + " to PASS) ";

		int move = TUI.getMove(question);

		if (move != GoGameConstants.PASSint) {
			boolean validPlacement = board.isField(move) && board.isEmptyField(move);

			Board checkSamePrevious = board.deepCopy();
			try {
				checkSamePrevious.setField(move, this.getColour()); // NOTE: checking on clone of board, not actually placing stone
			} catch (InvalidFieldException e) {
				TUI.showMessage("Something went wrong when checking the stone: " + e.getLocalizedMessage());
				e.printStackTrace();
			}

			boolean validPrevious = board.checkSamePreviousState(checkSamePrevious.returnIntersectionArray());

			boolean valid = validPlacement && validPrevious; //TODO: reanable previious 

			while (!valid) {
				TUI.showMessage("ERROR: field " + move + " is no valid move.");
				move = TUI.getInt(question);
				valid = board.isField(move) && board.isEmptyField(move);
			}
		}        
		return move;
	}

	//    @Override TODO implement makeMove here ?
	//    public char makeMove(Board board) {

	/**
	 * Check if this Local Player has a GUI
	 * @return true if this local player has a GUI, false if not.
	 */
	@Override
	public boolean hasGUI() {
		return (this.GUI != null);
	}
	
	/**
	 * Displays updated board in this players GUI 
	 * (or if not present: message to TUI)
	 */
	@Override
	public void updateGUI(Board board) {
		if (this.hasGUI()) {
			this.GUIupdater.updateWholeBoard(board);
		} else {
			this.TUI.showMessage("There is no GUI to update!");
		}
	}

	/**
	 * Shows the result of the move to the Player
	 * @param board data to update
	 */
	 @Override
	public void moveResult(char result, Board board) {
		 TUI.showMessage("Result: " + result);
	}
	
	/**
	 * Displays message on local TUI
	 */
	@Override
	public void displayMessage(String message) {
		this.TUI.showMessage(message);
	}

}
