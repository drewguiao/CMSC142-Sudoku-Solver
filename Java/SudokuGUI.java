import java.util.List;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.text.DefaultCaret;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

class SudokuGUI{
	private static final String TITLE = "SUDOKU";
	private static final int JTEXTFIELD_NUM_OF_COLS = 2;
	private static final int JFRAME_SIZE = 700;
	private static final String INPUT_FILE = "input.txt";
	private static final String OUTPUT_FILE = "output.txt";

	private JFrame sudokuFrame, solutionsFrame, listFrame;
	private JPanel menuPanel, solutionsPanel, listPanel;
	private JPanel boardPanel;
	private JButton selectPuzzleButton, solveButton, showPossibleSolutionsButton;
	private JTextArea solutionsArea;
	private JTextField grid[][];
	private PuzzleButton[] puzzleButtons;

	private int numberOfPuzzles;
	private List<Puzzle> puzzles;
	private Puzzle currentPuzzle;
	private int subGridSize;

	public SudokuGUI(List<Puzzle> puzzles){
		this.puzzles = puzzles;
		this.numberOfPuzzles = puzzles.size();
		this.currentPuzzle = puzzles.get(0);
		this.subGridSize = currentPuzzle.getSubGridSize();

		this.buildListFrame(); // build GUI that contains list of puzzles
		
		this.buildMenuPanel();
		this.buildBoardPanel();
		this.buildSudokuFrame();
	}

	public void render(){
		this.sudokuFrame.setVisible(true);
	}


	private void buildMenuPanel(){
		this.menuPanel = new JPanel();

		this.selectPuzzleButton = new JButton("Select Puzzle");
		this.solveButton = new JButton("Solve");
		this.showPossibleSolutionsButton = new JButton("Show possible solutions");

		this.selectPuzzleButton.addActionListener(provideSelectPuzzleListener());
		this.showPossibleSolutionsButton.addActionListener(provideShowPossibleSolutionsListener());
		this.solveButton.addActionListener(provideSolveListener());

		this.menuPanel.add(this.selectPuzzleButton);
		this.menuPanel.add(this.solveButton);
		this.menuPanel.add(this.showPossibleSolutionsButton);

	}

	private void buildBoardPanel(){
		int boardSize = this.currentPuzzle.getBoardSize();
		int[][] board = this.currentPuzzle.getBoard();

		this.boardPanel = new JPanel();
		this.boardPanel.setLayout(new GridLayout(boardSize,boardSize));

		this.grid = new JTextField[boardSize][boardSize];

		for(int i = 0; i < boardSize; i++){
			for(int j = 0; j < boardSize; j++){
				this.grid[i][j] = new JTextField(JTEXTFIELD_NUM_OF_COLS);
				this.grid[i][j].setHorizontalAlignment(JTextField.CENTER);
				this.grid[i][j].setText(""+ ((board[i][j] == 0)? "" : ""+board[i][j]));
				this.grid[i][j].setEditable((board[i][j] != 0) ? false: true);
				this.boardPanel.add(this.grid[i][j]);
			}
		}
	}


	private void buildSudokuFrame(){
		this.sudokuFrame = new JFrame(TITLE);
		this.sudokuFrame.setLayout(new BorderLayout());

		this.sudokuFrame.add(this.menuPanel,BorderLayout.PAGE_START);
		this.sudokuFrame.add(this.boardPanel,BorderLayout.CENTER);

		this.sudokuFrame.setSize(JFRAME_SIZE,JFRAME_SIZE);
		this.sudokuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}


	private ActionListener provideSelectPuzzleListener(){
		ActionListener listener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				listFrame.setVisible(true);
			}
		};
		return listener;
	}

	private ActionListener provideOnClickListener(){
		ActionListener listener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent ae){
				PuzzleButton clickedButton = (PuzzleButton) ae.getSource();
				setPuzzle(clickedButton.getPuzzle());
			}
		};
		return listener;
	}

	private ActionListener provideShowPossibleSolutionsListener(){
		ActionListener listener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				SudokuSolver sudokuSolver = new SudokuSolver();
				sudokuSolver.solve(currentPuzzle);
				SudokuDAO sudokuDAO = new SudokuDAO();
				String solutions = sudokuDAO.getSolutionsFromOutputFile(OUTPUT_FILE);
				buildSolutionsFrame(solutions);
				solutionsFrame.setVisible(true);
			}
		};
		return listener;
	}

	private ActionListener provideSolveListener(){
		ActionListener listener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				SudokuSolver solver = new SudokuSolver();
				if(solver.isALegitimateSolution(grid, subGridSize)){
					JOptionPane.showMessageDialog(sudokuFrame, "You got it right!");
				}else{
					JOptionPane.showMessageDialog(sudokuFrame, "Oh no! Your solution is wrong!");
				}
			}
		};
		return listener;
	}


	private void buildSolutionsFrame(String solutions){
		this.solutionsFrame = new JFrame("Solutions");
		this.solutionsPanel = new JPanel(new GridLayout(1,1));
		this.solutionsArea = new JTextArea();

		DefaultCaret caret = (DefaultCaret)solutionsArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.add(solutionsArea);
		scrollPane.setViewportView(solutionsArea);
		this.solutionsArea.setEditable(false);
		this.solutionsArea.setText(solutions);

		this.solutionsPanel.add(scrollPane);
		this.solutionsFrame.add(solutionsPanel);
		this.solutionsFrame.setSize(JFRAME_SIZE/2,JFRAME_SIZE);
	}

	private void buildListFrame(){
		this.listFrame = new JFrame("Select a puzzle:");
		this.listPanel = new JPanel();

		this.puzzleButtons = new PuzzleButton[this.numberOfPuzzles];
		for(int i = 0; i < numberOfPuzzles; i++){
			puzzleButtons[i] = new PuzzleButton(this.puzzles.get(i));
			puzzleButtons[i].setText("Puzzle #"+(i+1));
			puzzleButtons[i].addActionListener(provideOnClickListener());
			this.listPanel.add(puzzleButtons[i]);
		}

		this.listFrame.add(listPanel);
		this.listFrame.setSize(JFRAME_SIZE/2,JFRAME_SIZE);
		this.listFrame.pack();
	}
	
	public void setPuzzle(Puzzle puzzle){
		System.out.println(currentPuzzle);
		if(currentPuzzle != null){
			int currentBoardSize = currentPuzzle.getBoardSize();

			for(int i = 0; i < currentBoardSize; i++){
				for(int j = 0; j < currentBoardSize;j++){
					this.boardPanel.remove(grid[i][j]);
				}
			}
			this.sudokuFrame.remove(boardPanel);
		}

		
		this.currentPuzzle = puzzle;
		this.subGridSize = this.currentPuzzle.getSubGridSize();
		this.buildBoardPanel();

		this.sudokuFrame.add(this.boardPanel,BorderLayout.CENTER);
		// this.sudokuFrame.pack();
		this.sudokuFrame.revalidate();
		this.sudokuFrame.repaint();
	}


	
}