import java.util.List;

class SudokuRunner{
	private static final String INPUT_FILE = "input.txt";
	public static void main(String[] args){
		SudokuDAO sudokuDAO = new SudokuDAO();
		sudokuDAO.getPuzzlesFromFile(INPUT_FILE);
		List<Puzzle> puzzles = sudokuDAO.getPuzzles();
		SudokuGUI sudokuGUI = new SudokuGUI(puzzles);
		sudokuGUI.render();

	}
}
