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
import goComputerAI.GoComputerTUI;
import goGame.Board;
import goGame.GoGameConstants;
import goGame.GoLocalTUI;
import goGame.Stone;
import goProtocol.ProtocolMessages;
import goUI.GoGuiUpdater;
import goUI.GoTUI;
import goUI.GoTUICommands;

// To play MP3
import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Client for Networked Go Game.
 * 
 * @author Huub Lievestro
 */
public class GoClient {

	private Socket serverSock;
	private BufferedReader in;
	private BufferedWriter out;

	/**
	 * Setting to keep connecting if game ended.
	 */
	private boolean keepConnecting;


	/**
	 * Name of this client.
	 */
	private String clientName;

	/**
	 * Version of communication protocol this client is currently using.
	 */
	String protocolVersion = null;

	/**
	 * TUI used by this Client, may be local or ComputerAI.
	 */
	private GoTUI TUI;

	/**
	 * Boolean to indicate if this client is waiting or participating in a started game.
	 */
	boolean gameStarted;

	/**
	 * Colour of the client in the current game.
	 */
	private Stone playColour; 

	/**
	 * Local model of the board.
	 * Note: has NO (direct) association to the board in the Game
	 */
	private Board localBoard;

	/**
	 * Dimension of the Board, as derived from the received board (square root).
	 */
	private int localBoardDim;

	/** 
	 * Setting to print debugging information.
	 */
	private boolean printDebug = true;


	/**
	 * Setting to start a GUI.
	 */
	boolean startGUI;

	/** 
	 * Associated GUI of this client.
	 */
	private GoGuiIntegrator GUI;

	/** 
	 * To update the associated GUI of this client.
	 */
	private GoGuiUpdater GUIupdater;

	/** 
	 * Indicates if the board has to be sent to the TUI.
	 * TODO do something with this setting? 
	 */
	private boolean outputBoardToTUI;

	/**
	 * Setting to start the background music.
	 */
	boolean startBackgroundMusic;

	/**
	 * Path to the background music.
	 */
	private String backgroundMusicPath = "resources/InstrumentalAsianMusic.mp3";

	/**
	 * Media object of the background music.
	 */
	private Media backgroundMusicMedia; 

	/**
	 * Player object of the background music.
	 */
	private MediaPlayer mediaPlayer;

	/**
	 * Last move of the opponent, as send by the server.
	 */
	private String opponentLastMove; 


	/**
	 * Constructs a new GoClient. Initialises the TUI, and asks some other settings.
	 */
	public GoClient() {
		this.TUI = new GoLocalTUI();
		this.clientName = TUI.getString("What is your name?");
		this.gameStarted = false;
		this.keepConnecting = true;
		this.startGUI = this.TUI.getBoolean("Start a GUI for this client? [true] or [false]");
		if (this.startGUI) {
			this.startBackgroundMusic = 
				this.TUI.getBoolean("Start background music for this client? [true] or [false]");
		} else {
			this.startBackgroundMusic = false;
		}

		if (this.TUI.getBoolean("Do you want the computer AI to play for you? [true] or [false]")) {
			this.TUI = new GoComputerTUI(this);
		}
	}

	public String getClientName() {
		return clientName;
	}

	public Stone getPlayColour() {
		return playColour;
	}

	public Board getLocalBoard() {
		return localBoard;
	}

	public String getOpponentLastMove() {
		return opponentLastMove;
	}

	/**
	 * Starts creating a connection, 
	 * followed by the handshake as defined in the protocol. 
	 * After a successful connection and handshake, the Client waits for the start of the game.
	 * Then the Client plays the game, till it is completed.
	 * If keepConnecting is true, the Client will then again try to start another connection 
	 * 
	 * When errors occur or when the user terminates the client, the client will terminate.
	 */
	public void start() {
		while (keepConnecting) {
			try {
				this.createConnection();
				this.sendHandshake();
				this.waitForStartGame();
				this.playingGame();

				TUI.showMessage("Going to connect again...");
				
			} catch (ServerUnavailableException e) {
				TUI.showMessage("Server unavailable!");
				TUI.showMessage("Error while communicating: " + e.getLocalizedMessage());
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
				if (!TUI.getBoolean("Do you want to try again?")) {
					throw new ExitProgram("User indicated to exit.");
				}
			}
		}
	}

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
	 * Resets the serverSocket and In- and OutputStreams to null.
	 * 
	 * Always make sure to close current connections via closeConnection() 
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
				if (printDebug) {
					TUI.showMessage("Client send: " + msg);
				}
			} catch (IOException e) {
				System.out.println(e.getMessage());
				throw new ServerUnavailableException("Could not write " + msg
						+ "to server.");
			}
		} else {
			throw new ServerUnavailableException("Could not write " + msg
					+ "to server.");
		}
	}

	/**
	 * Reads and returns one line from the server.
	 * 
	 * @return the line sent by the server.
	 * @throws ServerUnavailableException if IO errors occur.
	 */
	public String readLineFromServer() throws ServerUnavailableException {
		if (in != null) {
			try {
				// Read and return answer from Server
				String answer = null;

				answer = in.readLine();
				if (answer == null) {
					throw new ServerUnavailableException("Could not read "
							+ "from server.");
				}
				if (printDebug) { 
					TUI.showMessage("Client read: " + answer);
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

	/**
	 * Handles the client-server handshake, according to the protocol
	 * 
	 * This method sends the handshake and checks whether the server response is valid
	 * If the response is not valid, this method throws a ProtocolException. 
	 * 
	 * @throws ServerUnavailableException if IO errors occur.
	 * @throws ProtocolException          if the server response is invalid.
	 */
	public void sendHandshake() 
			throws ServerUnavailableException, ProtocolException {

		String requestedVersion = "1.0";
		String requestedColour = 
				TUI.getString("Do you want " + ProtocolMessages.BLACK 
						+ " or " + ProtocolMessages.WHITE + "?");

		String handshake = ProtocolMessages.HANDSHAKE + ProtocolMessages.DELIMITER 
				+ requestedVersion + ProtocolMessages.DELIMITER + this.getClientName()
				+ ProtocolMessages.DELIMITER + requestedColour;

		String handshakeServerMessage;

		this.sendMessage(handshake);

		String handshakeResponse = this.readLineFromServer();

		if (handshakeResponse != null && !handshakeResponse.equalsIgnoreCase("")) {
			String[] splitHandshakeResponse = handshakeResponse.split(ProtocolMessages.DELIMITER);
			if (splitHandshakeResponse[0].equals(String.valueOf(ProtocolMessages.HANDSHAKE))) {
				if (splitHandshakeResponse.length == 3) {
					this.protocolVersion = splitHandshakeResponse[1];
					handshakeServerMessage = splitHandshakeResponse[2];
					TUI.showMessage("Connected to the Go game server: " + handshakeServerMessage);
				} else if (splitHandshakeResponse.length == 2) {
					this.protocolVersion = splitHandshakeResponse[1];
					TUI.showMessage("Connected to the Go game server "
							+ "(which did not send a welcome message, but be welcome anyway!)");
				} else {
					throw new ProtocolException("Handshake failed: Wrong number of arguments (" 
							+ splitHandshakeResponse.length 
							+ " instead of 2 or 3) in response from server");
				}
			} else {
				throw new ProtocolException("Handshake failed: server did not reply correctly "
						+ "(was " + handshakeResponse + " )");
			}
		} else {
			throw new ProtocolException("Handshake failed: Empty response from server");
		}
	}

	/**
	 * This client is waiting for the Server to start a Game.
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
			if (splitServerStarts[0].equals(String.valueOf(ProtocolMessages.GAME))) {
				String localBoardString = splitServerStarts[1];

				this.localBoard = Board.newBoardFromString(localBoardString);

				char playColourChar = splitServerStarts[2].charAt(0);
				this.playColour = Stone.charToStone(playColourChar);

				this.localBoardDim = (int) Math.sqrt(localBoardString.length());
				this.gameStarted = true;
				TUI.showMessage("... game started!");
			}
		}
	}

	private void playingGame() {
		if (this.startGUI) {
			this.TUI.showMessage("Player " + clientName + " will use a GUI");
			this.GUI = new GoGuiIntegrator(true, true, this.localBoardDim);
			this.GUI.startGUI();
			this.GUI.setBoardSize(this.localBoardDim);
			this.GUIupdater = new GoGuiUpdater(this.GUI);
			this.outputBoardToTUI = false;

			if (this.startBackgroundMusic) {
// TODO starting mediaplayer in separate thread, but should not be necessary
//				Thread musicThread = new Thread(new Runnable() {
//					String bip = "resources/InstrumentalAsianMusic.mp3";
//					Media hit = new Media(new File(bip).toURI().toString());
//					MediaPlayer mediaPlayer = new MediaPlayer(hit); // to avoid garbare collection
//					public void run() {
//						mediaPlayer.play();
//					}
//				});
//				musicThread.start();
				
				backgroundMusicMedia = new Media(new File(backgroundMusicPath).toURI().toString());
				mediaPlayer = new MediaPlayer(backgroundMusicMedia);		
				mediaPlayer.play();
				
				if (printDebug) {
					TUI.showMessage("Background music is played...");
				}
			}
		} else {
			this.TUI.showMessage("Player " + clientName + " will use a TUI");
			this.outputBoardToTUI = true;
		}

		// TODO this is actual playing, separate code above into "prepare to play"?
		while (gameStarted == true) {
			TUI.showMessage("Waiting for response from server..");
			String serverResponse = null;

			try {
				serverResponse = this.readLineFromServer();

				String[] splitServerResponse = serverResponse.split(ProtocolMessages.DELIMITER);

				switch (splitServerResponse[0].charAt(0)) {

					case ProtocolMessages.TURN:
						String localBoardStringTURN = splitServerResponse[1]; 
						this.localBoard =  Board.newBoardFromString(localBoardStringTURN);
						if (this.GUI != null) {
							this.GUIupdater.updateWholeBoard(this.localBoard);
							// TODO ook verder als GUI failt
						}

						this.opponentLastMove = splitServerResponse[2];
						if (this.GUI != null) {
							this.GUIupdater.setMarkerAtOpponent(opponentLastMove);
						} else {
							TUI.showMessage("Last move of opponent was: " + opponentLastMove);
						}

						String moveString = null;

						int getMove = TUI.getMove("What is your move? (index or " + GoTUICommands.PASS + " to PASS)");
						if (getMove == GoGameConstants.PASSint) {
							moveString = String.valueOf(ProtocolMessages.PASS);
						} else {
							moveString = String.valueOf(getMove);
						}

						String makeMove = ProtocolMessages.MOVE + ProtocolMessages.DELIMITER
								+ moveString;
						this.sendMessage(makeMove); // TODO check validity before sending
						break;

					case ProtocolMessages.RESULT:
						String valid = splitServerResponse[1];

						if (valid.contains(String.valueOf(ProtocolMessages.INVALID))) {
							TUI.showMessage("Idiot, you did sent an invalid move!");
						}

						String localBoardStringRESULT = splitServerResponse[2];
						this.localBoard = Board.newBoardFromString(localBoardStringRESULT);
						if (this.GUI != null) {
							this.GUIupdater.updateWholeBoard(this.localBoard);
						}
						break;

					case ProtocolMessages.END:
						char reasonEnd = splitServerResponse[1].charAt(0);
						String winner = splitServerResponse[2];
						String scoreBlack = splitServerResponse[3];
						String scoreWhite = splitServerResponse[4];
						this.displayGameResult(reasonEnd, winner, scoreBlack, scoreWhite);
						this.gameStarted = false;
						break;

					case ProtocolMessages.ERROR:	
						throw new ProtocolException("Invalid message received by server");

					// TODO: add default?
				}	

			} catch (ServerUnavailableException e) {
				TUI.showMessage("Error while communicating: " + e.getLocalizedMessage());
				e.printStackTrace();
			} catch (ProtocolException e) {
				TUI.showMessage("Protocol violation: " + e.getLocalizedMessage());
				e.printStackTrace();
			}
		}
	}

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
		this.clearConnection();
	}

	/**
	 * Displays the game result to the TUI.
	 */
	public void displayGameResult(char reasonEnd, 
			String winner, String scoreBlack, String scoreWhite) {
		TUI.showMessage(" -- This game has ended -- ");
		switch (reasonEnd) { 
			case GoGameConstants.FINISHED:
				TUI.showMessage(GoGameConstants.FINISHEDdescription);
				break;

			case GoGameConstants.CHEAT:
				TUI.showMessage(GoGameConstants.CHEATdescription);
				break;

			case GoGameConstants.DISCONNECT:
				TUI.showMessage(GoGameConstants.DISCONNECTdescription);
				break;

			case GoGameConstants.EXIT:
				TUI.showMessage(GoGameConstants.EXITdescription);
				break;

			default:
				TUI.showMessage("Something unexpected happend");
				break;
		}
		TUI.showMessage(" ------------------------ ");
		TUI.showMessage("Black has scored: " + scoreBlack); 
		TUI.showMessage("White has scored: " + scoreWhite);	
		TUI.showMessage("The winner is: " + winner);	
	}

	/**
	 * This method starts a new GoClient.
	 * 
	 * @param args 
	 */
	public static void main(String[] args) {
		(new GoClient()).start();
	}

}
