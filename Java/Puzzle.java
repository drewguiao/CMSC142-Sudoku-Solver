import java.util.List;
import java.util.ArrayList;

class Puzzle{

	private int subGridSize;
	private int boardSize;
	public int[][] board;
	private int[][] originalBoard;
	private int puzzleNumber;

	private int numOfSolutions = 0;
	private List<int[][]> solutions = new ArrayList<>();

	public Puzzle(int subGridSize, int boardSize, int[][]  board, int puzzleNumber){
		this.subGridSize = subGridSize;
		this.boardSize = boardSize;
		this.board = board;
		this.originalBoard = board;
		this.puzzleNumber = puzzleNumber;
	}

	public int getSubGridSize(){
		return this.subGridSize;
	}

	public int getBoardSize(){
		return this.boardSize;
	}

	public int[][] getBoard(){
		return this.board;
	}

	public String getVerboseSolutions(){
		String retVal="";

		for(int[][] solution: solutions){
			for(int i = 0; i < boardSize; i++){
				for(int j = 0; j < boardSize; j++){
					retVal += ""+solution[i][j]+" ";
				}
				retVal += "\n";
			}
			retVal += "\n\n";
		}
		return retVal;
	}


	public void addSolution(int[][] board){
		this.solutions.add(board);
	}

	public List<int[][]> getSolutions(){
		return this.solutions;
	}

	public int[][] getOriginalBoard(){
		return this.originalBoard;
	}

	
	public int getNumberOfSolutions(){
		return this.solutions.size();
	}

	public void resetSolutions(){
		this.solutions.clear();
	}

	public int getPuzzleNumber(){
		return this.puzzleNumber;
	}

	@Override
	public String toString(){
		String retVal = "Size: "+this.subGridSize+"\n";
		for(int i = 0; i < boardSize; i++){
			for(int j = 0; j < boardSize; j++){
				retVal += ""+board[i][j]+" ";
			}
			retVal+="\n";
		}
		return retVal;
	}
}