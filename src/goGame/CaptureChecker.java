package goGame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** CaptureChecker, to check for captures.
 *  Must be used by Game, to enforce the rules (local or as server)
 *  May be used as Player, to calculate the result of a certain move (mimicking expected server response)
 * @author huub.lievestro
 *
 */

public class CaptureChecker {

// TODO: all static >> CANNOT BE STATIC AND HAVE INSTANCE VARIABLES
	
	private static final char ABOVE = 'a';
	private static final char RIGHT = 'r';
	private static final char BELOW = 'b';
	private static final char LEFT = 'l';
	
	// Instance variables, to keep track of progress
	private List<Integer> indicesToCheck = new ArrayList<Integer>(); // TODO: do I want this to be static? 
	private List<Integer> indexOwnUnoccupied = new ArrayList<Integer>(); // TODO: do I want this to be static? 
	private List<Integer> indicesOpponentFree = new ArrayList<Integer>(); // TODO: do I want this to be static? 
	private List<Integer> indicesOpponentCaptured = new ArrayList<Integer>(); // TODO: do I want this to be static? 

	

	public void doOpponentCaptures(Board board, Stone ownStone) {		
		// Check capture of OPPONENT stones (take priority of capture vs self-capture in mind)
		// first simple implementation, TODO: check for groups
		
		indicesToCheck.addAll(IntStream.rangeClosed(0, board.DIM*board.DIM-1) // TDO: mind -1!
				.boxed().collect(Collectors.toList())); // do NOT use ^ for squaring

		Iterator<Integer> boardIterator = indicesToCheck.iterator();

		while (boardIterator.hasNext()) {

			int checkingIndex = boardIterator.next();

			if (board.getField(checkingIndex).equals(ownStone) 
					|| board.getField(checkingIndex).equals(Stone.UNOCCUPIED)) {
				moveToOwnUnoccupied(checkingIndex);
			} else { // opponent's stone
				if (surroundedOwn(board, checkingIndex, ownStone)) { // NOT right for recursive> cannot be if opponent group
					moveToOpponentCaptured(checkingIndex);
					board.setField(checkingIndex, Stone.UNOCCUPIED); // TODO: ONLY IF ALL SURROUNDED!
						// TODO: update board should be processed by GUI and/or somewhere else
				} else {
				// TODO: KEEP TRACK OF PROCESSED STONES, TO AVOID DUPLICATES/INFINTE LOOPS
					// also provide direction of search
				List<Integer> foundIndices = new ArrayList<Integer>();
				foundIndices.add(checkingIndex);
					
					if(board.above(checkingIndex)== -1 || board.getField(board.above(checkingIndex))==ownStone){
						checkOpponentStone(board, board.above(index), ownStone, BELOW); // TODO WIP
					} else if(board.right(checkingIndex)== -1 || board.getField(board.right(checkingIndex))==ownStone){
						
					} else if(board.below(checkingIndex)== -1 || board.getField(board.below(checkingIndex))==ownStone){
						
					} else if(board.left(checkingIndex)== -1 || board.getField(board.left(checkingIndex))==ownStone){
						
					}
					checkOpponentStone(board, checkingIndex, ownStone);
				}
			}
		}
	}

	private void checkOpponentStone(Board board, int index, Stone ownStone) { // TODO avoid going back to prev stone!
		if (hasLiberty(board, index)) {
			moveToOpponentFree(index);
		} else {

			if (surroundedOwn(board, index, ownStone)) { // NOT right> cannot be if opponent group
				moveToOpponentCaptured(index);
				board.setField(index, Stone.UNOCCUPIED); // TODO: ONLY IF ALL SURROUNDED!
			} else {
				if (board.above(index)!=-1 && !board.getField(board.above(index)).equals(ownStone)) {
					checkOpponentStone(board, board.above(index), ownStone);
				}
				if (board.right(index)!=-1 && !board.getField(board.right(index)).equals(ownStone)) {
					checkOpponentStone(board, board.right(index), ownStone);
				}
				if (board.below(index)!=-1 && !board.getField(board.below(index)).equals(ownStone)) {
					checkOpponentStone(board, board.below(index), ownStone);
				}
				if (board.left(index)!=-1 && !board.getField(board.left(index)).equals(ownStone)) {
					checkOpponentStone(board, board.left(index), ownStone);
				}
			}
			// if surrounded by own = capture, en alle omliggende own
			// else: recursive on all opponent stones
		}
	}


	public boolean hasLiberty(Board board, int index) { // one or more are not outside and empty
		if ( (board.above(index)!=-1 && board.isEmptyField(board.above(index))) 
			|| (board.right(index)!=-1 && board.isEmptyField(board.right(index))) 
			|| (board.below(index)!=-1 && board.isEmptyField(board.below(index)))
			|| (board.left(index)!=-1 && board.isEmptyField(board.left(index)))) {
	 return true;
		}
		return false;
	}
	
	public boolean surroundedOwn(Board board, int index, Stone ownStone) { // all are outside or own
		if ( (board.above(index)== -1 || board.getField(board.above(index))==(ownStone))
				&& (board.right(index)== -1 || board.getField(board.right(index))==(ownStone))
				&& (board.below(index)== -1 || board.getField(board.below(index))==(ownStone)) 
				&& (board.left(index)== -1 || board.getField(board.left(index))==(ownStone)) ) {
			return true;
		}
		return false;
	}
	
	private void moveToOwnUnoccupied(int index) {
		indexOwnUnoccupied.add(index);
		//indicesToCheck.removeAll(Arrays.asList(index)); // TODO https://stackoverflow.com/a/21795392
	}
	
	private void moveToOpponentFree(int index) {
		indicesOpponentFree.add(index);
		//indicesToCheck.removeAll(Arrays.asList(index)); // TODO https://stackoverflow.com/a/21795392
	}
	
	private void moveToOpponentCaptured(int index) {
		indicesOpponentCaptured.add(index);
		//indicesToCheck.removeAll(Arrays.asList(index)); // TODO https://stackoverflow.com/a/21795392
	}
//TODO: cannot remove when iterating over list, use iterator.remove
	// https://stackoverflow.com/questions/29954427/java-util-arraylistitr-checkforcomodification-exception-thrown
}
