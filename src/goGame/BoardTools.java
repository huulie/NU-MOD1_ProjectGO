package goGame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import exceptions.InvalidFieldException;

/** CaptureChecker, to check for captures and execute them.
 *  Must be used by Game, to enforce the rules (local or as server)
 *  May be used as Player, to calculate the result of a certain move (mimicking expected server response)
 * @author huub.lievestro
 *
 */

public class BoardTools { 
// TODO: all static >> CANNOT BE STATIC CLASS AND HAVE INSTANCE VARIABLES
	
	private static final char ABOVE = 'a';
	private static final char RIGHT = 'r';
	private static final char BELOW = 'b';
	private static final char LEFT = 'l';
	
	private boolean printDebug;
	
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

	
	private List<Integer> blackStoneIndices = new ArrayList<Integer>(); // no removing from this list (checking using checkedIndices)
	private List<Integer> whiteStoneIndices = new ArrayList<Integer>(); // no removing from this list (checking using checkedIndices)
	// TODO same as all area indices: private List<Integer> unoccupiedStoneIndices = new ArrayList<Integer>(); // no removing from this list (checking using checkedIndices)

	private List<Integer> blackAreaIndices = new ArrayList<Integer>(); // no removing from this list (checking using checkedIndices)
	private List<Integer> whiteAreaIndices = new ArrayList<Integer>(); // no removing from this list (checking using checkedIndices)
	private List<Integer> unoccupiedAreaIndices = new ArrayList<Integer>(); // no removing from this list (checking using checkedIndices)
	Stone areaColor = null;
	
	// TODO CAPTURE CHECKING
	/**
	 * @param printDebug
	 */
	public BoardTools(boolean printDebug) {
		this.printDebug = printDebug;
	}

	public void doOpponentCaptures(Board board, Stone ownStone) { // TODO: return number of stones caputerd?		
		// Check capture of OPPONENT stones (take priority of capture vs self-capture in mind)
		// first simple implementation, TODO: check for groups
		
		
		// TODO: HERE CLEAR FOR OTHER SEARCHES?! OR MAKE NEW CHECKER? 
		allIndices.clear();
		indexOwnUnoccupied.clear();
		indicesOpponentFree.clear();
		indicesOpponentCaptured.clear();
		checkedIndices.clear();
		
		this.board = board;
		this.ownStone = ownStone;
		
		int boardDim = board.getDim();
		
		allIndices.addAll(IntStream.rangeClosed(0, boardDim*boardDim-1) // TODO: mind -1!
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
			
				// opponent's stone
			} else if (hasLiberty(checkingIndex)) {
					if (printDebug) System.out.println("DEBUG: stone " + checkingIndex + " has liberties, so not captured");
			} else { 
				if (surroundedOwn(checkingIndex)) { // TODO: dubbel, eficienter om weg te laten en alle 4 te checken? 
					moveToOpponentCaptured(checkingIndex);
					
					try {
						board.setField(checkingIndex, Stone.UNOCCUPIED);
					} catch (InvalidFieldException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					// TODO: ONLY IF ALL SURROUNDED!
					// TODO: update board should be processed/udate by GUI and/or somewhere else
					
					
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
						// above is no out-of-bounds field or own stone (then leave it true), so it is opponent: recurse
						// Stone has o 
						if (printDebug) System.out.println("DEBUG: ## first search above started");
						captureAbove = checkOpponentStone(board.above(checkingIndex),0); 
					} // TODO: no else!
					if(board.right(checkingIndex)!= -1 && board.getField(board.right(checkingIndex))!=ownStone){
						if (printDebug) System.out.println("DEBUG: ## first search right started");
						captureRight = checkOpponentStone(board.right(checkingIndex),0); 
					} 
					if(board.below(checkingIndex)!= -1 && board.getField(board.below(checkingIndex))!=ownStone){
						if (printDebug) System.out.println("DEBUG: ## first search below started");
						captureBelow = checkOpponentStone(board.below(checkingIndex),0); 
					} 
					if(board.left(checkingIndex)!= -1 && board.getField(board.left(checkingIndex))!=ownStone){
						if (printDebug) System.out.println("DEBUG: ## first search left started");
						captureLeft = checkOpponentStone(board.left(checkingIndex),0); 
					}
					
					if (captureAbove == true && captureRight == true && captureBelow == true && captureLeft == true) {
						indicesOpponentCaptured.addAll(recursiveFound); 
						if (printDebug) System.out.println("DEBUG: ## end recursive search => opponent group CAPTURE: " + recursiveFound);
						
						for (int processIndex : recursiveFound) {
						
						try {
							board.setField(processIndex, Stone.UNOCCUPIED);
						} catch (InvalidFieldException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
//						
							//checkedIndices.add(processIndex); TODO: already done in submethod
						}

					} else {
						indicesOpponentFree.addAll(recursiveFound); 
						if (printDebug) System.out.println("DEBUG: ## end recursive search => opponent group FREE: " + recursiveFound);

//						for (int processIndex : checkedIndices) {
////							checkedIndices.add(processIndex); TODO: already done in submethod
//						}
					}
					
				}
			}
			//boardIterator.remove(); // removed checked index from list
		}
	}

	private boolean checkOpponentStone(int index, int searchStartingDepth) { // Board board, , char direction TODO avoid going back to prev stone!
		boolean captureAbove = true; // if no stone found, it also cannot block capture
		boolean captureRight = true;
		boolean captureBelow = true;
		boolean captureLeft = true;
		
		int searchDepth = searchStartingDepth + 1;
		
		if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " \\/ subchecking stone at index " + index);
		
		if(checkedIndices.contains(index)) {
			if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " /\\ Already found opponent stone at index " + index + ": now skipping");
			return true; // TODO: should already checked be false or true?!
		} else {
			checkedIndices.add(index);
		}
		
		recursiveFound.add(index);
		if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " || index " + index + " added to found group");
		if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " || >> found group is now " + recursiveFound);
		
//		if (hasLiberty(index)) { // TODO: THIS IS BREAKING ANY FURTHER RECURSIVE SEARCH IS ONLY ONE ADJECTED STONE IS U
//			if (printDebug) System.out.println("DEBUG: stone " + index + " has liberties, so not captured");
//			//moveToOpponentFree(index);
//			return false;
//		} else {

			if (surroundedOwnRecursive(index)) { // different search, taking into account linked stones
				//moveToOpponentCaptured(index);
				//board.setField(index, Stone.UNOCCUPIED); // TODO: ONLY IF ALL SURROUNDED!
//				recursiveFound.add(index); ALL STONES SHOULD BE ADDED TO FOUND GROUP
//				System.out.println("Index " + index + "added to set to unoccupied");

				if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " /\\ opponent subgroup captured (surrounded)");
				return true;
			} else {
				if (board.above(index)!=-1 && !board.getField(board.above(index)).equals(ownStone) && !board.isEmptyField(board.above(index))) { //TODO empty
					if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " ||-> search above started");
					captureAbove = checkOpponentStone(board.above(index),searchDepth);
					if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " ||-> search above done");
				}
				if (board.right(index)!=-1 && !board.getField(board.right(index)).equals(ownStone) && !board.isEmptyField(board.right(index))) {
					if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " ||-> search right started");
					captureRight = checkOpponentStone(board.right(index),searchDepth);
					if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " ||-> search right done");
				}
				if (board.below(index)!=-1 && !board.getField(board.below(index)).equals(ownStone) && !board.isEmptyField(board.below(index))) {
					if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " ||-> search below started");
					captureBelow = checkOpponentStone(board.below(index),searchDepth);
					if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " ||-> search below done");
				}
				if (board.left(index)!=-1 && !board.getField(board.left(index)).equals(ownStone) && !board.isEmptyField(board.left(index))) {
					if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " ||-> search left started");
					captureLeft = checkOpponentStone(board.left(index),searchDepth);
					if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " ||-> search left done");
				}
				
				if (hasLiberty(index)) { // TODO: THIS IS BREAKING ANY FURTHER RECURSIVE SEARCH IS ONLY ONE ADJECTED STONE IS U
					if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + "/\\ stone " + index + " has liberties, so not captured");
					//moveToOpponentFree(index);
					return false;
				} 
				
				if (captureAbove == true && captureRight == true && captureBelow == true && captureLeft == true) {
					if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + "/\\ opponent subgroup captured (subgroups have capture)");
//					recursiveFound.add(index); ALL STONES SHOULD BE ADDED TO FOUND GROUP
//					System.out.println("Index " + index + "added to set to unoccupied");
					return true;
				} else {
					if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + "/\\ opponent subgroup free (subgroups have no capture)");
//					recursiveFound.add(index); ALL STONES SHOULD BE ADDED TO FOUND GROUP
					return false;
				}
			}
			// if surrounded by own = capture, en alle omliggende own
			// else: recursive on all opponent stones
		}
//	}


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
	
	
	// TODO: RENAME METHODS?
	public void doOwnCaptures(Board board, Stone ownStone) {
		doOpponentCaptures(board,ownStone.other());
	}
	
	
	// TODO SCORE COUNTING

	/**
	 * TODO ADD JAVADOC
	 * @param board
	 * @return
	 */
	public String getScores(Game game) {
		// TODO: HERE CLEAR FOR OTHER SEARCHES?! OR MAKE NEW CHECKER? 
		allIndices.clear();
		checkedIndices.clear();
		
		blackStoneIndices.clear();
		whiteStoneIndices.clear();

		blackAreaIndices.clear(); 
		whiteAreaIndices.clear(); 
		unoccupiedAreaIndices.clear();
		
		this.board = game.getBoard();
		int boardDim = board.getDim();

		allIndices.addAll(IntStream.rangeClosed(0, boardDim*boardDim-1) 
				.boxed().collect(Collectors.toList())); // do NOT use ^ for squaring

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

			if (board.getField(checkingIndex).equals(Stone.BLACK) ) {
				blackStoneIndices.add(checkingIndex); // and do nothing
				if (printDebug) System.out.println("DEBUG: stone is BLACK");
			} else if (board.getField(checkingIndex).equals(Stone.WHITE) ) {
				whiteStoneIndices.add(checkingIndex); // and do nothing
				if (printDebug) System.out.println("DEBUG: stone is WHITE"); 
			} else if (board.getField(checkingIndex).equals(Stone.UNOCCUPIED) ) { // UNOCCUPIED stone
				if (printDebug) System.out.println("DEBUG: stone is UNOCCUPIED >> needs further checking");

				//				Stone areaColor = null;
				areaColor = null;
				boolean hasUnoccupiedNeigbours = false;

				if (printDebug) System.out.println("DEBUG: > looking above...");
				if ( (board.above(checkingIndex)!= -1 && board.getField(board.above(checkingIndex))!=(Stone.UNOCCUPIED))) {
					//TODO also add them to corresponding list black/white?
					areaColor = board.getField(board.above(checkingIndex));
					if (printDebug) System.out.println("DEBUG: >> areaColor set to " + areaColor.toString());
				} else if ((board.above(checkingIndex)!= -1 && board.getField(board.above(checkingIndex))==(Stone.UNOCCUPIED))){
					hasUnoccupiedNeigbours = true; // TODO: start here with recursive search to abovve?
					if (printDebug) System.out.println("DEBUG: >> stone has UNOCCUPIED neighbours > starting recursive search");
				}

				if (printDebug) System.out.println("DEBUG: > looking right...");
				if ( (board.right(checkingIndex)!= -1 && board.getField(board.right(checkingIndex))!=(Stone.UNOCCUPIED))) {
					if(areaColor == null) {
						areaColor = board.getField(board.right(checkingIndex));
						if (printDebug) System.out.println("DEBUG: >> areaColor set to " + areaColor.toString());
					} else if(areaColor != board.getField(board.right(checkingIndex))) {
						// field becomes neutral / U area
						unoccupiedAreaIndices.add(checkingIndex);
						if (printDebug) System.out.println("DEBUG: >> non-matching colors, areaColor set to neutral / U");
						continue;
					} 
					// else groupcolor stays the same, and continue checking
				} else if ((board.right(checkingIndex)!= -1 && board.getField(board.right(checkingIndex))==(Stone.UNOCCUPIED))){
					hasUnoccupiedNeigbours = true; // TODO: start here with recursive search to right?
					if (printDebug) System.out.println("DEBUG: >> stone has UNOCCUPIED neighbours > starting recursive search");
				}

				if (printDebug) System.out.println("DEBUG: > looking below...");
				if ( (board.below(checkingIndex)!= -1 && board.getField(board.below(checkingIndex))!=(Stone.UNOCCUPIED))) {
					if(areaColor == null) {
						areaColor = board.getField(board.below(checkingIndex));
						if (printDebug) System.out.println("DEBUG: >> areaColor set to " + areaColor.toString());
					} else if(areaColor != board.getField(board.below(checkingIndex))) {
						// field becomes neutral / U area
						unoccupiedAreaIndices.add(checkingIndex);
						if (printDebug) System.out.println("DEBUG: >> non-matching colors, areaColor set to neutral / U");
						continue;
					} 
					// else groupcolor stays the same, and continue checking
				} else if ((board.below(checkingIndex)!= -1 && board.getField(board.below(checkingIndex))==(Stone.UNOCCUPIED))){
					hasUnoccupiedNeigbours = true; // TODO: start here with recursive search to below?
					if (printDebug) System.out.println("DEBUG: >> stone has UNOCCUPIED neighbours > starting recursive search");

				}

				if (printDebug) System.out.println("DEBUG: > looking left...");
				if ( (board.left(checkingIndex)!= -1 && board.getField(board.left(checkingIndex))!=(Stone.UNOCCUPIED))) {
					if(areaColor == null) {
						areaColor = board.getField(board.left(checkingIndex));
						if (printDebug) System.out.println("DEBUG: >> areaColor set to " + areaColor.toString());
					} else if(areaColor != board.getField(board.left(checkingIndex))) {
						// field becomes neutral / U area
						unoccupiedAreaIndices.add(checkingIndex);
						if (printDebug) System.out.println("DEBUG: >> non-matching colors, areaColor set to neutral / U");
						continue;
					} 
					// else groupcolor stays the same, and continue checking
				} else if ((board.left(checkingIndex)!= -1 && board.getField(board.left(checkingIndex))==(Stone.UNOCCUPIED))){
					hasUnoccupiedNeigbours = true; // TODO: start here with recursive search to left?
					if (printDebug) System.out.println("DEBUG: >> stone has UNOCCUPIED neighbours > starting recursive search");

				}

				recursiveFound.clear();
				recursiveFound.add(checkingIndex);

				boolean oneColorAbove = true; // if no stone found, it also cannot block capture
				boolean oneColorRight = true;
				boolean oneColorBelow = true;
				boolean oneColorLeft = true;

				if (hasUnoccupiedNeigbours) { // recursive search needed
					if (printDebug) System.out.println("DEBUG: # starting recursive search");

					if(board.above(checkingIndex)!= -1 && board.getField(board.above(checkingIndex))==Stone.UNOCCUPIED){	
						if (printDebug) System.out.println("DEBUG: ## first search above started");
						oneColorAbove = recursiveScoring(board.above(checkingIndex),0); // ,areaColor
					} // TODO: no else!
					if(board.right(checkingIndex)!= -1 && board.getField(board.right(checkingIndex))==Stone.UNOCCUPIED){
						if (printDebug) System.out.println("DEBUG: ## first search right started");
						oneColorRight = recursiveScoring(board.right(checkingIndex),0); // ,areaColor
					} 
					if(board.below(checkingIndex)!= -1 && board.getField(board.below(checkingIndex))==Stone.UNOCCUPIED){
						if (printDebug) System.out.println("DEBUG: ## first search below started");
						oneColorBelow = recursiveScoring(board.below(checkingIndex),0); // ,areaColor
					} 
					if(board.left(checkingIndex)!= -1 && board.getField(board.left(checkingIndex))==Stone.UNOCCUPIED){
						if (printDebug) System.out.println("DEBUG: ## first search left started");
						oneColorLeft = recursiveScoring(board.left(checkingIndex),0); // ,areaColor
					}
				}

				if (!(oneColorAbove == true && oneColorRight == true && oneColorBelow == true && oneColorLeft == true)) {
					unoccupiedAreaIndices.addAll(recursiveFound);
					if (printDebug) System.out.println("DEBUG: # end recursive search => Area assigned to neutral: " + recursiveFound);
				} else if (areaColor == Stone.BLACK) {
					blackAreaIndices.addAll(recursiveFound);
					if (printDebug) System.out.println("DEBUG: # end recursive search => Area assigned to black: " + recursiveFound);
				} else if (areaColor == Stone.WHITE) {
					whiteAreaIndices.addAll(recursiveFound);
					if (printDebug) System.out.println("DEBUG: # end recursive search => Area assigned to white: " + recursiveFound);
				} else {
					if (printDebug) System.out.println("DEBUG: # end recursive search => something weird happend with: " + recursiveFound);
				}

			}
		}

		return calculateScore(game);
	}
			
	private boolean recursiveScoring(int index, int searchStartingDepth) { // , Stone areaColor 
		int searchDepth = searchStartingDepth + 1;

		if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " \\/ subchecking stone at index " + index);

		if(checkedIndices.contains(index)) {
			if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " /\\ Already found UNOCCUPIED stone at index " + index + ": now skipping");
			return true; // TODO: should already checked be false or true?!
		} else {
			checkedIndices.add(index);
		}

		recursiveFound.add(index);
		if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " || index " + index + " added to found group");
		if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " || >> found group is now " + recursiveFound);


		boolean oneColorAbove = true; // if no stone found, it also cannot block capture
		boolean oneColorRight = true;
		boolean oneColorBelow = true;
		boolean oneColorLeft = true;

		if(board.above(index)!= -1 && board.getField(board.above(index))==Stone.UNOCCUPIED){	
			if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " ||-> search above started");					
			oneColorAbove = recursiveScoring(board.above(index),searchDepth); // ,areaColor
			if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " ||-> search above done");
		} else if(board.above(index)!= -1 && areaColor == null) {
			this.areaColor = board.getField(board.above(index));
			if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " /\\ stone above of " + index + ": areaColor set to " + areaColor);
			return true;
		} else if(board.above(index)!= -1 && areaColor != board.getField(board.above(index))) {
			if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " /\\ stone " + index + " has different neighbouring colors, so assigned to neutral");
			return false;
		}
		
		if(board.right(index)!= -1 && board.getField(board.right(index))==Stone.UNOCCUPIED){
			if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " ||-> search right started");						
			oneColorRight = recursiveScoring(board.right(index),searchDepth); // ,areaColor
			if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " ||-> search right done");
		} else if(board.right(index)!= -1 && areaColor == null) {
			this.areaColor = board.getField(board.right(index));
			if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " /\\ stone right of " + index + ": areaColor set to " + areaColor);
			return true;
		} else if(board.right(index)!= -1 && areaColor != board.getField(board.right(index))) {
			if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " /\\ stone " + index + " has different neighbouring colors, so assigned to neutral");
			return false;
		}
		
		if(board.below(index)!= -1 && board.getField(board.below(index))==Stone.UNOCCUPIED){
			if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " ||-> search below started");
			oneColorBelow = recursiveScoring(board.below(index),searchDepth); // ,areaColor
			if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " ||-> search below done");
		} else if(board.below(index)!= -1 && areaColor == null) {
			this.areaColor = board.getField(board.below(index));
			if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " /\\ stone below of " + index + ": areaColor set to " + areaColor);
			return true;
		} else if(board.below(index)!= -1 && areaColor != board.getField(board.below(index))) {
			if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " /\\ stone " + index + " has different neighbouring colors, so assigned to neutral");
			return false;
		}
		
		if(board.left(index)!= -1 && board.getField(board.left(index))==Stone.UNOCCUPIED){
			if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " ||-> search left started");					
			oneColorLeft = recursiveScoring(board.left(index),searchDepth); // ,areaColor
			if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " ||-> search left done");
		} else if(board.left(index)!= -1 && areaColor == null) {
			this.areaColor = board.getField(board.left(index));
			if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " /\\ stone left of" + index + ": areaColor set to " + areaColor);
			return true;
		} else if(board.left(index)!= -1 && areaColor != board.getField(board.left(index))) {
			if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " /\\ stone " + index + " has different neighbouring colors, so assigned to neutral");
			return false;
		}

		if (!(oneColorAbove == true && oneColorRight == true && oneColorBelow == true && oneColorLeft == true)) {
			if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " /\\ stone " + index + ": a connected stone has different neighbouring colors, so assigned to neutral");
			return false;
		} else {
			if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " /\\ stone " + index + ": a connected stone has same neighbouring colors, so may be assigned to black/white area");
			return true;	
		}



	}

			
			private String calculateScore(Game game) {
				
				
				double scoreBlack = blackStoneIndices.size() + blackAreaIndices.size();
				double scoreWhite = whiteStoneIndices.size() + whiteAreaIndices.size()+ game.getKomi();
				
				String score = scoreBlack + GoGameConstants.DELIMITER + scoreWhite;
				
				if (printDebug) {
					int boardDim = game.getBoard().getDim();
					
					if (scoreBlack + scoreWhite + unoccupiedAreaIndices.size() - game.getKomi() == boardDim*boardDim) {
						 System.out.println("DEBUG: number of intersections checksum: OK");
					} else {
						System.out.println("DEBUG: number of intersections checksum: FAILURE");
					}
				}
				return score;
			}
}
