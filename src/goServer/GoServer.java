package goServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import ss.week3.hotel.Hotel;
import ss.week3.hotel.PricedSafe;
import ss.week3.hotel.Room;
import ss.week3.bill.StringPrinter;

import ss.week7.hotel.exceptions.ExitProgram;
import ss.week7.hotel.protocol.ProtocolMessages;
import ss.week7.hotel.protocol.ServerProtocol;

//KIJK OOK EVEN BIJ TICTACTOE.JAVA
//GAME ALS THREAD? OF NOG APARTE GAMECCONTROLLER? 


/**
 * Server TUI for Networked Hotel Application.
 * 
 * Intended Functionality: interactively set up & monitor a new server.
 * 
 * @author Wim Kamerman, adapted by Huub Lievestro
 */
public class GoServer implements Runnable, ServerProtocol {

	/** The ServerSocket of this GoServer. */
	private ServerSocket ssock;

	/** List of GoClientHandlers, one for each connected client. */
	private List<GoClientHandler> clients;
	
	/** Next client number, increasing for every new connection. */
	private int next_client_no;

	/** The view of this HotelServer. */
	private HotelServerTUI view;

	/** The name of the Hotel. */
	private static String HOTELNAME = null; // final
	private static int NROOMS = 0;
	
	/** The Go Game (containing all business logic). */
	private GoGame game = null; 
	
	/** Network info for server. */
	InetAddress localIP = null;
	int port = 0;


	/**
	 * Constructs a new HotelServer. Initializes the clients list, 
	 * the view and the next_client_no.
	 */
	public GoServer() {
		clients = new ArrayList<>();
		view = new HotelServerTUI();
		next_client_no = 1;
	}
	
	/**
	 * Returns the name of the hotel.
	 * 
	 * @requires hotel != null;
	 * @return the name of the hotel.
	 */
	public String getHotelName() {
		return HOTELNAME;
	}

	/**
	 * Opens a new socket by calling {@link #setup()} and starts a new
	 * HotelClientHandler for every connecting client.
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
					view.showMessage("New client [" + name + "] connected!");
					GoClientHandler handler = 
							new GoClientHandler(sock, this, name);
					new Thread(handler).start();
					clients.add(handler);
				}
			} catch (ExitProgram e1) {
				// If setup() throws an ExitProgram exception, 
				// stop the program.
				openNewSocket = false;
			} catch (IOException e) {
				System.out.println("A server IO error occurred: " 
						+ e.getMessage());

				if (!view.getBoolean("Do you want to open a new socket?")) {
					openNewSocket = false;
				}
			}
		}
		view.showMessage("See you later!");
	}

	/**
	 * Sets up a new Hotel using {@link #setupHotel()} and opens a new 
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
	public void setup() throws ExitProgram {
		// First, initialise the Hotel.
		setupHotel();

		ssock = null;
		while (ssock == null) {
			port = view.getInt("Please enter the server port.");
			
			Socket discoverLocalIP = new Socket(); // this ways, it returns the preferred outbound IP
			try {
				discoverLocalIP.connect(new InetSocketAddress("google.com", 80));
				localIP = discoverLocalIP.getLocalAddress();
				view.showMessage("Discovering local IP address: " + discoverLocalIP.getLocalAddress());
				discoverLocalIP.close();
			} catch (IOException e1) {
				view.showMessage("IO Exception while Discovering local IP address: " 
						+ e1.getLocalizedMessage());
				e1.printStackTrace();
			}

			// try to open a new ServerSocket
			try {
				view.showMessage("Attempting to open a socket at " + localIP
						+ " on port " + port + "...");
				ssock = new ServerSocket(port, 0, localIP);
				view.showMessage("Server started at port " + port);
				view.showMessage("Waiting for clients...");

			} catch (IOException e) {
				view.showMessage("ERROR: could not create a socket on "
						+ localIP + " and port " + port + ".");

				if (!view.getBoolean("Do you want to try again?")) {
					throw new ExitProgram("User indicated to exit the "
							+ "program.");
				}
			}
		}
	}
	
	/**
	 * Asks the user for a hotel name and initialises
	 * a new Hotel with this name.
	 */
	public void setupHotel() {
		HOTELNAME = view.getString("What is the name of the hotel?");
		NROOMS = view.getInt("How many rooms does the hotel have? (use integer numbers)");
		hotel = new Hotel(HOTELNAME, NROOMS);
	}
	
	/**
	 * Removes a clientHandler from the client list.
	 * @requires client != null
	 */
	public void removeClient(GoClientHandler client) {
		this.clients.remove(client);
	}

	// ------------------ Server Methods --------------------------

	/**
	 * Returns a String to be sent as a response to a Client HELLO request,
	 * including the name of the hotel: ProtocolMessages.HELLO +
	 * ProtocolMessages.DELIMITER + (Hotel Name);
	 * 
	 * @return String to be sent to client as a handshake response.
	 */
	@Override
	public String getHello() {
		System.out.println("Received handshake, saying Hello and telling my Name");
		return ProtocolMessages.HELLO + ProtocolMessages.DELIMITER + HOTELNAME;
	}
	
	/**
	 * Given the name of a guest, the checkIn command of the hotel application is
	 * called. The result is returned as String and can be: - Parameter is wrong
	 * (guestName is null) - CheckIn failed (no room assigned) - CheckIn successful
	 * + room number
	 * 
	 * @requires guestName != null
	 * @param guestName Name of the guest
	 * @return textual result, to be shown to the user
	 */
	@Override
	public synchronized String doIn(String guestName) {
		// return "DEBUGserver:" + ProtocolMessages.IN + ProtocolMessages.DELIMITER + cmd;
		String result = null;
		
		Room assignedRoom = hotel.checkIn(guestName);
		if (assignedRoom != null) {
			result = guestName + " is checked into [" + assignedRoom.getNumber() + "]> " 
					+ assignedRoom.toString();
		} else {
			result = "Cannot check-in: no free rooms or already a guest with name " + guestName;
		}

		return result;
	}

	/**
	 * Given the name of a guest, the checkOut command of the hotel application is
	 * called. The result is returned as String and can be: - Parameter is wrong
	 * (guestName is null) - CheckOut successful
	 * 
	 * @requires guestName != null
	 * @param guestName Name of the guest
	 * @return textual result, to be shown to the user
	 */
	@Override
	public synchronized String doOut(String guestName) {
		// return "DEBUGserver:" + ProtocolMessages.OUT + ProtocolMessages.DELIMITER + guestName;
		if (hotel.getRoom(guestName) != null) {
			hotel.checkOut(guestName);
		} else {
			return guestName + " does not have a room";
		}
		
		return ("Check out of " + guestName + " done");
	}

	/**
	 * Given the name of a guest, the corresponding room is returned. The result is
	 * returned as String and can be: - Parameter is wrong (guestName is null) -
	 * Guest does not have a room - Guest has room + room number
	 * 
	 * @requires guestName != null
	 * @param guestName Name of the guest
	 * @return textual result, to be shown to the user
	 */
	@Override
	public synchronized String doRoom(String guestName) {
		// return "DEBUGserver:" + ProtocolMessages.ROOM + ProtocolMessages.DELIMITER + cmd;
		String result = null;
		if (guestName == null) {	
			return "Cannot search without any data";
		} else if (hotel.getRoom(guestName) != null) {
			result = guestName + " has [" + hotel.getRoom(guestName).getNumber() + "]> " 
					+ hotel.getRoom(guestName).toString();
		} else {
			result =  guestName + " does not have a room";
		}
		
		return result;
	}

	/**
	 * Given the name of a guest, the safe in the room of the guest is activated.
	 * The result is returned as String and can be: - Parameters are wrong
	 * (guestName is null or password is required) - Safe has not been activated
	 * (guest has no room) - Safe has been activated
	 * 
	 * @requires guestName != null
	 * @param guestName Name of the guest
	 * @param password  (Optional) Password in case of a protected safe
	 * @return textual result, to be shown to the user
	 */
	@Override
	public synchronized String doAct(String guestName, String password) {
		// return "DEBUGserver:" + ProtocolMessages.ACT + ProtocolMessages.DELIMITER 
		//	+ cmd1 + ProtocolMessages.DELIMITER + cmd2;
		String result = null;
		if (password != null) {
			if (hotel.getRoom(guestName) != null) {
				PricedSafe safe = (PricedSafe) hotel.getRoom(guestName).getSafe();
				safe.activate(password);
				if (safe.isActive()) {
					result = "Safe of " + guestName + " activated";
				} else {
					result = "FAILED: Safe of " + guestName + " NOT activated";
				}
			} else {
				result =  guestName + " does not have a room";
			}
		} else {
			result = "[SECURITY] Password null: acces denied";
		}
		return result;
	}

	/**
	 * Given the name of a guest and the number of nights of the stay, the bill is
	 * requested. The result is returned as String and can be: - Parameters are
	 * wrong (guestName or nights is null or nights is no integer) - The String of
	 * the bill for the guest
	 * 
	 * NOTE: don't forget to include the line epd + EOT message, to indicate end of multiple line transmission
	 * 
	 * @requires guestName != null &&
	 * @param guestName Name of the guest
	 * @param nrNights    Number of nights of the
	 * @return textual result, to be shown to the user
	 */
	@Override
	public synchronized String doBill(String guestName, int nrNights) {
		//		return "DEBUGserver:" + ProtocolMessages.BILL + ProtocolMessages.DELIMITER 
		//			+ guestName + ProtocolMessages.DELIMITER + nrNights 
		//			+ System.lineSeparator() + ProtocolMessages.EOT;
		String result = null;

		printer = new StringPrinter();

		if (hotel.getRoom(guestName) != null) {
			hotel.getBill(guestName, nrNights, this.printer);
			view.showMessage("Bill is sent to printer: returning result to client");
			result = printer.getResult() + System.lineSeparator() + ProtocolMessages.EOT;
		} else {
			result =  guestName + " does not have a room";
		}

		return result;
	}

	/**
	 * Returns the state of the Hotel, containing an overview of the rooms, its
	 * guests and the state of the safes.
	 * 
	 * NOTE: don't forget to include the linesep + EOT message, to indicate end of multiple line transmission
	 * 
	 * @return the string representation of the Hotel
	 */
	@Override
	public synchronized String doPrint() {
		//return "DEBUGserver:" + ProtocolMessages.PRINT + System.lineSeparator() + ProtocolMessages.EOT;
		return " --- " + hotel.getName() + " --- \n" + hotel.toString() 
		    + System.lineSeparator() + ProtocolMessages.EOT;
	}
	
	/**
	 * Returns name and network information to client
	 * 
	 * @return the string representation of the Hotel
	 */
	//@Override
	public synchronized String doHelp() {
		return "#SERVER#[INFO]: HotelServer " + hotel.getName() + " (" + NROOMS 
					+ " rooms), listening: " + this.localIP + " on port " + this.port; 
	}

	// ------------------ Main --------------------------

	/** Start a new HotelServer. */
	public static void main(String[] args) {
		GoServer server = new GoServer();
		System.out.println("Welcome to the GO Server! \n Starting...");
		new Thread(server).start();
	}
	
}
