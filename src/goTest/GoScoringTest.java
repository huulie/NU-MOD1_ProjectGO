package goTest;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import exceptions.InvalidFieldException;
import goGame.Board;
import goGame.BoardTools;
import goGame.Game;
import goGame.GoGameConstants;
import goGame.Stone;

/**
 * Tests for doing captures on the GO board
 * @author huub.lievestro
 *
 */
public class GoScoringTest {
	private static final String WHITE = " " + Stone.WHITE.toString() + " ";
	private static final String BLACK = " " + Stone.BLACK.toString() + " ";
	private static final String UNOCCUPIED = " " + Stone.UNOCCUPIED.toString() + " ";

	@Test
	void testScore4TwoSidesAllOccupied() {
		try {
		BoardTools boardTools = new BoardTools(true);
		int boardDim = 4;
		// B B W W	 0  1  2  3
		// B B W W 	 4  5  6  7
		// B B w W 	 8  9 10 11
		// B B W W	12 13 14 15
		
		// Get a board, check if it is indeed empty
		Game testGame = new Game(boardDim, null, null); // Game has no players (for this test)
		Board testBoard = testGame.getBoard();
		testBoard.reset();
		assertThat(testBoard.toString(), containsString(UNOCCUPIED.repeat(boardDim*boardDim)));

		testBoard.setField(0, Stone.BLACK); 
		testBoard.setField(4, Stone.BLACK); 
		testBoard.setField(8, Stone.BLACK); 
		testBoard.setField(12, Stone.BLACK);
		testBoard.setField(1, Stone.BLACK); 
		testBoard.setField(5, Stone.BLACK); 
		testBoard.setField(9, Stone.BLACK); 
		testBoard.setField(13, Stone.BLACK); 
		testBoard.setField(2, Stone.WHITE); 
		testBoard.setField(6, Stone.WHITE); 
		testBoard.setField(10, Stone.WHITE); 
		testBoard.setField(14, Stone.WHITE); 
		testBoard.setField(3, Stone.WHITE); 
		testBoard.setField(7, Stone.WHITE); 
		testBoard.setField(11, Stone.WHITE); 
		testBoard.setField(15, Stone.WHITE); 
		
		assertThat(testBoard.toString(), containsString(
				BLACK + BLACK + WHITE + WHITE
				+ BLACK + BLACK + WHITE + WHITE
				+ BLACK + BLACK + WHITE + WHITE
				+ BLACK + BLACK + WHITE + WHITE));

		System.out.println(testBoard.toStringFormatted());
		System.out.println("Calculating scores...");
		String testScore = boardTools.getScores(testGame); 
		double scoreBlack = 8; // calculate yourself
		double scoreWhite = 8+testGame.getKomi(); // calculate yourself, think of Komi!

		assertThat(testScore.toString(), containsString(scoreBlack + GoGameConstants.DELIMITER + scoreWhite)); 
		} catch (InvalidFieldException e) {
			System.out.println("ERROR: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	
	@Test
	void testScore4TwoSidesWithUnoccupied() {
		try {
		BoardTools boardTools = new BoardTools(true);
		int boardDim = 4;
		// U B W U	 0  1  2  3
		// U B W U 	 4  5  6  7
		// U B w U 	 8  9 10 11
		// U B W U	12 13 14 15
		
		// Get a board, check if it is indeed empty
		Game testGame = new Game(boardDim, null, null); // Game has no players (for this test)
		Board testBoard = testGame.getBoard(); //game.getBoard();
		testBoard.reset();
		assertThat(testBoard.toString(), containsString(UNOCCUPIED.repeat(boardDim*boardDim)));

		testBoard.setField(1, Stone.BLACK); 
		testBoard.setField(5, Stone.BLACK); 
		testBoard.setField(9, Stone.BLACK); 
		testBoard.setField(13, Stone.BLACK); 
		testBoard.setField(2, Stone.WHITE); 
		testBoard.setField(6, Stone.WHITE); 
		testBoard.setField(10, Stone.WHITE); 
		testBoard.setField(14, Stone.WHITE); 
		
		assertThat(testBoard.toString(), containsString(
				UNOCCUPIED + BLACK + WHITE + UNOCCUPIED
				+ UNOCCUPIED + BLACK + WHITE + UNOCCUPIED
				+ UNOCCUPIED + BLACK + WHITE + UNOCCUPIED
				+ UNOCCUPIED + BLACK + WHITE + UNOCCUPIED));

		System.out.println(testBoard.toStringFormatted());
		System.out.println("Calculating scores...");
		String testScore = boardTools.getScores(testGame); 
		double scoreBlack = 8; // calculate yourself
		double scoreWhite = 8+testGame.getKomi(); // calculate yourself, think of Komi!

		assertThat(testScore.toString(), containsString(scoreBlack + GoGameConstants.DELIMITER + scoreWhite)); 
		} catch (InvalidFieldException e) {
			System.out.println("ERROR: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	
	@Test
	void testScore4LargeBlackSmallWhiteWithUnoccupied() {
		try {
		BoardTools boardTools = new BoardTools(true);
		int boardDim = 4;
		// U U U B	 0  1  2  3
		// U B B B 	 4  5  6  7
		// U B W W 	 8  9 10 11
		// U B W U	12 13 14 15
		
		// Get a board, check if it is indeed empty
		Game testGame = new Game(boardDim, null, null); // Game has no players (for this test)
		Board testBoard = testGame.getBoard(); 
		testBoard.reset();
		assertThat(testBoard.toString(), containsString(UNOCCUPIED.repeat(boardDim*boardDim)));

		testBoard.setField(3, Stone.BLACK); 
		testBoard.setField(5, Stone.BLACK); 
		testBoard.setField(6, Stone.BLACK); 
		testBoard.setField(7, Stone.BLACK); 
		testBoard.setField(9, Stone.BLACK); 
		testBoard.setField(13, Stone.BLACK); 
		testBoard.setField(10, Stone.WHITE); 		
		testBoard.setField(11, Stone.WHITE); 
		testBoard.setField(14, Stone.WHITE); 
		
		assertThat(testBoard.toString(), containsString(
				UNOCCUPIED + UNOCCUPIED + UNOCCUPIED + BLACK
				+ UNOCCUPIED + BLACK + BLACK + BLACK
				+ UNOCCUPIED + BLACK + WHITE + WHITE
				+ UNOCCUPIED + BLACK + WHITE + UNOCCUPIED));

		System.out.println(testBoard.toStringFormatted());
		System.out.println("Calculating scores...");
		String testScore = boardTools.getScores(testGame); 
		double scoreBlack = 12; // calculate yourself
		double scoreWhite = 4+testGame.getKomi(); // calculate yourself, think of Komi!

		assertThat(testScore.toString(), containsString(scoreBlack + GoGameConstants.DELIMITER + scoreWhite)); 
		} catch (InvalidFieldException e) {
			System.out.println("ERROR: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	void testScore4UnoccupiedIsNeutral() {
		try {
		BoardTools boardTools = new BoardTools(true);
		int boardDim = 4;
		// B U U W	 0  1  2  3
		// B U U W 	 4  5  6  7
		// B U U W 	 8  9 10 11
		// B U U W	12 13 14 15
		
		// Get a board, check if it is indeed empty
		Game testGame = new Game(boardDim, null, null); // Game has no players (for this test)
		Board testBoard = testGame.getBoard(); 
		testBoard.reset();
		assertThat(testBoard.toString(), containsString(UNOCCUPIED.repeat(boardDim*boardDim)));

		testBoard.setField(0, Stone.BLACK); 
		testBoard.setField(4, Stone.BLACK); 
		testBoard.setField(8, Stone.BLACK); 
		testBoard.setField(12, Stone.BLACK); 
		testBoard.setField(3, Stone.WHITE); 
		testBoard.setField(7, Stone.WHITE); 		
		testBoard.setField(11, Stone.WHITE); 
		testBoard.setField(15, Stone.WHITE); 
		
		assertThat(testBoard.toString(), containsString(
				BLACK + UNOCCUPIED + UNOCCUPIED + WHITE
				+ BLACK + UNOCCUPIED + UNOCCUPIED + WHITE
				+ BLACK + UNOCCUPIED + UNOCCUPIED + WHITE
				+ BLACK + UNOCCUPIED + UNOCCUPIED + WHITE));

		System.out.println(testBoard.toStringFormatted());
		System.out.println("Calculating scores...");
		String testScore = boardTools.getScores(testGame); 
		double scoreBlack = 4; // calculate yourself
		double scoreWhite = 4+testGame.getKomi(); // calculate yourself, think of Komi!

		assertThat(testScore.toString(), containsString(scoreBlack + GoGameConstants.DELIMITER + scoreWhite)); 
		} catch (InvalidFieldException e) {
			System.out.println("ERROR: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	void testScore8MultipleBlackWhiteWithUnoccupied() {
		try {
		BoardTools boardTools = new BoardTools(true);
		int boardDim = 8;
		// U U B B U W U B	 0  1  2  3  4  5  6  7
		// U B B B U W U B 	 8  9 10 11 12 13 14 15
		// B B W W U W W W 	16 17 18 19 20 21 22 23
		// U B W U U W B B	24 25 26 27 28 29 30 31
		// U U B B U U U B	32 33 34 35 36 37 38 39
		// U B B B U B B B 	40 41 42 43 44 45 46 47
		// U B W W W B U B 	48 49 50 51 52 53 54 55
		// U B W U W B U B	56 57 58 59 60 61 62 63
		
		// Get a board, check if it is indeed empty
		Game testGame = new Game(boardDim, null, null); // Game has no players (for this test)
		Board testBoard = testGame.getBoard(); 
		testBoard.reset();
		assertThat(testBoard.toString(), containsString(UNOCCUPIED.repeat(boardDim*boardDim)));

		testBoard.setField(2, Stone.BLACK); 
		testBoard.setField(3, Stone.BLACK); 
		testBoard.setField(7, Stone.BLACK); 
		testBoard.setField(9, Stone.BLACK); 
		testBoard.setField(10, Stone.BLACK);
		testBoard.setField(11, Stone.BLACK); 
		testBoard.setField(15, Stone.BLACK); 
		testBoard.setField(16, Stone.BLACK); 
		testBoard.setField(17, Stone.BLACK); 
		testBoard.setField(25, Stone.BLACK);
		testBoard.setField(30, Stone.BLACK); 
		testBoard.setField(31, Stone.BLACK); 
		testBoard.setField(34, Stone.BLACK); 
		testBoard.setField(35, Stone.BLACK); 
		testBoard.setField(39, Stone.BLACK);
		testBoard.setField(41, Stone.BLACK);
		testBoard.setField(42, Stone.BLACK); 
		testBoard.setField(43, Stone.BLACK); 
		testBoard.setField(45, Stone.BLACK); 
		testBoard.setField(46, Stone.BLACK); 
		testBoard.setField(47, Stone.BLACK);
		testBoard.setField(49, Stone.BLACK); 
		testBoard.setField(53, Stone.BLACK); 		
		testBoard.setField(55, Stone.BLACK); 
		testBoard.setField(57, Stone.BLACK); 
		testBoard.setField(61, Stone.BLACK);
		testBoard.setField(63, Stone.BLACK);
		testBoard.setField(5, Stone.WHITE);
		testBoard.setField(13, Stone.WHITE);
		testBoard.setField(18, Stone.WHITE);
		testBoard.setField(19, Stone.WHITE);
		testBoard.setField(21, Stone.WHITE);
		testBoard.setField(22, Stone.WHITE);
		testBoard.setField(23, Stone.WHITE);
		testBoard.setField(26, Stone.WHITE);
		testBoard.setField(29, Stone.WHITE);
		testBoard.setField(50, Stone.WHITE);
		testBoard.setField(51, Stone.WHITE);
		testBoard.setField(52, Stone.WHITE);
		testBoard.setField(58, Stone.WHITE);
		testBoard.setField(60, Stone.WHITE);
		
		assertThat(testBoard.toString(), containsString(
				UNOCCUPIED + UNOCCUPIED + BLACK + BLACK + UNOCCUPIED + WHITE + UNOCCUPIED + BLACK
				+ UNOCCUPIED + BLACK + BLACK + BLACK + UNOCCUPIED + WHITE + UNOCCUPIED + BLACK
				+ BLACK + BLACK + WHITE + WHITE + UNOCCUPIED + WHITE + WHITE + WHITE
				+ UNOCCUPIED + BLACK + WHITE + UNOCCUPIED + UNOCCUPIED + WHITE + BLACK + BLACK
				+ UNOCCUPIED + UNOCCUPIED + BLACK + BLACK + UNOCCUPIED + UNOCCUPIED + UNOCCUPIED + BLACK
				+ UNOCCUPIED + BLACK + BLACK + BLACK + UNOCCUPIED + BLACK + BLACK + BLACK
				+ UNOCCUPIED + BLACK + WHITE + WHITE + WHITE + BLACK + UNOCCUPIED + BLACK
				+ UNOCCUPIED + BLACK + WHITE + UNOCCUPIED + WHITE + BLACK + UNOCCUPIED + BLACK));

		System.out.println(testBoard.toStringFormatted());
		System.out.println("Calculating scores...");
		String testScore = boardTools.getScores(testGame); 
		double scoreBlack = 38; // calculate yourself
		double scoreWhite = 15+testGame.getKomi(); // calculate yourself, think of Komi!

		assertThat(testScore.toString(), containsString(scoreBlack + GoGameConstants.DELIMITER + scoreWhite)); 
		} catch (InvalidFieldException e) {
			System.out.println("ERROR: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
}
