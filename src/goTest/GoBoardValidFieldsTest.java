package goTest;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import exceptions.InvalidFieldException;
import goGame.Board;
import goGame.Stone;


/**
 * Tests for placing (in)valid stones on the GO board
 * @author huub.lievestro
 *
 */
public class GoBoardValidFieldsTest {		
	private static final String WHITE = (" " + Stone.WHITE.toString() + " ").replaceAll("\\s+","");
	private static final String BLACK = (" " + Stone.BLACK.toString() + " ").replaceAll("\\s+","");
	private static final String UNOCCUPIED = (" " + Stone.UNOCCUPIED.toString() + " ").replaceAll("\\s+","");

	@Test
	void testPlacingValid() {
		try {
			int boardDim = 3;
			int centerIndex = 4;
			// U B U	0 1 2
			// B W B 	3 4 5
			// U B U 	6 7 8

			// Get a board, check if it is indeed empty
			Board testBoard;

			testBoard = new Board(boardDim);
			testBoard.reset();
			assertThat(testBoard.toString(), containsString(UNOCCUPIED.repeat(boardDim*boardDim)));

			// Check placing four black stones surrounding a white stone
			testBoard.setField(centerIndex, Stone.WHITE); // the center stone
			assertThat(testBoard.toString(), containsString(UNOCCUPIED.toString().repeat(centerIndex) 
					+ WHITE + UNOCCUPIED.repeat(boardDim*boardDim-centerIndex-1))); // note minus one to convert to index

			testBoard.setField(testBoard.above(centerIndex), Stone.BLACK); // above, will be first
			testBoard.setField(testBoard.right(centerIndex), Stone.BLACK); // right, will be fourth, directly after WHITE = 3rd)
			testBoard.setField(testBoard.below(centerIndex), Stone.BLACK); // below, will be last
			testBoard.setField(testBoard.left(centerIndex), Stone.BLACK); // left, will be second, directly before WHITE
			assertThat(testBoard.toString(), containsString(UNOCCUPIED.repeat(testBoard.above(centerIndex))
					+ BLACK // above
					+ UNOCCUPIED.repeat(testBoard.left(centerIndex)-testBoard.above(centerIndex)-1)  
					+ BLACK + WHITE + BLACK  // left, white and right
					+ UNOCCUPIED.repeat(testBoard.below(centerIndex)-testBoard.right(centerIndex)-1) 
					+ BLACK // below
					+ UNOCCUPIED.repeat(boardDim*boardDim-testBoard.below(centerIndex)-1) 
					)); // note minus one to convert to index, crossed out in first index
			System.out.println(testBoard.toStringFormatted());
		} catch (InvalidFieldException e) {
			System.out.println("ERROR: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	@Test
	void testPlacingInvalidOutsideBoard() {
		try {
			int boardDim = 4;
			// 0  1  2  3
			// 4  5  6  7
			// 8  9 10 11
			// 12 13 14 15

			// Get a board, check if it is indeed empty
			Board testBoard = new Board(boardDim); //game.getBoard();
			testBoard.reset();
			assertThat(testBoard.toString(), containsString(UNOCCUPIED.repeat(boardDim*boardDim)));
			
			// test placing outside board
			Exception exceptionOutside = assertThrows(InvalidFieldException.class, () -> {
			testBoard.setField(4*4+1, Stone.WHITE); // see map on top of this test
			});
			String expectedMessageOutside  = "is not a valid field on this board!";
		    String actualMessageOutside  = exceptionOutside.getMessage();
		 
		    assertTrue(actualMessageOutside.contains(expectedMessageOutside));
			
		} catch (InvalidFieldException e) {
			System.out.println("ERROR: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	void testPlacingInvalidRecreatingPrevious() {
		try {
			int boardDim = 4;
			// U B B U	 0  1  2  3
			// B W W B 	 4  5  6  7
			// B W B U 	 8  9 10 11
			// U B U U	12 13 14 15


			// Get a board, check if it is indeed empty
			Board testBoard = new Board(boardDim); //game.getBoard();
			testBoard.reset();
			assertThat(testBoard.toString(), containsString(UNOCCUPIED.repeat(boardDim*boardDim)));

			// Test placing some valid stones
			testBoard.setField(5, Stone.WHITE); // see map on top of this test
			testBoard.setField(6, Stone.WHITE); // see map on top of this test
			testBoard.setField(9, Stone.WHITE); // see map on top of this test
			assertThat(testBoard.toString(), containsString(
					UNOCCUPIED + UNOCCUPIED + UNOCCUPIED + UNOCCUPIED
					+ UNOCCUPIED + WHITE + WHITE + UNOCCUPIED 
					+ UNOCCUPIED + WHITE + UNOCCUPIED + UNOCCUPIED
					+ UNOCCUPIED + UNOCCUPIED + UNOCCUPIED +  UNOCCUPIED)); 

			testBoard.setField(1, Stone.BLACK); // see map on top of this test
			testBoard.setField(2, Stone.BLACK); // see map on top of this test
			testBoard.setField(4, Stone.BLACK); // see map on top of this test
			testBoard.setField(7, Stone.BLACK); // see map on top of this test
			testBoard.setField(8, Stone.BLACK); // see map on top of this test
			testBoard.setField(10, Stone.BLACK); // see map on top of this test
			testBoard.setField(13, Stone.BLACK); // see map on top of this test
			assertThat(testBoard.toString(), containsString(
					UNOCCUPIED + BLACK + BLACK + UNOCCUPIED
					+ BLACK + WHITE + WHITE + BLACK 
					+ BLACK + WHITE + BLACK + UNOCCUPIED
					+ UNOCCUPIED + BLACK + UNOCCUPIED +  UNOCCUPIED));

			System.out.println(testBoard.toStringFormatted());
			
			// test removing and replacing one stone
			Board testBoardClone = testBoard.clone(); // a clone is certainly a previous state
			assertThat(testBoardClone.toString(), containsString( // but testing to be sure
					UNOCCUPIED + BLACK + BLACK + UNOCCUPIED
					+ BLACK + WHITE + WHITE + BLACK 
					+ BLACK + WHITE + BLACK + UNOCCUPIED
					+ UNOCCUPIED + BLACK + UNOCCUPIED +  UNOCCUPIED));
			assertTrue(testBoard.checkSamePreviousState(testBoardClone.returnIntersectionArray()));
			
			testBoardClone.setField(0,Stone.BLACK); // change one stone to make it different
			assertThat(testBoardClone.toString(), containsString( // and testing to be sure
					BLACK + BLACK + BLACK + UNOCCUPIED
					+ BLACK + WHITE + WHITE + BLACK 
					+ BLACK + WHITE + BLACK + UNOCCUPIED
					+ UNOCCUPIED + BLACK + UNOCCUPIED +  UNOCCUPIED));
			assertFalse(testBoard.checkSamePreviousState(testBoardClone.returnIntersectionArray()));

			
			
		} catch (InvalidFieldException e) {
			System.out.println("ERROR: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
}
