package goComputerAI;

import goClient.GoClient;
import goComputerAI.Strategy;

public class ComputerAI { 

	private Strategy strategy;
	
	public Strategy getStrategy() {
		return strategy;
	}

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

	public ComputerAI(Strategy strategy) {		
		this.strategy = strategy;
	}
	
	public int calculateMove(GoClient client) {
		return strategy.calculateMove(client);
	}
	

}
