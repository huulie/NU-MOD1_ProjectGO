package goGame;

/**
 * Represents a stone in the GO game. There three possible values:
 * Mark.BLACK, Mark.WHITE and Mark.UNOCCUPIED.
 * 
 * @author Huub Lievestro
 */
public enum Stone {
	BLACK("B"), WHITE("W"), UNOCCUPIED("U"); // TODO: vervangen door protocol messages?!
	
	private final String print;
	
	private Stone(String print) {
		this.print = print;
	}
    
    @Override
    public String toString() {
    	return this.print;
    }

    /**
     * Returns the other stone.
     * @ensures that OO is returned if this mark is XX, that XX is returned 
     * 			when if mark is OO and EMPTY otherwise 
     * @return the other mark is this mark is not EMPTY or EMPTY
     */
    public Stone other() {
        if (this == BLACK) {
            return WHITE;
        } else if (this == WHITE) {
            return BLACK;
        } else {
            return UNOCCUPIED;
        }
    }
}
