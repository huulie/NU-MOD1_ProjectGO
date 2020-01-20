package goGame;

import com.nedap.go.gui.GoGuiIntegrator;

// import ss.utils.TextIO;

/**
 * Class for maintaining a human player in Go.
 * 
 * @author Huub Lievestro
- */
public class LocalPlayer extends Player {

	private GoLocalTUI TUI;
	private GoGuiIntegrator GUI;
	
    // -- Constructors -----------------------------------------------

    /**
     * Creates a new local player object.
     * @requires name is not null
     * @requires mark is either XX or OO
     * @ensures the Name of this player will be name
     * @ensures the Mark of this player will be mark
     */
    public LocalPlayer(String name, Stone color, int boardDim) {
        super(name, color);
        this.TUI = new GoLocalTUI(this);
        this.GUI = new GoGuiIntegrator(true, true, boardDim);
        this.GUI.startGUI();
        this.GUI.setBoardSize(boardDim);
    }

    // -- Commands ---------------------------------------------------

    /**
     * Asks the user to input the field where to place the next mark. This is
     * done using the standard input/output.
     * @requires board is not null
     * @ensures the returned in is a valid field index and that field is empty 
     * @param board the game board
     * @return the player's chosen field
     */
    public int determineMove(Board board) {
    	String question = "> " + getName() + " (" + getColor() + ")"
                + ", what is your move? ";
 
    	int move = TUI.getInt(question);
        
        boolean validPlacement = board.isField(move) && board.isEmptyField(move);
        
        Board checkPrevious = board.deepCopy();
        checkPrevious.setField(move, this.getColor());
        boolean validPrevious = board.checkSamePreviousState(checkPrevious.returnIntersectionArray());
        
        boolean valid = validPlacement && validPrevious; 
      
        while (!valid) {
            TUI.showMessage("ERROR: field " + move + " is no valid move.");
            move = TUI.getInt(question);
            valid = board.isField(move) && board.isEmptyField(move);
            
        }        
        return move;
    }
    
    
    /**
     * Makes a move on the board. <br>
     * @requires board is not null and not full
     * @param board the current board
     */
    @Override
    public void makeMove(Board board) {
        int choice = determineMove(board);
        board.setField(choice, this.getColor());
		GUI.addStone(board.index(choice).getCol(), board.index(choice).getRow(), (this.getColor().equals(Stone.WHITE))); // TODO: think about location
    }
    
    
    /**
     * Displays message on local user interface
     */
    @Override
    public void displayMessage(String message) {
            this.TUI.showMessage(message);
    }

}
