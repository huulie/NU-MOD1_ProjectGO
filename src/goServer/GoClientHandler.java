package goServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import exceptions.ClientUnavailableException;
import exceptions.TimeOutException;
import goGame.Board;
import goGame.GoGameConstants;
import goGame.Player;
import goGame.RemotePlayer;
import goGame.Stone;
import goProtocol.ProtocolMessages;
import goUI.GoTUICommands;


/**
 * GoClientHandler for the GO Server application.
 * This class can handle the communication with one client. 
 * 
 * @author Huub Lievestro
 */
public class GoClientHandler implements Runnable {

	/** The socket and In- and OutputStreams. */
	private BufferedReader in;
	private BufferedWriter out;
	private Socket sock;
	
	/** The connected GoServer. */
	private GoServer srv;

	/** Name of this ClientHandler. */
	private String clientName;
	
	/**
	 * Associated Remote Player, taking part in the game
	 */
	Player remotePlayer;
	
	/** 
	 * TODO: keep? then doc!
	 */
	private boolean printDebug = true;
	
	
//	private boolean moveAnswer = false;
	/**
	 * MOVE TODO
	 */
	private String chosenMove = null; //TODO minus 2 to signal not set

	/**
	 * Constructs a new GoClientHandler. Opens the In- and OutputStreams.
	 * 
	 * @param sock The client socket
	 * @param srv  The connected server
	 * @param name The name of this ClientHandler
	 */
	public GoClientHandler(Socket sock, GoServer srv, String name) { 
		try {
			in = new BufferedReader(
					new InputStreamReader(sock.getInputStream()));
			out = new BufferedWriter(
					new OutputStreamWriter(sock.getOutputStream()));
			this.sock = sock;
			this.srv = srv;
			this.clientName = name;
			
			// this.remotePlayer = createRemotePlayer(this.name); TODO create after handshake
			
		} catch (IOException e) {
			shutdown();
		}
	}

	/**
	 * Continuously listens to client input and forwards the input to the
	 * {@link #handleIncoming(String)} method.
	 */
	public void run() {
		String msg;
		try {
			msg = in.readLine();
			while (msg != null) {
				System.out.println("> [" + clientName + "] Incoming: " + msg);
				handleIncoming(msg);
				
//				out.newLine(); TODO: DON'T send (only) a newline!
//				out.flush();
				
				msg = in.readLine();
			}
			
			System.out.println("DEBUG: message was null ");
			this.getRemotePlayer().getGame().endGame(GoGameConstants.DISCONNECT, this.getRemotePlayer().getColour().print());
			shutdown();
		} catch (IOException e) {
			System.out.println("DEBUG: IO exception: " +e.getLocalizedMessage());
			this.getRemotePlayer().getGame().endGame(GoGameConstants.DISCONNECT, this.getRemotePlayer().getColour().print());
			shutdown();
		}
	}

	/**
	 * Handles commands received from the client by calling the according 
	 * methods at the GoServer. For example, when the message "i Name" 
	 * is received, the method doIn() of HotelServer should be called 
	 * and the output must be sent to the client.
	 * 
	 * If the received input is not valid, send an "Unknown Command" 
	 * message to the server.
	 * 
	 * @param msg command from client
	 * @throws IOException if an IO errors occur.
	 */
	private void handleIncoming(String msg) throws IOException {
		String [] split = msg.split(ProtocolMessages.DELIMITER);

		char command = split[0].charAt(0); // convert to char
		String param1 = null;
		String param2 = null;
		String param3 = null;

		if (split.length > 1) {
			param1 = split[1]; // [0] is command
		}
		if (split.length > 2) {
			param2 = split[2];
		}
		if (split.length > 3) {
			param3 = split[3];
		}

		try {
		switch (command) {
		
		    case ProtocolMessages.HANDSHAKE:
			String requestedVersion = param1;
	    	String playerName = param2;
			String requestColor = param3;
		    	
			this.remotePlayer = createRemotePlayer(playerName);	
			this.sendMessage(srv.respondHandshake(this));
			
			srv.checkWaitingList();
		    	break;
		    	
		    case ProtocolMessages.MOVE: // TODO: this handled in requestmove?
		    	 // do something with move
		    	this.chosenMove = param1;
		    	
		    	// TODO check if turn? (will do nothing if not in move function, only set next move
		    	//this.chosenMove.notifyAll(); // wake waiting requestMove
		    	
		    	break;
//		    	
//		    	
//		    	// TODO validation
//		    	String board = this.remotePlayer.getGame().getGameBoard().toString();
//		    	
//		    	String result = ProtocolMessages.RESULT + ProtocolMessages.DELIMITER 
//		    	+ ProtocolMessages.VALID + ProtocolMessages.DELIMITER + board;
//		    	this.sendMessage(result);
//		    	break;
    
		    case ProtocolMessages.QUIT:
		    	this.getRemotePlayer().getGame().endGame(ProtocolMessages.EXIT,
		    			this.getRemotePlayer().getColour().print());
		    	this.shutdown();
		    	break;
		    
		    	
    		default:
    			System.out.println("DEBUG I don't understand this command, try again"); //TODO sent to system out
    			// and send invalid
    			this.sendMessage(ProtocolMessages.ERROR + ProtocolMessages.DELIMITER + "unkown command" );
		}
		} catch (ClientUnavailableException e) {
			System.out.println("Error while communicating with client: " + e.getLocalizedMessage()); // TODO where to send this to? 
			e.printStackTrace();
			// TODO not crahs when cleint disconnect stream closed
		}
	}

	
	/** 
	 * 
	 */
	public Player createRemotePlayer(String name) {
		// TODO LET IT ASK FOR COLOUR
		
		Stone colour = Stone.BLACK;
		
		this.remotePlayer = new RemotePlayer(name, colour, this);
		return this.remotePlayer;
	}
	
	/**
	 * Shut down the connection to this client by closing the socket and 
	 * the In- and OutputStreams.
	 */
	private void shutdown() {
		System.out.println("> [" + clientName + "] Shutting down.");
		try {
			in.close();
			out.close();
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		srv.removeClient(this);
	}

	public int requestMove(int previousMove) throws TimeOutException { // TODO synchronized ?! (this.move concurrent)
		String board = this.remotePlayer.getGame().getGameBoard().toString();
		String opponentsLastMove = null;
		
		if (previousMove == GoGameConstants.PASSint) {
		 opponentsLastMove = "P"; // TODO also implement
		} else if(previousMove == GoGameConstants.NOMOVEint) {
			 opponentsLastMove = null; // TODO also implement
		} else {
			 opponentsLastMove = String.valueOf(previousMove); // TODO also implement
		}
		

		//String moveAnswer = null;
		int move = -2; // TODO think of default
		Boolean answerValid = false;

		while (!answerValid) { // keep asking till valid integer
// TODO implement pass
			try {
				this.sendMessage(ProtocolMessages.TURN + ProtocolMessages.DELIMITER 
						+ board + ProtocolMessages.DELIMITER + opponentsLastMove);
			} catch (ClientUnavailableException e) {
				System.out.println("Error while communicating with client: " + e.getLocalizedMessage()); // TODO where to send this to? 
				e.printStackTrace();
			}

			//			try {
			//				chosenMove.wait();
			//			} catch (InterruptedException e) {
			//				// TODO Auto-generated catch block
			//				e.printStackTrace();
			//			}
			//while (this.chosenMove == null) {
//				String msg;
//				try {
//					msg = in.readLine();
//					handleIncoming(msg); // TODO use handleIncoming to enable other commands
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				
			//}
			
			int secondsToWait = 60; // TODO: 30 sec timer, only for remote players
			long endWaitTime = System.currentTimeMillis() + secondsToWait*1000;
	        while ( (this.chosenMove == null)) { // System.currentTimeMillis() < endWaitTime &&
	                if (System.currentTimeMillis() >= endWaitTime ) {
	    	            throw new TimeOutException("TIME OUT: move not inside 8 sec");
	                } else {
	            	
	            	try {
						Thread.sleep(1000); // TODO updating every sec
						
						long remaining = endWaitTime - System.currentTimeMillis();
	        System.out.println("DEBUG: [" + clientName + "] = waiting for move, remaining millisec: " + remaining);

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	                }
	            }
	        
			
			
			
//			long startTime = System.currentTimeMillis();
//			int timeout = 30000; // milliseconds
//			while (this.chosenMove == null) {
//			long remaining = System.currentTimeMillis() - startTime - timeout;
//	        if (remaining < 0) {
//	            throw new TimeOutException("TIME OUT: move not inside 3 sec");
//	        }
//	        System.out.println("DEBUG: waiting for move, remaining millisec: " + remaining);
////	        try {
////				this.chosenMove.wait(remaining);
////			} catch (InterruptedException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			}
//			}

			try {
				//moveAnswer = in.readLine();
				move = Integer.parseInt(this.chosenMove);
				answerValid = true;
			} catch (NumberFormatException eFormat) {
				if (this.chosenMove.equalsIgnoreCase(String.valueOf(ProtocolMessages.PASS))) {
					move = GoGameConstants.PASSint;
					answerValid = true;
				} else {
				System.out.println("DEBUG: answer not a int"); //TODO handle this elegantly
				//		this.showMessage("ERROR> " + answer +  " is not an integer (" 
				//				+ eFormat.getLocalizedMessage() + ") try again!");
				}
			}
		}
		// TODO validation
		this.chosenMove = null;
		return move;
	}
	
	
	public void resultMove(char result, Board board) {
	// TODO implement
	String resultMessage = ProtocolMessages.RESULT + ProtocolMessages.DELIMITER 
			+ result + ProtocolMessages.DELIMITER + board;
	// TODO check if result confirms to procotol messages
	try {
		this.sendMessage(resultMessage);
	} catch (ClientUnavailableException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	
	
	/**
	 * TODO doc
	 */
	public void clientStartGame( ) {
		
		String startGame = ProtocolMessages.GAME + ProtocolMessages.DELIMITER 
				+ this.getRemotePlayer().getGame().getGameBoard() + ProtocolMessages.DELIMITER
				+ this.getRemotePlayer().getColour();
		try {
			this.sendMessage(startGame);
		} catch (ClientUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * TODO doc and place in file
	 * @param reason
	 */
	public void clientEndGame(char reason, char winner, double scoreBlack, double scoreWhite) {
		String endGame = ProtocolMessages.END + ProtocolMessages.DELIMITER 
				+ reason + ProtocolMessages.DELIMITER
				+ winner + ProtocolMessages.DELIMITER 
				+ scoreBlack + ProtocolMessages.DELIMITER 
				+ scoreWhite;

		try {
			this.sendMessage(endGame);
		} catch (ClientUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Sends a message to the connected client, followed by a new line. 
	 * The stream is then flushed.
	 * 
	 * @param msg the message to write to the OutputStream.
	 * @throws ClientUnavailableException if IO errors occur.
	 */
	public synchronized void sendMessage(String msg) 
			throws ClientUnavailableException {
		if (out != null) {
			try {
				out.write(msg);
				out.newLine();
				out.flush();
				if (printDebug) System.out.println("Handler [" + clientName + "] send: " + msg);
			} catch (IOException e) {
				System.out.println(e.getMessage());
				throw new ClientUnavailableException("Could not write "
						+ "to client: IO esception > " + e.getLocalizedMessage());
			}
		} else {
			throw new ClientUnavailableException("Could not write "
					+ "to client: no out writer");
		}
	}

	public Player getRemotePlayer() {
		return remotePlayer;
	}

	public String getClientName() {
		return this.clientName;
	}

}
