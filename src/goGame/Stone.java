package goGame;

/**
 * Represents a stone in the GO game. There three possible values:
 * GoGameConstants.BLACK, GoGameConstants.WHITE and GoGameConstants.UNOCCUPIED.
 * 
 * @author Huub Lievestro
 */
public enum Stone {
	BLACK(GoGameConstants.BLACK), WHITE(GoGameConstants.WHITE), UNOCCUPIED(GoGameConstants.UNOCCUPIED); // TODO: vervangen door protocol messages?!
	
	private final char print;
	
	private Stone(char print) {
		this.print = print;
	}
	
	private char print() {
		return this.print;
	}
    
    @Override
    public String toString() {
    	return String.valueOf(this.print);
    }

    /**
     * Convert char to Stone TODO: DOC
     * @param name
     * @return
     */
    public static Stone charToStone(char name){
    	for (Stone stone : Stone.values())
            if (stone.print() == name)
                return stone;

        return null;
    	
        // TODO: JAVA 8, but no Arrays in enum?
    	//return Arrays.stream(FileType.values()).filter(f -> f.name == name).findAny().orElseThrow(IllegalArgumentException::new);
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
