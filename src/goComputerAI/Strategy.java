package goComputerAI;

import goClient.GoClient;

/**
 * A GO strategy, to let a computer determine moves
 * @author huub.lievestro
 *
 */
public interface Strategy {
	
	public String getName();
	
	public int calculateMove(GoClient client); // TODO Board board, Stone color
}
