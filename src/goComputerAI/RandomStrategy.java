package goComputerAI;

import java.util.ArrayList;
import java.util.List;

import exceptions.InvalidFieldException;
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
		
		List<Integer> possibleMoves = board.getEmptyFields(); 
		
		int move = GoGameConstants.NOMOVEint;
		boolean valid = false;

		while (!valid && !possibleMoves.isEmpty()) {
			int lower = 0; // TODO inclusive?
			int upper = possibleMoves.size()-1; // TODO exclusive?
			
			int randomIndex = (int) (Math.random() * (upper - lower)) + lower;

			move = possibleMoves.get(randomIndex);
			
			boolean invalidPrevious = false;
			
			try {
				invalidPrevious = board.checkSamePreviousState(move, client.getPlayColour());
			} catch (InvalidFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (board.isField(move) && board.isEmptyField(move) && !invalidPrevious) {
				valid = true;
				System.out.println("RANDOM MOVE!");
			} else {
				possibleMoves.remove(randomIndex);
			}
		}
		
		if (possibleMoves.isEmpty()) {
			move = GoGameConstants.PASSint;
			System.out.println("PASSING...");
		}
		
		System.out.println("Doing random move...");
		return move;
	}

}
