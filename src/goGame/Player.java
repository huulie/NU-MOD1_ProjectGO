package goGame;

import goUI.GoTUI;

//import goProtocol.ProtocolMessages;


/**
 * Abstract class for keeping a model of a player in the GO game. 
 * 
 * @author Huub Lievestro
 */
public abstract class Player {

    // -- Instance variables -----------------------------------------

    private String name;
    private Stone color;
    private GoTUI TUI;
    

    // -- Constructors -----------------------------------------------

    /**
     * Creates a new Player object.
     * @requires name is not null
     * @requires color is either ProtocolMessages.BLACK or ProtocolMessages.WHITE
     * @ensures the Name of this player will be name
     * @ensures the Mark of this player will be color
     */
    public Player(String name, Stone color) {
        this.name = name;
        this.color = color;
    }

    // -- Queries ----------------------------------------------------

    /**
     * Returns the name of the player.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the color of the player.
     */
    public Stone getColor() {
        return color;
    }

    /**
     * Determines the field for the next move.
     * @requires board is not null and not full
     * @ensures the returned in is a valid field index and that field is empty
     * @param board the current game board
     * @return the player's choice
     */
    public abstract int determineMove(Board board);

    // -- Commands ---------------------------------------------------

    /**
     * Makes a move on the board. <br>
     * @requires board is not null and not full
     * @param board the current board
     */
    public void makeMove(Board board) {
        int choice = determineMove(board);
        board.setField(choice, this.getColor());
    }
    
    /**
     * Displays message on user interface
     */
    public void displayMessage(String message) {
            this.TUI.showMessage(message);
    }

	public abstract void updateGUI(Board board); // TODO: keep or adjust this?

}
