package goGame;

import com.nedap.go.gui.GoGuiIntegrator;

import exceptions.TimeOutException;
import goUI.GoGuiUpdater;


/** Game controller, to control a game of Go.
 * @author huub.lievestro
 *
 */
public class GameController implements Runnable {

	/**
	 * Associated game, controlled by this GameController.
	 */
	private Game game;

	/**
	 * GUI, to monitor current state of the game. 
	 * (use when two local players cannot open two GUIs or on server as monitor)
	 */
	private GoGuiIntegrator gameGUI;

	/**
	 * Updater of (optional) associated GUI.
	 */
	private GoGuiUpdater gameGUIupdater;

	/**
	 * keep track of previous move, can be asked by a player.
	 */
	private int previousMove = GoGameConstants.NOMOVEint;
	
	/**
	 * Track if game is actively playing or over
	 * (also used externally, e.g. to interrupt waiting for move 
	 *  from a player while other player has gone)
	 */
	boolean gameOver;
	
	/** 
	 * Setting to print debugging information.
	 */
	private boolean printDebug = true;
	

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
	}

	/**
	 * Starts the Go game. <br>
	 *
	 */
	public void startGame() {
		if (printDebug) System.out.println("DEBUG: Game controller is starting..."); 
		this.game.reset(); 
		play();
	}

	/**
	 * Plays the Go game. <br> 
	 * First the (still empty) board is shown. Then the game is played until it is over. 
	 * Players can make a move one after the other. After each move, the game state is updated
	 */
	private void play() {
		gameOver = false;
		boolean firstPassed = false;

		while (gameOver != true) { 
			if (printDebug) System.out.println("DEBUG: make a new move...");
			
			int chosenMove = GoGameConstants.NOMOVEint;
			
			try {
				chosenMove = this.game.getCurrentPlayer().determineMove(this.game.getBoard());
			} catch (TimeOutException e) {
				System.out.println("Be faster, now lost!");
				gameOver = true;
				this.endGame(GoGameConstants.CHEAT, 
						this.game.getCurrentPlayer().getColour().print());
			}
			
			char resultMove = this.game.getCurrentPlayer()
					.makeMove(this.game.getBoard(), chosenMove);
			
//			if (resultMove == GoGameConstants.PASS || resultMove == GoGameConstants.VALID) {
//				this.game.getCurrentPlayer()
//					.moveResult(GoGameConstants.VALID, this.game.getBoard());
//			} else if (resultMove == GoGameConstants.INVALID) {
//				this.game.getCurrentPlayer()
//					.moveResult(GoGameConstants.INVALID, this.game.getBoard());
//			}

			if (resultMove == GoGameConstants.PASS) {
				this.game.getCurrentPlayer()
				.moveResult(GoGameConstants.VALID, this.game.getBoard().toString());
				if (firstPassed) {
					gameOver = true;
					if (printDebug) System.out.println("DEBUG: going to end game.."); 
					this.endGame(GoGameConstants.FINISHED, GoGameConstants.UNOCCUPIED);
				} else {
					firstPassed = true;
					this.previousMove = GoGameConstants.PASSint;
					this.game.moveToNextPlayer();
				}
			} else if (resultMove == GoGameConstants.INVALID) {
				System.out.println("INVALID"); 
				gameOver = true;
				this.game.getCurrentPlayer()
				.moveResult(GoGameConstants.INVALID, "You lost, because you did an invalid move!");
				this.endGame(GoGameConstants.CHEAT,
						this.game.getCurrentPlayer().getColour().print());
			} else if (resultMove == GoGameConstants.INVALIDPLACEMENT) {
				System.out.println("INVALID"); 
				gameOver = true;
				this.game.getCurrentPlayer()
				.moveResult(GoGameConstants.INVALID, "You lost, because you tried to place an invalid stone!");
				this.endGame(GoGameConstants.CHEAT,
						this.game.getCurrentPlayer().getColour().print());
			} else if (resultMove == GoGameConstants.INVALIDPREVIOUS) {
				System.out.println("INVALID"); 
				gameOver = true;
				this.game.getCurrentPlayer()
				.moveResult(GoGameConstants.INVALID, "You lost, because you tried to recreate a previous boardstate!");
				this.endGame(GoGameConstants.CHEAT,
						this.game.getCurrentPlayer().getColour().print());
			} else { // valid move, and no pass
				if (printDebug) System.out.println("DEBUG: update game.."); 
				this.game.update();
				this.getGameBoard().addPreviousStateWithCaptures(this.getGameBoard(), this.game.getCurrentPlayer().getColour());
				
				// if present, update GUI's for local player or gameController
				if (this.game.getCurrentPlayer().hasGUI()) {
					this.game.getCurrentPlayer().updateGUI(this.game.getBoard());
				}
				if (this.hasGameGUI()) {
					this.updateGameGUI(this.game.getBoard());
				} else {
					if (printDebug) this.game.print(); 
				}
				
//				if (resultMove == GoGameConstants.PASS || resultMove == GoGameConstants.VALID) {
//					this.game.getCurrentPlayer()
//						.moveResult(GoGameConstants.VALID, this.game.getBoard().toString());
//				} else if (resultMove == GoGameConstants.INVALID) {
//					this.game.getCurrentPlayer()
//						.moveResult(GoGameConstants.INVALID, this.game.getBoard());
//				}

				firstPassed = false;
				this.previousMove = chosenMove;
				this.game.moveToNextPlayer();
			}
		} 

		
	}
	
	/**
	 * Ends the Go game. <br> 
	 * Can also be called from other methods, e.g. when ending because of disconnect
	 */
	public void endGame(char reason, char caller) {
		gameOver = true;
		
		String scoreString = this.game.getScores();
		String[] scores = scoreString.split(GoGameConstants.DELIMITER);
		double scoreBlack = Double.parseDouble(scores[0]);
		double scoreWhite = Double.parseDouble(scores[1]);

		char winner = GoGameConstants.UNOCCUPIED; 
		
		if (caller == GoGameConstants.UNOCCUPIED) {
			if (scoreBlack > scoreWhite) {
				winner = GoGameConstants.BLACK; 
			} else {
				winner = GoGameConstants.WHITE;
			}
		} else {
			winner = Stone.charToStone(caller).other().print();
		}
		
		int noPlayers = this.game.getNumberPlayers();
		for (int i = 1; i <= noPlayers; i++) {
			
			if (printDebug) System.out.println("DEBUG: ending player" + i);
			this.game.getCurrentPlayer().endGame(reason, winner, scoreBlack, scoreWhite);
			if (i < noPlayers) {
				this.game.moveToNextPlayer();
			}
		}
	}

	/**
	 * Returns if this GameController has an associated gameGUI.
	 * @return true if it has a GUI, false if not
	 */
	public boolean hasGameGUI() {
		return this.gameGUI != null;
	}

	/**
	 * Displays updated board in gameGUI.
	 */
	public void updateGameGUI(Board board) {
		if (this.hasGameGUI()) {
			this.gameGUIupdater.updateWholeBoard(board);
		} else {
			if (printDebug) System.out.println("There is no GUI to update!");
		}
	}
	
	public boolean isGameOver() {
		return gameOver;
	}

	/** 
	 * Get board from the game associated to this GameController.
	 * @return board from the game associated to this GameController
	 */
	public Board getGameBoard() {
		return this.game.getBoard();
	}
	
	public int getPreviousMove() {
		return previousMove;
	}

	@Override
	public void run() {
		this.startGame();
		
	}

}
