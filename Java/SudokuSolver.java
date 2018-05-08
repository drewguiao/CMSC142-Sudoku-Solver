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

	public void solve(Puzzle puzzle){
		solve(puzzle, NATURAL_SOLVING);
		int naturalSolutions = puzzle.getNumberOfSolutions();
		System.out.println("NAT_SOLUTIONS #:"+naturalSolutions);
		solve(puzzle, X_SOLVING);
		int xSolutions = puzzle.getNumberOfSolutions() - naturalSolutions;
		System.out.println("X_SOLUTIONS #:"+xSolutions);
		solve(puzzle, Y_SOLVING);
		int ySolutions = puzzle.getNumberOfSolutions() - xSolutions - naturalSolutions;
		System.out.println("Y_SOLUTIONS #:"+ySolutions);
		solve(puzzle, XY_SOLVING);
		int xySolutions = puzzle.getNumberOfSolutions() - xSolutions - ySolutions - naturalSolutions;
		System.out.println("XY_SOLUTIONS #:"+xySolutions);
		saveSolutionsToFile(puzzle);
	}

	//lomboy branch
	private ArrayList<Integer> findValidEntries(int[][] board, int subGridSize, int row, int col, int identifier){
		ArrayList<Integer> validEntries = new ArrayList<Integer>();
		int size = board[0].length;
		for(int i = 1; i <= size; i++){
			if(isValid(board, subGridSize, row, col, i, identifier)){
				validEntries.add(i);
			}
		}
		return validEntries;
	}

	private boolean isValid(int[][] board, int subGridSize, int row, int column, int n, int ident){
		int r = row - row % subGridSize;
	    int c = column - column % subGridSize;
		int absoluteSize =subGridSize*subGridSize; 
		boolean checkXLeft = false, checkXRight=false, checkYRight=false, checkYLeft=false, checkYMiddle=false;
		
		if(ident==1 || ident == 3 )	{ 
			if(row==column)	checkXLeft = true;
			if(column==absoluteSize-row-1)	checkXRight=true;

		}
		if(ident == 2 || ident==3){
			if(subGridSize%2!=0){
				if(row==column && row <=absoluteSize/2)	checkYLeft = true;
				if(column==absoluteSize-row-1 && row <=absoluteSize/2)	checkYRight=true;
				if(column==absoluteSize/2 && row>=absoluteSize/2)	checkYMiddle=true;
			}
			
		}

		for(int i = 0; i < absoluteSize; i++){
			if(board[i][column]==n && i != row)	return false; //check column
			if(board[row][i]==n && i != column)	return false; //check row
			if(checkXLeft && board[i][i]==n && i!=row)	return false; //check left diagonal \
			if(checkXRight && board[i][absoluteSize-i-1]==n && i!=row)	return false; //check right diagonal /
			if(i<=absoluteSize/2 && i!=row){
				if((checkYLeft || checkYMiddle) && board[i][i]==n)	
					return false;
				
				if((checkYRight ||checkYMiddle) &&  board[i][absoluteSize-i-1]==n)
					return false;
					
			}
			if(i>=absoluteSize/2 && i!=row){
				if((checkYMiddle || checkYLeft || checkYRight) && board[i][absoluteSize/2]==n)	
					return false;	
				
			}
		}

	    //check subgrid
	    for(int i = r; i < r + subGridSize; i++){
	    	if(i==row)	continue;
	        for(int j = c; j < c + subGridSize; j++){
	            if(j == column)  continue;
	            if(board[i][j]==n) return false;
	            
	        }
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

	private void solve(Puzzle puzzle, int identifier){

		int row = 0, col = 0;
		int solutions = 0;
		int[][] initialBoard = puzzle.getBoard();
		int boardSize = puzzle.getBoardSize();
		int subGridSize = puzzle.getSubGridSize();
		int[][] board = new int[boardSize][boardSize];
		
		for(int i = 0; i < boardSize; i++){
			System.arraycopy(initialBoard[i], 0, board[i], 0, boardSize);
		}
		if((identifier==Y_SOLVING || identifier ==XY_SOLVING) && boardSize%2==0)		return;
		if((identifier==Y_SOLVING || identifier == XY_SOLVING) && hasIrregularitiesInY(puzzle))	return;
		if((identifier==X_SOLVING || identifier==XY_SOLVING) && hasIrregularitiesInX(puzzle))	return;
		
		boolean solutionFound = false;
		boolean backtrack = false;
		
		while(!(col==-1 && row==0)){

			if(!solutionFound){
				if(col==-1){
					row--;
					col = boardSize - 1;
				}
				else if(col == boardSize){
					if(row != boardSize - 1)	{
						row++;
						col = 0;
					}else{
						solutionFound = true;
						continue;
					}
					
				}
				if(isEmpty(initialBoard, row, col)){
					ArrayList<Integer> ve = findValidEntries(board, subGridSize, row, col, identifier);	
					if(ve.size()==0){
						board[row][col]=0;
						col--;
						backtrack = true;
						continue;
					}

					if(board[row][col] == ve.get(ve.size()-1))	{
						board[row][col]=0;
						col--;
						backtrack = true;
						continue;
					}
					backtrack = false;
					board[row][col] = ve.get(ve.indexOf(board[row][col])+1);	
					col++;

				}else if(backtrack != true){
					Cell newCell = findNextEmptyCell(puzzle, row, col);
					row = newCell.getX();
					col = newCell.getY();
				
				}else{
					Cell newCell = findPrevEmptyCell(puzzle, row, col);
					row = newCell.getX();
					col = newCell.getY();					
				}

			}else{
				solutionFound = false;
				solutions++;
				
				int[][] solvedBoard = copyBoard(board, puzzle.getBoardSize());
				puzzle.addSolution(solvedBoard);
				col--;
				while(col!=-1){
					if(isEmpty(initialBoard, row, col))board[row][col] = 0;
					col--;
				}row--;
				col = boardSize -1;
				if(!isEmpty(initialBoard, row, col)){
					Cell newCell = findPrevEmptyCell(puzzle, row, col);
					// printBoard(board, boardSize);
					row = newCell.getX();
					col = newCell.getY()+1;
					backtrack = true;
				}

			}
			
		}
		
	}

	private void saveToFile(int[][] board){
		try{
			FileWriter fw = new FileWriter(new File("output.txt"), true);
			for(int i = 0, j = 0; i < board[0].length; j++){
				if(j==board[0].length){
					j=-1;
					i++;
					fw.write("\n");
				}else fw.write(String.format("%d ", board[i][j]));

			}
			fw.write("\n");
			fw.close();
		}catch(FileNotFoundException e){
				System.out.println("File not found");
		} catch(Exception e){
			System.out.println(e.getMessage());
		}
		
	}


	////////////////////////////////////////////////////// MASTER BRANCH //////////////////////////////////////////////


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

	// public void solve(Puzzle puzzle, int solvingMode){
	// 	int boardSize = puzzle.getBoardSize();

	// 	if((solvingMode == Y_SOLVING || solvingMode == XY_SOLVING) && boardSize % 2 == 0){
	// 		// System.out.println("SOLUTION #"+puzzle.getNumberOfSolutions());
	// 	}else{
	// 		if((solvingMode == Y_SOLVING || solvingMode == XY_SOLVING) && hasIrregularitiesInY(puzzle)){
	// 			// System.out.println("SOLUTION #"+puzzle.getNumberOfSolutions());
	// 		}else if((solvingMode == X_SOLVING || solvingMode == XY_SOLVING) && hasIrregularitiesInX(puzzle)){

	// 		}else{
	// 			if(isFull(puzzle)){
	// 				int[][] solvedBoard = copyBoard(puzzle.board, puzzle.getBoardSize());
	// 				puzzle.addSolution(solvedBoard);
	// 			}else{
	// 				Cell emptyCell = findEmptyCell(puzzle);
	// 				int[] possibleEntries = getPossibleEntries(puzzle, emptyCell, solvingMode);

	// 				for(int x = 0; x < boardSize; x++){
	// 					if(possibleEntries[x] != EMPTY){
	// 						puzzle.board[emptyCell.getX()][emptyCell.getY()] = possibleEntries[x];
	// 						solve(puzzle,solvingMode);
	// 					}
	// 				}
	// 				puzzle.board[emptyCell.getX()][emptyCell.getY()] = EMPTY;
	// 			}
	// 		}
	// 	}
	// }


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