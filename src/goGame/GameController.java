package goGame;

import com.nedap.go.gui.GoGuiIntegrator;

import exceptions.TimeOutException;
import goUI.GoGuiUpdater;


/** Game controller, to control a game of Go
 * @author huub.lievestro
 *
 */
public class GameController implements Runnable{

	/**
	 * Associated game, controlled by this GameController.
	 */
	private Game game;

	/**
	 * GUI, to monitor current state of the game 
	 * (use when two local players cannot open two GUIs or on server as monitor)
	 */
	private GoGuiIntegrator gameGUI;

	/**
	 * Updater of (optional) associated GUI
	 */
	private GoGuiUpdater gameGUIupdater;

	/**
	 * TODO keep track of previous move, can be asked by a player
	 */
	private int previousMove = -2;

	

	/**
	 * Creates a new GameController, including the game.
	 * @param boardDim is dimension of the board
	 * @param blackPlayer the first player, and will be playing with BLACK
	 * @param whitePlayer the second player, and will be playing with WHITE
	 * @param GUI start a GUI to view the associated game (note: only one GUI can be started per VM)
	 */
	public GameController(int boardDim, Player blackPlayer, Player whitePlayer, boolean GUI) {
		this.game = new Game(boardDim, blackPlayer, whitePlayer);

		if (GUI) {
			this.gameGUI = new GoGuiIntegrator(true, true, boardDim);
			this.gameGUI.startGUI();
			this.gameGUI.setBoardSize(boardDim);
			this.gameGUIupdater = new GoGuiUpdater(this.gameGUI);
		}
		// TODO: let GameController create Player objects? >> how to determine which type? Local starter or server knows!
	}

	/**
	 * Starts the Go game. <br>
	 * Asks after each ended game if the user want to continue. Continues until
	 * the user does not want to play anymore. TODO implementation?
	 */
	public void startGame() {
		System.out.println("DEBUG: Game controller is starting..."); // TODO: eventually remove/disable

		this.game.reset(); // TODO: current player in game controller? 
		play();
	}

	/**
	 * Plays the Go game. <br> TODO: take a second look 
	 * First the (still empty) board is shown. Then the game is played until it is over. 
	 * Players can make a move one after the other. After each move, the game state is updated
	 */
	private void play() {
		boolean gameOver = false; // TODO implement
		boolean firstPassed = false;

		while (gameOver != true) { // TODO implement a this.board.gameOver() ?
			System.out.println("DEBUG: make a new move..."); // TODO: eventually remove/disable
			// TODO implement checks on validity / previous state etc
			
			int chosenMove = -2; //TODO think of default
			try {
				chosenMove = this.game.getCurrentPlayer().determineMove(this.game.getBoard());
			} catch (TimeOutException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
				System.out.println("Be faster, now lost!");
				gameOver = true;
				this.endGame(GoGameConstants.CHEAT, this.game.getCurrentPlayer().getColour().print());
				//chosenMove = -1;
			}
			char resultMove = this.game.getCurrentPlayer().makeMove(this.game.getBoard(),chosenMove);
			this.game.getCurrentPlayer().moveResult(resultMove, this.game.getBoard());

			if (resultMove == GoGameConstants.PASS) {
				if(firstPassed) {
					gameOver = true; // TODO properly end game
					// if game over, loop ends
					System.out.println("DEBUG: going to end game.."); // TODO: eventually remove/disable
					this.endGame(GoGameConstants.FINISHED, GoGameConstants.UNOCCUPIED);
				} else {
					firstPassed = true;
					this.previousMove = GoGameConstants.PASSint;
					this.game.moveToNextPlayer();
				}
			} else if (resultMove == GoGameConstants.INVALID) {
				System.out.println("INVALID"); // TODO end game as loser
				gameOver = true;
				this.endGame(GoGameConstants.CHEAT,this.game.getCurrentPlayer().getColour().print());
			} else {
				
				System.out.println("DEBUG: update game.."); // TODO: eventually remove/disable
				this.game.update(); // TODO: pattern render view something

				// send new game state to players 
				// TODO implement!

				// TODO ALWAYS this.game.getCurrentPlayer().moveResult(resultMove, this.game.getBoard());
				// if present, update GUI's for local player or gameController
				if (this.game.getCurrentPlayer().hasGUI()) {
					this.game.getCurrentPlayer().updateGUI(this.game.getBoard());
				}
				if (this.hasGameGUI()) {
					this.updateGameGUI(this.game.getBoard());
				} else {
					this.game.print(); // TODO printing for gamecontroller, not for players: keep?
				}

				firstPassed = false;
				this.previousMove = chosenMove;
				this.game.moveToNextPlayer();
			}
		} 

		
	}
	
	/**
	 * Plays the Go game. <br> TODO: take a second look 
	 * First the (still empty) board is shown. Then the game is played until it is over. 
	 * Players can make a move one after the other. After each move, the game state is updated
	 */
	public void endGame(char reason, char caller) {
		String scoreString = this.game.getScores(); // TODO is printing for game, not for players!
		String[] scores = scoreString.split(GoGameConstants.DELIMITER);
		double scoreBlack = Double.parseDouble(scores[0]);
		double scoreWhite = Double.parseDouble(scores[1]);

		char winner = GoGameConstants.UNOCCUPIED; // TODO: score should use players OR here convert color to player >> PROTOCOL USES COLORS?!
		
		if (caller == GoGameConstants.UNOCCUPIED) {
			if(scoreBlack>scoreWhite) {
				winner = GoGameConstants.BLACK; // " BLACK ";
			} else {
				winner = GoGameConstants.WHITE; //" WHITE ";
			}
		} else {
			winner = Stone.charToStone(caller).other().print();
		}
		
		int noPlayers = this.game.getNumberPlayers();
		for (int i = 1; i <= noPlayers; i++) {
			
			System.out.println("DEBUG: ending player" + i);
			this.game.getCurrentPlayer().endGame(reason, winner, scoreBlack, scoreWhite);
			if(i < noPlayers) {
				this.game.moveToNextPlayer();
			}
		}

		
		// TODO to seperate methods?! and make it server-client proof!
//				System.out.println(" -- GAME ENDED -- "); // TODO is printing for game, not for players!
//				String scoreString = this.game.getScores(); // TODO is printing for game, not for players!
//
//				String[] scores = scoreString.split(GoGameConstants.DELIMITER);
//				double scoreBlack = Double.parseDouble(scores[0]);
//				double scoreWhite = Double.parseDouble(scores[1]);
//
//				String winner = null; // TODO: score should use players OR here convert color to player >> PROTOCOL USES COLORS?!
//				if(scoreBlack>scoreWhite) {
//					winner = " BLACK ";
//				} else {
//					winner = " WHITE ";
//				}

//				System.out.println("Black has scored: " + scoreBlack); // TODO is printing for game, not for players!	
//				System.out.println("White has scored: " + scoreWhite);// TODO is printing for game, not for players!	
//				System.out.println("The winner is: " + winner);	// TODO is printing for game, not for players!
		
		
		
	}



	/**
	 * Returns if this GameController has an associated gameGUI.
	 * @return true if it has a GUI, false if not
	 */
	public boolean hasGameGUI() {
		return (this.gameGUI != null);
	}

	/**
	 * Displays updated board in gameGUI
	 */
	public void updateGameGUI(Board board) {
		if (this.hasGameGUI()) {
			this.gameGUIupdater.updateWholeBoard(board);
		} else {
			System.out.println("There is no GUI to update!");
		}

	}
	
	/** 
	 * Get board from the game associated to this GameController
	 * @return board from the game associated to this GameController
	 */
	public Board getGameBoard() {
		return this.game.getBoard();
	}
	
	/**
	 * TODO doc, and implement like this?
	 * @return
	 */
	public int getPreviousMove() {
		return previousMove;
	}

	@Override
	public void run() {
		this.startGame();
		
	}

}
