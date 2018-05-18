import javax.swing.JTextField;
import javax.swing.JOptionPane;
import java.util.List;
import java.util.List;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;

final class SudokuSolver implements Constants{

	private static final int EMPTY = 0;
	private static final int UNDERFLOW = -1;
	private static final String OUTPUT_FILE = "output.txt";
	

	private SudokuSolver(){}

	
	public static void solve(Puzzle puzzle){
		int[][] board = puzzle.getBoard();
		int boardSize = puzzle.getBoardSize();
		int subGridSize = puzzle.getSubGridSize();
		int overflow = boardSize + 1;

		List<Cell> emptyCells = getEmptyCells(puzzle);
		int numberOfEmptyCells = emptyCells.size();
		int numberOfSolutions = 0;
		int numberOfXSolutions = 0;
		int i = 0;

		while(i != UNDERFLOW){
			if(i == numberOfEmptyCells){
				numberOfSolutions++;
				int [][] boardCopy = copyBoard(board, boardSize);
				puzzle.addSolution(boardCopy);
				i--;
			}

			Cell currentEmptyCell = emptyCells.get(i);
			int x = currentEmptyCell.getX();
			int y = currentEmptyCell.getY();
			
			board[x][y]++;
			
			if(board[x][y] == overflow){
				board[x][y] = EMPTY;
				i--;
			}else if(isViable(board,currentEmptyCell,subGridSize)) i++;
		}
		findXYSolutions(puzzle);
	}

	private static int[][] copyBoard(int[][] board, int boardSize){
		int[][] newBoard = new int[boardSize][boardSize];
		for(int i = 0; i < boardSize; i++){
			for(int j = 0 ; j < boardSize; j++){
				newBoard[i][j] = board[i][j];
			}
		}
		return newBoard;
	}

	private static boolean isXSolvable(int[][] board, int boardSize){
		List<Integer> leftDiagonal = new ArrayList<>();
		List<Integer> rightDiagonal = new ArrayList<>();
		for(int i = 0; i < boardSize; i++){
			if(leftDiagonal.contains(board[i][i])) return false;
			else leftDiagonal.add(board[i][i]);

			if(rightDiagonal.contains(board[i][boardSize - i - 1])) return false;
			else rightDiagonal.add(board[i][boardSize - i - 1]);
		}
		return true;
	}

	private static boolean isYSolvable(int[][] board, int boardSize){
		if(boardSize % 2 == 0) return false;

		List<Integer> leftDiagonal = new ArrayList<>();
		List<Integer> rightDiagonal = new ArrayList<>();
		int halfOfBoard = boardSize / 2;

		for(int i = 0; i < boardSize; i++){
			if(i >= halfOfBoard){
				if(leftDiagonal.contains(board[i][halfOfBoard])) return false;
				else leftDiagonal.add(board[i][halfOfBoard]);

				if(rightDiagonal.contains(board[i][halfOfBoard])) return false;
				else rightDiagonal.add(board[i][halfOfBoard]);
			}else{
				if(leftDiagonal.contains(board[i][i])) return false;
				else leftDiagonal.add(board[i][i]);

				if(rightDiagonal.contains(board[i][boardSize - i - 1])) return false;
				else rightDiagonal.add(board[i][boardSize - i - 1]);
			}
		}
		return true;
	}

	private static void printBoard(int[][] board, int boardSize){
		for(int i = 0; i < boardSize; i++){
			for(int j = 0; j < boardSize; j++){
				System.out.print(board[i][j]+" ");
			}
			System.out.println();
		}
		System.out.println();
	}

	private static List<Cell> getEmptyCells(Puzzle puzzle){
		int boardSize = puzzle.getBoardSize();
		int[][] board = puzzle.getBoard();
		List<Cell> emptyCells = new ArrayList<>();
		for(int i = 0; i < boardSize; i++){
			for(int j = 0; j < boardSize; j++){
				if(board[i][j] == EMPTY) emptyCells.add(new Cell(i,j));
			}
		}
		return emptyCells;
	}

	private static boolean isViable(int[][] board, Cell emptyCell, int subGridSize){
		int boardSize = subGridSize * subGridSize;
		if(!isViableInRowAndColumn(board,emptyCell,boardSize)) return false;
		if(!isViableInSubGrid(board,emptyCell,subGridSize)) return false;
		return true;
	}

	private static boolean isViableInRowAndColumn(int[][] board, Cell emptyCell, int absoluteSize){
		int x = emptyCell.getX();
		int y = emptyCell.getY();
		for(int i = 0; i < absoluteSize; i++){
			if(board[x][i] != EMPTY && i != y && board[x][i] == board[x][y]) return false;
			if(board[i][y] != EMPTY && i != x && board[i][y] == board[x][y]) return false;
		}
		return true;
	}

	private static boolean isViableInSubGrid(int[][] board, Cell emptyCell, int subGridSize){
		int x = emptyCell.getX();
		int y = emptyCell.getY();
		int xBoundIndex = getBoundingIndex(x, subGridSize);
		int yBoundIndex = getBoundingIndex(y, subGridSize);

		int xUpperBound = xBoundIndex + subGridSize;
		int yUpperBound = yBoundIndex + subGridSize;

		for(int i = xBoundIndex; i < xUpperBound; i++){
			for(int j = yBoundIndex; j < yUpperBound; j++){
				if(board[i][j] != EMPTY && x != i && y != j && board[i][j] == board[x][y]) return false;
				
			}
		}
		return true;
	}

	private static int getBoundingIndex(int coordinate, int subGridSize){
		return (coordinate / subGridSize) * subGridSize;
	}

	public static void findXYSolutions(Puzzle puzzle){
		int boardSize = puzzle.getBoardSize();
		List<int[][]> solutions = puzzle.getSolutions();
		
		for(int[][] solvedBoard: puzzle.getSolutions()){
			// int[][] boardCopy = copyBoard(solvedBoard,boardSize);
			boolean isXSolvable = false;
			boolean isYSolvable = false;
			if(isXSolvable(solvedBoard,boardSize)){
				puzzle.addXSolution(solvedBoard);
				isXSolvable = true;	
			}
			if(isYSolvable(solvedBoard,boardSize)){
				puzzle.addYSolution(solvedBoard);
				isYSolvable = true;	
			}
			if(isXSolvable && isYSolvable){
				puzzle.addXYSolution(solvedBoard);
			}
		}
	}

	public static boolean isALegitimateSolution(JTextField[][] grid, int subGridSize){
		int boardSize = subGridSize * subGridSize;
		int[][] board = translateConfigurationToBoard(grid, boardSize);

		if(!isFull(board,subGridSize)) return false;

		for(int i = 0; i < boardSize; i++){
			for(int j = 0; j < boardSize; j++){
				if(!isViable(board, new Cell(i,j), subGridSize)) return false;
			}
		}
		return true;
	}

	private static boolean isFull(int[][] board, int boardSize){
		for(int i = 0; i < boardSize; i++){
			for(int j = 0; j < boardSize; j++){
				if(board[i][j] == EMPTY) return false;
			}
		}
		return true;
	}

	private static int[][] translateConfigurationToBoard(JTextField[][] grid, int boardSize){
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

	public static boolean isValid(JTextField[][] grid, int subGridSize, Cell currentCell, int solvingMode){
		int boardSize = subGridSize * subGridSize;
		int[][] board = translateConfigurationToBoard(grid,boardSize);
		if(!isViableInRowAndColumn(board,currentCell,boardSize)) return false;
		if(!isViableInSubGrid(board,currentCell,subGridSize)) return false;
		
		int x = currentCell.getX();
		int y = currentCell.getY();	
		
		if(solvingMode == X_SOLVING || solvingMode == XY_SOLVING){
			if(currentCell.getX() == currentCell.getY()){
				for(int i = 0; i < boardSize; i++){
					if(board[i][i] != EMPTY && i != x && board[i][i] == board[x][y]) return false;
				}
			}
			if(currentCell.getX() + currentCell.getY() == boardSize - 1){
				for(int i = 0; i < boardSize; i++){
					if(board[i][boardSize - i - 1] != EMPTY && i != x && boardSize -i - 1 != y && board[i][boardSize-i-1] == board[x][y]) return false;
				}
			}
		}

		if(solvingMode == Y_SOLVING || solvingMode == XY_SOLVING){
			if(subGridSize%2 == 0)	return false;
			if(x == y && x <= boardSize/2 && !(isValidInYLeft(board, boardSize, x, board[x][y])))	return false;
			if(y == boardSize-x-1 && x <= boardSize/2 && !(isValidInYRight(board, boardSize, x, board[x][y])))	return false;
		}
		return true;
	}

	private static boolean isValidInYLeft(int[][] board, int boardSize, int index, int number){
		for(int i = 0; i < boardSize; i++){
			if(i==index)	continue;
			if(board[i][i] == number && i <= boardSize/2)	return false;
			if(board[i][boardSize/2] == number && i >= boardSize/2)	return false;
		}
		return true;
	}

	private static boolean isValidInYRight(int[][] board, int boardSize, int index, int number){
		for(int i = 0; i < boardSize; i++){
			if(i==index)	continue;
			if(board[i][boardSize-i-1] == number && i <= boardSize/2)	return false;
			if(board[i][boardSize/2] == number && i >= boardSize/2)		return false;
		}
		return true;
	}

}

	