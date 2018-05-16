import javax.swing.JTextField;
import javax.swing.JOptionPane;
import java.io.PrintWriter;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.util.List;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;
import java.io.FileWriter;
class SudokuSolver implements Constants{

	private static final int EMPTY = 0;
	private static final String OUTPUT_FILE = "output.txt";
	public SudokuSolver(){}

	public void solve(Puzzle puzzle, int solvingMode){
		solveSudoku(puzzle, solvingMode);
		int noOfSolutions = puzzle.getNumberOfSolutions();
		switch(solvingMode){
			case NATURAL_SOLVING: System.out.println("NAT_SOLUTIONS #:"+noOfSolutions);
								  break;
			case X_SOLVING: System.out.println("X_SOLUTIONS #:"+noOfSolutions);
								  break;
			case Y_SOLVING: System.out.println("Y_SOLUTIONS #:"+noOfSolutions);
								  break;
			case XY_SOLVING: System.out.println("XY_SOLUTIONS #:"+noOfSolutions);
								  break;
		}
		saveSolutionsToFile(puzzle);
	}

	//lomboy branch
	private ArrayList<Integer> findValidEntries(int[][] board, int subGridSize, int row, int col, int solvingMode){
		ArrayList<Integer> validEntries = new ArrayList<Integer>();
		int size = board[0].length;
		for(int i = 1; i <= size; i++){
			if(isValid(board, subGridSize, row, col, i, solvingMode)){
				validEntries.add(i);
			}
		}
		return validEntries;
	}
	private boolean isValidInRowAndColumn(int[][] board, int boardSize, int rowIndex, int columnIndex, int number){
		for(int i = 0; i < boardSize; i++){
			if(board[rowIndex][i] == number && i != columnIndex)	return false; //checks row
			if(board[i][columnIndex] == number && i != rowIndex)	return false; //checks column
		}
		return true;
	}

	private boolean isValidInSubGrid(int[][] board, int subGridSize, int rowIndex, int columnIndex, int number){
		int startingRowIndex = rowIndex - rowIndex % subGridSize;
	    int startingColumnIndex = columnIndex - columnIndex % subGridSize;

		for(int i = startingRowIndex; i < startingRowIndex + subGridSize; i++){
	    	if(i == rowIndex)	continue;  //no need to check for the row since it is already checked in isValidInRowAndColumn
	        for(int j = startingColumnIndex; j < startingColumnIndex + subGridSize; j++){
	            if(j == columnIndex)  continue; //no need to check since it is already checked inisValidInRowAndColumn
	            if(board[i][j] == number) return false;
	            
	        }
	    } 
	    return true;

	}

	private boolean isValidInXLeft(int[][] board, int boardSize, int index, int number){
		for(int i = 0; i < boardSize; i++){
			if(board[i][i] == number && i != index)	return false;
		}
		return true;
	}

	private boolean isValidInXRight(int[][] board, int boardSize, int index, int number){
		for(int i = 0; i < boardSize; i++){
			if(board[i][boardSize-i-1] == number && i!= index)	return false;
		}
		return true;
	}

	private boolean isValidInYLeft(int[][] board, int boardSize, int index, int number){
		for(int i = 0; i < boardSize; i++){
			if(i==index)	continue;
			if(board[i][i] == number && i <= boardSize/2)	return false;
			if(board[i][boardSize/2] == number && i >= boardSize/2)	return false;
		}
		return true;
	}

	private boolean isValidInYRight(int[][] board, int boardSize, int index, int number){
		for(int i = 0; i < boardSize; i++){
			if(i==index)	continue;
			if(board[i][boardSize-i-1] == number && i <= boardSize/2)	return false;
			if(board[i][boardSize/2] == number && i >= boardSize/2)		return false;
		}
		return true;
	}
	public boolean isValid(JTextField[][] grid, int subGridSize, int rowIndex, int columnIndex, int solvingMode){
		int boardSize = subGridSize * subGridSize;
		int[][] board = translateConfigurationToBoard(grid, boardSize);
		int number = board[rowIndex][columnIndex];
		
		if(!(isValidInRowAndColumn(board, boardSize, rowIndex, columnIndex, number)))	return false;
		if(!(isValidInSubGrid(board, subGridSize, rowIndex, columnIndex, number)))	return false;
		if(solvingMode == X_SOLVING || solvingMode == XY_SOLVING)	{ 
			if(rowIndex == columnIndex && !(isValidInXLeft(board, boardSize, rowIndex, number)))	return false;	
			if(columnIndex == boardSize-rowIndex-1 && !(isValidInXRight(board, boardSize, rowIndex, number)))	return false; //should check the right diagonal /

		}
		if(solvingMode == Y_SOLVING || solvingMode == XY_SOLVING){
			
			if(rowIndex == columnIndex && rowIndex <= boardSize/2 && !(isValidInYLeft(board, boardSize, rowIndex, number)))	return false;
			if(columnIndex == boardSize-rowIndex-1 && rowIndex <= boardSize/2 && !(isValidInYRight(board, boardSize, rowIndex, number)))	return false;
			
			
		}
			
	    return true;

	}
	public boolean isValid(int[][] board, int subGridSize, int rowIndex, int columnIndex, int number, int solvingMode){
		int boardSize = subGridSize*subGridSize; 
		
		if(!(isValidInRowAndColumn(board, boardSize, rowIndex, columnIndex, number)))	return false;
		if(!(isValidInSubGrid(board, subGridSize, rowIndex, columnIndex, number)))	return false;
		if(solvingMode == X_SOLVING || solvingMode == XY_SOLVING)	{ 
			if(rowIndex == columnIndex && !(isValidInXLeft(board, boardSize, rowIndex, number)))	return false;	
			if(columnIndex == boardSize-rowIndex-1 && !(isValidInXRight(board, boardSize, rowIndex, number)))	return false; //should check the right diagonal /

		}
		if(solvingMode == Y_SOLVING || solvingMode == XY_SOLVING){
			
			if(rowIndex == columnIndex && rowIndex <= boardSize/2 && !(isValidInYLeft(board, boardSize, rowIndex, number)))	return false;
			if(columnIndex == boardSize-rowIndex-1 && rowIndex <= boardSize/2 && !(isValidInYRight(board, boardSize, rowIndex, number)))	return false;
			
			
		}
			
	    return true;
	}

	private boolean isEmpty(int[][] board, int row, int col){
		if(board[row][col]==0)
			return true;
		return false;
	}

	private Cell findNextEmptyCell(Puzzle puzzle, int row, int col){
		int[][] board = puzzle.getBoard();
		int size = puzzle.getBoardSize();
		do{
			col++;
			if(col==size){
				if(row!=size-1)	row++;
				else return new Cell(row, col);
				col=0;
		
			}
		}while(!isEmpty(board, row, col));
		return new Cell(row, col);

	}
	private Cell findPrevEmptyCell(Puzzle puzzle, int row, int col){
		int[][] board = puzzle.getBoard();
		int size = puzzle.getBoardSize();
		do{
			col--;
			if(col==-1){
				if(row==0)	return new Cell(row, col);
				row--;
				col=size-1;
			}
		}while(!isEmpty(board, row, col));
		return new Cell(row, col);
	}

	private void printBoard(int[][] board, int size){
		for(int i = 0, j = 0; i < size; j++){
			if(j==size){
				j=-1;
				i++;
				System.out.println("");
			}else System.out.print(board[i][j]+" ");
		}
		System.out.println("");
	}

	private void solveSudoku(Puzzle puzzle, int solvingMode){
		int rowIndex = 0, columnIndex = 0;
		int[][] initialBoard = puzzle.getBoard();
		int boardSize = puzzle.getBoardSize();
		int subGridSize = puzzle.getSubGridSize();
		int[][] board = new int[boardSize][boardSize];
		boolean solutionFound = false;
		boolean backtrack = false;


		if((solvingMode == Y_SOLVING || solvingMode == XY_SOLVING) && (boardSize%2 == 0 || hasIrregularitiesInY(puzzle)))	return;
		if((solvingMode == X_SOLVING || solvingMode == XY_SOLVING) && hasIrregularitiesInX(puzzle))	return;

		for(int i = 0; i < boardSize; i++)
			System.arraycopy(initialBoard[i], 0, board[i], 0, boardSize);
		
		
		while(!(columnIndex == -1 && rowIndex == 0)){

			if(!solutionFound){ // current board is not solved
				if(columnIndex==-1){
					rowIndex--;
					columnIndex = boardSize - 1;
				}
				else if(columnIndex == boardSize){
					if(rowIndex != boardSize - 1)	{
						rowIndex++;
						columnIndex = 0;
					}else{
						solutionFound = true;
						continue;
					}
					
				}
				if(isEmpty(initialBoard, rowIndex, columnIndex)){
					ArrayList<Integer> ve = findValidEntries(board, subGridSize, rowIndex, columnIndex, solvingMode);	
					//there are no valid entries for current cell or current cell contains the last valid entry
					if(ve.size() == 0 || board[rowIndex][columnIndex] == ve.get(ve.size()-1)){ 
						//backtrack
						board[rowIndex][columnIndex] = 0;
						columnIndex--;
						backtrack = true;
						continue;
					}
					//else 
					backtrack = false;
					//sets the value in the current cell the next valid entry
					board[rowIndex][columnIndex] = ve.get(ve.indexOf(board[rowIndex][columnIndex])+1);
					columnIndex++;

				}else if(backtrack){
					Cell newCell = findPrevEmptyCell(puzzle, rowIndex, columnIndex);
					rowIndex = newCell.getX();
					columnIndex = newCell.getY();	
				
				}else{
					Cell newCell = findNextEmptyCell(puzzle, rowIndex, columnIndex);
					rowIndex = newCell.getX();
					columnIndex = newCell.getY();				
				}

			}else{ //board is solved
				solutionFound = false;
			
				//adds current board to the puzzle's solution(s)
				int[][] solvedBoard = new int[boardSize][boardSize];				
				for(int i = 0; i < boardSize; i++){
					System.arraycopy(board[i], 0, solvedBoard[i], 0, boardSize);
				}
				puzzle.addSolution(solvedBoard);

				//backtracks to the first cell in the last row 
				columnIndex--; 
				while(columnIndex!=-1){ 
					if(isEmpty(initialBoard, rowIndex, columnIndex))
						board[rowIndex][columnIndex] = 0; 
					columnIndex--;
				}
				rowIndex--;
				columnIndex = boardSize - 1;

				//sets the current cell to the first previous cell it will encounter that is editable(its initial value is 0)
				if(!isEmpty(initialBoard, rowIndex, columnIndex)){
					Cell newCell = findPrevEmptyCell(puzzle, rowIndex, columnIndex);
					rowIndex = newCell.getX();
					columnIndex = newCell.getY()+1;
					backtrack = true;
				}

			}
			
		}
		
	}

	private void saveSolutionsToFile(Puzzle puzzle){

		try(PrintWriter pwriter = new PrintWriter(new File(OUTPUT_FILE))){
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

	private boolean hasIrregularitiesInX(Puzzle puzzle){
		int[][] board = puzzle.getBoard();
		int boardSize = puzzle.getBoardSize();


		for(int i = 0; i < boardSize; i++){
			if(board[i][i] != EMPTY){
				int leftComparator = board[i][i];
				for(int j =  i + 1 ; j < boardSize - 1; j++){
					if(leftComparator == board[j][j]) return true;
				}
			}

			if(board[i][boardSize - i - 1] != EMPTY){
				int rightComparator = board[i][boardSize - i - 1];
				for(int j =  i + 1 ; j < boardSize - 1; j++){
					if(rightComparator == board[j][boardSize - j -1]) return true;
				}
			}

		}
		return false;
	}

	private boolean hasIrregularitiesInY(Puzzle puzzle){
		int[][] board = puzzle.getOriginalBoard();
		int boardSize = puzzle.getBoardSize();
		int halfOfBoard = boardSize / 2;

		//check if a number in left and right diagonal exists already in stem
		for(int i = 0; i < halfOfBoard; i++){
			//left side
			if(board[i][i] != EMPTY){
				int comparator = board[i][i];
				for(int x = halfOfBoard, y = halfOfBoard; x < boardSize; x++){
					if(comparator == board[x][y]) return true;
				}
			}
			//right side
			if(board[i][boardSize - i - 1] != EMPTY){
				int comparator = board[i][boardSize - i - 1];
				for(int x = halfOfBoard, y = halfOfBoard; x < boardSize; x++){
					if(comparator == board[x][y]) return true;

				}
			}
		}

		return false;
	}

	public boolean isFull(int[][] board, int boardSize){
		for(int i = 0; i < boardSize; i++){
			for(int j = 0; j < boardSize; j++){
				if(board[i][j] == EMPTY) return false;
			}
		}
		return true;
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
			// JOptionPane.showMessageDialog(null, "Invalid element!");
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

	