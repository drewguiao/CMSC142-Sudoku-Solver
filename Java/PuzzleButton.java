import javax.swing.JButton;


class PuzzleButton extends JButton{
	private Puzzle puzzle;

	public PuzzleButton(Puzzle puzzle){
		this.puzzle = puzzle;
	}

	public Puzzle getPuzzle(){
		return this.puzzle;
	}



}