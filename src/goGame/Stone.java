package goGame;

/**
 * Represents a stone in the GO game. There three possible values:
 * GoGameConstants.BLACK, GoGameConstants.WHITE and GoGameConstants.UNOCCUPIED.
 * 
 * @author Huub Lievestro
 */
public enum Stone {
	BLACK(GoGameConstants.BLACK), WHITE(GoGameConstants.WHITE), UNOCCUPIED(GoGameConstants.UNOCCUPIED); // TODO: vervangen door protocol messages?!
	
	private final String print;
	
	private Stone(char print) {
		this.print = String.valueOf(print);
	}
    
    @Override
    public String toString() {
    	return this.print;
    }

    /**
     * Returns the other stone colour.
     * @ensures that WHITE is returned if this stone is BLACK, and vice versa.
     * Returns UNOCCUPIED otherwise 
     * @return the other stone colour if this mark is not UNOCCUPIED, or UNOCCUPIED
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
