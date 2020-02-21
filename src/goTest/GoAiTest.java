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
public class GoAiTest {		
	private static final String WHITE = (" " + Stone.WHITE.toString() + " ").replaceAll("\\s+","");
	private static final String BLACK = (" " + Stone.BLACK.toString() + " ").replaceAll("\\s+","");
	private static final String UNOCCUPIED = (" " + Stone.UNOCCUPIED.toString() + " ").replaceAll("\\s+","");

		@Test
	void testAI() {
		try {
			BoardTools boardTools = new BoardTools(true);
			int boardDim = 19;
			
			
			
			
			Board testBoard = Board.newBoardFromString("UUUUUBUUBUUWUUWWUUUUUUUUWBBWUUBUUUBUUUUUUUUBWUUUUWUUUWUUUUUUUUWBUUUUBUUUBUUUUUUUUBUUUUUUUUUWUUUUUUUUWUUUUUUUUUBUUUUUUUUBUUUUUUUUUWUUUUUUUUWUUUUUUUUUBUUUUUUWUBUUUUUUUUUWUUUUUUUUWUUUUUUUUUBUUUUUUUUBUUUUUUUUUWUUUUUUUUWUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU");
			
			
			
			
//		} catch (InvalidFieldException e) {
//			System.out.println("ERROR: " + e.getLocalizedMessage());
//			e.printStackTrace();
//		}
		}
}
