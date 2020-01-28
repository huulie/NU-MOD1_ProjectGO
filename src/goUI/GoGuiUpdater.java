package goUI;

import com.nedap.go.gui.GoGUIIntegrator;

import goGame.Board;
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
	private GoGUIIntegrator connectedGUI;

	/**
	 * Dimensions of associated GUI
	 */
	private int boardDim;

	/**
	 * Create a new GUI updater
	 * @param connectedGUI associated GUI to be updated
	 */
	public GoGuiUpdater(GoGUIIntegrator connectedGUI, int boardDim) { 
		this.connectedGUI = connectedGUI;
		this.boardDim = boardDim; 
		// JAR-file has different implementation then source code in repo:
		// "connectedGUI.getBoardSize();" is NOT supported 
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

//		public updateOne(int index) { // TODO IMPLEMENT?
//			
//		}

}
