import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

class SudokuDAO{
	private int numberOfPuzzles;
	private List<Puzzle> puzzles = new ArrayList<>();

	public SudokuDAO(){}

	public void getPuzzlesFromFile(String fileName){
		try{
			BufferedReader breader = new BufferedReader(new FileReader(fileName));
			this.numberOfPuzzles = Integer.parseInt(breader.readLine());
			for(int i = 0 ; i < this.numberOfPuzzles; i++){
				int subGridSize = Integer.parseInt(breader.readLine());
				int boardSize = subGridSize * subGridSize;
				int[][] board = new int[boardSize][boardSize];

				for(int x = 0; x < boardSize; x++){
					String currentLine = breader.readLine();
					String[] tokens = currentLine.split(" ");
					for(int y = 0; y < boardSize; y++){
						board[x][y] = Integer.parseInt(tokens[y]);
					}
				}

				Puzzle puzzle = new Puzzle(subGridSize, boardSize, board, i);
				this.puzzles.add(puzzle);
			}
			breader.close();
		}catch(IOException ioe){
			System.out.println("SudokuDAO.java.getPuzzlesFromFile():"+ioe.getMessage());
		}
	}
	
	public int getNumberOfPuzzles(){
		return this.numberOfPuzzles;
	}

	public List<Puzzle> getPuzzles(){
		return this.puzzles;
	}

	public String getSolutionsFromOutputFile(String fileName){
		String solutions = "";
		try{
			BufferedReader breader = new BufferedReader(new FileReader(fileName));
			while(breader.ready()){
				solutions += breader.readLine() +"\n";
			}
			breader.close();
		}catch(IOException ioe){
			System.out.println("SudokuDAO.java.getSolutionsFromOutputFile():"+ioe.getMessage());
		}
		return solutions;
	}

}