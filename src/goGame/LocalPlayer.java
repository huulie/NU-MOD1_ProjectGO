package goGame;

import com.nedap.go.gui.GoGuiIntegrator;

import exceptions.InvalidFieldException;
import goUI.GoGuiUpdater;
import goUI.GoTUICommands;

// import ss.utils.TextIO;

/**
 * Class for maintaining a human player in Go.
 * 
 * @author Huub Lievestro
- */
public class LocalPlayer extends Player {

	private GoLocalTUI TUI;
	private GoGuiIntegrator GUI;
	private GoGuiUpdater GUIupdater;

	private boolean outputBoardToTUI;
	
    // -- Constructors -----------------------------------------------

    /**
     * Creates a new local player object.
     * @requires name is not null
     * @requires mark is either XX or OO
     * @ensures the Name of this player will be name
     * @ensures the Mark of this player will be mark
     * 
     * @param GUI start a GUI (note: only one GUI can be started per VM)
     */
    public LocalPlayer(String name, Stone color, int boardDim, boolean GUI) {
        super(name, color);
        this.TUI = new GoLocalTUI(this);
        
        if (GUI) {
    	this.TUI.showMessage("Player " + name + " will use a GUI");
        this.GUI = new GoGuiIntegrator(true, true, boardDim);
    	this.GUI.startGUI();
        this.GUI.setBoardSize(boardDim);
        this.GUIupdater = new GoGuiUpdater(this.GUI);
        this.outputBoardToTUI = false;
        } else {
        	this.TUI.showMessage("Player " + name + " will use a TUI");
        	this.outputBoardToTUI = true;
        }
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
    			+ ", what is your move? (index or " + GoTUICommands.PASS + " to PASS) ";

    	//    	int move = TUI.getInt(question);
    	int move = TUI.getMove(question);

    	if (move != GoGameConstants.PASSint) {
    		boolean validPlacement = board.isField(move) && board.isEmptyField(move);

    		Board checkPrevious = board.deepCopy();
    		try {
    			checkPrevious.setField(move, this.getColor());
    		} catch (InvalidFieldException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}

    		boolean validPrevious = board.checkSamePreviousState(checkPrevious.returnIntersectionArray());

    		boolean valid = validPlacement; // && validPrevious; //TODO: reanable previious 

    		while (!valid) {
    			TUI.showMessage("ERROR: field " + move + " is no valid move.");
    			move = TUI.getInt(question);
    			valid = board.isField(move) && board.isEmptyField(move);
    		}
    	}        
        return move;
    }
    
    
//    /**
//     * Makes a move on the board. <br>
//     * @requires board is not null and not full
//     * @param board the current board
//     * @return 
//     */
//    @Override
//    public char makeMove(Board board) {
//        int choice = determineMove(board);
//        board.setField(choice, this.getColor());
//        
//        if (outputBoardToTUI) {
//        	TUI.showMessage(board.toString());
//        }
//        
//        //this.GUIupdater.updateWholeBoard(board);
//		//GUI.addStone(board.index(choice).getCol(), board.index(choice).getRow(), (this.getColor().equals(Stone.WHITE))); // TODO: think about location
//    }
    
    /**
     * Displays updated board in GUI
     */
    // TODO: decide how and where to do this
    @Override
    public void updateGUI(Board board) {
    	if (this.hasGUI()) {
    	this.GUIupdater.updateWholeBoard(board);
    	} else {
    		this.TUI.showMessage("There is no GUI to update!");
    	}
    	
    }
    
    /**
     * TODO doc
     */
    @Override
    public boolean hasGUI() {
		return (this.GUI != null);
	}



	/**
     * Displays message on local user interface
     */
    @Override
    public void displayMessage(String message) {
            this.TUI.showMessage(message);
    }


}
