package goGame;

import exceptions.TimeOutException;
import goServer.GoClientHandler;

// TODO: documentation and implementation
public class RemotePlayer extends Player {

	// Inherited from Player: name, colour and TUI
	// Remote Player has no TUI (implemented yet)
	// Remote Player has no GUI and no GUIupdater
	
	/**
	 * Associated client handler, handling communication with remote client
	 */
	GoClientHandler clientHandler; 
	
	/**
     * Creates a new remote player object.
     * @requires name is not null
	 * @requires colour is either GoGameConstants.BLACK or GoGameConstants.WHITE
	 * @ensures the Name of this player will be name
	 * @ensures the Stone of this player will be colour
     */
	public RemotePlayer(String name, Stone colour, GoClientHandler handler) {
		super(name, colour);
		this.clientHandler = handler;
	}
	
	@Override
	public int determineMove(Board board) throws TimeOutException {
		return clientHandler.requestMove(this.getGame().getPreviousMove()); 
	}
	
	/**
	 * Shows the result of the move to the Player
	 * @param board data to update
	 */
	 @Override
	public void moveResult(char result, Board board) {
		 clientHandler.resultMove(result, board);
	}
	
	
	 /**
	  * Ends game for player, showing result TODO
	  * @param board data to update
	  */
	 @Override
	 public void endGame(char reason, char winner, double scoreBlack, double scoreWhite) {
		 clientHandler.clientEndGame(reason, winner, scoreBlack, scoreWhite);
	 }
//	/**
//	 * Displays message on user interface of client
//	 */
//	@Override
//	public void displayMessage(String message) {
//		this.clientHandler.showMessage(message);
//	}

}
