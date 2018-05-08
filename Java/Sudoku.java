import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class Sudoku extends JFrame{
	private int numOfPuzzles;
	private static final String INPUT_FILE = "input.txt";
	private static final String SPACE = " ";
	private static final int EMPTY = 0;
	private List<Puzzle> puzzles = new ArrayList<>();
	private int solutions = 0;
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
			solve(puzzle, 2);
		}
	}

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
		// if(checkYLeft)	System.out.println("checkYLeft: "+" "+row+" "+column);
		// if(checkYRight)	System.out.println("checkYRight: "+" "+row+" "+column);
		// if(checkYMiddle)	System.out.println("checkYMiddle: "+" "+row+" "+column);

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
		//if identifier== 0 Sudoku 
		//if identifier==1 SudokuX
		//if identifier==2	SudokuY
		//if identifier==3 SundokuXY
		int row = 0, col = 0;
		solutions = 0;
		int[][] initialBoard = puzzle.getBoard();
		int boardSize = puzzle.getBoardSize();
		int subGridSize = puzzle.getSubGridSize();
		int[][] board = new int[boardSize][boardSize];
		
		for(int i = 0; i < boardSize; i++){
			System.arraycopy(initialBoard[i], 0, board[i], 0, boardSize);
		}
		
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
				printBoard(board, boardSize);
				saveToFile(board);
				col--;
				System.out.println(row+" " +col);
				while(col!=-1){
					if(isEmpty(initialBoard, row, col))board[row][col] = 0;
					col--;
				}row--;
				col = boardSize -1;
				if(!isEmpty(initialBoard, row, col)){
					Cell newCell = findPrevEmptyCell(puzzle, row, col);
					printBoard(board, boardSize);
					row = newCell.getX();
					col = newCell.getY()+1;
					backtrack = true;
				}

			}
			

		}
		try{
			FileWriter fw = new FileWriter(new File("output.txt"), true);
			fw.write("No of Solutions: " + solutions+"\n");
			fw.close();
		}catch(FileNotFoundException e){
				System.out.println("File not found");
		} catch(Exception e){
			System.out.println(e.getMessage());
		}
		// }System.out.println("No of Solutions: " + solutions);
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