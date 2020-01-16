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
	private Board board;

	/**
	 * The 2 players of the game.
	 * @invariant the length of the array equals NUMBER_PLAYERS
	 * @invariant all array items are never null
	 */
	private Player[] players;

	/**
	 * Index of the current player.
	 * @invariant the index is always between 0 and NUMBER_PLAYERS
	 */
	private int current;

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
		current = 0;
	}

	// -- Commands ---------------------------------------------------

	/**
	 * Starts the Go game. <br>
	 * Asks after each ended game if the user want to continue. Continues until
	 * the user does not want to play anymore.
	 */
	public void start() {
		boolean continueGame = true;
		while (continueGame) {
			reset();
			play();
			System.out.println("\n> Play another time? (y/n)?");
//			continueGame = TextIO.getBoolean();
		}
		System.out.println("Bye bye!");
	}

	/**
	 * Resets the game. <br>
	 * The board is emptied and player[0] becomes the current player.
	 */
	private void reset() {
		current = 0;
		board.reset();
	}

	/**
	 * Plays the Go game. <br>
	 * First the (still empty) board is shown. Then the game is played
	 * until it is over. Players can make a move one after the other. 
	 * After each move, the changed game situation is printed.
	 */
	private void play() {
		
boolean gameOver = false; // TODO implement
		
		System.out.println(this.board.toString());

		int playerCounter = 0;
		while (gameOver != true) { // this.board.gameOver()
			current = playerCounter % NUMBER_PLAYERS;

			System.out.println("\n");
			
//			if (players[current]instanceof HumanPlayer)  {
//				this.board.setField(players[current].determineMove(this.board),players[current].getMark());
//			} else {
//				players[current].makeMove(this.board);
//			}
			
			players[current].makeMove(this.board);

			playerCounter++;
			this.update();
		} 

		this.printResult();
	}

	/**
	 * Prints the game situation.
	 */
	private void update() {
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
