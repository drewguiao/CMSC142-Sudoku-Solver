import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

class Sudoku implements Constants{
	private int numOfPuzzles;
	
	private static final String SPACE = " ";
	private static final int EMPTY = 0;
	private List<Puzzle> puzzles = new ArrayList<>();

	public Sudoku(){}

	public void readFile(String fileName){
		try{
			BufferedReader breader = new BufferedReader(new FileReader(fileName));
			this.numOfPuzzles = Integer.parseInt(breader.readLine());
			for(int i = 0 ; i < this.numOfPuzzles; i++){
				int subGridSize = Integer.parseInt(breader.readLine());
				int boardSize = subGridSize * subGridSize;
				int[][] board = new int[boardSize][boardSize];

				for(int x = 0; x < boardSize; x++){	
					String currentLine = breader.readLine();
					String[] tokens = currentLine.split(SPACE);
					for(int y = 0; y < boardSize; y++){
						board[x][y] = Integer.parseInt(tokens[y]);
					}
				}

				Puzzle puzzle = new Puzzle(subGridSize, boardSize, board);
				puzzles.add(puzzle);
			}
			breader.close();
		}catch(IOException ioe){
			System.out.println("Sudoku.java.readFile: "+ioe.getMessage());
		}
	}

	public void startSolving(){
		for(int i = 0 ; i < this.numOfPuzzles; i++){
			Puzzle puzzle = this.puzzles.get(i);
			System.out.println("PUZZLE #"+(i+1));
			System.out.println(puzzle);
			// System.out.println();
			solve(puzzle);
			// solveNonRecursive(puzzle);
		}
	}

	public void solveNonRecursive(Puzzle puzzle){

	}

	public void solve(Puzzle puzzle){
		if(isFull(puzzle)){
			System.out.println("Puzzle solved!");
			puzzle.addSolution(puzzle.board);
			System.out.println("SOLUTION #"+(puzzle.getNumberOfSolutions()));

			int absoluteSize = puzzle.getSubGridSize() * puzzle.getSubGridSize();
			System.out.println("SOLVED CONFIG:");
			for(int i = 0; i < absoluteSize; i++){
				for(int j = 0; j < absoluteSize; j++){
					System.out.print(puzzle.board[i][j] + " ");
				}
				System.out.println();
			}
			System.out.println();

			System.out.println(puzzle);
		}else{
			Cell emptyCell = findEmptyCell(puzzle);
			int[] possibleEntries = getPossibleEntries(puzzle, emptyCell);
			int boardSize = puzzle.getBoardSize();
			for(int x = 0; x < boardSize; x++){
				if(possibleEntries[x] != EMPTY){
					puzzle.board[emptyCell.getX()][emptyCell.getY()] = possibleEntries[x];
					solve(puzzle);
				}
			}
			puzzle.board[emptyCell.getX()][emptyCell.getY()] = EMPTY;

		}
	}


	public void solve(Puzzle puzzle, int identifier){
		int boardSize = puzzle.getBoardSize();

		if((identifier == Y_SOLVING || identifier == XY_SOLVING) && boardSize % 2 == 0){
			System.out.println("SOLUTION #"+puzzle.getNumberOfSolutions());
		}else{
			if((identifier == Y_SOLVING || identifier == XY_SOLVING) && hasIrregularities(puzzle)){
				System.out.println("SOLUTION #"+puzzle.getNumberOfSolutions());
				System.out.println("NANI");
			}else{
				if(isFull(puzzle)){
					System.out.println("Puzzle solved!");
					puzzle.addSolution(puzzle.board);
					System.out.println("SOLUTION #"+(puzzle.getNumberOfSolutions()));
					System.out.println(puzzle);
				}else{
					Cell emptyCell = findEmptyCell(puzzle);
					int[] possibleEntries = getPossibleEntries(puzzle, emptyCell, identifier);
					
					for(int x = 0; x < boardSize; x++){
						if(possibleEntries[x] != EMPTY){
							puzzle.board[emptyCell.getX()][emptyCell.getY()] = possibleEntries[x];
							solve(puzzle,identifier);
						}
					}
					puzzle.board[emptyCell.getX()][emptyCell.getY()] = EMPTY;
				}
			}
		}

		
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

	private int[] getPossibleEntries(Puzzle puzzle, Cell emptyCell, int identifier){
		
		int boardSize = puzzle.getBoardSize();
		int[] possibleEntries = new int[boardSize];
		int xIndex = emptyCell.getX();
		int yIndex = emptyCell.getY();


		if(identifier == NATURAL_SOLVING){
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
			int boxIndexX = findBoundingBox(emptyCell.getX(), subGridSize);
			int boxIndexY = findBoundingBox(emptyCell.getY(), subGridSize);

			int xBounds = subGridSize + boxIndexX;
			int yBounds = subGridSize + boxIndexY;

			for(int x = boxIndexX; x < xBounds; x++){
				for(int y = boxIndexY; y < yBounds; y++){
					if(puzzle.board[x][y] != EMPTY){
						possibleEntries[puzzle.board[x][y] - 1] = 1;
					}
				}
			}

		}else if(identifier == X_SOLVING){

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
			int boxIndexX = findBoundingBox(emptyCell.getX(), subGridSize);
			int boxIndexY = findBoundingBox(emptyCell.getY(), subGridSize);

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
		}else if(identifier == Y_SOLVING){
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
			int boxIndexX = findBoundingBox(emptyCell.getX(), subGridSize);
			int boxIndexY = findBoundingBox(emptyCell.getY(), subGridSize);

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
		}else if(identifier == XY_SOLVING){
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
			int boxIndexX = findBoundingBox(emptyCell.getX(), subGridSize);
			int boxIndexY = findBoundingBox(emptyCell.getY(), subGridSize);

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



	private boolean isFull(Puzzle puzzle){
		int boardSize = puzzle.getBoardSize();
		for(int i = 0 ; i < boardSize; i++){
			for(int j = 0; j < boardSize; j++){
				if(puzzle.board[i][j] == EMPTY) return false;
			}
		}
		return true;
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

	private int[] getPossibleEntries(Puzzle puzzle, Cell emptyCell){
		
		int boardSize = puzzle.getBoardSize();
		int[] possibleEntries = new int[boardSize];
		int xIndex = emptyCell.getX();
		int yIndex = emptyCell.getY();

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
		int boxIndexX = findBoundingBox(emptyCell.getX(), subGridSize);
		int boxIndexY = findBoundingBox(emptyCell.getY(), subGridSize);

		int xBounds = subGridSize + boxIndexX;
		int yBounds = subGridSize + boxIndexY;

		for(int x = boxIndexX; x < xBounds; x++){
			for(int y = boxIndexY; y < yBounds; y++){
				if(puzzle.board[x][y] != EMPTY){
					possibleEntries[puzzle.board[x][y] - 1] = 1;
				}
			}
		}

		// //findEntriesInXTopLeftToRightDiagonal
		// if(xIndex == yIndex){
		// 	for(int x = 0; x < boardSize; x++){
		// 		if(puzzle.board[x][x] != EMPTY){
		// 			possibleEntries[puzzle.board[x][x] - 1] = 1;
		// 		}
		// 	}
		// }

		// //findEntriesInXTopRightToLeftDiagonal
		// if(xIndex + yIndex == boardSize - 1){
		// 	for(int x = 0, y = boardSize - 1; x < boardSize; x++,y--){
		// 		if(puzzle.board[x][y] != EMPTY){
		// 			possibleEntries[puzzle.board[x][y] - 1] = 1;
		// 		}
		// 	}
		// }

		//findEntriesInY
		if(boardSize % 2 != 0){
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

	private int[] findEntriesInRow(int[] possibleEntries, Puzzle puzzle, int xIndex){
		int boardSize = puzzle.getBoardSize();
		for(int y = 0; y < boardSize; y++){
			if(puzzle.board[xIndex][y] != EMPTY){
				possibleEntries[puzzle.board[xIndex][y]-1] = 1;
			}
		}
		return possibleEntries;
	}

	private int[] findEntriesInColumn(int[] possibleEntries, Puzzle puzzle, int yIndex){
		int boardSize = puzzle.getBoardSize();
		for(int x = 0; x < boardSize; x++){
			if(puzzle.board[x][yIndex] != EMPTY){
				possibleEntries[puzzle.board[x][yIndex]-1] = 1;
			}
		}
		return possibleEntries;
	}

	private int[] findEntriesInSubGrid(int[] possibleEntries, Puzzle puzzle, Cell emptyCell){
		
		int subGridSize = puzzle.getSubGridSize();
		int boxIndexX = findBoundingBox(emptyCell.getX(), subGridSize);
		int boxIndexY = findBoundingBox(emptyCell.getY(), subGridSize);

		int xBounds = subGridSize + boxIndexX;
		int yBounds = subGridSize + boxIndexY;

		for(int x = boxIndexX; x < xBounds; x++){
			for(int y = boxIndexY; y < yBounds; y++){
				if(puzzle.board[x][y] != EMPTY){
					possibleEntries[puzzle.board[x][y]-1] = 1;
				}
			}
		}
		return possibleEntries;
	}

	private int findBoundingBox(int index, int subGridSize){
		return (index/subGridSize) * subGridSize;
	}

	public List<Puzzle> getPuzzles(){
		return this.puzzles;
	}

}