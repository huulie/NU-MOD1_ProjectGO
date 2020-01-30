package goComputerAI;

import goClient.GoClient; 
import goUI.GoTUI;

/** Go Game Computer TUI.
 * Can be used by a computer AI: locally or via client on server
 * @author huub.lievestro
 *
 */
public class GoComputerTUI extends GoTUI {

	private ComputerAI connectedAI;
	
	private GoClient connectedClient;
	
	public GoComputerTUI(GoClient client) {
		super();
//		Strategy randomStrategy = new RandomStrategy();
		Strategy chaseAndCaptureStrategy = new ChaseAndCaptureStrategy();
		this.connectedAI = new ComputerAI(chaseAndCaptureStrategy);
		this.connectedClient = client;
	}

	/**
	 * Prints the question and asks the user to input an Move (int, with -1 for PASS).
	 * 
	 * @param question the question shown to the user, asking for input
	 * @return The written Integer, or -1 if PASS.
	 */
	@Override
	public int getMove(String question) {
        return this.connectedAI.calculateMove(connectedClient);
	}
	
}
