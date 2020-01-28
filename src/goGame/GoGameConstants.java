package goGame;

import goProtocol.ProtocolMessages;

	/**
	 * All constants used in the Go Game.
	 * 
	 * @author Huub Lievestro
	 */
	public class GoGameConstants {

		// Reserved characters, to represent information in the game
		public static final char VALID = ProtocolMessages.VALID;
		public static final char INVALID = ProtocolMessages.INVALID;
		public static final char PASS = ProtocolMessages.PASS; //used to indicate a pass move, when using char
		public static final int PASSint = -1; //used to indicate a pass move, when using int
		public static final String DELIMITER = ProtocolMessages.DELIMITER;
		
		public static final char WHITE = ProtocolMessages.WHITE;
		public static final char BLACK = ProtocolMessages.BLACK;
		public static final char UNOCCUPIED = ProtocolMessages.UNOCCUPIED;
		
		public static final char FINISHED = ProtocolMessages.FINISHED; 
		//From server, indicates normal end of game (after double pass)
	public static final char DISCONNECT = ProtocolMessages.DISCONNECT; 
		//From server, indicates that other player disconnected (= end of game)
	public static final char CHEAT = ProtocolMessages.CHEAT; //From server, to players (the non-cheating player wins!)
		
	}
