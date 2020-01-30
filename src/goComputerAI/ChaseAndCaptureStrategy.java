package goComputerAI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import exceptions.InvalidFieldException;
import goClient.GoClient;
import goGame.Board;
import goGame.BoardTools;
import goGame.GoGameConstants;
import goGame.Stone;
import goProtocol.ProtocolMessages;

/**
 * Strategy to chase opponent and try to maximize opponent capture
 * Will pass with a certain probability and only if own score is higher then opponent.
 * @author huub.lievestro
 *
 */
public class ChaseAndCaptureStrategy implements Strategy {

	/**
	 * Name of this strategy
	 */
	private String name = "Chase and Capture strategy";

	/**
	 * Get the name of this strategy
	 */
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int calculateMove(GoClient client) {
		int move;

		Board board = client.getLocalBoard();
		BoardTools boardTools = new BoardTools(false); // TODO leave debugging?
		String opponentLastMove = client.getOpponentLastMove();
		Stone ownStone = client.getPlayColour();


		if (opponentLastMove == null || opponentLastMove.equals("null") 
				|| opponentLastMove.equals(String.valueOf(ProtocolMessages.PASS))) {
			move = new RandomStrategy().calculateMove(client);
		} else {

			int opponentMoveInt = Integer.parseInt(opponentLastMove);

			int[] indices = new int[4]; // above, right, below, left
			Board[] boards = new Board[4]; // above, right, below, left
			int[] captures = new int[4]; // above, right, below, left


			for (int i = 0; i < indices.length; i++) {
				try {
					indices[i] = board.above(opponentMoveInt);
					if (board.isField(indices[0]) && board.isEmptyField(indices[i])) {
						boards[i] = board.clone();
						boards[i].setField(indices[0], ownStone);
						captures[i] = boardTools.doOpponentCaptures(boards[i], ownStone);
					}
				} catch (InvalidFieldException e) {
					System.out.println("Invalid field:" + e.getLocalizedMessage());
					e.printStackTrace();
				}
			}

			int indexMaxCaptures = Arrays.stream(captures).max().getAsInt(); 
			// TODO order of checking when equal max

			if (Math.random() > 0.8) { // TODO adjust chance? 
				double komi = 0.5; // TODO hardcoded
				String scoreString = boardTools.getScores(board,komi); 
				String[] scores = scoreString.split(GoGameConstants.DELIMITER);
				double scoreBlack = Double.parseDouble(scores[0]);
				double scoreWhite = Double.parseDouble(scores[1]);

				if ((client.getPlayColour() == Stone.BLACK) && (scoreBlack > scoreWhite)) {
					move = GoGameConstants.PASSint;
				} else if ((client.getPlayColour() == Stone.WHITE) && (scoreBlack < scoreWhite)) {
					move = GoGameConstants.PASSint;
				} else {
					move = indices[indexMaxCaptures];
				}
			} else {
				move = indices[indexMaxCaptures];
			}
		}
		return move;
	}
	
}
