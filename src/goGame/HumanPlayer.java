package goGame;

// import ss.utils.TextIO;

/**
 * Class for maintaining a human player in Go.
 * 
 * @author Huub Lievestro
- */
public class HumanPlayer extends Player {

    // -- Constructors -----------------------------------------------

    /**
     * Creates a new human player object.
     * @requires name is not null
     * @requires mark is either XX or OO
     * @ensures the Name of this player will be name
     * @ensures the Mark of this player will be mark
     */
    public HumanPlayer(String name, Stone color) {
        super(name, color);
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
        String prompt = "> " + getName() + " (" + getColor() + ")"
                + ", what is your choice? ";
        
        System.out.println(prompt);
 //       int choice = TextIO.getInt();
        int choice = 0;
        
        boolean valid = board.isField(choice) && board.isEmptyField(choice);
        while (!valid) {
            System.out.println("ERROR: field " + choice
                    + " is no valid choice.");
            System.out.println(prompt);
//            choice = TextIO.getInt();
            valid = board.isField(choice) && board.isEmptyField(choice);
        }
        return choice;
    }

}
