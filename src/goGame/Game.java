package goGame;

import exceptions.InvalidFieldException;


/**
 * Class containing the model for the GO game.
 * @author Huub Lievestro
 */
public class Game {
	/**
	 * The number of player of one game
	 * @invariant NUMBER_PLAYERS is always 2
	 */
	private final int NUMBER_PLAYERS = 2; 

	/**
	 * The board, associated to this game..
	 * @invariant board is never null
	 */
	private Board board;

	/**
	 * The NUMBER_PLAYERS players of the game.
	 * @invariant the length of the array equals NUMBER_PLAYERS
	 * @invariant all array items are never null
	 */
	private Player[] players;

	/**
	 * Index of the current turn, counting total number of turns that have past.
	 * @invariant the index is always zero or positive
	 */
	private int currentTotalTurn;
	
	/**
	 * Board tools, used to execute game rules on the board
	 */
	private BoardTools boardTools;
	
	/**
	 * The Komi is a compensation for BLACK starting the game.
	 * From the rules of Go:
	 * Black's initial advantage of moving first can be offset by komi (compensation points):
	 * a fixed number of points, agreed before the game, added to White's score at the end of the game
	 */
	private double komi = 0.5;
	
	
	/**
	 * Creates a new Game instance, including the board, and associates Players to it.
	 * @requires blackPlayer and whitePlayer to be non-null
	 * @param boardDim is dimension of the board
	 * @param blackPlayer the first player, and will be playing with BLACK
	 * @param whitePlayer the second player, and will be playing with WHITE
	 */
	public Game(int boardDim, Player blackPlayer, Player whitePlayer) {
		try {
			board = new Board(boardDim);
		} catch (InvalidFieldException e) {
			System.out.println("ERROR something went wrong when making a new board: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		boardTools  = new BoardTools(false);
		players = new Player[NUMBER_PLAYERS];
		players[0] = blackPlayer;
		players[1] = whitePlayer;
		currentTotalTurn = 0;
	}

	/**
	 * Resets the game. <br>
	 * The board is emptied and player[0] becomes the current player.
	 */
	public void reset() { // TODO: set visibility to protected? https://docs.oracle.com/javase/tutorial/java/javaOO/accesscontrol.html
		currentTotalTurn = 0;
		try {
			board.reset();
		} catch (InvalidFieldException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR something went wrong when resetting the board: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Returns the current player
	 * @return Player current player
	 */
	public Player getCurrentPlayer() { // TODO: set visibility to protected?
		return players[currentTotalTurn % NUMBER_PLAYERS];
	}
	
	/**
	 * Moves game to the next player
	 * 
	 */
	public void moveToNextPlayer() { // TODO: set visibility to protected?
		currentTotalTurn++;
	}

	/**
	 * Prints the game situation, to the standard system output (of the VM running the game).
	 */
	public void print() { // TODO: set visibility to protected?
		System.out.println("\n [GAME] Current game situation: \n\n" + board.toStringFormatted()
		+ "\n");
	}
	
	/**
	 * Updates the game, by applying the following GO rule(s) to the board:
	 * - A stone or solidly connected group of stones of one color is captured and removed 
	 *   from the board when all the intersections directly adjacent to it are occupied by the enemy. 
	 *   (Capture of the enemy takes precedence over self-capture.)
	 */
	public void update() { // TODO: set visibility to protected?
	boardTools.doOpponentCaptures(this.board, this.getCurrentPlayer().getColour());
	boardTools.doOwnCaptures(this.board, this.getCurrentPlayer().getColour());
	}

	/**
	 * Get the current scores of both players.
	 * @return scores String, with scoreBlack + DELIMITER + scoreWhite.
	 */
	public String getScores() {
		return boardTools.getScores(this.getBoard(), this.getKomi());
	}
	
	/**
	 * Get the Komi as currently set for this game
	 * @return Komi for this game
	 */
	public double getKomi() {
		return komi;
	}

	/**
	 * Set the Komi for this game
	 * @ensures this game has Komi set to requested komi
	 */
	public void setKomi(double komi) {
		this.komi = komi;
	}

	/**
	 * Get the board of this game.
	 * @return board, the current state of the board associated with this game.
	 */
	public Board getBoard() {
		return board;
	}

	/**
	 * Get the number of players of this game.
	 * @return NUMBER_PLAYERS, the current number of players associated with this game.
	 */
	public int getNumberPlayers() {
		return NUMBER_PLAYERS;
	}

}
