package goComputerAI;

import java.util.Arrays;
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
		BoardTools boardTools = new BoardTools(false);
		String opponentLastMove = client.getOpponentLastMove();
		Stone ownStone = client.getPlayColour();


		if (opponentLastMove == null || opponentLastMove.equals("null")) {
			move = new RandomStrategy().calculateMove(client);
		} else if (opponentLastMove.equals(String.valueOf(ProtocolMessages.PASS))) {
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
				move = new RandomStrategy().calculateMove(client); 
			}
		} else {

			int opponentMoveInt = Integer.parseInt(opponentLastMove);

			int[] indices = new int[4]; // above, right, below, left
			Board[] boards = new Board[4]; // above, right, below, left
			Integer[] captures = new Integer[4]; // above, right, below, left


			indices[0] = board.above(opponentMoveInt); // TODO this outside loop!
			indices[1] = board.right(opponentMoveInt);
			indices[2] = board.below(opponentMoveInt);
			indices[3] = board.left(opponentMoveInt);

			
			for (int i = 0; i < indices.length; i++) {
				try {
					if (board.isField(indices[i]) && board.isEmptyField(indices[i])) { // TODO isField was 0 instead of i
						boards[i] = board.clone();
						boards[i].setField(indices[i], ownStone); // TODO set field 0 instead of i
						captures[i] = boardTools.doOpponentCaptures(boards[i], ownStone);
					} else {
						captures[i] = -1; // never place stone
					}
				} catch (InvalidFieldException e) {
					System.out.println("Invalid field:" + e.getLocalizedMessage());
					e.printStackTrace();
				}
			}

			//find the maximum value using stream API of the java 8
			Integer max = Arrays.stream(captures).max(Integer::compare).get();
			// TODO order of checking when equal max: chosing first max

			// find the index of that value
			int indexMaxCaptures  = Arrays.asList(captures).indexOf(max);
			
			if (indices[indexMaxCaptures] == -1) { // all are invalid fields
				move = new RandomStrategy().calculateMove(client); 
			} else if (Math.random() > 0.8) { // TODO adjust chance? 
				double komi = 0.5; // TODO hardcoded
				String scoreString = boardTools.getScores(board, komi); 
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
