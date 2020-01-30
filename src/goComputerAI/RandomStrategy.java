package goComputerAI;

import goClient.GoClient;
import goGame.Board;
import goGame.GoGameConstants;

/**
 * Strategy to randomly make moves.
 * @author huub.lievestro
 *
 */
public class RandomStrategy implements Strategy {
	
	/**
	 * Name of this strategy.
	 */
	private String name = "Random strategy";

	/**
	 * Get the name of this strategy.
	 */
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int calculateMove(GoClient client) {

		Board board = client.getLocalBoard();
		int boardDim = board.getDim();
		
		int move = GoGameConstants.NOMOVEint;
		boolean valid = false;
		
		int lower = 0; // TODO inclusive?
		int upper = boardDim * boardDim; // TODO exclusive?
		
		while (!valid) {
			move = (int) (Math.random() * (upper - lower)) + lower;

			if (board.isField(move) && board.isEmptyField(move)) {
				valid = true;
			}
		}
		
		return move;
	}

}
