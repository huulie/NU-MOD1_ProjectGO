package goGame;

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
	public int determineMove(Board board) {
		return clientHandler.requestMove(); 
	}
	
//	/**
//	 * Displays message on user interface of client
//	 */
//	@Override
//	public void displayMessage(String message) {
//		this.clientHandler.showMessage(message);
//	}

}
