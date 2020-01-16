package goClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

import ss.week3.bill.StringPrinter;
import ss.week7.hotel.exceptions.ExitProgram;
import ss.week7.hotel.exceptions.ProtocolException;
import ss.week7.hotel.exceptions.ServerUnavailableException;
import ss.week7.hotel.protocol.ClientProtocol;
import ss.week7.hotel.protocol.ProtocolMessages;

/**
 * Client for Networked Go Game
 * 
 * @author Huub Lievestro
 */
public class GoClient { //implements ClientProtocol {
	
	private Socket serverSock;
	private BufferedReader in;
	private BufferedWriter out;
	
	private String hotelName;
	
	private HotelClientTUI TUI;
	
	private String serverResponseMarker = "> ";
	private String localErrorMarker = "!ERROR: ";

	/**
	 * Constructs a new HotelClient. Initialises the view.
	 */
	public GoClient() {
		this.TUI = new HotelClientTUI(this);
	}

	/**
	 * Starts a new HotelClient by creating a connection, followed by the 
	 * HELLO handshake as defined in the protocol. After a successful 
	 * connection and handshake, the view is started. The view asks for 
	 * used input and handles all further calls to methods of this class. 
	 * 
	 * When errors occur, or when the user terminates a server connection, the
	 * user is asked whether a new connection should be made.
	 */
	public void start() {
		try {
			this.createConnection();
			this.handleHello();
			
			TUI.start();
		} catch (ServerUnavailableException e) {
			System.out.println("Server unavailable!");
			e.printStackTrace();
		} catch (ExitProgram e) { // from create connection
			System.out.println("CLIENT EXIT");
			e.printStackTrace();
		} catch (ProtocolException e) { // from handle hello
			System.out.println("Protocol exception:" + e.getLocalizedMessage());
			e.printStackTrace();
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

	/**
	 * Reads and returns multiple lines from the server until the end of 
	 * the text is indicated using a line containing ProtocolMessages.EOT.
	 * 
	 * @return the concatenated lines sent by the server.
	 * @throws ServerUnavailableException if IO errors occur.
	 */
	public String readMultipleLinesFromServer() 
			throws ServerUnavailableException {
		if (in != null) {
			try {
				// Read and return answer from Server
				StringBuilder sb = new StringBuilder();
				for (String line = in.readLine(); line != null
						&& !line.equals(ProtocolMessages.EOT); 
						line = in.readLine()) {
					sb.append(line + System.lineSeparator());
				}
				return sb.toString();
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
	@Override
	public void handleHello() 
			throws ServerUnavailableException, ProtocolException {
		this.sendMessage(String.valueOf(ProtocolMessages.HELLO));
		
		String helloResponse = this.readLineFromServer();
		
		if (helloResponse != null && !helloResponse.equalsIgnoreCase("")) {
			String[] splitHelloResponse = helloResponse.split(ProtocolMessages.DELIMITER);
			if (splitHelloResponse.length == 2) {
				if (splitHelloResponse[0].equals(String.valueOf(ProtocolMessages.HELLO))) {
					 hotelName = splitHelloResponse[1];
				} else {
					throw new ProtocolException("Handshake failed: server did not say Hello");
				}
			} else {
				throw new ProtocolException("Handshake failed: Wrong number of arguments (" 
			+ splitHelloResponse.length + " instead of 2) in response from server");
			}
		} else {
			throw new ProtocolException("Handshake failed: Empty response from server");
		}
		// System.out.println("DEBUG" + Welcome to the Hotel booking system of hotel: " + hotelName);
		TUI.showMessage("Connected to the hotel booking system of: " + hotelName);
	}
	
	/**
	 * Sends a checkIn request to the server.
	 * 
	 * Given the name of a guest, the doIn() method sends the following message to
	 * the server: ProtocolMessages.IN + ProtocolMessages.DELIMITER + guestName
	 * 
	 * The result (one line) is then retrieved and forwarded to the view.
	 * 
	 * @requires guestName != null
	 * @param guestName Name of the guest
	 * @throws ServerUnavailableException if IO errors occur.
	 */
	@Override
	public void doIn(String guestName) throws ServerUnavailableException {
        assert guestName != null : "guestName may not be null";
		
        this.sendMessage(ProtocolMessages.IN + ProtocolMessages.DELIMITER + guestName);
        
		String doInResponse = this.readLineFromServer();
		
		// System.out.println("DEBUG" + serverResponseMarker + doInResponse);
		TUI.showMessage(serverResponseMarker + doInResponse);

	}

	/**
	 * Sends a checkOut request to the server.
	 * 
	 * Given the name of a guest, the doOut() method sends the following message to
	 * the server: ProtocolMessages.OUT + ProtocolMessages.DELIMITER + guestName
	 * 
	 * The result (one line) is then retrieved and forwarded to the view.
	 * 
	 * @requires guestName != null
	 * @param guestName Name of the guest
	 * @throws ServerUnavailableException if IO errors occur.
	 */
	@Override
	public void doOut(String guestName) throws ServerUnavailableException {
		assert guestName != null : "guestName may not be null";
		
        this.sendMessage(ProtocolMessages.OUT + ProtocolMessages.DELIMITER + guestName);
        
		String doOutResponse = this.readLineFromServer();
		
		// System.out.println("DEBUG" + serverResponseMarker + doOutResponse);
		TUI.showMessage(serverResponseMarker + doOutResponse);
	}

	/**
	 * Sends a room request to the server.
	 * 
	 * Given the name of a guest, the doRoom() method sends the following message to
	 * the server: ProtocolMessages.ROOM + ProtocolMessages.DELIMITER + guestName
	 * 
	 * The result (one line) is then retrieved and forwarded to the view.
	 * 
	 * @requires guestName != null
	 * @param guestName Name of the guest
	 * @throws ServerUnavailableException if IO errors occur.
	 */
	@Override
	public void doRoom(String guestName) throws ServerUnavailableException {
		assert guestName != null : "guestName may not be null";
		
        this.sendMessage(ProtocolMessages.ROOM + ProtocolMessages.DELIMITER + guestName);
        
		String doRoomResponse = this.readLineFromServer();
		
		// System.out.println("DEBUG" + serverResponseMarker + doRoomResponse);
		TUI.showMessage(serverResponseMarker + doRoomResponse);
	}

	/**
	 * Sends a safe activation request to the server.
	 * 
	 * Given the name of a guest, the doAct() method sends the following message to
	 * the server: ProtocolMessages.ACT + ProtocolMessages.DELIMITER + guestName +
	 * ProtocolMessages.DELIMITER + password
	 * 
	 * The result (one line) is then retrieved and forwarded to the view.
	 * 
	 * @requires guestName != null
	 * @param guestName Name of the guest
	 * @param password  (Optional) Password in case of a protected safe
	 * @throws ServerUnavailableException if IO errors occur.
	 */
	@Override
	public void doAct(String guestName, String password) 
			throws ServerUnavailableException {
		assert guestName != null : "guestName may not be null";
		assert password != null : "password may not be null";

        this.sendMessage(ProtocolMessages.ACT + ProtocolMessages.DELIMITER + guestName +
        		  ProtocolMessages.DELIMITER + password);
        
		String doActResponse = this.readLineFromServer();
		
		// System.out.println("DEBUG" + serverResponseMarker + doActResponse);
		TUI.showMessage(serverResponseMarker + doActResponse);
	}

	/**
	 * Requests the bill for a guest at the server.
	 * 
	 * Given the name of a guest and the number of nights of the stay, the doBill()
	 * method sends the following message to the server: ProtocolMessages.ACT +
	 * ProtocolMessages.DELIMITER + guestName + ProtocolMessages.DELIMITER +
	 * nights
	 * 
	 * If nights is not an integer or not a positive number, a message is shown in
	 * the view and no request is sent to the server.
	 * 
	 * When a request is sent to the server, the result (multiple lines, ending with
	 * ProtocolMessages.EOT) is retrieved and forwarded to the view.
	 * 
	 * @requires guestName != null
	 * @requires nights to be integer and > 0
	 * @param guestName Name of the guest
	 * @param nights    Number of nights of the stay
	 * @throws ServerUnavailableException if IO errors occur.
	 */
	@Override
	public void doBill(String guestName, String nights) 
			throws ServerUnavailableException {
		assert guestName != null : "guestName may not be null";
//		assert (nights != null && Integer.parseInt(nights) > 0) : "nights may not be null or less then one";

		try {
			if (Integer.parseInt(nights) <= 0) {
				TUI.showMessage(localErrorMarker 
						+ "number of nights has to be greater than zero");
			} else {
				this.sendMessage(ProtocolMessages.BILL + ProtocolMessages.DELIMITER + guestName 
						+ ProtocolMessages.DELIMITER + nights);
				
				String doBillResponse = this.readMultipleLinesFromServer();
				
				// System.out.println("DEBUG" + serverResponseMarker + doBillResponse);
				TUI.showMessage(serverResponseMarker + doBillResponse);
			}
		} catch (NumberFormatException nfe) {
			// System.out.println("DEBUG" + localErrorMarker + "number of nights could not be converted to integer");
			TUI.showMessage(localErrorMarker + "number of nights could not be converted to integer");
		}
	}

	/**
	 * Requests the state of the hotel at the server. The state contains an overview
	 * of the rooms, its guests and the state of the safes.
	 * 
	 * The doPrint() method sends the following message to the server:
	 * ProtocolMessages.PRINT
	 * 
	 * The result (multiple lines, ending with ProtocolMessages.EOT) is retrieved
	 * and forwarded to the view
	 * 
	 * @throws ServerUnavailableException if IO errors occur.
	 */
	@Override
	public void doPrint() throws ServerUnavailableException {
		this.sendMessage(String.valueOf(ProtocolMessages.PRINT));
      
		String doPrintResponse = this.readMultipleLinesFromServer();
		
		// System.out.println("DEBUG" + serverResponseMarker + doPrintResponse);
		TUI.showMessage(serverResponseMarker + doPrintResponse);
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
	@Override
	public void sendExit() throws ServerUnavailableException {
		this.sendMessage(String.valueOf(ProtocolMessages.EXIT));
		
		this.closeConnection();
	}
	
	/**
	 * Requests name of hotel and network information, to show in help
	 * 
	 * The doHelp() method sends the following message to the server:
	 * ProtocolMessages.HELP
	 * 
	 * The result (one line) is retrieved and forwarded to the view
	 * 
	 * @throws ServerUnavailableException if IO errors occur.
	 */
	// @Override
	public void doHelp() throws ServerUnavailableException {
		this.sendMessage(String.valueOf(ProtocolMessages.HELP));
      
		String doPrintResponse = this.readLineFromServer();
		
		// System.out.println("DEBUG" + serverResponseMarker + doPrintResponse);
		TUI.showMessage(serverResponseMarker + doPrintResponse);
	}

	/**
	 * This method starts a new HotelClient.
	 * 
	 * @param args 
	 */
	public static void main(String[] args) {
		(new HotelClient()).start();
	}

}
