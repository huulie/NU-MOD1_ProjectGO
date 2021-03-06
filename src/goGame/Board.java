package goGame;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import exceptions.InvalidFieldException;

import org.apache.commons.codec.binary.Hex;

/**
 * Model of board for the GO game. 
 *
 * @author Huub Lievestro
 */
public class Board {

	/**
	 * Dimension of the board (defined as number of intersections on one side).
	 */
	private int DIM;

	/**
	 * The DIM by DIM intersections of the GO board, represented as Stone objects. 
	 * Intersections are numbered with a linear index, row-major order, starting at zero
	 * These intersections are stored in 2D array, 
	 * first index is horizontal axis and second index is vertical axis.
	 * @invariant there are always DIM*DIM intersections
	 * @invariant all intersections are either Stone.BLACK, Stone.WHITE or Stone.UNOCCUPIED
	 */
	private Stone[][] intersections;

	/**
	 * The previous states of the DIM by DIM intersections of the GO board, saved as hash. 
	 * List index is in chronological order, with highest index being latest state
	 */
	private List<String> previousStates = new ArrayList<String>(); // save only hashes

	/**
	 * Creates an empty board.
	 * @throws InvalidFieldException 
	 * @ensures all intersections are UNOCCUPIED
	 */
	public Board(int dimension) throws InvalidFieldException {
		DIM = dimension;
		intersections = new Stone[DIM][DIM];
		this.reset(); // set all to empty (instead of default null), also resets previous states
	}

	/** 
	 * Get the dimension of the board.
	 * @return DIM dimension of the board
	 */
	public int getDim() {
		return this.DIM;
	}

	/**
	 * Creates a deep copy of this board.
	 * @ensures the result is a new object, so not this object
	 * @ensures the values of all intersections of the copy match the ones of this Board
	 */
	public Board deepCopy() {
		return this.clone();
	}

	/**
	 * Calculates the index in the linear array of intersections from a (row, col) pair.
	 * @requires row to be between 0 and DIM
	 * @requires col to be between 0 and DIM
	 * @return the index belonging to the (row,col)-intersection, 
	 * or -1 if 2D is outside board TODO use exception?
	 */
	public int indexLinear(int row, int col) {
		if (!(row >= 0 && row < DIM) || !(col >= 0 && col < DIM)) {
			return -1;
		}
		return row * DIM + col;
	}

	/**
	 * Calculates the 2D coordinate in the 2D matrix of intersections from a linear index pair.
	 * @requires linear index to be between 0 and DIM*DIM
	 * @return the corresponding (row,col) index, or null if outside board TODO use exception?
	 */
	public Coordinate2D index(int i) {
		if (!(i >= 0) && !(i < DIM * DIM)) {
			return null;
		}
		Coordinate2D index = new Coordinate2D(i / DIM, i % DIM);
		return index;
	}

	/**
	 * Calculates the 2D coordinate in the 2D matrix of intersections from a row,col pair.
	 * @requires row to be between 0 and DIM
	 * @requires col to be between 0 and DIM
	 * @return the corresponding (row,col) index
	 */
	public Coordinate2D index(int row, int col) {
		assert row >= 0 && row < DIM : "Row should be between zero and DIM";
		assert col >= 0 && col < DIM : "Col should be between zero and DIM";		
		// what TODO with assertions? 

		Coordinate2D index = new Coordinate2D(row, col);
		return index;
	}

	/** Check if linear index is valid (on board).
	 * Returns true if index is a valid index of a intersection on the board.
	 * @ensures a positive result when the index is between 0 and DIM*DIM
	 * @return true if 0 <= index < DIM*DIM
	 */
	public boolean isField(int index) {
		return isField(this.index(index));
	}

	/**
	 * Returns true if the (row,col) pair refers to a valid intersection on the board.
	 * @ensures true when both row and col are within the board's bounds
	 * @return true if 0 <= row < DIM && 0 <= col < DIM
	 */
	public boolean isField(int row, int col) {
		return isField(this.index(row, col));
	}

	/**
	 * Returns true of the 2D coordinate refers to a valid intersection on the board.
	 * @ensures true when both row and col are within the board's bounds
	 * @return true if 0 <= row < DIM && 0 <= col < DIM
	 */
	public boolean isField(Coordinate2D coordinate) {
		if ((coordinate.getRow() >= 0 && coordinate.getRow() < DIM) 
				&& 	(coordinate.getCol() >= 0 && coordinate.getCol()  < DIM)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns the content of the intersection i.
	 * @requires i to be a valid intersection
	 * @ensures the result to be either BLACK, WHITE or UNOCCUPIED
	 * @param i the index of the intersection
	 * @return the Stone on the intersection
	 */
	public Stone getField(int i) {
		assert this.isField(i) : "Non-valid intersection for this board!";
		return getField(this.index(i));
	}

	/**
	 * Returns the content of the intersection referred to by the (row,col) pair.
	 * @requires (row, col) to be a valid intersection
	 * @ensures the result to be either BLACK, WHITE or UNOCCUPIED
	 * @param row the row of the intersection
	 * @param col the column of the intersection
	 * @return the stone on the intersection
	 */
	public Stone getField(int row, int col) {
		assert this.isField(row, col) : "Non-valid intersection for this board!";
		return getField(this.index(row, col));
	}

	/**
	 * Returns the content of the intersection referred to by the 2D coordinate.
	 * @requires 2D coordinate to be a valid intersection
	 * @ensures the result to be either BLACK, WHITE or UNOCCUPIED
	 * @param coordinate of the intersection
	 * @return the stone on the intersection
	 */
	public Stone getField(Coordinate2D coordinate) {
		assert this.isField(coordinate) : "Non-valid intersection for this board!";
		return this.intersections[coordinate.getRow()][coordinate.getCol()];
	}

	/**
	 * Returns true if the intersection i is empty.
	 * @requires i to be a valid intersection index
	 * @ensures true when the Stone at index i is UNOCCUPIED
	 * @param i the index of the intersection 
	 * @return true if the intersection is empty
	 */
	public boolean isEmptyField(int i) {
		assert this.isField(i) : "Non-valid intersection for this board!";
		return isEmptyField(this.index(i));
	}

	/**
	 * Returns true if the intersection referred to by the (row,col) pair it empty.
	 * @requires (row, col) to be a valid intersection
	 * @ensures true when the Stone at (row, col) is UNOCCUPIED
	 * @param row the row of the intersection
	 * @param col the column of the intersection
	 * @return true if the intersection is empty
	 */
	public boolean isEmptyField(int row, int col) {
		assert this.isField(row, col) : "Non-valid intersection for this board!";
		return isEmptyField(this.index(row, col));
	}

	/**
	 * Returns true if the intersection referred to by the 2D coordinate it empty.
	 * @requires 2D coordinate to be a valid intersection
	 * @ensures true when the Stone at 2D coordinate is UNOCCUPIED
	 * @param coordinate of the intersection
	 * @return true if the intersection is empty
	 */
	public boolean isEmptyField(Coordinate2D coordinate) {
		assert this.isField(coordinate) : "Non-valid intersection for this board!"; 
		return getField(coordinate) == Stone.UNOCCUPIED;
	}

	/**
	 * Returns a String representation of this board. 
	 *
	 * @return the game situation as String
	 */
	public String toString() {
		String s = "";
		for (int i = 0; i < DIM; i++) {
			String row = "";
			for (int j = 0; j < DIM; j++) {
				row = row + " " + getField(i, j).toString() + " ";
			}
			s = s + row;
		}
		s = s.replaceAll("\\s+", "");
		return s;
	}

	/**
	 * Returns a formatted String representation of this board.
	 * TODO: also implement numbering?
	 *
	 * @return the game situation as String
	 */
	public String toStringFormatted() {
		String s = "";
		for (int i = 0; i < DIM; i++) {
			String row = "";
			for (int j = 0; j < DIM; j++) {
				row = row + " " + getField(i, j).toString();
			}
			s = s + row + "\n";
		}
		return s;
	}
	
	/**
	 * Creates a new Board object from a String representation of the board.
	 * NOTE: creates a new board (will not be connected to the game)
	 * @param boardString to parse
	 * @return newBoard from String
	 */
	public static Board newBoardFromString(String boardString) {
		int boardDim = (int) Math.sqrt(boardString.length());
		Board newBoard;
		try {
			newBoard = new Board(boardDim);

			for (int i = 0; i < boardDim * boardDim; i++) {
				newBoard.setField(i, Stone.charToStone(boardString.charAt(i)));
			}
		} catch (InvalidFieldException e) {
			System.out.println("Invalid field exception: " + e.getLocalizedMessage());
			e.printStackTrace();
			newBoard = null;
		}
		return newBoard;
	}
	
	/**
	 * Overrides a existing Board object from a String representation of the board.
	 * NOTE: modifies an existing board (will not be connected to the game, but previous states are remembered)
	 * NOTE: does NOT process any captures before adding the overridden board state to the current state
	 * @param boardString to parse
	 * @return void
	 */
	public void overrideBoardFromString(String boardString) {
		int boardDim = (int) Math.sqrt(boardString.length());
		try {
			for (int i = 0; i < boardDim * boardDim; i++) {
				this.setField(i, Stone.charToStone(boardString.charAt(i)));
			}
		} catch (InvalidFieldException e) {
			System.out.println("Invalid field exception: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		this.previousStates.add(this.getIntersectionsHash(this.returnIntersectionArray()));
		System.out.println("Add overridden: " + this.toString());
	}
	
	/**
	 * Returns a String representation of an intersections array. 
	 *
	 * @param intersections to parse
	 * @return the game situation as String
	 */
	public String intersectionsToString(Stone[][] intersections) {
		String s = "";
		for (int i = 0; i < DIM; i++) {
			String row = "";
			for (int j = 0; j < DIM; j++) {
				row = row + " " + intersections[i][j].toString() + " ";
			}
			s = s + row;
		}
		s = s.replaceAll("\\s+", "");
		return s;
	}

	/**
	 * Empties all intersections of this board (i.e., let them refer to the value Stone.UNOCCUPIED).
	 * @throws InvalidFieldException 
	 * @ensures all intersections are UNOCCUPIED
	 */
	public void reset() throws InvalidFieldException {
		for (int emptyRow = 0; emptyRow < DIM; emptyRow++) {
			for (int emptyColumn = 0; emptyColumn < DIM; emptyColumn++) {
				intersections[emptyRow][emptyColumn] = Stone.UNOCCUPIED;
			}
		}
		this.previousStates.clear();
		this.previousStates.add(getIntersectionsHash(intersections));
	}

	/**
	 * Sets the content of intersection i to the stone s.
	 * @requires i to be a valid intersection
	 * @ensures intersection i to be set to Stone s
	 * @param i the intersection index 
	 * @param color of the stone to be placed
	 * @throws InvalidFieldException 
	 */
	public void setField(int i, Stone color) throws InvalidFieldException {
		this.setField(this.index(i), color);
		return;
	}

	/**
	 * Sets the content of the intersection represented by the (row,col) pair to the stone s.
	 * @requires (row, col) to be a valid intersection
	 * @ensures intersection (row, col) to be set to Stone s
	 * @param row the intersection's row
	 * @param col the intersection's column
	 * @param color of the stone to be placed
	 * @throws InvalidFieldException 
	 */
	public void setField(int row, int col, Stone color) throws InvalidFieldException {
		this.setField(this.index(row, col), color);
		return;
	}

	/**
	 * Sets the content of the intersection represented by the 2D coordinate to the stone s.
	 * @requires 2D coordinate be a valid intersection
	 * @ensures 2D coordinate to be set to Stone s
	 * @param coordinate the intersection's 2D coordinate
	 * @param color of the stone to be placed
	 * @throws InvalidFieldException  when trying to set invalid field (outside board)
	 */
	public char setField(Coordinate2D coordinate, Stone color) throws InvalidFieldException {
		if (this.isField(coordinate)) {
			this.intersections[coordinate.getRow()][coordinate.getCol()] = color;
			
			// TODO only add state AFTER processing the captures, to 
			// this.previousStates.add(getIntersectionsHash(intersections));
			//System.out.println("Add set field: " + this.toString());
			
			return GoGameConstants.VALID;
		} else {
			throw new InvalidFieldException(coordinate.toString() 
					+ " is not a valid field on this board!");
		}
	}

	/**
	 * Add the current board state to the list of previous states.
	 * NOTE: when interpreting the rule as "move may not lead to any previous state", also process captures
	 * @throws InvalidFieldException 
	 */
	public void addPreviousStateWithCaptures(Board stateToAdd, Stone currentPlayer) {
			BoardTools boardTools = new BoardTools(false);
			boardTools.doOpponentCaptures(stateToAdd, currentPlayer);
			boardTools.doOwnCaptures(stateToAdd, currentPlayer);
			this.previousStates.add(getIntersectionsHash(stateToAdd.returnIntersectionArray()));
	}
	
	/**
	 * Check if the current board state is equal to any previous state.
	 * NOTE: do checking BEFORE adding the new state to the list, otherwise it will always be true
	 * NOTE: when interpreting the rule as "move may not lead to any previous state", also process captures
	 * @throws InvalidFieldException 
	 * @return true if there is an same previous board state
	 */
	public boolean checkSamePreviousState(int choice, Stone colour) throws InvalidFieldException {
		// checkSamePreviousState(Stone[][] newState) {
		//return this.previousStates.contains(getIntersectionsHash(newState));
		
		
		Board checkSamePrevious = this.deepCopy();
		
		checkSamePrevious.setField(choice, colour); 
		
		BoardTools boardTools = new BoardTools(false);
		boardTools.doOpponentCaptures(checkSamePrevious, colour);
		boardTools.doOwnCaptures(checkSamePrevious, colour);

		// NOTE: checking on clone of board, not actually placing stone
		System.out.println("DEBUG: checking this board: " + checkSamePrevious.toString());
		System.out.println("DEBUG: contains previous state: "+ this.previousStates.contains(
				getIntersectionsHash(checkSamePrevious.returnIntersectionArray())));
		return this.previousStates.contains(
				getIntersectionsHash(checkSamePrevious.returnIntersectionArray()));
		
	}
	
	@Override
	public Board clone() {
		Board boardClone = null;
		try {
			boardClone = new Board(this.DIM);
		} catch (InvalidFieldException e) {
			System.out.println("ERROR> something went wrong while cloning the board: " 
					+ e.getLocalizedMessage());
			e.printStackTrace();
		}
		for (int indexRow = 0; indexRow < DIM; indexRow++) {
			for (int indexColumn = 0; indexColumn < DIM; indexColumn++) {
				boardClone.intersections[indexRow][indexColumn] = 
						this.intersections[indexRow][indexColumn];
			}
		}
		return boardClone;
	}

	/** 
	 * 2D coordinates, representing a (row,col) on the board.
	 * 
	 * All board indices are eventually converted into 2D coordinates.
	 * Note: these are not limited to the board! (negative also possible)
	 * 
	 * @author huub.lievestro
	 *
	 */
	public class Coordinate2D { 
		private int row;
		private int col;

		public Coordinate2D(int row, int col) {
			this.setRow(row);
			this.setCol(col);
		}

		public int getRow() {
			return row;
		}

		public void setRow(int row) {
			this.row = row;
		}

		public int getCol() {
			return col;
		}

		public void setCol(int col) {
			this.col = col;
		}

	}

	/**
	 * Sets the content of the intersection represented by the 2D coordinate to the stone s.
	 * @requires 2D coordinate be a valid intersection
	 * @ensures 2D coordinate to be set to Stone s
	 * @param coordinate the intersection's 2D coordinate
	 * @param color of the stone to be placed
	 */
	public Stone[][] returnIntersectionArray() {
		return intersections;
	}


	/**
	 * Returns the index of the intersection above the given intersection, or -1 if outside board.
	 * @requires index to refer to a valid intersection
	 * @returns index of the intersection above the given intersection, or -1 if outside board
	 * @param index intersection to start from
	 */
	public int above(int index) { // TODO: static, cannot because need to know dimensions
		Coordinate2D currentIntersection = index(index);

		int indexAbove = 
				this.indexLinear(currentIntersection.getRow() - 1, currentIntersection.getCol());

		//	if( (currentIntersection.getRow()-1)<0 || !this.isField(indexAbove)) { TODO: already here check if row in bounds? 
		if (!this.isField(indexAbove)) {
			return -1; // to indicate outside board
		}
		return indexAbove;
	}

	/**
	 * Returns the index of the intersection below the given intersection, or -1 if outside board.
	 * @requires index to refer to a valid intersection
	 * @returns index of the intersection below the given intersection, or -1 if outside board
	 * @param index intersection to start from
	 */
	public int below(int index) { 
		Coordinate2D currentIntersection = index(index);

		int indexBelow = 
				this.indexLinear(currentIntersection.getRow() + 1, currentIntersection.getCol());

		if (!this.isField(indexBelow)) {
			return -1; // to indicate outside board
		}
		return indexBelow;
	}

	/**
	 * Returns the index of the intersection left the given intersection, or -1 if outside board.
	 * @requires index to refer to a valid intersection
	 * @returns index of the intersection left the given intersection, or -1 if outside board
	 * @param index intersection to start from
	 */
	public int left(int index) { 
		Coordinate2D currentIntersection = index(index); 

		int indexLeft = 
				this.indexLinear(currentIntersection.getRow(), currentIntersection.getCol() - 1);
		int col = currentIntersection.getCol() - 1;
		if (!this.isField(indexLeft)) {
			return -1; // to indicate outside board
		}
		return indexLeft;
	}

	/**
	 * Returns index of the intersection right of the given intersection, or -1 if outside board.
	 * @requires index to refer to a valid intersection
	 * @returns index of the intersection right of the given intersection, or -1 if outside board
	 * @param index intersection to start from
	 */
	public int right(int index) { 
		Coordinate2D currentIntersection = index(index); 

		int indexRight = 
				this.indexLinear(currentIntersection.getRow(), currentIntersection.getCol() + 1);

		if (!this.isField(indexRight)) {
			return -1; // to indicate outside board
		}
		return indexRight;
	}
	
	/**
	 * Calculates the hash of 2D-array representing intersections.
	 * @param intersections
	 * @return string with hash of intersections
	 */
	private String getIntersectionsHash(Stone[][] intersections) {
		String intersectionsString = this.intersectionsToString(intersections);
		
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(intersectionsString.getBytes());
			return Hex.encodeHexString(md.digest()); 
		} catch (NoSuchAlgorithmException e) {
			System.out.println("No such algorithm: " + e.getLocalizedMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	/** 
	 * Returns List of integers with all empty intersections
	 */
	public List<Integer> getEmptyFields() {
		List<Integer> emptyFields = new ArrayList<Integer>();
		
		for (int indexRow = 0; indexRow < DIM; indexRow++) {
			for (int indexColumn = 0; indexColumn < DIM; indexColumn++) {
				if (this.isEmptyField(indexRow, indexColumn)){
					emptyFields.add(this.indexLinear(indexRow, indexColumn));
				}
			}
		}
		
		return emptyFields;
	}
	
}
