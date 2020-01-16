package goGame;

// import ss.utils.TextIO;
//import goProtocol.ProtocolMessages;

public class GameController {

	public static void main(String[] args) {
		assert (args.length <= 2) : "Sorry, two players maximum!";
		
		String name1;
		String name2;
		
		if (args.length == 0) {
			System.out.println("Please enter name of the first player and press return: ");
			name1 = "PlayerOne";//TextIO.getWord();
		} else {
			System.out.println("The first player is: " + args[0]);
			name1 = args[0];
		}
		
		if (args.length <= 1) {
			System.out.println("Please enter name of the second player and press return: ");
			name2 = "PlayerTwo"; //TextIO.getWord();
		} else {
			System.out.println("The second player is: " + args[1]);
			name2 = args[1];
		}


		Player player1 = createNewPlayer(name1, Stone.BLACK); // TODO change to colors, changeable?
		Player player2 = createNewPlayer(name2, Stone.WHITE); // TODO change to colors, changeable?

		Game currentGame = new Game(19, player1, player2); // TODO fixed harcoded game settings
		
		System.out.println("\n -- Let the game begin! -- \n"); 
		currentGame.start();
		

	}
	
	private static Player createNewPlayer(String inputName, Stone inputColor) {
		Player newPlayer;
		
		if (inputName.contains("-N")) {
			newPlayer = new ComputerPlayer(inputColor, new RandomStrategy());
		} else if (inputName.contains("-S")) {
			newPlayer = new ComputerPlayer(inputColor, new RandomStrategy());
		} else {
			newPlayer = new HumanPlayer(inputName, inputColor);
		}
		return newPlayer;
	}

}
