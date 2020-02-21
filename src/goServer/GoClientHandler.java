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
	 * Associated Remote Player, taking part in the game.
	 */
	Player remotePlayer;
	
	/** 
	 * Setting to print debug messages.
	 */
	private boolean printDebug = true;
	
	/**
	 * Chosen move from client.
	 */
	private String chosenMove = null;

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
				if (printDebug) System.out.println("> [" + clientName + "] Incoming: " + msg);
				handleIncoming(msg);
				
				msg = in.readLine();
			}
			
			if (printDebug) System.out.println("DEBUG: message was null ");
			System.out.println("TERMINATING CLIENT HANDLER [" + clientName + "]");
			if (this.getRemotePlayer().getGame() != null) {
				this.getRemotePlayer().getGame()
					.endGame(GoGameConstants.DISCONNECT,
							this.getRemotePlayer().getColour().print());
			}
			shutdown();
		} catch (IOException e) {
			if (printDebug) System.out.println("DEBUG: IO exception: " + e.getLocalizedMessage());
			System.out.println("TERMINATING CLIENT HANDLER [" + clientName + "]");
			if (this.getRemotePlayer().getGame() != null) {
				this.getRemotePlayer().getGame()
					.endGame(GoGameConstants.DISCONNECT,
							this.getRemotePlayer().getColour().print());
			}
			shutdown();
		}
	}

	/**
	 * Handles commands received from the client by calling the according 
	 * methods at the RemotePlayer and/or GoServer. 
	 * 
	 * If the received input is not valid according to the protocol,
	 *  send an "Unknown Command" message to the Client.
	 * 
	 * @param msg command from client
	 * @throws IOException if an IO errors occur.
	 */
	private void handleIncoming(String msg) throws IOException {
		String[] split = msg.split(ProtocolMessages.DELIMITER);

		char command = split[0].charAt(0);
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
					String requestedVersion = param1; //TODO something with requested version: fixed
					String playerName = param2;
					String requestColour = param3; //TODO something with requested colour: now fixed
					
					this.remotePlayer = createRemotePlayer(playerName);	
					this.sendMessage(srv.respondHandshake(this));
					
					srv.checkWaitingList();
					break;

				case ProtocolMessages.MOVE: 
					this.chosenMove = param1;
					break;	    	

				case ProtocolMessages.QUIT:
					this.getRemotePlayer().getGame().endGame(ProtocolMessages.EXIT,
						this.getRemotePlayer().getColour().print());
					this.shutdown();
					break;

				default:
					if (printDebug) System.out.println("DEBUG I don't understand this command, try again");
					this.sendMessage(ProtocolMessages.ERROR + ProtocolMessages.DELIMITER 
							+ "unknown command");
			}
		} catch (ClientUnavailableException e) {
			if (printDebug) System.out.println("Error while communicating with client: " + e.getLocalizedMessage());
			System.out.println("TERMINATING CLIENT HANDLER [" + clientName + "]");
			if (this.getRemotePlayer().getGame() != null) {
				this.getRemotePlayer().getGame()
				.endGame(GoGameConstants.DISCONNECT, this.getRemotePlayer().getColour().print());
			}
			shutdown();
		}
	}
	
	/** 
	 * Creates a new Remote Player, with a yet unknown stone colour (will be set at start game).
	 */
	public Player createRemotePlayer(String name) {
		Stone colour = null;
		
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

	public int requestMove(int previousMove) throws TimeOutException {
		String board = this.remotePlayer.getGame().getGameBoard().toString();
		String opponentsLastMove = null;
		
		if (previousMove == GoGameConstants.PASSint) {
			opponentsLastMove = "P"; 
		} else if (previousMove == GoGameConstants.NOMOVEint) {
			opponentsLastMove = null; 
		} else {
			opponentsLastMove = String.valueOf(previousMove);
		}
		
		int move = GoGameConstants.NOMOVEint;
		Boolean answerValid = false;

		while (!answerValid) { 
			try {
				this.sendMessage(ProtocolMessages.TURN + ProtocolMessages.DELIMITER 
						+ board + ProtocolMessages.DELIMITER + opponentsLastMove);
			} catch (ClientUnavailableException e) {
				if (printDebug) System.out.println("Error while communicating with client: " + e.getLocalizedMessage());
				System.out.println("TERMINATING CLIENT HANDLER [" + clientName + "]");
				if (this.getRemotePlayer().getGame() != null) {
					this.getRemotePlayer().getGame()
						.endGame(GoGameConstants.DISCONNECT, 
							this.getRemotePlayer().getColour().print());
				}
				shutdown();
			}
			
			int secondsToWait = 60; // hardcoded from protocol: 60 sec timer
			long endWaitTime = System.currentTimeMillis() + secondsToWait * 1000;
	        while (this.chosenMove == null) { 
	        	if (this.getRemotePlayer().getGame().isGameOver()) {
	        		if (printDebug) System.out.println("DEBUG: [" + clientName 
	        				+ "] = waiting for move interrupted: GAME OVER");
	        		return GoGameConstants.PASSint; // to avoid further changes and not break makemove
	        	} else if (System.currentTimeMillis() >= endWaitTime ) {
	        		throw new TimeOutException("TIME OUT: move not inside 60 sec");
	        	} else {
	            	try {
						Thread.sleep(1000); // updating every sec
						long remaining = endWaitTime - System.currentTimeMillis();
						if (printDebug) System.out.println("DEBUG: [" + clientName 
								+ "] = waiting for move, remaining millisec: " 
						+ remaining);
	            	} catch (InterruptedException e) {
	            		e.printStackTrace();
	            	}
	        	}
	        }

			try {
				move = Integer.parseInt(this.chosenMove);
				answerValid = true;
			} catch (NumberFormatException eFormat) {
				if (this.chosenMove.equalsIgnoreCase(String.valueOf(ProtocolMessages.PASS))) {
					move = GoGameConstants.PASSint;
					answerValid = true;
				} else {
				System.out.println("DEBUG: answer not a int"); //TODO handle this elegantly
				}
			}
		}
		this.chosenMove = null;
		return move;
	}
	
	public void resultMove(char result, String boardOrMessage) {
		String resultMessage = ProtocolMessages.RESULT + ProtocolMessages.DELIMITER 
				+ result + ProtocolMessages.DELIMITER + boardOrMessage;
		try {
			this.sendMessage(resultMessage);
		} catch (ClientUnavailableException e) {
			if (printDebug) System.out.println("Error while communicating with client: " + e.getLocalizedMessage());
			System.out.println("TERMINATING CLIENT HANDLER [" + clientName + "]");
			if (this.getRemotePlayer().getGame() != null) {
				this.getRemotePlayer().getGame()
				.endGame(GoGameConstants.DISCONNECT, this.getRemotePlayer().getColour().print());
			}
			shutdown();
		}
	}
	
	/**
	 * Signal remote client that Game has started.
	 */
	public void clientStartGame() {
		String startGame = ProtocolMessages.GAME + ProtocolMessages.DELIMITER 
				+ this.getRemotePlayer().getGame().getGameBoard() + ProtocolMessages.DELIMITER
				+ this.getRemotePlayer().getColour();
		try {
			this.sendMessage(startGame);
		} catch (ClientUnavailableException e) {
			if (printDebug) System.out.println("Error while communicating with client: " + e.getLocalizedMessage());
			System.out.println("TERMINATING CLIENT HANDLER [" + clientName + "]");
			if (this.getRemotePlayer().getGame() != null) {
				this.getRemotePlayer().getGame()
				.endGame(GoGameConstants.DISCONNECT, this.getRemotePlayer().getColour().print());
			}
			shutdown();
		}
	}
	
	/**
	 * Signal remote client that Game has ended.
	 * @param reason for ending
	 * @param winner BLACK or WHITE
	 * @param scoreBlack score of BLACK player
	 * @param scoreWhite score of WHITE player
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
			if (printDebug) System.out.println("Error while communicating with client: " + e.getLocalizedMessage());
			System.out.println("TERMINATING CLIENT HANDLER [" + clientName + "]");
			// do NOT try to end game again
			shutdown();
		}
		
	}
	
	/**
	 * Sends a message to the connected client, followed by a new line. 
	 * The stream is then flushed.
	 * 
	 * @param msg the message to write to the OutputStream.
	 * @throws ClientUnavailableException if IO errors occur.
	 */
	public synchronized void sendMessage(String msg) throws ClientUnavailableException {
		if (out != null) {
			try {
				out.write(msg);
				out.newLine();
				out.flush();
				if (printDebug) System.out.println("Handler [" + clientName + "] send: " + msg);
			} catch (IOException e) {
				throw new ClientUnavailableException("Could not write "
						+ "to client [" + clientName + "]: IO exception > " 
						+ e.getLocalizedMessage());
			}
		} else {
			throw new ClientUnavailableException("Could not write "
					+ "to client [" + clientName + "]: no out writer");
		}
	}

	public Player getRemotePlayer() {
		return remotePlayer;
	}

	public String getClientName() {
		return this.clientName;
	}

}
