package goStrategy;

import java.util.ArrayList;
import java.util.List;

import goGame.Board;
import goGame.Stone;

/**
 * Strategy to randomly make moves
 * @author huub.lievestro
 *
 */
public class RandomStrategy implements Strategy {
	
	/**
	 * Name of this strategy
	 */
	private String name = "Random strategy";

	/**
	 * Get the name of this strategy
	 */
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int determineMove(Board board, Stone color) {
// TODO implement when working on computer players
		//		int middleDim = board.DIM / 2; // Note: will floor, if there is no single field in the middle
		//		
		//		if (board.isEmptyField(middleDim, middleDim)) { // if possible, place in middle and return
		//			return board.indexLinear(middleDim, middleDim);
		//		}
		//		
		//		Board testBoard = board.clone();
		//
		//		List<Integer> freeFields = new ArrayList<Integer>();
		//		int i = 0;
		//		while (board.isField(i)) {
		//			if (board.isEmptyField(i)) {
		//				freeFields.add(i);
		//			}
		//			i++;
		//		}	
		//
		//		for (int indexCheck = 0; indexCheck < freeFields.size(); indexCheck++) {
		//			
		//			// direct win (NB: first found option)
		//			testBoard.setField(freeFields.get(indexCheck), mark);
		//			if (testBoard.isWinner(mark)) { 
		//				return freeFields.get(indexCheck);
		//			}
		//			
		//			// prevent opponent win (first found option)
		//			testBoard.setField(freeFields.get(indexCheck), mark.other());
		//			if (testBoard.isWinner(mark.other())) { 
		//				return freeFields.get(indexCheck);
		//			} 
		//		}
		//
		//		// If nothing else: select random field
		//		NaiveStrategy random = new NaiveStrategy();
		//		return random.determineMove(board, mark);
		return 0;
	}

}
