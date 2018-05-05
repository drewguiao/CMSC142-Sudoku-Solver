import java.util.List;

class SudokuRunner{
	private static final String INPUT_FILE = "input.txt";
	public static void main(String[] args){
		Sudoku s = new Sudoku();
		s.readFile(INPUT_FILE);
		// s.startSolving();

		List<Puzzle> puzzles= s.getPuzzles();
		Puzzle puzzle = puzzles.get(0);
		int[][] board = puzzle.getBoard();
		int subGridSize = puzzle.getSubGridSize();

		SudokuGUI gui = new SudokuGUI(subGridSize,board);
		gui.render();
	}

}