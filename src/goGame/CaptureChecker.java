package goGame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CaptureChecker {

// TODO: all static?!
	
	// Instance variables, to keep track of progress
	private static List<Integer> indicesToCheck = new ArrayList<Integer>(); // TODO: do I want this to be static? 
	private static List<Integer> indicesOwn = new ArrayList<Integer>(); // TODO: do I want this to be static? 
	private static List<Integer> indicesOpponentFree = new ArrayList<Integer>(); // TODO: do I want this to be static? 
	private static List<Integer> indicesOpponentCaptured = new ArrayList<Integer>(); // TODO: do I want this to be static? 

	

	public static void doOpponentCaptures(Board board, Player currentPlayer) {		
		// Check capture of OPPONENT stones (take priority of capture vs self-capture in mind)
		// first simple implementation, TODO: check for groups
		
		indicesToCheck.addAll(IntStream.rangeClosed(0, board.DIM*board.DIM-1) // TDO: mind -1!
				.boxed().collect(Collectors.toList())); // do NOT use ^ for squaring

		Iterator<Integer> boardIterator = indicesToCheck.iterator();

		while (boardIterator.hasNext()) {

			int checkingIndex = boardIterator.next();

			if (board.getField(checkingIndex).equals(currentPlayer.getColor()) 
					|| board.getField(checkingIndex).equals(Stone.UNOCCUPIED)) {
				moveToOwn(checkingIndex);
			} else { // opponent's stone
				checkOpponentStone(board, checkingIndex, currentPlayer);
			}
		}
	}

	private static void checkOpponentStone(Board board, int index, Player currentPlayer) {
		if (hasLiberty(board, index)) {
			moveToOpponentFree(index);
		} else {

			if (surroundedOwn(board, index, currentPlayer)) {
				moveToOpponentCaptured(index);
				board.setField(index, Stone.UNOCCUPIED); // TODO: ONLY IF ALL SURROUNDED!
			} else {
				if (board.above(index)!=-1 && !board.getField(board.above(index)).equals(currentPlayer.getColor())) {
					checkOpponentStone(board, board.above(index), currentPlayer);
				}
				if (board.right(index)!=-1 && !board.getField(board.right(index)).equals(currentPlayer.getColor())) {
					checkOpponentStone(board, board.right(index), currentPlayer);
				}
				if (board.below(index)!=-1 && !board.getField(board.below(index)).equals(currentPlayer.getColor())) {
					checkOpponentStone(board, board.below(index), currentPlayer);
				}
				if (board.left(index)!=-1 && !board.getField(board.left(index)).equals(currentPlayer.getColor())) {
					checkOpponentStone(board, board.left(index), currentPlayer);
				}
			}
			// if surrounded by own = capture, en alle omliggende own
			// else: recursive on all opponent stones
		}
	}


	public static boolean hasLiberty(Board board, int index) {
		if ( (board.above(index)!=-1 && board.isEmptyField(board.above(index)))
			|| (board.right(index)!=-1 && board.isEmptyField(board.right(index))) 
			|| (board.below(index)!=-1 && board.isEmptyField(board.below(index)))
			|| (board.left(index)!=-1 && board.isEmptyField(board.left(index)))) {
	 return true;
		}
		return false;
	}
	
	public static boolean surroundedOwn(Board board, int index, Player currentPlayer) {
		if ( (board.above(index)== -1 || board.getField(board.above(index)).equals(currentPlayer.getColor()))
				&& (board.right(index)== -1 || board.getField(board.right(index)).equals(currentPlayer.getColor()))
				&& (board.below(index)== -1 || board.getField(board.below(index)).equals(currentPlayer.getColor())) 
				&& (board.left(index)== -1 || board.getField(board.left(index)).equals(currentPlayer.getColor())) ) {
			return true;
		}
		return false;
	}
	
	private static void moveToOwn(int index) {
		indicesOwn.add(index);
		//indicesToCheck.removeAll(Arrays.asList(index)); // TODO https://stackoverflow.com/a/21795392
	}
	
	private static void moveToOpponentFree(int index) {
		indicesOpponentFree.add(index);
		//indicesToCheck.removeAll(Arrays.asList(index)); // TODO https://stackoverflow.com/a/21795392
	}
	
	private static void moveToOpponentCaptured(int index) {
		indicesOpponentCaptured.add(index);
		//indicesToCheck.removeAll(Arrays.asList(index)); // TODO https://stackoverflow.com/a/21795392
	}
//TODO: cannot remove when iterating over list, use iterator.remove
	// https://stackoverflow.com/questions/29954427/java-util-arraylistitr-checkforcomodification-exception-thrown
}
