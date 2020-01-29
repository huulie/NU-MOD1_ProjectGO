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

	public void updateWholeBoard(Board board) {
		for ( int i = 0; i < boardDim*boardDim; i++) {
			Stone stoneAtField = board.getField(i);

			try {
				if (stoneAtField==(Stone.WHITE) || stoneAtField==(Stone.BLACK) ) { 
					connectedGUI.removeStone(board.index(i).getCol(), board.index(i).getRow());
					connectedGUI.addStone(board.index(i).getCol(), board.index(i).getRow(), stoneAtField.equals(Stone.WHITE));
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

	public void updateWholeBoard(String boardString) {
		// TODO: add conversion from string to board here or in board?!
		// HERE, because cannot create a new board (will not be connected to the game)

		for ( int i = 0; i < boardDim*boardDim; i++) {
			Stone stoneAtField = Stone.charToStone(boardString.charAt(i));
			
			int row = i / boardDim; // TODO: check if safe enough
			int col = i % boardDim; // TODO: check if safe enough
			
			
			try {
				if (stoneAtField==(Stone.WHITE) || stoneAtField==(Stone.BLACK) ) { 
					connectedGUI.removeStone(col, row);
					connectedGUI.addStone(col, row, stoneAtField.equals(Stone.WHITE));
				} else if (stoneAtField == Stone.UNOCCUPIED ) {
					connectedGUI.removeStone(col, row);
				} else {
					throw new Exception("ERROR GUI-UPDATER: I don't understand this Stone on this board");
				}
			} catch (Exception e) {
				System.out.println(e.getLocalizedMessage());
			}
		}
	}

	/**
	 * TODO doc
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
		
		if(lastMove >= 0 && lastMove <boardDim*boardDim) {
			int row = lastMove / boardDim; // TODO: check if safe enough
			int col = lastMove % boardDim; // TODO: check if safe enough
			connectedGUI.addHintIndicator(row, col);
		}
	}

//		public updateOne(int index) { // TODO IMPLEMENT?
//			
//		}

}
