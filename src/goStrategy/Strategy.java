package goStrategy;

import goGame.Board;
import goGame.Stone;

/**
 * A GO strategy, to let a computer determine moves
 * @author huub.lievestro
 *
 */
public interface Strategy {
	
	public String getName();
	
	public int determineMove(Board board, Stone color);
}
