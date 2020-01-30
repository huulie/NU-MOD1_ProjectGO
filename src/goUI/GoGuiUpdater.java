package goUI;

import com.nedap.go.gui.GoGuiIntegrator;

import goGame.Board;
import goGame.GoGameConstants;
import goGame.Stone;

/**
 * Class helping to update an associated GUI
 * @author huub.lievestro
 *
 */
public class GoGuiUpdater {

	/**
	 * associated GUI to be updated
	 */
	private GoGuiIntegrator connectedGUI;

	/**
	 * Dimensions of associated GUI
	 */
	private int boardDim;

	/**
	 * Create a new GUI updater
	 * @param connectedGUI associated GUI to be updated
	 */
	public GoGuiUpdater(GoGuiIntegrator connectedGUI) { 
		this.connectedGUI = connectedGUI;
		this.boardDim = connectedGUI.getBoardSize(); 
	}

	/**
	 * TODO doc
	 * @param board
	 */
	public void updateWholeBoard(Board board) {
		for (int i = 0; i < boardDim * boardDim; i++) {
			Stone stoneAtField = board.getField(i);

			try {
				if (stoneAtField == (Stone.WHITE) || stoneAtField == (Stone.BLACK)) { 
					connectedGUI.removeStone(board.index(i).getCol(), board.index(i).getRow());
					connectedGUI.addStone(board.index(i).getCol(),
							board.index(i).getRow(), stoneAtField.equals(Stone.WHITE));
				} else if (stoneAtField == Stone.UNOCCUPIED ) {
					connectedGUI.removeStone(board.index(i).getCol(), board.index(i).getRow());
				} else {
					throw new Exception("ERROR GUI-UPDATER: I don't understand this Stone on this board");
				}
			} catch (Exception e) {
				System.out.println(e.getLocalizedMessage());
			}
		}

	}

	/**
	 * Updates whole board from String
	 * Note: a new board is created, which is NOT associated with a Game
	 * @param boardString
	 */
	public void updateWholeBoard(String boardString) {
		Board newBoard = Board.newBoardFromString(boardString);
		this.updateWholeBoard(newBoard);	
	}

	/**
	 * Sets marker at index of last move of opponent
	 * @param opponentLastMove
	 */
	public void setMarkerAtOpponent(String opponentLastMove) {
		connectedGUI.removeHintIdicator();
		
		int lastMove = GoGameConstants.NOMOVEint;
					
		try {
			lastMove = Integer.parseInt(opponentLastMove);
		} catch (NumberFormatException eFormat) {
			System.out.println("DEBUG opponent move was no int"); // TODO remove or make nice
		}
		
		if (lastMove >= 0 && lastMove < boardDim * boardDim ) {
			int row = lastMove / boardDim; // TODO: check if safe enough
			int col = lastMove % boardDim; // TODO: check if safe enough
			connectedGUI.addHintIndicator(col, row);
		}
	}

}
