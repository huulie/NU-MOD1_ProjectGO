package goUI;

import com.nedap.go.gui.GoGuiIntegrator;

import goGame.Board;
import goGame.Stone;

public class GoGuiUpdater {

	private GoGuiIntegrator connectedGUI;
	
	//private Board currentBoardState = null;

	// private Player connectedPlayer;	
	
	/**
	 * @param connectedGUI
	 * @param currentBoardState
	 */
	public GoGuiUpdater(GoGuiIntegrator connectedGUI) { //, Board currentBoardState) {
		super();
		this.connectedGUI = connectedGUI;
		//this.currentBoardState = currentBoardState;
	}
	
	
//	public void updatePlayerBoard() {
//		this.connectedPlayer.
//		this.game.
//	}
	
	public void updateWholeBoard(Board board) {
		for ( int i =0; i < board.getDim()*board.getDim(); i++) {
			Stone stoneAtField = board.getField(i);
			
			Stone test = Stone.WHITE;
			
			try {
			if (stoneAtField==(Stone.WHITE) || stoneAtField==(Stone.BLACK) ) { // TODO: use ==instead of equal, to compare values instead of individual objects
				connectedGUI.removeStone(board.index(i).getCol(), board.index(i).getRow());
				connectedGUI.addStone(board.index(i).getCol(), board.index(i).getRow(), stoneAtField.equals(Stone.WHITE));
			} else if (stoneAtField == Stone.UNOCCUPIED ) { // TODO: use ==instead of equal, to compare values instead of individual objects
				connectedGUI.removeStone(board.index(i).getCol(), board.index(i).getRow());
			} else {
			throw new Exception("ERROR GUI-UPDATER: I don't understand this Stone on this board");
			}
			} catch (Exception e) {
				System.out.println(e.getLocalizedMessage());
			}
		}
		
		
		
		
	}

//	public void updateWholeBoard(String board, int DIM) {
//		currentBoardState = board.toString();
//		updateWholeBoard(currentBoardState, board.getDim());
//	}
//	
//	public updateOne(int index) {
//		
//	}
	
}
