package goGame;

// TODO: documentation and implementation
public class RemotePlayer extends Player {

	/**
     * Creates a new remote player object.
     * @requires color is either ProtocolMessages.BLACK or ProtocolMessages.WHITE
     * @requires strategy is not null
     * @ensures the color of this player will be mark
     * @ensures the Strategy of this player is strategy
     * @ensures the Name of this player will be COMPUTER_strategy-mark
     */
	public RemotePlayer(String name, Stone colour) {
		super(name, colour);
	
		// TODO to implement
		// and will never have a GUI, so not asking boardDim and GUI
		
	}
	
	@Override
	public int determineMove(Board board) {
		System.out.println("Computer " + this.getName() + " makes a move!");
		return 0; 
	}

}
