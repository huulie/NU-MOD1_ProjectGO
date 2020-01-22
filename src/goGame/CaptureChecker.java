package goGame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** CaptureChecker, to check for captures and execute them.
 *  Must be used by Game, to enforce the rules (local or as server)
 *  May be used as Player, to calculate the result of a certain move (mimicking expected server response)
 * @author huub.lievestro
 *
 */

public class CaptureChecker {

// TODO: all static >> CANNOT BE STATIC CLASS AND HAVE INSTANCE VARIABLES
	
	private static final char ABOVE = 'a';
	private static final char RIGHT = 'r';
	private static final char BELOW = 'b';
	private static final char LEFT = 'l';
	
	private boolean printDebug = true;
	
	private Board board;
	
	Stone ownStone;
	
	Iterator<Integer> boardIterator;
	// Instance variables, to keep track of progress
	private List<Integer> allIndices = new ArrayList<Integer>(); // no removing from this list (checking using checkedIndices)
	private List<Integer> indexOwnUnoccupied = new ArrayList<Integer>(); // TODO: do I want this to be static? 
	private List<Integer> indicesOpponentFree = new ArrayList<Integer>(); // TODO: do I want this to be static? 
	private List<Integer> indicesOpponentCaptured = new ArrayList<Integer>(); // TODO: do I want this to be static? 

	List<Integer> checkedIndices = new ArrayList<Integer>();
	List<Integer> recursiveFound = new ArrayList<Integer>(); // TODO; separate, to know which stones to transer after recursive search


	public void doOpponentCaptures(Board board, Stone ownStone) {		
		// Check capture of OPPONENT stones (take priority of capture vs self-capture in mind)
		// first simple implementation, TODO: check for groups
		
		this.board = board;
		this.ownStone = ownStone;
		
		allIndices.addAll(IntStream.rangeClosed(0, board.DIM*board.DIM-1) // TDO: mind -1!
				.boxed().collect(Collectors.toList())); // do NOT use ^ for squaring

		//Iterator<Integer> boardIterator = indicesToCheck.iterator();
		boardIterator = allIndices.iterator();

		while (boardIterator.hasNext()) {

			int checkingIndex = boardIterator.next();
			
			if(checkedIndices.contains(checkingIndex)) {
				if (printDebug) System.out.println("DEBUG: Already checked this stone at index " + checkingIndex + ": now skipping");
				continue;
			} else { 
				checkedIndices.add(checkingIndex);
			}
			if (printDebug) System.out.println("DEBUG: checking stone at index " + checkingIndex);
			
			if (board.getField(checkingIndex).equals(ownStone) 
					|| board.getField(checkingIndex).equals(Stone.UNOCCUPIED)) {
				moveToOwnUnoccupied(checkingIndex); // and do nothing
				if (printDebug) System.out.println("DEBUG: stone is own or unoccupied");
			} else { // opponent's stone
				if (surroundedOwn(checkingIndex)) { // TODO: dubbel, eficienter om weg te laten en alle 4 te checken? 
					moveToOpponentCaptured(checkingIndex);
					board.setField(checkingIndex, Stone.UNOCCUPIED); // TODO: ONLY IF ALL SURROUNDED!
						// TODO: update board should be processed by GUI and/or somewhere else
					if (printDebug) System.out.println("DEBUG: opponent stone is fully surrounded by own stone = single CAPTURE");
				} else { // more than one opponent stone
				if (printDebug) System.out.println("DEBUG: starting recursive search...");
				
				recursiveFound.clear(); // TODO: not nessecary (all unique), but avoid setting all recursive found to U again and again
				recursiveFound.add(checkingIndex);
				boolean captureAbove = true; // if no stone found, it also cannot block capture
				boolean captureRight = true;
				boolean captureBelow = true;
				boolean captureLeft = true;
				
					if(board.above(checkingIndex)!= -1 && board.getField(board.above(checkingIndex))!=ownStone){ // TODO AND
						// above is no empty field or own stone (then leave it true), so it is opponent: recurse
						if (printDebug) System.out.println("DEBUG: first search above started");
						captureAbove = checkOpponentStone(board.above(checkingIndex)); 
					} // TODO: no else!
					if(board.right(checkingIndex)!= -1 && board.getField(board.right(checkingIndex))!=ownStone){
						if (printDebug) System.out.println("DEBUG: first search right started");
						captureRight = checkOpponentStone(board.right(checkingIndex)); 
					} 
					if(board.below(checkingIndex)!= -1 && board.getField(board.below(checkingIndex))!=ownStone){
						if (printDebug) System.out.println("DEBUG: first search below started");
						captureBelow = checkOpponentStone(board.below(checkingIndex)); 
					} 
					if(board.left(checkingIndex)!= -1 && board.getField(board.left(checkingIndex))!=ownStone){
						if (printDebug) System.out.println("DEBUG: first search left started");
						captureLeft = checkOpponentStone(board.left(checkingIndex)); 
					}
					
					if (captureAbove == true && captureRight == true && captureBelow == true && captureLeft == true) {
						indicesOpponentCaptured.addAll(recursiveFound); 
						if (printDebug) System.out.println("DEBUG: end recursive search => opponent group (recursive) CAPTURE");
						
						for (int processIndex : recursiveFound) {
						board.setField(processIndex, Stone.UNOCCUPIED);
//						checkedIndices.add(processIndex); TODO: already done in submethod
						}

					} else {
						indicesOpponentFree.addAll(recursiveFound); 
						if (printDebug) System.out.println("DEBUG: end recursive search => opponent group (recursive) FREE");

//						for (int processIndex : checkedIndices) {
////							checkedIndices.add(processIndex); TODO: already done in submethod
//						}
					}
					
				}
			}
			//boardIterator.remove(); // removed checked index from list
		}
	}

	private boolean checkOpponentStone(int index) { // Board board, , char direction TODO avoid going back to prev stone!
		boolean captureAbove = true; // if no stone found, it also cannot block capture
		boolean captureRight = true;
		boolean captureBelow = true;
		boolean captureLeft = true;
		
		if (printDebug) System.out.println("DEBUG: subchecking stone at index " + index);
		
		if(checkedIndices.contains(index)) {
			if (printDebug) System.out.println("DEBUG: Already found opponent stone at index " + index + ": now skipping");
			return true; // TODO: should already checked be false or true?!
		} else {
			checkedIndices.add(index);
		}
		
		if (hasLiberty(index)) {
			if (printDebug) System.out.println("DEBUG: stone " + index + " has liberties, so not captured");
			//moveToOpponentFree(index);
			return false;
		} else {

			if (surroundedOwnRecursive(index)) { // different search, taking into account linked stones
				//moveToOpponentCaptured(index);
				//board.setField(index, Stone.UNOCCUPIED); // TODO: ONLY IF ALL SURROUNDED!
				recursiveFound.add(index);
				if (printDebug) System.out.println("DEBUG: opponent subgroup captured (surrounded)");
				return true;
			} else {
				if (board.above(index)!=-1 && !board.getField(board.above(index)).equals(ownStone)) { //TODO empty
					if (printDebug) System.out.println("DEBUG: search above started");
					captureAbove = checkOpponentStone(board.above(index));
					if (printDebug) System.out.println("DEBUG: search above done");
				}
				if (board.right(index)!=-1 && !board.getField(board.right(index)).equals(ownStone)) {
					if (printDebug) System.out.println("DEBUG: search right started");
					captureRight = checkOpponentStone(board.right(index));
					if (printDebug) System.out.println("DEBUG: search right done");
				}
				if (board.below(index)!=-1 && !board.getField(board.below(index)).equals(ownStone)) {
					if (printDebug) System.out.println("DEBUG: search below started");
					captureBelow = checkOpponentStone(board.below(index));
					if (printDebug) System.out.println("DEBUG: search below done");
				}
				if (board.left(index)!=-1 && !board.getField(board.left(index)).equals(ownStone)) {
					if (printDebug) System.out.println("DEBUG: search left started");
					captureLeft = checkOpponentStone(board.left(index));
					if (printDebug) System.out.println("DEBUG: search left done");
				}
				
				if (captureAbove == true && captureRight == true && captureBelow == true && captureLeft == true) {
					if (printDebug) System.out.println("DEBUG: opponent subgroup captured (subgroups have capture)");
					return true;
				} else {
					if (printDebug) System.out.println("DEBUG: opponent subgroup free (subgroups have no capture)");
					return false;
				}
			}
			// if surrounded by own = capture, en alle omliggende own
			// else: recursive on all opponent stones
		}
	}


	public boolean hasLiberty(int index) { // one or more are not outside and empty
		if ( (board.above(index)!=-1 && board.isEmptyField(board.above(index))) 
			|| (board.right(index)!=-1 && board.isEmptyField(board.right(index))) 
			|| (board.below(index)!=-1 && board.isEmptyField(board.below(index)))
			|| (board.left(index)!=-1 && board.isEmptyField(board.left(index)))) {
	 return true;
		}
		return false;
	}
	
	public boolean surroundedOwn(int index) { // all are outside or own
		if ( (board.above(index)== -1 || board.getField(board.above(index))==(ownStone))
				&& (board.right(index)== -1 || board.getField(board.right(index))==(ownStone))
				&& (board.below(index)== -1 || board.getField(board.below(index))==(ownStone)) 
				&& (board.left(index)== -1 || board.getField(board.left(index))==(ownStone)) ) {
			return true;
		}
		return false;
	}
	
	public boolean surroundedOwnRecursive(int index) { // all are outside or own
		if ( (board.above(index)== -1 || checkedIndices.contains(board.above(index)) ||  board.getField(board.above(index))==(ownStone))
				&& (board.right(index)== -1 || checkedIndices.contains(board.right(index)) || board.getField(board.right(index))==(ownStone))
				&& (board.below(index)== -1 || checkedIndices.contains(board.below(index)) || board.getField(board.below(index))==(ownStone)) 
				&& (board.left(index)== -1 || checkedIndices.contains(board.left(index)) || board.getField(board.left(index))==(ownStone)) ) {
			return true;
		}
		return false;
	}
	
//	/**
//	 * 
//	 * @param board
//	 * @param index
//	 * @param ownStone
//	 * @param direction as seen from previous stone (e.g.: checking stone to the left, so now on the right)
//	 * @return
//	 */
//	
//	public boolean surroundedOwn(Board board, int index, Stone ownStone, char direction) { // all are outside, previous stone or own
//		if ( (board.above(index)== -1 || direction == BELOW || board.getField(board.above(index))==(ownStone))
//				&& (board.right(index)== -1 || board.getField(board.right(index))==(ownStone))
//				&& (board.below(index)== -1 || board.getField(board.below(index))==(ownStone)) 
//				&& (board.left(index)== -1 || board.getField(board.left(index))==(ownStone)) ) {
//			return true;
//		}
//		return false;
//	}
	
	private void moveToOwnUnoccupied(int index) {
		indexOwnUnoccupied.add(index);
		//indicesToCheck.removeAll(Arrays.asList(index)); // TODO https://stackoverflow.com/a/21795392
		//boardIterator.remove(index);
		 //.remove(Integer.valueOf(index));
		
	}
	
	private void moveToOpponentFree(int index) {
		indicesOpponentFree.add(index);
		//indicesToCheck.removeAll(Arrays.asList(index)); // TODO https://stackoverflow.com/a/21795392
		//boardIterator.remove(index);

	}
	
	private void moveToOpponentCaptured(int index) {
		indicesOpponentCaptured.add(index);
		//indicesToCheck.removeAll(Arrays.asList(index)); // TODO https://stackoverflow.com/a/21795392
		//boardIterator.remove(index);

	}
//TODO: cannot remove when iterating over list, use iterator.remove
	// https://stackoverflow.com/questions/29954427/java-util-arraylistitr-checkforcomodification-exception-thrown
}
