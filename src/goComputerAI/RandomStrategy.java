package goComputerAI;

import goClient.GoClient;
import goGame.Board;

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
	public int calculateMove(GoClient client) { // Board board, Stone color

		Board board = client.getLocalBoard();
		int boardDim = board.getDim();
		
		int lower = 0; // TODO inclusive?
		int upper = boardDim * boardDim; // TODO exclusive?
		
		int move = (int) (Math.random() * (upper - lower)) + lower;
		//TODO check if empty field? 
		
		return move;
	}

}
