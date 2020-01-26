package goUI;

import com.nedap.go.gui.GoGuiIntegrator;

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
		boardDim = connectedGUI.getBoardSize();
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

//		for ( int i = 0; i < boardDim*boardDim; i++) {
//			Stone stoneAtField = board.getField(i);
//
//			try {
//				if (stoneAtField==(Stone.WHITE) || stoneAtField==(Stone.BLACK) ) { 
//					connectedGUI.removeStone(board.index(i).getCol(), board.index(i).getRow());
//					connectedGUI.addStone(board.index(i).getCol(), board.index(i).getRow(), stoneAtField.equals(Stone.WHITE));
//				} else if (stoneAtField == Stone.UNOCCUPIED ) {
//					connectedGUI.removeStone(board.index(i).getCol(), board.index(i).getRow());
//				} else {
//					throw new Exception("ERROR GUI-UPDATER: I don't understand this Stone on this board");
//				}
//			} catch (Exception e) {
//				System.out.println(e.getLocalizedMessage());
//			}
//		}
	}

//		public updateOne(int index) { // TODO IMPLEMENT?
//			
//		}

}
