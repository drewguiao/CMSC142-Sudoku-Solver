class SudokuRunner{
	private static final String INPUT_FILE = "input.txt";

	public static void main(String[] args){
		Sudoku s = new Sudoku();
		s.readFile(INPUT_FILE);
		s.startSolving();
	}

}