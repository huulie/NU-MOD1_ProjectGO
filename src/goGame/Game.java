package goGame;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import exceptions.InvalidFieldException;

//import ss.utils.TextIO; // TODO: rewrite

/**
 * Class containing the model for the GO game.
 * @author Huub Lievestro
 */
public class Game {
	/**
	 * The number of player of one game
	 * @invariant number_players is always 2
	 */
	public static final int NUMBER_PLAYERS = 2; 

	/**
	 * The board.
	 * @invariant board is never null
	 */
	Board board; // TDO: board is now visible for package, check and create getters/setters?

	/**
	 * The 2 players of the game.
	 * @invariant the length of the array equals NUMBER_PLAYERS
	 * @invariant all array items are never null
	 */
	private Player[] players;

	/**
	 * Index of the current turn.
	 * @invariant the index is always between 0 and NUMBER_PLAYERS
	 */
	private int currentTotalTurn;
	
	/**
	 * Capture checker TODO extend
	 */
	private CaptureChecker captureChecker;
	
	// TODO add GUI TO GAME, FOR SERVER TO VISUALIZE OR LOCAL TO SEE TWO PLAYERS

	// -- Constructors -----------------------------------------------

	/**
	 * Creates a new Game object.
	 * @requires s0 and s1 to be non-null
	 * @param boardDim is dimension of the board
	 * @param s0 the first player
	 * @param s1 the second player
	 */
	public Game(int boardDim, Player s0, Player s1) {
		try {
			board = new Board(boardDim);
		} catch (InvalidFieldException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR something went wrong when making a new board");
			e.printStackTrace();
		}
		captureChecker  = new CaptureChecker(false);
		players = new Player[NUMBER_PLAYERS];
		players[0] = s0;
		players[1] = s1;
		currentTotalTurn = 0;
	}

	// -- Commands ---------------------------------------------------

	/**
	 * Resets the game. <br>
	 * The board is emptied and player[0] becomes the current player.
	 */
	void reset() { // TDO: now visibilty is package
		currentTotalTurn = 0;
		try {
			board.reset();
		} catch (InvalidFieldException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR something went wrong when resetting the board");
			e.printStackTrace();
		}
	}

	/**
	 * Returns the current player
	 * 
	 */
	Player getCurrentPlayer() { // TODO: now visibilty is package
		return players[currentTotalTurn % NUMBER_PLAYERS];
	}
	
	/**
	 * Resturns the next player
	 * 
	 */
	void moveToNextPlayer() { // TODO: now visibilty is package, void instaed of Player
		currentTotalTurn++;
		//return players[currentTotalTurn % NUMBER_PLAYERS];
	}

	/**
	 * Prints the game situation.
	 */
	void print() { // TODO visibility now package, check or get/setters
		System.out.println("\n [GAME] Current game situation: \n\n" + board.toStringFormatted()
		+ "\n");
	}
	
	/**
	 * Applies the following GO rule(s) to the board:
	 * - A stone or solidly connected group of stones of one color is captured and removed 
	 *   from the board when all the intersections directly adjacent to it are occupied by the enemy. 
	 *   (Capture of the enemy takes precedence over self-capture.)
	 */
	void update() { // TODO visibility now package, check or get/setters
	captureChecker.doOpponentCaptures(this.board, this.getCurrentPlayer().getColor());
	captureChecker.doOwnCaptures(this.board, this.getCurrentPlayer().getColor());
	// logic moved to capture checker, because splitting to smaller methods and use its own isntance variables
	}

	/**
	 * @TODO add JavaDoc
	 * @param color
	 * @return
	 */
	public int getScore(Stone color) {
		
		int numberOfStones = 0;
		int enclosedEmtpyIntersections = 0; // TODO: how?
		
		int boardDim = this.board.getDim();
		
		for (int iRow = 0; iRow < boardDim; iRow++) {
			for (int iCol = 0; iCol < boardDim; iCol++) {
				if (this.board.getField(iRow, iCol) == color) {
					numberOfStones++;
				}
			}
		}
		
		return numberOfStones + enclosedEmtpyIntersections;
	}
	
	/**
	 * Prints the result of the last game. <br>
	 * @requires the game to be over
	 */
	private void printResult() {
//		if (board.hasWinner()) {
//			Player winner = board.isWinner(players[0].getMark()) ? players[0]
//					: players[1];
//			System.out.println("Player " + winner.getName() + " ("
//					+ winner.getMark().toString() + ") has won!");
//		} else {
//			System.out.println("Draw. There is no winner!");
//		}
	}

	public Board getBoard() {
		return board;
	}

}
