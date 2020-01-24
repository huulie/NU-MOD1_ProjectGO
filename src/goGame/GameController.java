package goGame;

// import ss.utils.TextIO;
//import goProtocol.ProtocolMessages;

/** Game controller, to control a game of Go
 * @author huub.lievestro
 *
 */
public class GameController {

	private Game game;
	
	int boardDim;
	
	/**
	 * The number of player of one game
	 * @invariant number_players is always 2
	 */
	public static final int NUMBER_PLAYERS = 2; 
	
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
	
	/**
	 * @param boardDim
	 * @param player1
	 * @param player2
	 */
	public GameController(int boardDim, Player player1, Player player2) {
		this.boardDim = boardDim;
		
		this.players = new Player[NUMBER_PLAYERS];
		this.players[0] = player1;
		this.players[1] = player2;
		
		this.game = new Game(this.boardDim, this.players[0], this.players[1]);
	}

	/**
	 * Starts the Go game. <br>
	 * Asks after each ended game if the user want to continue. Continues until
	 * the user does not want to play anymore.
	 */
	public void start() {
		System.out.println("DEBUG: Game controller is starting..."); // TODO: eventually remove
	
			this.game.reset();
			current = 0;
			play();
			
	}
	
	/**
	 * Plays the Go game. <br>
	 * First the (still empty) board is shown. Then the game is played
	 * until it is over. Players can make a move one after the other. 
	 * After each move, the changed game situation is printed.
	 */
	private void play() {
		
		boolean gameOver = false; // TODO implement
		boolean firstPassed = false;

		//Game currentGame = new Game(19, player1, player2); // TODO fixed harcoded game settings

//		this.game.players[0].displayMessage("PLAYER1: " + this.game.board.toString());
//		this.players[1].displayMessage("PLAYER2: " + this.game.board.toString());

		int playerCounter = 0;
		while (gameOver != true) { // this.board.gameOver()
//			current = playerCounter % NUMBER_PLAYERS;
//
//			System.out.println("\n");
//			
////			if (players[current]instanceof HumanPlayer)  {
////				this.board.setField(players[current].determineMove(this.board),players[current].getMark());
////			} else {
////				players[current].makeMove(this.board);
////			}
//			
//			players[current].makeMove(this.board);
//
//			playerCounter++;
//			this.update();
			
			char currentMove = this.game.getCurrentPlayer().makeMove(this.game.getBoard());
			
			if (currentMove == GoGameConstants.PASS) {
				if(firstPassed) {
					gameOver = true; // TODO properly end game
				} else {
				firstPassed = true;
				this.game.moveToNextPlayer();
				}
			} else if (currentMove == GoGameConstants.INVALID) {
				System.out.println(" INVALID"); // TODO end game as loser
				gameOver = true;
			} else {
				this.game.update(); // TODO: pattern render view something
				
				if (this.game.getCurrentPlayer().hasGUI()) {
				this.game.getCurrentPlayer().updateGUI(this.game.getBoard());
				}
				
				this.game.print(); // TODO is printing for game, not for players!
//				if (outputBoardToTUI) {
//		        	TUI.showMessage(board.toString());
//		        }
				firstPassed = false;
				this.game.moveToNextPlayer();
				playerCounter++; // TDO: what does playerCounter do? 
			}
			

		} 

		System.out.println(" -- GAME ENDED -- "); // TODO is printing for game, not for players!
		String scoreString = this.game.getScores(); // TODO is printing for game, not for players!

		String[] scores = scoreString.split(GoGameConstants.DELIMITER);
		double scoreBlack = Double.parseDouble(scores[0]);
		double scoreWhite = Double.parseDouble(scores[1]);
		
		String winner = null; // TODO: score should use players OR here convert color to player >> PROTOCOL USES COLORS?!
		if(scoreBlack>scoreWhite) {
			winner = " BLACK ";
		} else {
			winner = " WHITE ";
		}
		
		System.out.println("Black has scored: " + scoreBlack); // TODO is printing for game, not for players!	
		System.out.println("White has scored: " + scoreWhite);// TODO is printing for game, not for players!	
		System.out.println("The winner is: " + winner);	// TODO is printing for game, not for players!
		
	
	}
	
	// TODO: do something with this?
//	private static Player createNewPlayer(String inputName, Stone inputColor, int boardDim) {
//		Player newPlayer;
//		
//		if (inputName.contains("-N")) {
//			newPlayer = new ComputerPlayer(inputColor, new RandomStrategy());
//		} else if (inputName.contains("-S")) {
//			newPlayer = new ComputerPlayer(inputColor, new RandomStrategy());
//		} else {
//			newPlayer = new LocalPlayer(inputName, inputColor, boardDim, false);
//		}
//		return newPlayer;
//	}

}
