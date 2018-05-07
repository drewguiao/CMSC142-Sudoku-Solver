import java.util.List;
import java.util.Scanner;
class SudokuRunner implements Constants{
	private static final String INPUT_FILE = "input.txt";
	private static final int DEFAULT_CHOICE = 99;
	private static final int EXIT = 0;
	private static final int NEXT = 5;

	public static void main(String[] args){
		Scanner console = new Scanner(System.in);
		int choice = DEFAULT_CHOICE;

		Sudoku s = new Sudoku();
		s.readFile(INPUT_FILE);
		s.startSolving();

		List<Puzzle> puzzles = s.getPuzzles();
		int numOfPuzzles = puzzles.size();

		// while(choice != EXIT){
		// 	for(int i = 0; i < numOfPuzzles;){
		// 		Puzzle puzzle = puzzles.get(i);
		// 		System.out.println(puzzle);
		// 		showMenu();
		// 		choice = console.nextInt();
		// 		switch(choice){
		// 			case NATURAL_SOLVING: s.solve(puzzle,NATURAL_SOLVING);
		// 			break;
		// 			case X_SOLVING: s.solve(puzzle, X_SOLVING);
		// 			break;
		// 			case Y_SOLVING: s.solve(puzzle, Y_SOLVING);
		// 			break;
		// 			case XY_SOLVING: s.solve(puzzle, XY_SOLVING);
		// 			break;
		// 			case NEXT: i++;
		// 			break;
		// 			case EXIT: System.out.println("Exiting application!");
		// 						i = numOfPuzzles;
		// 			break;
		// 		}
		// 		puzzle.board = puzzle.getOriginalBoard();
		// 		puzzle.resetSolutions();
		// 	}
		// }

		Puzzle puzzle = puzzles.get(0);
		int[][] board = puzzle.getBoard();
		int subGridSize = puzzle.getSubGridSize();

		SudokuGUI gui = new SudokuGUI(subGridSize,board);
		gui.render();
	}

	private static void showMenu(){
		System.out.println("Solve via:");
		System.out.println("[1] Natural");
		System.out.println("[2] X");
		System.out.println("[3] Y");
		System.out.println("[4] XY");
		System.out.println("[5] Next Puzzle");
	}

}