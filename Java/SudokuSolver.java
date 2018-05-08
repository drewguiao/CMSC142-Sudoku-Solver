import javax.swing.JTextField;
import javax.swing.JOptionPane;
class SudokuSolver{

	private static final int EMPTY = 0;

	public SudokuSolver(){}


	public int[][] translateConfigurationToBoard(JTextField[][] grid, int boardSize){
		int[][] board = new int[boardSize][boardSize];
		try{
			for(int i = 0; i < boardSize; i++){
				for(int j = 0; j < boardSize; j++){
					String cellText = grid[i][j].getText();
					if(cellText.equals("")) board[i][j] = EMPTY;
					else {
						board[i][j] = Integer.parseInt(cellText);
						if(board[i][j] > boardSize || board[i][j] < 1) throw new NumberFormatException("Not a valid number: board("+i+","+j+"):"+board[i][j]);
					}
				}
			}
		}catch(NumberFormatException nfe){
			JOptionPane.showMessageDialog(null, "Invalid element!");
		}

		return board;
	}

	public boolean isALegitimateSolution(JTextField[][] grid, int subGridSize){
		int boardSize = subGridSize * subGridSize;
		int[][] board = translateConfigurationToBoard(grid, boardSize);

		if(!isFull(board,subGridSize)) return false;

		for(int i = 0; i < boardSize; i++){
			for(int j = 0; j < boardSize; j++){
				if(!isViable(board, i, j, subGridSize)) return false;
			}
		}


		return true;
	}

	public boolean isFull(int[][] board, int boardSize){
		for(int i = 0; i < boardSize; i++){
			for(int j = 0; j < boardSize; j++){
				if(board[i][j] == EMPTY) return false;
			}
		}
		return true;
	}

	private boolean isViable(int[][] board, int rowIndex, int columnIndex, int subGridSize){
		int boardSize = subGridSize * subGridSize;
	
		if(!isViableInRowAndColumn(board,rowIndex,columnIndex,boardSize)) return false;
		if(!isViableInSubGrid(board,rowIndex,columnIndex,subGridSize)) return false;
		return true;
	}

	private boolean isViableInRowAndColumn(int[][] board, int rowIndex, int columnIndex, int boardSize){
		for(int i = 0; i < boardSize; i++){
			if(board[rowIndex][i] == board[rowIndex][columnIndex] && i != columnIndex) return false;
			if(board[i][columnIndex] == board[rowIndex][columnIndex] && i != rowIndex) return false;
		}
		return true;
	}

	private boolean isViableInSubGrid(int[][] board, int rowIndex, int columnIndex, int subGridSize){
		int subGridXIndex = findBoundingBoxIndex(rowIndex, subGridSize);
		int subGridYIndex = findBoundingBoxIndex(columnIndex, subGridSize);

		int xBounds = subGridXIndex + subGridSize;
		int yBounds = subGridYIndex + subGridSize;

		for(int i = subGridXIndex; i < xBounds; i++){
			for(int j = subGridYIndex; j < yBounds; j++){
				if(i != rowIndex && j!= columnIndex){
					if(board[i][j] == board[rowIndex][columnIndex]) {
						System.out.println("NOT VIABLE IN SUBGRID:("+i+","+j+"):"+board[i][j]);
						return false;
					}
				}
			}
		}
		return true;
	}

	private int findBoundingBoxIndex(int index, int subGridSize){
		return (index / subGridSize) * subGridSize;
	}
}