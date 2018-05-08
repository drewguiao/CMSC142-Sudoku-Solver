import javax.swing.JTextField;
import javax.swing.JOptionPane;
import java.io.PrintWriter;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.util.List;

class SudokuSolver implements Constants{

	private static final int EMPTY = 0;
	private static final String OUTPUT_FILE = "output.txt";
	public SudokuSolver(){}

	public void solve(Puzzle puzzle){
		solve(puzzle, NATURAL_SOLVING);
		int naturalSolutions = puzzle.getNumberOfSolutions();
		solve(puzzle, X_SOLVING);
		int xSolutions = puzzle.getNumberOfSolutions() - naturalSolutions;
		solve(puzzle, Y_SOLVING);
		int ySolutions = puzzle.getNumberOfSolutions() - xSolutions;
		solve(puzzle, XY_SOLVING);
		int xySolutions = puzzle.getNumberOfSolutions() - ySolutions;
		saveSolutionsToFile(puzzle);
	}

	private void saveSolutionsToFile(Puzzle puzzle){
		try(PrintWriter pwriter = new PrintWriter(new FileOutputStream(new File(OUTPUT_FILE),false))){
			pwriter.write("PUZZLE #"+puzzle.getPuzzleNumber()+"\n");
			List<int[][]> solutions = puzzle.getSolutions();
			for(int[][] solution : solutions) {
	      		int absoluteSize = puzzle.getSubGridSize() * puzzle.getSubGridSize();
				for(int i = 0; i < absoluteSize;i++){
	           		for(int j = 0; j < absoluteSize; j++){
	                	pwriter.write(solution[i][j] + " ");
	            	}
	      			pwriter.write("\n");
	      		}
	        	pwriter.write("\n");
	      	}
	    pwriter.close();
		}catch(IOException ioe){
			System.out.println("SudokuSolver.java.saveSolutionsToFile():"+ioe.getMessage());
		}
	}

	public void solve(Puzzle puzzle, int solvingMode){
		int boardSize = puzzle.getBoardSize();

		if((solvingMode == Y_SOLVING || solvingMode == XY_SOLVING) && boardSize % 2 == 0){
			System.out.println("SOLUTION #"+puzzle.getNumberOfSolutions());
		}else{
			if((solvingMode == Y_SOLVING || solvingMode == XY_SOLVING) && hasIrregularities(puzzle)){
				System.out.println("SOLUTION #"+puzzle.getNumberOfSolutions());
			}else{
				if(isFull(puzzle)){
					int[][] solvedBoard = copyBoard(puzzle.board, puzzle.getBoardSize());
					puzzle.addSolution(solvedBoard);
				}else{
					Cell emptyCell = findEmptyCell(puzzle);
					int[] possibleEntries = getPossibleEntries(puzzle, emptyCell, solvingMode);

					for(int x = 0; x < boardSize; x++){
						if(possibleEntries[x] != EMPTY){
							puzzle.board[emptyCell.getX()][emptyCell.getY()] = possibleEntries[x];
							solve(puzzle,solvingMode);
						}
					}
					puzzle.board[emptyCell.getX()][emptyCell.getY()] = EMPTY;
				}
			}
		}
	}

	private int[][] copyBoard(int[][] board, int boardSize){
		int boardCopy[][] = new int[boardSize][boardSize];
		for(int i = 0; i < boardSize; i++){
			for(int j = 0; j < boardSize; j++){
				boardCopy[i][j] = board[i][j];
			}
		}
		return boardCopy;
	}


	private Cell findEmptyCell(Puzzle puzzle){
		int boardSize = puzzle.getBoardSize();
		for(int i = 0; i < boardSize; i++){
			for(int j = 0; j < boardSize; j++){
				if(puzzle.board[i][j] == EMPTY){
					return new Cell(i,j);
				}
			}
		}
		return null;
	}

	private int[] getPossibleEntries(Puzzle puzzle, Cell emptyCell, int solvingMode){

		int boardSize = puzzle.getBoardSize();
		int[] possibleEntries = new int[boardSize];
		int xIndex = emptyCell.getX();
		int yIndex = emptyCell.getY();


		if(solvingMode == NATURAL_SOLVING){
			//findEntriesInRow
			for(int y = 0; y < boardSize; y++){
				if(puzzle.board[xIndex][y] != EMPTY){
					possibleEntries[puzzle.board[xIndex][y] - 1] = 1;
				}
			}

			//findEntriesInColumn
			for(int x = 0; x < boardSize; x++){
				if(puzzle.board[x][yIndex] != EMPTY){
					possibleEntries[puzzle.board[x][yIndex] - 1] = 1;
				}
			}

			//findEntriesInSubGrid
			int subGridSize = puzzle.getSubGridSize();
			int boxIndexX = findBoundingBoxIndex(emptyCell.getX(), subGridSize);
			int boxIndexY = findBoundingBoxIndex(emptyCell.getY(), subGridSize);

			int xBounds = subGridSize + boxIndexX;
			int yBounds = subGridSize + boxIndexY;

			for(int x = boxIndexX; x < xBounds; x++){
				for(int y = boxIndexY; y < yBounds; y++){
					if(puzzle.board[x][y] != EMPTY){
						possibleEntries[puzzle.board[x][y] - 1] = 1;
					}
				}
			}

		}else if(solvingMode == X_SOLVING){

			//findEntriesInRow
			for(int y = 0; y < boardSize; y++){
				if(puzzle.board[xIndex][y] != EMPTY){
					possibleEntries[puzzle.board[xIndex][y] - 1] = 1;
				}
			}

			//findEntriesInColumn
			for(int x = 0; x < boardSize; x++){
				if(puzzle.board[x][yIndex] != EMPTY){
					possibleEntries[puzzle.board[x][yIndex] - 1] = 1;
				}
			}

			//findEntriesInSubGrid
			int subGridSize = puzzle.getSubGridSize();
			int boxIndexX = findBoundingBoxIndex(emptyCell.getX(), subGridSize);
			int boxIndexY = findBoundingBoxIndex(emptyCell.getY(), subGridSize);

			int xBounds = subGridSize + boxIndexX;
			int yBounds = subGridSize + boxIndexY;

			for(int x = boxIndexX; x < xBounds; x++){
				for(int y = boxIndexY; y < yBounds; y++){
					if(puzzle.board[x][y] != EMPTY){
						possibleEntries[puzzle.board[x][y] - 1] = 1;
					}
				}
			}
			//findEntriesInXTopLeftToRightDiagonal
			if(xIndex == yIndex){
				for(int x = 0; x < boardSize; x++){
					if(puzzle.board[x][x] != EMPTY){
						possibleEntries[puzzle.board[x][x] - 1] = 1;
					}
				}
			}

			//findEntriesInXTopRightToLeftDiagonal
			if(xIndex + yIndex == boardSize - 1){
				for(int x = 0, y = boardSize - 1; x < boardSize; x++,y--){
					if(puzzle.board[x][y] != EMPTY){
						possibleEntries[puzzle.board[x][y] - 1] = 1;
					}
				}
			}
		}else if(solvingMode == Y_SOLVING){
			//findEntriesInY


			//findEntriesInRow
			for(int y = 0; y < boardSize; y++){
				if(puzzle.board[xIndex][y] != EMPTY){
					possibleEntries[puzzle.board[xIndex][y] - 1] = 1;
				}
			}

			//findEntriesInColumn
			for(int x = 0; x < boardSize; x++){
				if(puzzle.board[x][yIndex] != EMPTY){
					possibleEntries[puzzle.board[x][yIndex] - 1] = 1;
				}
			}

			//findEntriesInSubGrid
			int subGridSize = puzzle.getSubGridSize();
			int boxIndexX = findBoundingBoxIndex(emptyCell.getX(), subGridSize);
			int boxIndexY = findBoundingBoxIndex(emptyCell.getY(), subGridSize);

			int xBounds = subGridSize + boxIndexX;
			int yBounds = subGridSize + boxIndexY;

			for(int x = boxIndexX; x < xBounds; x++){
				for(int y = boxIndexY; y < yBounds; y++){
					if(puzzle.board[x][y] != EMPTY){
						possibleEntries[puzzle.board[x][y] - 1] = 1;
					}
				}
			}

			int halfOfBoard = boardSize / 2;

			//check upper half left diagonal
			if(xIndex < halfOfBoard && xIndex == yIndex){
				for(int x = 0; x < halfOfBoard; x++){
					if(puzzle.board[x][x] != EMPTY){
						possibleEntries[puzzle.board[x][x] - 1] = 1;
					}
				}
				for(int x = xIndex,y = yIndex; x < boardSize; x++){
					if(puzzle.board[x][y] != EMPTY){
						possibleEntries[puzzle.board[x][y] - 1] = 1;
					}
				}
			}
			//check upper half right diagonal
			if(xIndex < halfOfBoard && xIndex + yIndex == boardSize -1 ){
				for(int x = 0, y = boardSize - 1; x < halfOfBoard; x++,y--){
					if(puzzle.board[x][y] != EMPTY){
						possibleEntries[puzzle.board[x][y] - 1] = 1;
					}
				}
				for(int x = xIndex,y = yIndex; x < boardSize; x++){
					if(puzzle.board[x][y] != EMPTY){
						possibleEntries[puzzle.board[x][y] - 1] = 1;
					}
				}
			}

			//check middle for Y stem
			if(xIndex >= halfOfBoard && yIndex == halfOfBoard){
				for(int x = 0; x < halfOfBoard; x++){
					if(puzzle.board[x][x] != EMPTY){
						possibleEntries[puzzle.board[x][x] - 1] = 1;
					}
				}
				for(int x = 0, y = boardSize - 1; x < halfOfBoard; x++,y--){
					if(puzzle.board[x][y] != EMPTY){
						possibleEntries[puzzle.board[x][y] - 1] = 1;
					}
				}
				for(int x = xIndex,y = yIndex; x < boardSize; x++){
					if(puzzle.board[x][y] != EMPTY){
						possibleEntries[puzzle.board[x][y] - 1] = 1;
					}
				}
			}
		}else if(solvingMode == XY_SOLVING){
			//findEntriesInRow
			for(int y = 0; y < boardSize; y++){
				if(puzzle.board[xIndex][y] != EMPTY){
					possibleEntries[puzzle.board[xIndex][y] - 1] = 1;
				}
			}

			//findEntriesInColumn
			for(int x = 0; x < boardSize; x++){
				if(puzzle.board[x][yIndex] != EMPTY){
					possibleEntries[puzzle.board[x][yIndex] - 1] = 1;
				}
			}

			//findEntriesInSubGrid
			int subGridSize = puzzle.getSubGridSize();
			int boxIndexX = findBoundingBoxIndex(emptyCell.getX(), subGridSize);
			int boxIndexY = findBoundingBoxIndex(emptyCell.getY(), subGridSize);

			int xBounds = subGridSize + boxIndexX;
			int yBounds = subGridSize + boxIndexY;

			for(int x = boxIndexX; x < xBounds; x++){
				for(int y = boxIndexY; y < yBounds; y++){
					if(puzzle.board[x][y] != EMPTY){
						possibleEntries[puzzle.board[x][y] - 1] = 1;
					}
				}
			}
			//findEntriesInXTopLeftToRightDiagonal
			if(xIndex == yIndex){
				for(int x = 0; x < boardSize; x++){
					if(puzzle.board[x][x] != EMPTY){
						possibleEntries[puzzle.board[x][x] - 1] = 1;
					}
				}
			}

			//findEntriesInXTopRightToLeftDiagonal
			if(xIndex + yIndex == boardSize - 1){
				for(int x = 0, y = boardSize - 1; x < boardSize; x++,y--){
					if(puzzle.board[x][y] != EMPTY){
						possibleEntries[puzzle.board[x][y] - 1] = 1;
					}
				}
			}

			int halfOfBoard = boardSize / 2;

			//check upper half left diagonal
			if(xIndex < halfOfBoard && xIndex == yIndex){
				for(int x = 0; x < halfOfBoard; x++){
					if(puzzle.board[x][x] != EMPTY){
						possibleEntries[puzzle.board[x][x] - 1] = 1;
					}
				}
			}
			//check upper half right diagonal
			if(xIndex < halfOfBoard && xIndex + yIndex == boardSize -1 ){
				for(int x = 0, y = boardSize - 1; x < halfOfBoard; x++,y--){
					if(puzzle.board[x][y] != EMPTY){
						possibleEntries[puzzle.board[x][y] - 1] = 1;
					}
				}
			}

			//check middle for Y stem
			if(xIndex >= halfOfBoard && yIndex == halfOfBoard){
				for(int x = xIndex,y = yIndex; x < boardSize; x++){
					if(puzzle.board[x][y] != EMPTY){
						possibleEntries[puzzle.board[x][y] - 1] = 1;
					}
				}
			}

		}

		for(int x = 0; x < boardSize; x++){
       		possibleEntries[x] = (possibleEntries[x] == EMPTY) ? (x+1) : 0;
   		 }

		return possibleEntries;
	}

	private boolean hasIrregularities(Puzzle puzzle){
		int[][] board = puzzle.getOriginalBoard();
		int boardSize = puzzle.getBoardSize();
		int halfOfBoard = boardSize / 2;

		//check if a number in left and right diagonal exists already in stem
		for(int i = 0; i < halfOfBoard; i++){
			if(board[i][i] != EMPTY){
				int comparator = board[i][i];
				for(int x = halfOfBoard, y = halfOfBoard; x < boardSize; x++){
					if(comparator == board[x][y]) return true;
				}
			}
			if(board[i][boardSize - i - 1] != EMPTY){
				int comparator = board[i][boardSize - i - 1];
				for(int x = halfOfBoard, y = halfOfBoard; x < boardSize; x++){
					if(comparator == board[x][y]) return true;

				}
			}
		}

		return false;
	}

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

	public boolean isFull(Puzzle puzzle){
		int[][] board = puzzle.getBoard();
		int boardSize = puzzle.getBoardSize();
		for(int i = 0; i < boardSize; i++){
			for(int j = 0; j < boardSize; j++){
				if(board[i][j] == EMPTY) return false;
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