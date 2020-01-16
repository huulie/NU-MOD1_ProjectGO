package goStrategy;

public interface Strategy {
	
	public String getName(); // interface method, has no body
	
	public int determineMove(Board board, Mark mark);
}
