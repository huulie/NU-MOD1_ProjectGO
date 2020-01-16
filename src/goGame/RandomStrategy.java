package goGame;

import java.util.ArrayList;
import java.util.List;

public class RandomStrategy implements Strategy {
	private String name = "Random strategy";

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int determineMove(Board board, Stone color) {
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
