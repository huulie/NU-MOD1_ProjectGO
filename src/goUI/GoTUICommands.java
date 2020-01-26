package goUI;

import goGame.GoGameConstants;
import goProtocol.ProtocolMessages;

/**
 * All commands used in a TUI for the Go Game.
 * 
 * @author Huub Lievestro
 */
public class GoTUICommands {

	// Commands to control local TUI and/or program	
	public static final char EXIT = 'x';
	public static final char HELP = 'h';
		
	//Constants representing ..
	public static final char WHITE = GoGameConstants.WHITE;
	public static final char BLACK = GoGameConstants.BLACK;
	public static final char UNOCCUPIED = GoGameConstants.UNOCCUPIED;
	public static final char PASS = ProtocolMessages.PASS; //used to indicate a pass move 
	// TODO use single source?
	

}
