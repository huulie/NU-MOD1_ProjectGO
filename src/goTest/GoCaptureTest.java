package goTest;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import exceptions.InvalidFieldException;
import goGame.Board;
import goGame.BoardTools;
import goGame.Game;
import goGame.GameController;
import goGame.LocalPlayer;
import goGame.Stone;

/**
 * Tests for doing captures on the GO board
 * @author huub.lievestro
 *
 */
public class GoCaptureTest {		
	private static final String WHITE = (" " + Stone.WHITE.toString() + " ").replaceAll("\\s+","");
	private static final String BLACK = (" " + Stone.BLACK.toString() + " ").replaceAll("\\s+","");
	private static final String UNOCCUPIED = (" " + Stone.UNOCCUPIED.toString() + " ").replaceAll("\\s+","");

	@Test
	void testCaptureSingleWhiteSurroundedWithBlack() {
		try {
			BoardTools boardTools = new BoardTools(true);
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

			// Check when four black stones surround a white stone, the white stone is removed
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
			System.out.println("        || \n [capturing WHITE] \n        || \n        \\/");
			boardTools.doOpponentCaptures(testBoard, Stone.BLACK);// do capture, owning BLACK
			System.out.println(testBoard.toStringFormatted());
			assertThat(testBoard.toString(), containsString(UNOCCUPIED.repeat(testBoard.above(centerIndex))
					+ BLACK
					+ UNOCCUPIED.repeat(testBoard.left(centerIndex)-testBoard.above(centerIndex)-1)  
					+ BLACK + UNOCCUPIED + BLACK  
					+ UNOCCUPIED.repeat(testBoard.below(centerIndex)-testBoard.right(centerIndex)-1) 
					+ BLACK
					+ UNOCCUPIED.repeat(boardDim*boardDim-testBoard.below(centerIndex)-1) 
					)); // note minus one to convert to index, crossed out in first index
		} catch (InvalidFieldException e) {
			System.out.println("ERROR: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	@Test
	void testCaptureThreeWhiteSurroundedWithBlack() {
		try {
			BoardTools boardTools = new BoardTools(true);
			int boardDim = 4;
			// U B B U	 0  1  2  3
			// B W W B 	 4  5  6  7
			// B W B U 	 8  9 10 11
			// U B U U	12 13 14 15


			// Get a board, check if it is indeed empty
			Board testBoard = new Board(boardDim); //game.getBoard();
			testBoard.reset();
			assertThat(testBoard.toString(), containsString(UNOCCUPIED.repeat(boardDim*boardDim)));

			// Check when black stones surround three white stones, the white stones are removed
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
			System.out.println("        || \n [capturing WHITE] \n        || \n        \\/");
			boardTools.doOpponentCaptures(testBoard, Stone.BLACK);// do capture, owning WHITE
			System.out.println(testBoard.toStringFormatted());
			assertThat(testBoard.toString(), containsString(
					UNOCCUPIED + BLACK + BLACK + UNOCCUPIED
					+ BLACK + UNOCCUPIED + UNOCCUPIED + BLACK 
					+ BLACK + UNOCCUPIED + BLACK + UNOCCUPIED
					+ UNOCCUPIED + BLACK + UNOCCUPIED +  UNOCCUPIED));
			// U B B U	 0  1  2  3
			// B * * B 	 4  5  6  7
			// B * B U 	 8  9 10 11
			// U B U U	12 13 14 15
			// * = should be captured, so now U
		} catch (InvalidFieldException e) {
			System.out.println("ERROR: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	@Test
	void testCaptureThreeWhiteEdgeSurroundedWithBlack() {
		try {
			BoardTools boardTools = new BoardTools(true);
			int boardDim = 4;
			// W W B U	 0  1  2  3
			// W B U U 	 4  5  6  7
			// B U U U 	 8  9 10 11
			// U U U U	12 13 14 15


			// Get a board, check if it is indeed empty
			Board testBoard = new Board(boardDim); //game.getBoard();
			testBoard.reset();
			assertThat(testBoard.toString(), containsString(UNOCCUPIED.repeat(boardDim*boardDim)));

			// Check when black stones surround three white stones, the white stones are removed
			testBoard.setField(0, Stone.WHITE); // see map on top of this test
			testBoard.setField(1, Stone.WHITE); // see map on top of this test
			testBoard.setField(4, Stone.WHITE); // see map on top of this test
			assertThat(testBoard.toString(), containsString(
					WHITE + WHITE + UNOCCUPIED + UNOCCUPIED
					+ WHITE + UNOCCUPIED + UNOCCUPIED + UNOCCUPIED 
					+ UNOCCUPIED + UNOCCUPIED + UNOCCUPIED + UNOCCUPIED
					+ UNOCCUPIED + UNOCCUPIED + UNOCCUPIED +  UNOCCUPIED)); 

			testBoard.setField(2, Stone.BLACK); // see map on top of this test
			testBoard.setField(5, Stone.BLACK); // see map on top of this test
			testBoard.setField(8, Stone.BLACK); // see map on top of this test
			assertThat(testBoard.toString(), containsString(
					WHITE + WHITE + BLACK + UNOCCUPIED
					+ WHITE + BLACK + UNOCCUPIED + UNOCCUPIED 
					+ BLACK + UNOCCUPIED + UNOCCUPIED + UNOCCUPIED
					+ UNOCCUPIED + UNOCCUPIED + UNOCCUPIED +  UNOCCUPIED));

			System.out.println(testBoard.toStringFormatted());
			System.out.println("        || \n [capturing WHITE] \n        || \n        \\/");
			boardTools.doOpponentCaptures(testBoard, Stone.BLACK);// do capture, owning WHITE
			System.out.println(testBoard.toStringFormatted());
			assertThat(testBoard.toString(), containsString(
					UNOCCUPIED + UNOCCUPIED + BLACK + UNOCCUPIED
					+ UNOCCUPIED + BLACK + UNOCCUPIED + UNOCCUPIED 
					+ BLACK + UNOCCUPIED + UNOCCUPIED + UNOCCUPIED
					+ UNOCCUPIED + UNOCCUPIED + UNOCCUPIED +  UNOCCUPIED));
			// * * B U	 0  1  2  3
			// * B U U 	 4  5  6  7
			// B U U U 	 8  9 10 11
			// U U U U	12 13 14 15
			// * = should be captured, so now U
		} catch (InvalidFieldException e) {
			System.out.println("ERROR: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	@Test
	void testCaptureTwelveBlackFilledOvalSurroundedWithWhite() {
		try {
			BoardTools boardTools = new BoardTools(true);
			int boardDim = 6;
			// U W W W W U 	 0  1  2  3  4  5
			// W B B B B W 	 6  7  8  9 10 11
			// W B B B B W 	12 13 14 15 16 17
			// W B B B B W	18 19 20 21 22 23
			// U W W W W U 	24 25 26 27 28 29
			// U W U U U W 	30 31 32 33 34 35

			// Get a board, check if it is indeed empty
			Board testBoard = new Board(boardDim); //game.getBoard();
			testBoard.reset();
			assertThat(testBoard.toString(), containsString(UNOCCUPIED.repeat(boardDim*boardDim)));

			// Check when black oval surrounded by white stones, the black stones are removed
			testBoard.setField(1, Stone.WHITE); // see map on top of this test
			testBoard.setField(2, Stone.WHITE); // see map on top of this test
			testBoard.setField(3, Stone.WHITE); // see map on top of this test
			testBoard.setField(4, Stone.WHITE); // see map on top of this test
			testBoard.setField(6, Stone.WHITE); // see map on top of this test
			testBoard.setField(11, Stone.WHITE); // see map on top of this test
			testBoard.setField(12, Stone.WHITE); // see map on top of this test
			testBoard.setField(17, Stone.WHITE); // see map on top of this test
			testBoard.setField(18, Stone.WHITE); // see map on top of this test
			testBoard.setField(23, Stone.WHITE); // see map on top of this test
			testBoard.setField(25, Stone.WHITE); // see map on top of this test
			testBoard.setField(26, Stone.WHITE); // see map on top of this test
			testBoard.setField(27, Stone.WHITE); // see map on top of this test
			testBoard.setField(28, Stone.WHITE); // see map on top of this test
			testBoard.setField(31, Stone.WHITE); // see map on top of this test
			testBoard.setField(35, Stone.WHITE); // see map on top of this test
			testBoard.setField(7, Stone.BLACK); // see map on top of this test
			testBoard.setField(8, Stone.BLACK); // see map on top of this test
			testBoard.setField(9, Stone.BLACK); // see map on top of this test
			testBoard.setField(10, Stone.BLACK); // see map on top of this test
			testBoard.setField(16, Stone.BLACK); // see map on top of this test
			testBoard.setField(22, Stone.BLACK); // see map on top of this test
			testBoard.setField(21, Stone.BLACK); // see map on top of this test
			testBoard.setField(20, Stone.BLACK); // see map on top of this test
			testBoard.setField(19, Stone.BLACK); // see map on top of this test
			testBoard.setField(13, Stone.BLACK); // see map on top of this test
			testBoard.setField(14, Stone.BLACK); // see map on top of this test
			testBoard.setField(15, Stone.BLACK); // see map on top of this test
			assertThat(testBoard.toString(), containsString(
					UNOCCUPIED + WHITE + WHITE + WHITE + WHITE + UNOCCUPIED 
					+ WHITE + BLACK + BLACK + BLACK + BLACK + WHITE 
					+ WHITE + BLACK + BLACK + BLACK + BLACK + WHITE 
					+ WHITE + BLACK + BLACK + BLACK + BLACK + WHITE 
					+ UNOCCUPIED + WHITE + WHITE + WHITE + WHITE + UNOCCUPIED 
					+ UNOCCUPIED + WHITE + UNOCCUPIED + UNOCCUPIED + UNOCCUPIED + WHITE)); 

			System.out.println(testBoard.toStringFormatted());
			System.out.println("        || \n [capturing BLACK] \n        || \n        \\/");
			boardTools.doOpponentCaptures(testBoard, Stone.WHITE);// do capture, owning WHITE
			System.out.println(testBoard.toStringFormatted());
			assertThat(testBoard.toString(), containsString(
					UNOCCUPIED + WHITE + WHITE + WHITE + WHITE + UNOCCUPIED 
					+ WHITE + UNOCCUPIED + UNOCCUPIED + UNOCCUPIED + UNOCCUPIED + WHITE 
					+ WHITE + UNOCCUPIED + UNOCCUPIED + UNOCCUPIED + UNOCCUPIED + WHITE 
					+ WHITE + UNOCCUPIED + UNOCCUPIED + UNOCCUPIED + UNOCCUPIED + WHITE 
					+ UNOCCUPIED + WHITE + WHITE + WHITE + WHITE + UNOCCUPIED 
					+ UNOCCUPIED + WHITE + UNOCCUPIED + UNOCCUPIED + UNOCCUPIED + WHITE));
			// U W W W W U 	 0  1  2  3  4  5
			// W * * * * W 	 6  7  8  9 10 11
			// W * * * * W 	12 13 14 15 16 17
			// W * * * * W	18 19 20 21 22 23
			// U W W W W U 	24 25 26 27 28 29
			// U W U U U W 	30 31 32 33 34 35
			// * = should be captured, so now U
		} catch (InvalidFieldException e) {
			System.out.println("ERROR: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	@Test
	void testNoCaptureEightBlackOpenOvalSurroundedWithWhite() {
		try {
			BoardTools boardTools = new BoardTools(true);
			int boardDim = 6;
			// U W W W W U 	 0  1  2  3  4  5
			// W B B B B W 	 6  7  8  9 10 11
			// W B U U B W 	12 13 14 15 16 17
			// W B B B B W	18 19 20 21 22 23
			// U W W W W U 	24 25 26 27 28 29
			// U W U U U W 	30 31 32 33 34 35

			// Get a board, check if it is indeed empty
			Board testBoard = new Board(boardDim); //game.getBoard();
			testBoard.reset();
			assertThat(testBoard.toString(), containsString(UNOCCUPIED.repeat(boardDim*boardDim)));

			// Check when black oval surrounded by white stones, the black stones are removed
			testBoard.setField(1, Stone.WHITE); // see map on top of this test
			testBoard.setField(2, Stone.WHITE); // see map on top of this test
			testBoard.setField(3, Stone.WHITE); // see map on top of this test
			testBoard.setField(4, Stone.WHITE); // see map on top of this test
			testBoard.setField(6, Stone.WHITE); // see map on top of this test
			testBoard.setField(11, Stone.WHITE); // see map on top of this test
			testBoard.setField(12, Stone.WHITE); // see map on top of this test
			testBoard.setField(17, Stone.WHITE); // see map on top of this test
			testBoard.setField(18, Stone.WHITE); // see map on top of this test
			testBoard.setField(23, Stone.WHITE); // see map on top of this test
			testBoard.setField(25, Stone.WHITE); // see map on top of this test
			testBoard.setField(26, Stone.WHITE); // see map on top of this test
			testBoard.setField(27, Stone.WHITE); // see map on top of this test
			testBoard.setField(28, Stone.WHITE); // see map on top of this test
			testBoard.setField(31, Stone.WHITE); // see map on top of this test
			testBoard.setField(35, Stone.WHITE); // see map on top of this test
			testBoard.setField(7, Stone.BLACK); // see map on top of this test
			testBoard.setField(8, Stone.BLACK); // see map on top of this test
			testBoard.setField(9, Stone.BLACK); // see map on top of this test
			testBoard.setField(10, Stone.BLACK); // see map on top of this test
			testBoard.setField(16, Stone.BLACK); // see map on top of this test
			testBoard.setField(22, Stone.BLACK); // see map on top of this test
			testBoard.setField(21, Stone.BLACK); // see map on top of this test
			testBoard.setField(20, Stone.BLACK); // see map on top of this test
			testBoard.setField(19, Stone.BLACK); // see map on top of this test
			testBoard.setField(13, Stone.BLACK); // see map on top of this test
			assertThat(testBoard.toString(), containsString(
					UNOCCUPIED + WHITE + WHITE + WHITE + WHITE + UNOCCUPIED 
					+ WHITE + BLACK + BLACK + BLACK + BLACK + WHITE 
					+ WHITE + BLACK + UNOCCUPIED + UNOCCUPIED + BLACK + WHITE 
					+ WHITE + BLACK + BLACK + BLACK + BLACK + WHITE 
					+ UNOCCUPIED + WHITE + WHITE + WHITE + WHITE + UNOCCUPIED 
					+ UNOCCUPIED + WHITE + UNOCCUPIED + UNOCCUPIED + UNOCCUPIED + WHITE)); 

			System.out.println(testBoard.toStringFormatted());
			System.out.println("        || \n [capturing BLACK] \n        || \n        \\/");
			boardTools.doOpponentCaptures(testBoard, Stone.WHITE);// do capture, owning WHITE
			System.out.println(testBoard.toStringFormatted());
			assertThat(testBoard.toString(), containsString(
					UNOCCUPIED + WHITE + WHITE + WHITE + WHITE + UNOCCUPIED 
					+ WHITE + BLACK + BLACK + BLACK + BLACK + WHITE 
					+ WHITE + BLACK + UNOCCUPIED + UNOCCUPIED + BLACK + WHITE 
					+ WHITE + BLACK + BLACK + BLACK + BLACK + WHITE 
					+ UNOCCUPIED + WHITE + WHITE + WHITE + WHITE + UNOCCUPIED 
					+ UNOCCUPIED + WHITE + UNOCCUPIED + UNOCCUPIED + UNOCCUPIED + WHITE)); 
			// Should be no captures, so see map on top
		} catch (InvalidFieldException e) {
			System.out.println("ERROR: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	@Test
	void testCaptureThirteenBlackRandomComplexFigureSurroundedWithWhite() {
		try {
			BoardTools boardTools = new BoardTools(true);
			int boardDim = 6;
			// U W W W W U 	 0  1  2  3  4  5
			// W B W B B W 	 6  7  8  9 10 11
			// W B W B W B 	12 13 14 15 16 17
			// W B B B B B	18 19 20 21 22 23
			// U W B W W B 	24 25 26 27 28 29
			// U W W W U W 	30 31 32 33 34 35

			// Get a board, check if it is indeed empty
			Board testBoard = new Board(boardDim); //game.getBoard();
			testBoard.reset();
			assertThat(testBoard.toString(), containsString(UNOCCUPIED.repeat(boardDim*boardDim)));

			// Check when black oval surrounded by white stones, the black stones are removed
			testBoard.setField(1, Stone.WHITE); // see map on top of this test
			testBoard.setField(2, Stone.WHITE); // see map on top of this test
			testBoard.setField(3, Stone.WHITE); // see map on top of this test
			testBoard.setField(4, Stone.WHITE); // see map on top of this test
			testBoard.setField(6, Stone.WHITE); // see map on top of this test
			testBoard.setField(8, Stone.WHITE); // see map on top of this test
			testBoard.setField(11, Stone.WHITE); // see map on top of this test
			testBoard.setField(12, Stone.WHITE); // see map on top of this test
			testBoard.setField(14, Stone.WHITE); // see map on top of this test
			testBoard.setField(16, Stone.WHITE); // see map on top of this test
			testBoard.setField(18, Stone.WHITE); // see map on top of this test
			testBoard.setField(25, Stone.WHITE); // see map on top of this test
			testBoard.setField(27, Stone.WHITE); // see map on top of this test
			testBoard.setField(28, Stone.WHITE); // see map on top of this test
			testBoard.setField(31, Stone.WHITE); // see map on top of this test
			testBoard.setField(32, Stone.WHITE); // see map on top of this test
			testBoard.setField(33, Stone.WHITE); // see map on top of this test
			testBoard.setField(35, Stone.WHITE); // see map on top of this test

			testBoard.setField(7, Stone.BLACK); // see map on top of this test
			testBoard.setField(9, Stone.BLACK); // see map on top of this test
			testBoard.setField(10, Stone.BLACK); // see map on top of this test
			testBoard.setField(13, Stone.BLACK); // see map on top of this test
			testBoard.setField(15, Stone.BLACK); // see map on top of this test
			testBoard.setField(17, Stone.BLACK); // see map on top of this test
			testBoard.setField(19, Stone.BLACK); // see map on top of this test
			testBoard.setField(20, Stone.BLACK); // see map on top of this test
			testBoard.setField(21, Stone.BLACK); // see map on top of this test
			testBoard.setField(22, Stone.BLACK); // see map on top of this test
			testBoard.setField(23, Stone.BLACK); // see map on top of this test
			testBoard.setField(26, Stone.BLACK); // see map on top of this test
			testBoard.setField(29, Stone.BLACK); // see map on top of this test

			assertThat(testBoard.toString(), containsString(
					UNOCCUPIED + WHITE + WHITE + WHITE + WHITE + UNOCCUPIED 
					+ WHITE + BLACK + WHITE + BLACK + BLACK + WHITE 
					+ WHITE + BLACK + WHITE + BLACK + WHITE + BLACK 
					+ WHITE + BLACK + BLACK + BLACK + BLACK + BLACK 
					+ UNOCCUPIED + WHITE + BLACK + WHITE + WHITE + BLACK 
					+ UNOCCUPIED + WHITE + WHITE + WHITE + UNOCCUPIED + WHITE)); 

			System.out.println(testBoard.toStringFormatted());
			System.out.println("        || \n [capturing BLACK] \n        || \n        \\/");
			boardTools.doOpponentCaptures(testBoard, Stone.WHITE);// do capture, owning WHITE
			System.out.println(testBoard.toStringFormatted());
			assertThat(testBoard.toString(), containsString(
					UNOCCUPIED + WHITE + WHITE + WHITE + WHITE + UNOCCUPIED 
					+ WHITE + UNOCCUPIED + WHITE + UNOCCUPIED + UNOCCUPIED + WHITE 
					+ WHITE + UNOCCUPIED + WHITE + UNOCCUPIED + WHITE + UNOCCUPIED 
					+ WHITE + UNOCCUPIED + UNOCCUPIED + UNOCCUPIED + UNOCCUPIED + UNOCCUPIED 
					+ UNOCCUPIED + WHITE + UNOCCUPIED + WHITE + WHITE + UNOCCUPIED 
					+ UNOCCUPIED + WHITE + WHITE + WHITE + UNOCCUPIED + WHITE)); 
			// U W W W W U 	 0  1  2  3  4  5
			// W * W * * W 	 6  7  8  9 10 11
			// W * W * W * 	12 13 14 15 16 17
			// W * * * * *	18 19 20 21 22 23
			// U W * W W * 	24 25 26 27 28 29
			// U W W W U W 	30 31 32 33 34 35
			// * = should be captured, so now U
		} catch (InvalidFieldException e) {
			System.out.println("ERROR: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	@Test
	void testCaptureMultipleBlackSurroundedWithWhite() {
		try {
			BoardTools boardTools = new BoardTools(true);
			int boardDim = 6;
			// B W W W B W 	 0  1  2  3  4  5
			// W W B B B W 	 6  7  8  9 10 11
			// U U W W W U 	12 13 14 15 16 17
			// W W B W B W	18 19 20 21 22 23
			// U W B B W W 	24 25 26 27 28 29
			// U W W W W B 	30 31 32 33 34 35

			// Get a board, check if it is indeed empty
			Board testBoard = new Board(boardDim); //game.getBoard();
			testBoard.reset();
			assertThat(testBoard.toString(), containsString(UNOCCUPIED.repeat(boardDim*boardDim)));

			// Check when black oval surrounded by white stones, the black stones are removed
			testBoard.setField(1, Stone.WHITE); // see map on top of this test
			testBoard.setField(2, Stone.WHITE); // see map on top of this test
			testBoard.setField(3, Stone.WHITE); // see map on top of this test
			testBoard.setField(5, Stone.WHITE); // see map on top of this test
			testBoard.setField(6, Stone.WHITE); // see map on top of this test
			testBoard.setField(7, Stone.WHITE); // see map on top of this test
			testBoard.setField(11, Stone.WHITE); // see map on top of this test
			testBoard.setField(14, Stone.WHITE); // see map on top of this test
			testBoard.setField(15, Stone.WHITE); // see map on top of this test
			testBoard.setField(16, Stone.WHITE); // see map on top of this test
			testBoard.setField(18, Stone.WHITE); // see map on top of this test
			testBoard.setField(19, Stone.WHITE); // see map on top of this test
			testBoard.setField(21, Stone.WHITE); // see map on top of this test
			testBoard.setField(23, Stone.WHITE); // see map on top of this test
			testBoard.setField(25, Stone.WHITE); // see map on top of this test
			testBoard.setField(28, Stone.WHITE); // see map on top of this test
			testBoard.setField(29, Stone.WHITE); // see map on top of this test
			testBoard.setField(31, Stone.WHITE); // see map on top of this test
			testBoard.setField(32, Stone.WHITE); // see map on top of this test
			testBoard.setField(33, Stone.WHITE); // see map on top of this test
			testBoard.setField(34, Stone.WHITE); // see map on top of this test

			testBoard.setField(0, Stone.BLACK); // see map on top of this test
			testBoard.setField(4, Stone.BLACK); // see map on top of this test
			testBoard.setField(8, Stone.BLACK); // see map on top of this test
			testBoard.setField(9, Stone.BLACK); // see map on top of this test
			testBoard.setField(10, Stone.BLACK); // see map on top of this test
			testBoard.setField(20, Stone.BLACK); // see map on top of this test
			testBoard.setField(22, Stone.BLACK); // see map on top of this test
			testBoard.setField(26, Stone.BLACK); // see map on top of this test
			testBoard.setField(27, Stone.BLACK); // see map on top of this test
			testBoard.setField(35, Stone.BLACK); // see map on top of this test
			assertThat(testBoard.toString(), containsString(
					BLACK + WHITE + WHITE + WHITE + BLACK + WHITE
					+ WHITE + WHITE + BLACK + BLACK + BLACK + WHITE
					+ UNOCCUPIED + UNOCCUPIED + WHITE + WHITE + WHITE + UNOCCUPIED
					+ WHITE + WHITE + BLACK + WHITE + BLACK + WHITE
					+ UNOCCUPIED + WHITE + BLACK + BLACK + WHITE + WHITE
					+ UNOCCUPIED + WHITE + WHITE + WHITE + WHITE + BLACK )); 

			System.out.println(testBoard.toStringFormatted());
			System.out.println("        || \n [capturing BLACK] \n        || \n        \\/");
			boardTools.doOpponentCaptures(testBoard, Stone.WHITE);// do capture, owning WHITE
			System.out.println(testBoard.toStringFormatted());
			assertThat(testBoard.toString(), containsString(
					UNOCCUPIED + WHITE + WHITE + WHITE + UNOCCUPIED + WHITE
					+ WHITE + WHITE + UNOCCUPIED + UNOCCUPIED + UNOCCUPIED + WHITE
					+ UNOCCUPIED + UNOCCUPIED + WHITE + WHITE + WHITE + UNOCCUPIED
					+ WHITE + WHITE + UNOCCUPIED + WHITE + UNOCCUPIED + WHITE
					+ UNOCCUPIED + WHITE + UNOCCUPIED + UNOCCUPIED + WHITE + WHITE
					+ UNOCCUPIED + WHITE + WHITE + WHITE + WHITE + UNOCCUPIED ));  
			// Should be no captures, so see map on top
		} catch (InvalidFieldException e) {
			System.out.println("ERROR: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	@Test
	void testOwnCaptureMultipleBlackSurroundedWithWhite() {
		try {
			BoardTools boardTools = new BoardTools(true);
			int boardDim = 6;
			// B W W W B W 	 0  1  2  3  4  5
			// W W B B B W 	 6  7  8  9 10 11
			// U U W W W U 	12 13 14 15 16 17
			// W W B W B W	18 19 20 21 22 23
			// U W B B W W 	24 25 26 27 28 29
			// U W W W W B 	30 31 32 33 34 35

			// Get a board, check if it is indeed empty
			Board testBoard = new Board(boardDim); //game.getBoard();
			testBoard.reset();
			assertThat(testBoard.toString(), containsString(UNOCCUPIED.repeat(boardDim*boardDim)));

			// Check when black oval surrounded by white stones, the black stones are removed
			testBoard.setField(1, Stone.WHITE); // see map on top of this test
			testBoard.setField(2, Stone.WHITE); // see map on top of this test
			testBoard.setField(3, Stone.WHITE); // see map on top of this test
			testBoard.setField(5, Stone.WHITE); // see map on top of this test
			testBoard.setField(6, Stone.WHITE); // see map on top of this test
			testBoard.setField(7, Stone.WHITE); // see map on top of this test
			testBoard.setField(11, Stone.WHITE); // see map on top of this test
			testBoard.setField(14, Stone.WHITE); // see map on top of this test
			testBoard.setField(15, Stone.WHITE); // see map on top of this test
			testBoard.setField(16, Stone.WHITE); // see map on top of this test
			testBoard.setField(18, Stone.WHITE); // see map on top of this test
			testBoard.setField(19, Stone.WHITE); // see map on top of this test
			testBoard.setField(21, Stone.WHITE); // see map on top of this test
			testBoard.setField(23, Stone.WHITE); // see map on top of this test
			testBoard.setField(25, Stone.WHITE); // see map on top of this test
			testBoard.setField(28, Stone.WHITE); // see map on top of this test
			testBoard.setField(29, Stone.WHITE); // see map on top of this test
			testBoard.setField(31, Stone.WHITE); // see map on top of this test
			testBoard.setField(32, Stone.WHITE); // see map on top of this test
			testBoard.setField(33, Stone.WHITE); // see map on top of this test
			testBoard.setField(34, Stone.WHITE); // see map on top of this test

			testBoard.setField(0, Stone.BLACK); // see map on top of this test
			testBoard.setField(4, Stone.BLACK); // see map on top of this test
			testBoard.setField(8, Stone.BLACK); // see map on top of this test
			testBoard.setField(9, Stone.BLACK); // see map on top of this test
			testBoard.setField(10, Stone.BLACK); // see map on top of this test
			testBoard.setField(20, Stone.BLACK); // see map on top of this test
			testBoard.setField(22, Stone.BLACK); // see map on top of this test
			testBoard.setField(26, Stone.BLACK); // see map on top of this test
			testBoard.setField(27, Stone.BLACK); // see map on top of this test
			testBoard.setField(35, Stone.BLACK); // see map on top of this test
			assertThat(testBoard.toString(), containsString(
					BLACK + WHITE + WHITE + WHITE + BLACK + WHITE
					+ WHITE + WHITE + BLACK + BLACK + BLACK + WHITE
					+ UNOCCUPIED + UNOCCUPIED + WHITE + WHITE + WHITE + UNOCCUPIED
					+ WHITE + WHITE + BLACK + WHITE + BLACK + WHITE
					+ UNOCCUPIED + WHITE + BLACK + BLACK + WHITE + WHITE
					+ UNOCCUPIED + WHITE + WHITE + WHITE + WHITE + BLACK )); 

			System.out.println(testBoard.toStringFormatted());
			System.out.println("        || \n [capturing BLACK] \n        || \n        \\/");
			boardTools.doOwnCaptures(testBoard, Stone.BLACK);// do own capture, owning WHITE
			System.out.println(testBoard.toStringFormatted());
			assertThat(testBoard.toString(), containsString(
					UNOCCUPIED + WHITE + WHITE + WHITE + UNOCCUPIED + WHITE
					+ WHITE + WHITE + UNOCCUPIED + UNOCCUPIED + UNOCCUPIED + WHITE
					+ UNOCCUPIED + UNOCCUPIED + WHITE + WHITE + WHITE + UNOCCUPIED
					+ WHITE + WHITE + UNOCCUPIED + WHITE + UNOCCUPIED + WHITE
					+ UNOCCUPIED + WHITE + UNOCCUPIED + UNOCCUPIED + WHITE + WHITE
					+ UNOCCUPIED + WHITE + WHITE + WHITE + WHITE + UNOCCUPIED ));  
			// Should be no captures, so see map on top
		} catch (InvalidFieldException e) {
			System.out.println("ERROR: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	@Test
	void testTwiceCaptureThreeWhiteSurroundedWithBlack() {
		try {
			BoardTools boardTools = new BoardTools(true);
			int boardDim = 4;
			// U B B U	 0  1  2  3
			// B W W B 	 4  5  6  7
			// B W B U 	 8  9 10 11
			// U B U U	12 13 14 15


			// Get a board, check if it is indeed empty
			Board testBoard = new Board(boardDim); //game.getBoard();
			testBoard.reset();
			assertThat(testBoard.toString(), containsString(UNOCCUPIED.repeat(boardDim*boardDim)));

			// Check when black stones surround three white stones, the white stones are removed
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
			System.out.println("        || \n [capturing WHITE] \n        || \n        \\/");

			Board testBoardFirst = testBoard.clone();
			boardTools.doOpponentCaptures(testBoardFirst, Stone.BLACK);// do capture, owning WHITE
			System.out.println(testBoardFirst.toStringFormatted());
			assertThat(testBoardFirst.toString(), containsString(
					UNOCCUPIED + BLACK + BLACK + UNOCCUPIED
					+ BLACK + UNOCCUPIED + UNOCCUPIED + BLACK 
					+ BLACK + UNOCCUPIED + BLACK + UNOCCUPIED
					+ UNOCCUPIED + BLACK + UNOCCUPIED +  UNOCCUPIED));

			Board testBoardSecond = testBoard.clone();
			boardTools.doOpponentCaptures(testBoardSecond, Stone.BLACK);// do capture, owning WHITE
			System.out.println(testBoardSecond.toStringFormatted());
			assertThat(testBoardSecond.toString(), containsString(
					UNOCCUPIED + BLACK + BLACK + UNOCCUPIED
					+ BLACK + UNOCCUPIED + UNOCCUPIED + BLACK 
					+ BLACK + UNOCCUPIED + BLACK + UNOCCUPIED
					+ UNOCCUPIED + BLACK + UNOCCUPIED +  UNOCCUPIED));
			// U B B U	 0  1  2  3
			// B * * B 	 4  5  6  7
			// B * B U 	 8  9 10 11
			// U B U U	12 13 14 15
			// * = should be captured, so now U
		} catch (InvalidFieldException e) {
			System.out.println("ERROR: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
}
