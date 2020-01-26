package goStrategy;

import goGame.Board;
import goStrategy.Strategy;

// TODO look at this when working on computer player / AI, think about how to integrate
public class ComputerPlaysNAME { // TODO extends Player?

	
	private Strategy strategy;
	
	
	public Strategy getStrategy() {
		return strategy;
	}

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

	/**
     * Creates a new computer player object.
     * @requires color is either ProtocolMessages.BLACK or ProtocolMessages.WHITE
     * @requires strategy is not null
     * @ensures the color of this player will be mark
     * @ensures the Strategy of this player is strategy
     * @ensures the Name of this player will be COMPUTER_strategy-mark
     */
	public ComputerPlaysNAME(Strategy strategy) {
		String string = "COMPUTER_" + strategy.getName(); //mark.toString(),mark
		
		this.strategy = strategy;
		
	}
	
//	/**
//     * Creates a new computer player object with a naive strategy.
//     * @requires color is either ProtocolMessages.BLACK or ProtocolMessages.WHITE
//     * @ensures the Mark of this player will be mark
//     * @ensures the Strategy of this player is a naive strategy
//     * @ensures the Name of this player will be COMPUTER_strategy-mark
//     */
//	public ComputerPlayer(Mark mark) {
//		this(mark, new NaiveStrategy());
//	}
	
	public int determineMove(Board board) {
		//System.out.println("Computer " + this.getName() + " makes a move!");
		//return this.strategy.determineMove(board, this.getColour());
		return 0;
	}
	

}
