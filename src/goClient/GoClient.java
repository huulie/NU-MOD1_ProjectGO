package goClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

import com.nedap.go.gui.GoGuiIntegrator;

import exceptions.ExitProgram;
import exceptions.ProtocolException;
import exceptions.ServerUnavailableException;
import goGame.GoLocalTUI;
import goProtocol.ProtocolMessages;
import goUI.GoGuiUpdater;
import goUI.GoTUICommands;

/**
 * Client for Networked Go Game
 * 
 * @author Huub Lievestro
 */
public class GoClient { //implements ClientProtocol {
	
	private Socket serverSock;
	private BufferedReader in;
	private BufferedWriter out;
	
	/**
	 * Setting to keep connecting if game ended //TODO refine
	 */
	private boolean keepConnecting;
	
	
	/**
	 * Name of this client
	 */
	private String clientName;
	
	/**
	 * Version of communication protocol this client is currently using.
	 */
	String protocolVersion = null;
	
	private GoLocalTUI TUI;
	
	private String serverResponseMarker = "> ";
	private String localErrorMarker = "!ERROR: ";
	
	/**
	 * Boolean to indicate if this client is waiting or participating in a started game
	 */
	boolean gameStarted;
	
	/**
	 * Color of the client in the current game
	 */
	private String playColour; // TODO change to Stone (and add conversion)
	
	/**
	 * Local model of the board // TODO: convert from STring to Board
	 */
	private String localBoard;

	/** 
	 * TODO: keep? then doc!
	 */
	private boolean printDebug = true;
	
	
	/** 
	 * Associated GUI of this client.
	 */
	private GoGuiIntegrator GUI;

	/** 
	 * To update the associated GUI of this client.
	 */
	private GoGuiUpdater GUIupdater;

	/** 
	 * Indicates if the board has to be sent to the TUI
	 * TODO what to do with this? 
	 */
	private boolean outputBoardToTUI;
	
	/**
	 * Constructs a new GoClient. Initialises the TUI.
	 */
	public GoClient() {
		this.TUI = new GoLocalTUI();
		this.clientName = TUI.getString("What is your name?");
		this.gameStarted = false;
		this.keepConnecting = true;
	}

	public String getClientName() {
		return clientName;
	}

	/**
	 * Starts a new GoClient by creating a connection, followed by the 
	 * handshake as defined in the protocol. TODO After a successful 
	 * connection and handshake, the view is started. The view asks for 
	 * used input and handles all further calls to methods of this class. 
	 * 
	 * When errors occur, or when the user terminates a server connection, the
	 * user is asked whether a new connection should be made.
	 */
	public void start() {
		
		while(keepConnecting){
		try {
			this.createConnection();
			this.sendHandshake();
			
			// TUI.start(); TODO no start TUI because locking thread?!!
			
			this.waitForStartGame();
			this.playingGame();
			
			// TODO: connect again? refine?
			
		} catch (ServerUnavailableException e) {
			TUI.showMessage("Server unavailable!");
			TUI.showMessage("Error while communicating with server: " + e.getLocalizedMessage());
			e.printStackTrace();
		} catch (ExitProgram e) { // from create connection
			TUI.showMessage("CLIENT EXIT");
			this.keepConnecting = false;
			e.printStackTrace();
		} catch (ProtocolException e) { // from handle hello
			TUI.showMessage("Protocol exception:" + e.getLocalizedMessage());
			e.printStackTrace();
		}
		}
	}

	

	/**
	 * Creates a connection to the server. Requests the IP and port to 
	 * connect to at the view (TUI).
	 * 
	 * The method continues to ask for an IP and port and attempts to connect 
	 * until a connection is established or until the user indicates to exit 
	 * the program.
	 * 
	 * @throws ExitProgram if a connection is not established and the user 
	 * 				       indicates to want to exit the program.
	 * @ensures serverSock contains a valid socket connection to a server
	 */
	public void createConnection() throws ExitProgram {
		clearConnection();
		while (serverSock == null) {
			InetAddress addr = TUI.getIp("Please enter the server IP:");
			int port = TUI.getInt("Please enter the server port:");

			// try to open a Socket to the server
			try {
				System.out.println("Attempting to connect to " + addr + ":" 
					+ port + "...");
				serverSock = new Socket(addr, port);
				in = new BufferedReader(new InputStreamReader(
						serverSock.getInputStream()));
				out = new BufferedWriter(new OutputStreamWriter(
						serverSock.getOutputStream()));
			} catch (IOException e) {
				System.out.println("ERROR: could not create a socket on " 
					+ addr + " and port " + port + ".");
				if(!TUI.getBoolean("Do you want to try again?")) {
					throw new ExitProgram("User indicated to exit.");
				}
			}
		}
	}

	/**
	 * Resets the serverSocket and In- and OutputStreams to null.
	 * 
	 * Always make sure to close current connections via shutdown() 
	 * before calling this method!
	 */
	public void clearConnection() {
		serverSock = null;
		in = null;
		out = null;
	}

	/**
	 * Sends a message to the connected server, followed by a new line. 
	 * The stream is then flushed.
	 * 
	 * @param msg the message to write to the OutputStream.
	 * @throws ServerUnavailableException if IO errors occur.
	 */
	public synchronized void sendMessage(String msg) 
			throws ServerUnavailableException {
		if (out != null) {
			try {
				out.write(msg);
				out.newLine();
				out.flush();
			} catch (IOException e) {
				System.out.println(e.getMessage());
				throw new ServerUnavailableException("Could not write "
						+ "to server.");
			}
		} else {
			throw new ServerUnavailableException("Could not write "
					+ "to server.");
		}
	}

	/**
	 * Reads and returns one line from the server.
	 * 
	 * @return the line sent by the server.
	 * @throws ServerUnavailableException if IO errors occur.
	 */
	public String readLineFromServer() 
			throws ServerUnavailableException {
		if (in != null) {
			try {
				// Read and return answer from Server
				String answer = in.readLine();
				if (answer == null) {
					throw new ServerUnavailableException("Could not read "
							+ "from server.");
				}
				return answer;
			} catch (IOException e) {
				throw new ServerUnavailableException("Could not read "
						+ "from server.");
			}
		} else {
			throw new ServerUnavailableException("Could not read "
					+ "from server.");
		}
	}

//	/**
//	 * Reads and returns multiple lines from the server until the end of 
//	 * the text is indicated using a line containing ProtocolMessages.EOT.
//	 * 
//	 * @return the concatenated lines sent by the server.
//	 * @throws ServerUnavailableException if IO errors occur.
//	 */
//	public String readMultipleLinesFromServer() 
//			throws ServerUnavailableException {
//		if (in != null) {
//			try {
//				// Read and return answer from Server
//				StringBuilder sb = new StringBuilder();
//				for (String line = in.readLine(); line != null
//						&& !line.equals(ProtocolMessages.EOT); 
//						line = in.readLine()) {
//					sb.append(line + System.lineSeparator());
//				}
//				return sb.toString();
//			} catch (IOException e) {
//				throw new ServerUnavailableException("Could not read "
//						+ "from server.");
//			}
//		} else {
//			throw new ServerUnavailableException("Could not read "
//					+ "from server.");
//		}
//	}

	/**
	 * Closes the connection by closing the In- and OutputStreams, as 
	 * well as the serverSocket.
	 */
	public void closeConnection() {
		System.out.println("Closing the connection...");
		try {
			in.close();
			out.close();
			serverSock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Handles the following server-client handshake: 1. Client sends
	 * ProtocolMessages.HELLO to server 2. Server returns one line containing
	 * ProtocolMessages.HELLO + ProtocolMessages.DELIMITER + (hotelName)
	 * 
	 * This method sends the HELLO and checks whether the server response is valid
	 * (must contain HELLO and the name of the hotel). - If the response is not
	 * valid, this method throws a ProtocolException. - If the response is valid, a
	 * welcome message including the hotel name is forwarded to the view.
	 * 
	 * @throws ServerUnavailableException if IO errors occur.
	 * @throws ProtocolException          if the server response is invalid.
	 */
	public void sendHandshake() 
			throws ServerUnavailableException, ProtocolException {
		
		String requestedVersion = "1.0";
		String requestedColour = TUI.getString("Do you want " + ProtocolMessages.BLACK + " or " + ProtocolMessages.WHITE +"?"); // TODO make a "get colour" 
		
		String handshake = ProtocolMessages.HANDSHAKE + ProtocolMessages.DELIMITER 
				+ requestedVersion + ProtocolMessages.DELIMITER + this.getClientName()
				+ ProtocolMessages.DELIMITER + requestedColour;
		
		String handshakeServerMessage;
		
		this.sendMessage(handshake);
		
		String handshakeResponse = this.readLineFromServer();
		
		if (handshakeResponse != null && !handshakeResponse.equalsIgnoreCase("")) {
			String[] splitHandshakeResponse = handshakeResponse.split(ProtocolMessages.DELIMITER);
			if (splitHandshakeResponse.length == 3) {
				if (splitHandshakeResponse[0].equals(String.valueOf(ProtocolMessages.HANDSHAKE))) {
					 this.protocolVersion = splitHandshakeResponse[1];
					 handshakeServerMessage = splitHandshakeResponse[2];
				} else {
					throw new ProtocolException("Handshake failed: server did not reply correctly (was " + handshakeResponse + " )");
				}
			} else {
				throw new ProtocolException("Handshake failed: Wrong number of arguments (" 
			+ splitHandshakeResponse.length + " instead of 2) in response from server");
			}
		} else {
			throw new ProtocolException("Handshake failed: Empty response from server");
		}
		// System.out.println("DEBUG" + Welcome to the Hotel booking system of hotel: " + hotelName);
		TUI.showMessage("Connected to the Go game server: " + handshakeServerMessage);
	}
	
	/**
	 * 
	 */
	public void waitForStartGame() {
		TUI.showMessage("Waiting for a game to begin...");

		while (this.gameStarted == false) {
			String serverStarts = null;
			try {
				serverStarts = this.readLineFromServer();
			} catch (ServerUnavailableException e) {
				TUI.showMessage("Error while reading from server: " + e.getLocalizedMessage());
				e.printStackTrace();
			}
			String[] splitServerStarts = serverStarts.split(ProtocolMessages.DELIMITER);
			if(splitServerStarts[0].equals(String.valueOf(ProtocolMessages.GAME))) {
				this.localBoard = splitServerStarts[1]; // TODO convert from String to board
				this.playColour = splitServerStarts[2];
				this.gameStarted = true;
				TUI.showMessage("... game started!");
			}
		}
	}
	
	private void playingGame() {
		int boardDim = 19; // TODO derive from sent board?!
		
		boolean startGUI = this.TUI.getBoolean("Start a GUI for this client? [true] or [false]");
		if (startGUI) {
			this.TUI.showMessage("Player " + clientName + " will use a GUI");
			this.GUI = new GoGuiIntegrator(true, true, boardDim);
			this.GUI.startGUI();
			this.GUI.setBoardSize(boardDim);
			this.GUIupdater = new GoGuiUpdater(this.GUI);
			this.outputBoardToTUI = false;
		} else {
			this.TUI.showMessage("Player " + clientName + " will use a TUI");
			this.outputBoardToTUI = true;
		}
		
		while (gameStarted == true) {
			TUI.showMessage("Waiting for response from server..");
			String serverResponse = null;

			try {
				serverResponse = this.readLineFromServer();
				if (printDebug) TUI.showMessage("DEBUG server sends: " + serverResponse);

				String[] splitServerResponse = serverResponse.split(ProtocolMessages.DELIMITER);
				
				switch(splitServerResponse[0].charAt(0)) {
				
				case ProtocolMessages.TURN:
					this.localBoard = splitServerResponse[1]; // TODO convert from String to board
					if (this.GUI != null) {
						this.GUIupdater.updateWholeBoard(this.localBoard);
						}
					
					String opponentLastMove = splitServerResponse[2];			

					String makeMove = ProtocolMessages.MOVE + ProtocolMessages.DELIMITER
							+ TUI.getMove("What is your move? (index or " + GoTUICommands.PASS + " to PASS)");
					this.sendMessage(makeMove);
					break;
					
				case ProtocolMessages.RESULT:
					String valid = splitServerResponse[1];
					this.localBoard = splitServerResponse[2];
					if (this.GUI != null) {
					this.GUIupdater.updateWholeBoard(this.localBoard);
					}
					break;
				
				case ProtocolMessages.END:
					String reasonEnd = splitServerResponse[1];
					String winner = splitServerResponse[2];
					String scoreBlack = splitServerResponse[3];
					String scoreWhite = splitServerResponse[4];
					TUI.showMessage("This game has ended [DEBUG]");
					this.gameStarted = false;
					
				case ProtocolMessages.ERROR:	
					throw new ProtocolException("Invalid message received by server");
			}	

			} catch (ServerUnavailableException e) {
				TUI.showMessage("Error while communicating with server: " + e.getLocalizedMessage());
				e.printStackTrace();
			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
//	/**
//	 * Sends a checkIn request to the server.
//	 * 
//	 * Given the name of a guest, the doIn() method sends the following message to
//	 * the server: ProtocolMessages.IN + ProtocolMessages.DELIMITER + guestName
//	 * 
//	 * The result (one line) is then retrieved and forwarded to the view.
//	 * 
//	 * @requires guestName != null
//	 * @param guestName Name of the guest
//	 * @throws ServerUnavailableException if IO errors occur.
//	 */
//	public void doIn(String guestName) throws ServerUnavailableException {
//        assert guestName != null : "guestName may not be null";
//		
//        this.sendMessage(ProtocolMessages.IN + ProtocolMessages.DELIMITER + guestName);
//        
//		String doInResponse = this.readLineFromServer();
//		
//		// System.out.println("DEBUG" + serverResponseMarker + doInResponse);
//		TUI.showMessage(serverResponseMarker + doInResponse);
//
//	}



	/**
	 * Sends a message to the server indicating that this client will exit:
	 * ProtocolMessages.EXIT;
	 * 
	 * Both the server and the client then close the connection. The client does
	 * this using the {@link #closeConnection()} method.
	 * 
	 * @throws ServerUnavailableException if IO errors occur.
	 */
	public void sendExit() throws ServerUnavailableException {
		this.sendMessage(String.valueOf(ProtocolMessages.EXIT));
		
		this.closeConnection();
	}
	
//	/**
//	 * Requests name of hotel and network information, to show in help
//	 * 
//	 * The doHelp() method sends the following message to the server:
//	 * ProtocolMessages.HELP
//	 * 
//	 * The result (one line) is retrieved and forwarded to the view
//	 * 
//	 * @throws ServerUnavailableException if IO errors occur.
//	 */
//	// @Override
//	public void doHelp() throws ServerUnavailableException {
//		this.sendMessage(String.valueOf(ProtocolMessages.));
//      
//		String doPrintResponse = this.readLineFromServer();
//		
//		// System.out.println("DEBUG" + serverResponseMarker + doPrintResponse);
//		TUI.showMessage(serverResponseMarker + doPrintResponse);
//	}

	/**
	 * This method starts a new GoClient.
	 * 
	 * @param args 
	 */
	public static void main(String[] args) {
		(new GoClient()).start();
	}

}
