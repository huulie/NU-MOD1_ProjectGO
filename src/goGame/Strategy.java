package goGame;

public interface Strategy {
	
	public String getName(); // interface method, has no body
	
	public int determineMove(Board board, Stone color);
}
