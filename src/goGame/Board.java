package goGame;

import java.util.ArrayList;
import java.util.List;

/**
 * Board for the GO game. 
 *
 * @author Huub Lievestro
 */
public class Board {
	public int DIM = 0;
//	private static final String[] NUMBERING = {" 0 | 1 | 2 ", "---+---+---",
//			" 3 | 4 | 5 ", "---+---+---", " 6 | 7 | 8 "};
//	private static final String LINE = "--------+--------+--------"; //NUMBERING[1]; for layout
//	private static final String DELIM = "     ";

	/**
	 * The DIM by DIM intersections of the GO board, represented as Stone objects. 
	 * Intersections are numbered with a linear index, row-major order
	 * These intersections are stored in 2D array, first index is horizontal axis and second index is vertical axis.
	 * @invariant there are always DIM*DIM intersections
	 * @invariant all intersections are either Stone.BLACK, Stone.WHITE or Stone.UNOCCUPIED
	 */
	private Stone[][] intersections;
	
	/**
	 * The previous states of the DIM by DIM intersections of the GO board. 
	 * List index is in chronological order, with highest being latest state
	 */
	private List<Stone[][]> previousStates = new ArrayList<Stone[][]>(); 

	// -- Constructors -----------------------------------------------

	/**
	 * Creates an empty board.
	 * @ensures all intersections are UNOCCUPIED
	 */
	public Board(int dimension) {
		DIM = dimension;
		intersections = new Stone[DIM][DIM];
		this.reset(); // to set all intersection to empty (instead of default null), also (re)sets previous states
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
	 * @return the index belonging to the (row,col)-intersection
	 */
	public int indexLinear(int row, int col) {
//		assert (row >= 0 && row < DIM) : "Row should be between zero and DIM";
//		assert (col >= 0 && col < DIM) : "Col should be between zero and DIM";
		// SHOULD RETURN FALSE AT .ISFIELD(), INSTEAD OF BREAKING PROGRAM

		return (row) * DIM + col;
	}

	/**
	 * Calculates the 2D coordinate in the 2D matrix of intersections from a linear index pair.
	 * @requires linear index to be between 0 and DIM*DIM
	 * @return the corresponding (row,col) index
	 */
	public Coordinate2D index(int i) {
//		assert (i >= 0 && i < DIM * DIM) : "Index should be between zero and DIM*DIM";
		// SHOULD RETURN FALSE AT .ISFIELD(), INSTEAD OF BREAKING PROGRAM

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
//		assert (row >= 0 && row < DIM) : "Row should be between zero and DIM";
//		assert (col >= 0 && col < DIM) : "Col should be between zero and DIM";,		
		// SHOULD RETURN FALSE AT .ISFIELD(), INSTEAD OF BREAKING PROGRAM


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
		return isField(this.index(row,col));
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
	 * @param i the number of the intersection (see NUMBERING)
	 * @return the Stone on the intersection
	 */
	public Stone getField(int i) {
		assert (this.isField(i)) : "Non-valid intersection for this board!"; // not in .index anymore
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
		assert (this.isField(row, col)) : "Non-valid intersection for this board!"; // not in .index anymore
		return getField(this.index(row,col));
	}

	/**
	 * Returns the content of the intersection referred to by the 2D coordinate.
	 * @requires 2D coordinate to be a valid intersection
	 * @ensures the result to be either BLACK, WHITE or UNOCCUPIED
	 * @param coordinate of the intersection
	 * @return the stone on the intersection
	 */
	public Stone getField(Coordinate2D coordinate) {
		assert (this.isField(coordinate)) : "Non-valid intersection for this board!"; // not in .index anymore
		return this.intersections[coordinate.getRow()][coordinate.getCol()];
	}

	/**
	 * Returns true if the intersection i is empty.
	 * @requires i to be a valid intersection index
	 * @ensures true when the Stone at index i is UNOCCUPIED
	 * @param i the index of the intersection (see NUMBERING)
	 * @return true if the intersection is empty
	 */
	public boolean isEmptyField(int i) {
		assert (this.isField(i)) : "Non-valid intersection for this board!"; // not in .index anymore
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
		assert (this.isField(row, col)) : "Non-valid intersection for this board!"; // not in .index anymore
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
		assert (this.isField(coordinate)) : "Non-valid intersection for this board!"; // not in .index anymore
		return (getField(coordinate) == Stone.UNOCCUPIED);
	}

//	/**
//	 * Tests if the whole board is full.
//	 * @ensures true if all intersections are occupied
//	 * @return true if all intersections are occupied
//	 */
//	public boolean isFull() {
//		for (int i = 0; i < DIM * DIM; i++) {
//			if (this.isEmptyField(i)) {
//				return false;
//			}
//		}
//		return true;
//	}
//
//	/**
//	 * Returns true if the game is over. The game is over when there is a winner
//	 * or the whole board is full.
//	 * @ensures true if the board is full or when there is a winner
//	 * @return true if the game is over
//	 */
//	public boolean gameOver() {
//		// see exercise P-4.6
//		if (this.hasWinner() || this.isFull()) {
//			return true;
//		} else {
//			return false;
//		}
//	}
//
//	/**
//	 * Checks whether there is a row which is full and only contains the stone
//	 * m.
//	 * @param m the Stone of interest
//	 * @return true if there is a row controlled by m
//	 */
//	public boolean hasRow(Stone s) {
//		// see exercise P-4.6
//		for (int checkRow = 0; checkRow < DIM; checkRow++) {
//			for (int checkColumn = 0; checkColumn < DIM; checkColumn++) {
//				if (this.getField(checkRow, checkColumn) != m) { // if any intersection not equal: break loop
//					break;
//				}
//				if (checkColumn == DIM - 1) { // if complete row loop is not broken: row is full of m
//					return true;
//				}
//			}
//		}
//		return false;
//	}
//
//	/**
//	 * Checks whether there is a column which is full and only contains the stone
//	 * m.
//	 * @param m the Stone of interest
//	 * @return true if there is a column controlled by m
//	 */
//	public boolean hasColumn(Stone s) {
//		// see exercise P-4.6,for (int iR = 0; iR < DIM; iR++) {
//		for (int checkColumn = 0; checkColumn < DIM; checkColumn++) {
//			for (int checkRow = 0; checkRow < DIM; checkRow++) {
//				if (this.getField(checkRow, checkColumn) != m) { // if any intersection not equal: break loop
//					break;
//				}
//				if (checkRow == DIM - 1) { // if complete row loop is not broken: row is full of m
//					return true;
//				}
//			}
//		}
//		return false;
//	}
//	
//	/**
//	 * Checks whether there is a diagonal which is full and only contains the
//	 * stone m.
//	 * @param m the Stone of interest
//	 * @return true if there is a diagonal controlled by m
//	 */
//	public boolean hasDiagonal(Stone s) {
//		for (int checkULdr = 0; checkULdr < DIM; checkULdr++) { // check up left to down right
//			if (this.getField(checkULdr, checkULdr) != m) { // if any intersection not equal: break loop
//				break;
//			}
//			if (checkULdr == DIM - 1) { // if complete row loop is not broken: row is full of m
//				return true;
//			}
//		}
//		for (int checkDLur = 0; checkDLur < DIM; checkDLur++) { // check down left to up right
//			if (this.getField(DIM - 1 - checkDLur, checkDLur) != m) { // if any intersection not equal: break loop
//				break;
//			}
//			if (checkDLur == DIM - 1) { // if complete row loop is not broken: row is full of m
//				return true;
//			}
//		}
//		return false;
//	}
//
//	/**
//	 * Checks if the stone m has won. A stone wins if it controls at
//	 * least one row, column or diagonal.
//	 * @requires m to be either XX or OO
//	 * @ensures true when m has a row, column or diagonal 
//	 * @param m the stone of interest
//	 * @return true if the stone has won
//	 */
//	public boolean isWinner(Stone s) {
//		// implement, see exercise P-4.6
//		if (this.hasRow(m) || this.hasColumn(m) || this.hasDiagonal(m)) {
//			return true;
//		}
//		return false;
//	}
//
//	/**
//	 * Returns true if the game has a winner. This is the case when one of the
//	 * stones controls at least one row, column or diagonal.
//	 * @ensures true when either XX or OO has won
//	 * @return true if the student has a winner.
//	 */
//	public boolean hasWinner() {
//		// implement, see exercise P-4.6
//		if (this.isWinner(Stone.OO) || this.isWinner(Stone.XX)) {
//			return true;
//		}
//		return false;
//	}

	/**
	 * Returns a String representation of this board. I
	 * in addition to the current situation, the String also shows the numbering of the intersections. TODO implement?
	 *
	 * @return the game situation as String
	 */
	public String toString() {
		String s = "";
		for (int i = 0; i < DIM; i++) {
			String row = "";
			for (int j = 0; j < DIM; j++) {
				row = row + " " + getField(i, j).toString() + " ";
//				if (j < DIM - 1) {
//					row = row + "|";
//				}
			}
			s = s + row;
//			if (i < DIM - 1) {
//				s = s + "\n" + LINE + DELIM + NUMBERING[i * 2 + 1] + "\n";
//			}
		}
		return s;
	}

	/**
	 * Empties all intersections of this board (i.e., let them refer to the value
	 * Stone.UNOCCUPIED).
	 * @ensures all intersections are EMPTY
	 */
	public void reset() {
		//implement, see exercise P-4.6
		for (int emptyRow = 0; emptyRow < DIM; emptyRow++) {
			for (int emptyColumn = 0; emptyColumn < DIM; emptyColumn++) {
				this.setField(emptyRow, emptyColumn, Stone.UNOCCUPIED);
			}
		}
		this.previousStates.clear();
		this.previousStates.add(intersections);
		return;
	
	}

	/**
	 * Sets the content of intersection i to the stone s.
	 * @requires i to be a valid intersection
	 * @ensures intersection i to be set to Stone s
	 * @param i the intersection number (see NUMBERING)
	 * @param color of the stone to be placed
	 */
	public void setField(int i, Stone color) {
		this.setField(this.index(i),color);
		return;
	}

	/**
	 * Sets the content of the intersection represented by the (row,col) pair to the stone s.
	 * @requires (row, col) to be a valid intersection
	 * @ensures intersection (row, col) to be set to Stone s
	 * @param row the intersection's row
	 * @param col the intersection's column
	 * @param color of the stone to be placed
	 */
	public void setField(int row, int col, Stone color) {
		this.setField(this.index(row,col),color);
		return;
	}
	
	/**
	 * Sets the content of the intersection represented by the 2D coordinate to the stone s.
	 * @requires 2D coordinate be a valid intersection
	 * @ensures 2D coordinate to be set to Stone s
	 * @param coordinate the intersection's 2D coordinate
	 * @param color of the stone to be placed
	 */
	public void setField(Coordinate2D coordinate, Stone color) {
		assert (this.isField(coordinate)) : "non-valid coordinate!";
		
		this.intersections[coordinate.getRow()][coordinate.getCol()] = color ; // TODO: check this stone object
		this.previousStates.add(intersections);
		return;
		
	}
	
	/**
	 * Check if the current board state is equal to any previos state.
	 * NOTE: do checking BEFORE adding the new state to the list, otherwise it will always be true
	 * @requires 2D coordinate be a valid intersection
	 * @ensures 2D coordinate to be set to Stone s
	 * @param coordinate the intersection's 2D coordinate
	 * @param s the stone to be placed
	 */
	public boolean checkSamePreviousState(Stone[][] newState) {
		boolean sameFound;
		
		if (this.previousStates.contains(newState) ) { // TODO: CECK not wanting to compare object, but compare their contents
			sameFound = true;
		} else {
			sameFound = false;
		}
	
		return sameFound;
		
	}

	@Override
	public Board clone() {
		Board boardClone = new Board(this.DIM);
		for (int indexRow = 0; indexRow < DIM; indexRow++) {
			for (int indexColumn = 0; indexColumn < DIM; indexColumn++) {
				boardClone.intersections[indexRow][indexColumn] = this.intersections[indexRow][indexColumn];
			}
		}
		return boardClone;
	}

	/** All board indices are eventually converted into 2D coordinates
	 * (note: these are not limited to the board)
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
	 * Returns the index of the intersection above the given intersection, or -1 if outside board
	 * @requires index to refer to a valid intersection
	 * @returns index of the intersection above the given intersection
	 * @param index intersection to start from
	 */
	public int above(int index) { // TODO: static, cannot because need to know dimensions
		Coordinate2D currentIntersection = index(index); // cannot directly convert to row, col: cannot return mutiple values
		
		int indexAbove = this.indexLinear(currentIntersection.getRow()-1, currentIntersection.getRow());
		
		if(!this.isField(indexAbove)) {
			return -1; // to indicate outside board
		}
		return indexAbove ;
	}
	
	/**
	 * Returns the index of the intersection below the given intersection, or -1 if outside board
	 * @requires index to refer to a valid intersection
	 * @returns index of the intersection below the given intersection
	 * @param index intersection to start from
	 */
	public int below(int index) { // TODO: static, cannot because need to know dimensions
		Coordinate2D currentIntersection = index(index); // cannot directly convert to row, col: cannot return mutiple values
		
		int indexBelow = this.indexLinear(currentIntersection.getRow()+1, currentIntersection.getRow());
		
		if(!this.isField(indexBelow)) {
			return -1; // to indicate outside board
		}
		return indexBelow ;
	}
	
	/**
	 * Returns the index of the intersection left the given intersection, or -1 if outside board
	 * @requires index to refer to a valid intersection
	 * @returns index of the intersection left the given intersection
	 * @param index intersection to start from
	 */
	public int left(int index) { // TODO: static, cannot because need to know dimensions
		Coordinate2D currentIntersection = index(index); // cannot directly convert to row, col: cannot return mutiple values
		
		int indexLeft = this.indexLinear(currentIntersection.getRow(), currentIntersection.getRow()-1);
		
		if(!this.isField(indexLeft)) {
			return -1; // to indicate outside board
		}
		return indexLeft ;
	}
	
	/**
	 * Returns the index of the intersection right of the given intersection, or -1 if outside board
	 * @requires index to refer to a valid intersection
	 * @returns index of the intersection right of the given intersection
	 * @param index intersection to start from
	 */
	public int right(int index) { // TODO: static, cannot because need to know dimensions
		Coordinate2D currentIntersection = index(index); // cannot directly convert to row, col: cannot return mutiple values
		
		int indexRight = this.indexLinear(currentIntersection.getRow(), currentIntersection.getRow()+1);
		
		if(!this.isField(indexRight)) {
			return -1; // to indicate outside board
		}
		return indexRight ;
	}

	public int getDim() {
		return this.DIM;
	}
	
}
