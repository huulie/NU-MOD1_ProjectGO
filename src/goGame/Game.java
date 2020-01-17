package goGame;

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

	// -- Constructors -----------------------------------------------

	/**
	 * Creates a new Game object.
	 * @requires s0 and s1 to be non-null
	 * @param boardDim is dimension of the board
	 * @param s0 the first player
	 * @param s1 the second player
	 */
	public Game(int boardDim, Player s0, Player s1) {
		board = new Board(boardDim);
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
		board.reset();
	}

	/**
	 * Returns the current player
	 * 
	 */
	Player getCurrentPlayer() { // TDO: now visibilty is package
		return players[currentTotalTurn % NUMBER_PLAYERS];
	}
	
	/**
	 * Resturns the next player
	 * 
	 */
	Player moveToNextPlayer() { // TDO: now visibilty is package
		currentTotalTurn++;
		return players[currentTotalTurn % NUMBER_PLAYERS];
	}

	/**
	 * Prints the game situation.
	 */
	void update() { // TODO visibility now package, check or get/setters
		System.out.println("\n Current game situation: \n\n" + board.toString()
		+ "\n");
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
}
