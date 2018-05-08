import java.util.List;
import java.util.Scanner;
class SudokuRunner implements Constants{
	private static final String INPUT_FILE = "input.txt";

	public static void main(String[] args){
		SudokuDAO sudokuDAO = new SudokuDAO();
		sudokuDAO.getPuzzlesFromFile(INPUT_FILE);
		List<Puzzle> puzzles = sudokuDAO.getPuzzles();
		SudokuGUI sudokuGUI = new SudokuGUI(puzzles);
		sudokuGUI.render();

	}



}