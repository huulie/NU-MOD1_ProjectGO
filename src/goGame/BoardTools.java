package goGame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import exceptions.InvalidFieldException;

/** BoardTools, to check and execute for captures, and get the scores.
 *  Must be used by Game, to enforce the rules (local or as server)
 *  May be used as Player, to calculate the result of a certain move (mimicking expected server response)
 * @author huub.lievestro
 *
 */
public class BoardTools { 
	// TODO: take a critical look at the INSTANCE VARIABLES, add JAVADOC per var?

	private boolean printDebug;
	private Board board;

	// Instance variables, to keep track of progress when iterating board
	Iterator<Integer> boardIterator;
	private List<Integer> allIndices = new ArrayList<Integer>(); 
	List<Integer> checkedIndices = new ArrayList<Integer>();
	List<Integer> recursiveFound = new ArrayList<Integer>(); 

	// Instance variables, for capturing
	Stone ownStone;
	private List<Integer> indexOwnUnoccupied = new ArrayList<Integer>(); // TODO: do I want this to be static? 
	private List<Integer> indicesOpponentFree = new ArrayList<Integer>(); // TODO: do I want this to be static? 
	private List<Integer> indicesOpponentCaptured = new ArrayList<Integer>(); // TODO: do I want this to be static? 

	// Instance variables, for scoring
	private List<Integer> blackStoneIndices = new ArrayList<Integer>();
	private List<Integer> whiteStoneIndices = new ArrayList<Integer>();
	private List<Integer> blackAreaIndices = new ArrayList<Integer>(); 
	private List<Integer> whiteAreaIndices = new ArrayList<Integer>(); 
	private List<Integer> unoccupiedAreaIndices = new ArrayList<Integer>(); 
	Stone areaColor = null;


	/** 
	 * Create a new instance of BoardTools to use.
	 * @param printDebug to switch printing of debug messages on / off
	 */
	public BoardTools(boolean printDebug) {
		this.printDebug = printDebug;
	}

	// CAPTURE CHECKING 
	// TODO: make it more efficient / compact / readable
	// TODO: Check capture of OPPONENT stones (take priority of capture vs self-capture in mind)

	/**
	 * Check for captures of opponent's stones and excute them
	 * @param board board to check
	 * @param ownStone color of current player (opposite of opponent)
	 */
	public void doOpponentCaptures(Board board, Stone ownStone) { // TODO: return number of stones captured for AI		
		allIndices.clear();
		indexOwnUnoccupied.clear();
		indicesOpponentFree.clear();
		indicesOpponentCaptured.clear();
		checkedIndices.clear();

		this.board = board;
		int boardDim = board.getDim();

		this.ownStone = ownStone;

		allIndices.addAll(IntStream.rangeClosed(0, boardDim*boardDim-1)
				.boxed().collect(Collectors.toList())); 

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

				// thus: it's a opponent's stone
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
					if (printDebug) System.out.println("DEBUG: opponent stone is fully surrounded by own stone = single CAPTURE");
				} else { // more than one opponent stone
					if (printDebug) System.out.println("DEBUG: starting recursive search...");

					recursiveFound.clear();
					recursiveFound.add(checkingIndex);
					boolean captureAbove = true;
					boolean captureRight = true;
					boolean captureBelow = true;
					boolean captureLeft = true;

					if(board.above(checkingIndex)!= -1 && board.getField(board.above(checkingIndex))!=ownStone){
						if (printDebug) System.out.println("DEBUG: ## first search above started");
						captureAbove = checkOpponentStone(board.above(checkingIndex),0); 
					} 
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
						}
					} else {
						indicesOpponentFree.addAll(recursiveFound); 
						if (printDebug) System.out.println("DEBUG: ## end recursive search => opponent group FREE: " + recursiveFound);
					}

				}
			}
		}
	}

	/**
	 * Recursively Check for captures of opponent's stones and excute them
	 * TODO: methods rename? 
	 * @param index index to check
	 * @param searchStartingDepth indicating current search depth
	 */
	private boolean checkOpponentStone(int index, int searchStartingDepth) { 
		boolean captureAbove = true; 
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

		if (surroundedOwnRecursive(index)) { // TODO different search, taking into account linked stones
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

			if (hasLiberty(index)) { 
				if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + "/\\ stone " + index + " has liberties, so not captured");
				return false;
			} 

			if (captureAbove == true && captureRight == true && captureBelow == true && captureLeft == true) {
				if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + "/\\ opponent subgroup captured (subgroups have capture)");
				return true;
			} else {
				if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + "/\\ opponent subgroup free (subgroups have no capture)");
				return false;
			}
		}
	}

	/**
	 * Check if this stone has any liberty (= unoccupied space above/right/below/left of it)
	 * @param index current stone
	 * @return true if current stone has one or more liberties, false if not
	 */
	public boolean hasLiberty(int index) {
		if ( (board.above(index)!=-1 && board.isEmptyField(board.above(index))) 
				|| (board.right(index)!=-1 && board.isEmptyField(board.right(index))) 
				|| (board.below(index)!=-1 && board.isEmptyField(board.below(index)))
				|| (board.left(index)!=-1 && board.isEmptyField(board.left(index)))) {
			return true;
		}
		return false;
	}

	/**
	 * Check if this stone is surrounded by own stones (above, right, below and left)
	 * @param index current stone
	 * @return true if surrounded by own stones, false if not
	 */
	public boolean surroundedOwn(int index) { // all are outside or own
		if ( (board.above(index)== -1 || board.getField(board.above(index))==(ownStone))
				&& (board.right(index)== -1 || board.getField(board.right(index))==(ownStone))
				&& (board.below(index)== -1 || board.getField(board.below(index))==(ownStone)) 
				&& (board.left(index)== -1 || board.getField(board.left(index))==(ownStone)) ) {
			return true;
		}
		return false;
	}

	/**
	 * Check if this stone is surrounded by own stones (above, right, below and left), ignoring already checked stones
	 * @param index current stone
	 * @return true if surrounded by own stones, false if not
	 */
	public boolean surroundedOwnRecursive(int index) { // all are outside or own
		if ( (board.above(index)== -1 || checkedIndices.contains(board.above(index)) ||  board.getField(board.above(index))==(ownStone))
				&& (board.right(index)== -1 || checkedIndices.contains(board.right(index)) || board.getField(board.right(index))==(ownStone))
				&& (board.below(index)== -1 || checkedIndices.contains(board.below(index)) || board.getField(board.below(index))==(ownStone)) 
				&& (board.left(index)== -1 || checkedIndices.contains(board.left(index)) || board.getField(board.left(index))==(ownStone)) ) {
			return true;
		}
		return false;
	}


	private void moveToOwnUnoccupied(int index) {
		indexOwnUnoccupied.add(index);
		//indicesToCheck.removeAll(Arrays.asList(index)); // TODO https://stackoverflow.com/a/21795392
		//TODO: cannot remove when iterating over list, use iterator.remove
	}

	private void moveToOpponentFree(int index) {
		indicesOpponentFree.add(index);
		//indicesToCheck.removeAll(Arrays.asList(index)); // TODO https://stackoverflow.com/a/21795392
	}

	private void moveToOpponentCaptured(int index) {
		indicesOpponentCaptured.add(index);
		//indicesToCheck.removeAll(Arrays.asList(index)); // TODO https://stackoverflow.com/a/21795392
	}


	/**
	 * Check for captures of own stones and execute them  TODO: extend JAVADOC
	 * @param board board to check
	 * @param ownStone color of current player (opposite of opponent)
	 */
	public void doOwnCaptures(Board board, Stone ownStone) { // TODO: RENAME METHODS?
		doOpponentCaptures(board,ownStone.other());
	}


	//SCORE COUNTING
	// TODO: make it more efficient / compact / readable

	/**
	 * TODO ADD JAVADOC
	 * @param board
	 * @return String
	 */
	public String getScores(Game game) {
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
				.boxed().collect(Collectors.toList())); 

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
				blackStoneIndices.add(checkingIndex); 
				if (printDebug) System.out.println("DEBUG: stone is BLACK");
			} else if (board.getField(checkingIndex).equals(Stone.WHITE) ) {
				whiteStoneIndices.add(checkingIndex); 
				if (printDebug) System.out.println("DEBUG: stone is WHITE"); 
			} else if (board.getField(checkingIndex).equals(Stone.UNOCCUPIED) ) { 
				if (printDebug) System.out.println("DEBUG: stone is UNOCCUPIED >> needs further checking");

				areaColor = null;
				boolean hasUnoccupiedNeigbours = false;

				if (printDebug) System.out.println("DEBUG: > looking above...");
				if ( (board.above(checkingIndex)!= -1 && board.getField(board.above(checkingIndex))!=(Stone.UNOCCUPIED))) {
					//TODO also add them to corresponding list black/white?
					areaColor = board.getField(board.above(checkingIndex));
					if (printDebug) System.out.println("DEBUG: >> areaColor set to " + areaColor.toString());
				} else if ((board.above(checkingIndex)!= -1 && board.getField(board.above(checkingIndex))==(Stone.UNOCCUPIED))){
					hasUnoccupiedNeigbours = true; // TODO: start here with recursive search to above?
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
						unoccupiedAreaIndices.add(checkingIndex);
						if (printDebug) System.out.println("DEBUG: >> non-matching colors, areaColor set to neutral / U");
						continue;
					} 
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
						unoccupiedAreaIndices.add(checkingIndex);
						if (printDebug) System.out.println("DEBUG: >> non-matching colors, areaColor set to neutral / U");
						continue;
					} 
				} else if ((board.left(checkingIndex)!= -1 && board.getField(board.left(checkingIndex))==(Stone.UNOCCUPIED))){
					hasUnoccupiedNeigbours = true; // TODO: start here with recursive search to left?
					if (printDebug) System.out.println("DEBUG: >> stone has UNOCCUPIED neighbours > starting recursive search");

				}

				recursiveFound.clear();
				recursiveFound.add(checkingIndex);

				boolean oneColorAbove = true; 
				boolean oneColorRight = true;
				boolean oneColorBelow = true;
				boolean oneColorLeft = true;

				if (hasUnoccupiedNeigbours) { 
					if (printDebug) System.out.println("DEBUG: # starting recursive search");

					if(board.above(checkingIndex)!= -1 && board.getField(board.above(checkingIndex))==Stone.UNOCCUPIED){	
						if (printDebug) System.out.println("DEBUG: ## first search above started");
						oneColorAbove = recursiveScoring(board.above(checkingIndex),0);
					} 
					if(board.right(checkingIndex)!= -1 && board.getField(board.right(checkingIndex))==Stone.UNOCCUPIED){
						if (printDebug) System.out.println("DEBUG: ## first search right started");
						oneColorRight = recursiveScoring(board.right(checkingIndex),0); 
					} 
					if(board.below(checkingIndex)!= -1 && board.getField(board.below(checkingIndex))==Stone.UNOCCUPIED){
						if (printDebug) System.out.println("DEBUG: ## first search below started");
						oneColorBelow = recursiveScoring(board.below(checkingIndex),0); 
					} 
					if(board.left(checkingIndex)!= -1 && board.getField(board.left(checkingIndex))==Stone.UNOCCUPIED){
						if (printDebug) System.out.println("DEBUG: ## first search left started");
						oneColorLeft = recursiveScoring(board.left(checkingIndex),0); 
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

	/**
	 * recursively TODO ADD JAVADOC
	 * @param board
	 * @return String
	 */
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


		boolean oneColorAbove = true; 
		boolean oneColorRight = true;
		boolean oneColorBelow = true;
		boolean oneColorLeft = true;

		if(board.above(index)!= -1 && board.getField(board.above(index))==Stone.UNOCCUPIED){	
			if (printDebug) System.out.println("DEBUG: " + "==".repeat(searchDepth) + " ||-> search above started");					
			oneColorAbove = recursiveScoring(board.above(index),searchDepth); 
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
			oneColorRight = recursiveScoring(board.right(index),searchDepth); 
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
			oneColorBelow = recursiveScoring(board.below(index),searchDepth); 
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
			oneColorLeft = recursiveScoring(board.left(index),searchDepth); 
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

	/**
	 * Calculates scores from found lists TODO JAVADOC
	 * @param game
	 * @return
	 */
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

//	TODO: implement? also see GUI updater
//	/**
//	 * Returns a formatted String representation of this board.
//	 *
//	 * @return the formatted String representation of board
//	 */
//	public String StringtoStringFormatted(String board) {
//		String s = "";
//		for (int i = 0; i < DIM; i++) {
//			String row = "";
//			for (int j = 0; j < DIM; j++) {
//				row = row + " " + getField(i, j).toString() ;
//			}
//			s = s + row + "\n";
//		}
//		return s;
//	}
	
}
