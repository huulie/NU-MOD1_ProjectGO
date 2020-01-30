package goServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import goGame.GameController;
import goGame.GoLocalTUI;
import goGame.Player;
import goGame.Stone;
import goProtocol.ProtocolMessages;


/**
 * Server to host games of Go
 *  
 * New clients can connect, and are placed in a waiting list
 * If there are two or more players, the first two players will be added to a new game
 * 
 *  
 * @author Huub Lievestro
 */
public class GoServer implements Runnable { 

	/** The ServerSocket of this GoServer. */
	private ServerSocket ssock;

	/** List of GoClientHandlers, one for each connected client. */
	private List<GoClientHandler> clients;
	
	/** List of GoClientHandlers, waiting to be connected to a game. */
	private List<GoClientHandler> clientsWaitingList;
	
	/** Next client number, increasing for every new connection. */
	private int next_client_no;

	/** The TUI of this HotelServer. */
	private GoLocalTUI TUI; // TODO: make a server TUI?!

	/** The name of this server. */
	private static String SERVERNAME = null; // final
	
	/** List of The Go Game controllers, one per game. */
	private List<GameController> games; 
	
	/**
	 * Fixed board dimensions for this server
	 */
	private int boardDim = 19;
	
	/** Network info for server. */
	InetAddress localIP = null;
	int port = 0;


	/**
	 * Constructs a new GoServer. 
	 * Initialises the clients list, the waiting list and the next_client_no.
	 */
	public GoServer() {
		clients = new ArrayList<>();
		clientsWaitingList = new ArrayList<>();
		games = new ArrayList<>();
		
		TUI = new GoLocalTUI();

		next_client_no = 1;
	}
	
	/**
	 * Returns the name of the server.
	 * 
	 * @requires hotel != null;
	 * @return the name of the sever.
	 */
	public String getServerName() {
		return SERVERNAME;
	}

	/**
	 * Opens a new socket by calling {@link #setup()} and starts a new
	 * GoClientHandler for every connecting client.
	 * 
	 * If {@link #setup()} throws a ExitProgram exception, stop the program. 
	 * In case of any other errors, ask the user whether the setup should be 
	 * ran again to open a new socket.
	 */
	public void run() {
		boolean openNewSocket = true;
		while (openNewSocket) {
			try {
				// Sets up the hotel application
				setup();

				while (true) {
					Socket sock = ssock.accept();
					String name = "Client " 
							+ String.format("%02d", next_client_no++);
					TUI.showMessage("A new client is trying to connect...");
					GoClientHandler handler = 
							new GoClientHandler(sock, this, name);
					new Thread(handler).start();
					clients.add(handler);
					TUI.showMessage("New client [" + name + "] connected!");
					
					
				}
			} catch (exceptions.ExitProgram eExit) {
				// If setup() throws an ExitProgram exception, stop the program.
				openNewSocket = false;
			} catch (IOException e) {
				System.out.println("A server IO error occurred: " 
						+ e.getMessage());

				if (!TUI.getBoolean("Do you want to open a new socket?")) {
					openNewSocket = false;
				}
			}
		}
		TUI.showMessage("See you later!");
	}

	/**
	 * Sets up a new GoServer using {@link #setupServer()} and opens a new 
	 * ServerSocket at localhost on a user-defined port.
	 * 
	 * The user is asked to input a port, after which a socket is attempted 
	 * to be opened. If the attempt succeeds, the method ends, If the 
	 * attempt fails, the user decides to try again, after which an 
	 * ExitProgram exception is thrown or a new port is entered.
	 * 
	 * @throws ExitProgram if a connection can not be created on the given 
	 *                     port and the user decides to exit the program.
	 * @ensures a serverSocket is opened.
	 */
	public void setup() throws exceptions.ExitProgram {
		// First, initialise the Server.
		setupServer();

		ssock = null;
		while (ssock == null) {
			port = TUI.getInt("Please enter the server port.");
			
			Socket discoverLocalIP = new Socket(); // this ways, it returns the preferred outbound IP
			try {
//				try {
//				discoverLocalIP.connect(new InetSocketAddress("google.com", 80));
//				} catch (UnknownHostException eUnknownHost) {
					TUI.showMessage("No internet access, trying locally to reach 192.168.1.1");
					discoverLocalIP.connect(new InetSocketAddress("192.168.1.1", 80));
//				}


				localIP = discoverLocalIP.getLocalAddress();
				TUI.showMessage("Discovering local IP address: " 
						+ discoverLocalIP.getLocalAddress());
				discoverLocalIP.close();
			} catch (IOException e1) {
				TUI.showMessage("IO Exception while Discovering local IP address: " 
						+ e1.getLocalizedMessage());
				e1.printStackTrace();
			}

			// try to open a new ServerSocket
			try {
				TUI.showMessage("Attempting to open a socket at " + localIP
						+ " on port " + port + "...");
				ssock = new ServerSocket(port, 0, localIP);
				TUI.showMessage("Server started at port " + port);
				TUI.showMessage("Waiting for clients...");

			} catch (IOException e) {
				TUI.showMessage("ERROR: could not create a socket on "
						+ localIP + " and port " + port + ".");

				if (!TUI.getBoolean("Do you want to try again?")) {
					throw new exceptions.ExitProgram("User indicated to exit the "
							+ "program.");
				}
			}
		}
	}
	
	/**
	 * Asks the user for a server name
	 */
	public void setupServer() {
		SERVERNAME = TUI.getString("What is the name of this server?");
	}
	
	/**
	 * Removes a clientHandler from the client list.
	 * @requires client != null
	 */
	public void removeClient(GoClientHandler client) {
		this.clients.remove(client);
		if (this.clientsWaitingList.contains(client)) {
			this.clientsWaitingList.remove(client);
		}
		// if client in game, this will be terminated by GameController
	}

	// ------------------ Server Methods --------------------------

	/**
	 * Returns a String to be sent as a response to a Client HELLO request,
	 * including the name of the hotel: ProtocolMessages.HELLO +
	 * ProtocolMessages.DELIMITER + (Hotel Name); TODO
	 * 
	 * @return String to be sent to client as a handshake response.
	 */
	public String respondHandshake(GoClientHandler handler) {
		TUI.showMessage("Received handshake, sending response...");
		
		String finalVersion = "1.0";
		String message = " Welcome to the server " + this.getServerName();
		
		clientsWaitingList.add(handler);
		TUI.showMessage("Player [" + handler.getRemotePlayer().getName() + "] on client [" + handler.getClientName() + "] added to the waiting list");

		return ProtocolMessages.HANDSHAKE + ProtocolMessages.DELIMITER + finalVersion 
				+ ProtocolMessages.DELIMITER + message;
	}
	
	/**
	 * Checks waiting list if two or more players are waiting = start new game.
	 */
	public void checkWaitingList() {
		TUI.showMessage("Current waiting list: " + clientsWaitingList); // TODO client/player names?!
		while (clientsWaitingList.size() >= 2) {
			TUI.showMessage("Two or more players are waiting: starting new game(s)...");
			this.startGameFromWaitingList();
		}
	}
	
	private void startGameFromWaitingList() {
		Player first = clientsWaitingList.get(0).getRemotePlayer();
		first.setColour(Stone.BLACK);
		Player second = clientsWaitingList.get(1).getRemotePlayer();
		second.setColour(Stone.WHITE);
		
		GameController newGame = new GameController(this.boardDim, first, second, false);
		first.setGame(newGame);
		clientsWaitingList.get(0).clientStartGame();
		second.setGame(newGame);
		clientsWaitingList.get(1).clientStartGame();

		
		this.games.add(newGame);
		new Thread(this.games.get(games.size() - 1)).start();
		TUI.showMessage("New game started: " + first.getName() + " vs " + second.getName());
		
		clientsWaitingList.remove(1);
		clientsWaitingList.remove(0);
		
	}


	// ------------------ Main --------------------------

	/** Start a new GoServer. */
	public static void main(String[] args) {
		GoServer server = new GoServer();
		System.out.println("Welcome to the GO Server! \n Starting...");
		new Thread(server).start();
	}
	
}
