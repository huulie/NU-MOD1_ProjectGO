package goGame;

import goProtocol.ProtocolMessages;

	/**
	 * All constants used in the Go Game.
	 * 
	 * @author Huub Lievestro
	 */
	public class GoGameConstants {

		// Commands to control local TUI and/or program	
		public static final char VALID = ProtocolMessages.VALID;
		public static final char INVALID = ProtocolMessages.INVALID;
		public static final char PASS = ProtocolMessages.PASS; //used to indicate a pass move, when using char
		public static final int PASSint = -1; //used to indicate a pass move, when using int
		public static final String DELIMITER = ProtocolMessages.DELIMITER;

		
	}
