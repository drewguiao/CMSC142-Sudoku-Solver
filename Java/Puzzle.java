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
	private List<int[][]> naturalSolutions = new ArrayList<>();
	private List<int[][]> xSolutions = new ArrayList<>();
	private List<int[][]> ySolutions = new ArrayList<>();
	private List<int[][]> xySolutions = new ArrayList<>();


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

	public void addSolution(int[][] board){
		this.solutions.add(board);
	}

	public void addXSolution(int[][] board){
		this.xSolutions.add(board);
	}
	
	public void addYSolution(int[][] board){
		this.ySolutions.add(board);
	}
	
	public void addXYSolution(int[][] board){
		this.xySolutions.add(board);
	}


	public List<int[][]> getSolutions(){
		return this.solutions;
	}

	public List<int[][]> getXSolutions(){
		return this.xSolutions;
	}

	public List<int[][]> getYSolutions(){
		return this.ySolutions;
	}

	public List<int[][]> getXYSolutions(){
		return this.xySolutions;
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
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < boardSize; i++){
			for(int j = 0; j < boardSize; j++){
				builder.append(this.board[i][j]);
				builder.append(" ");
			}
			builder.append("\n");
		}
		return builder.toString();
	}
}