import java.awt.Color;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.text.DefaultCaret;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

class SudokuGUI{
	private static final String TITLE = "SUDOKU";
	private static final int JTEXTFIELD_NUM_OF_COLS = 2;
	private static final int JFRAME_SIZE = 700;
	private static final String INPUT_FILE = "input.txt";
	private static final String OUTPUT_FILE = "output.txt";
	private static final int NORMAL = 0, SUDOKU_X = 1, SUDOKU_Y = 2, SUDOKU_XY = 3;
	private static final String MODE0 = "Normal Sudoku", MODE1 = "Sudoku X", MODE2 = "Sudoku Y", MODE3 = "Sudoku XY";
	private JFrame sudokuFrame, listFrame;
	private JPanel menuPanel, listPanel;
	private JPanel boardPanel;
	private JButton selectPuzzleButton, submitButton, showPossibleSolutionsButton;
	private JTextField grid[][];
	private PuzzleButton[] puzzleButtons;
	private JComboBox<String> modeSelector;

	private int numberOfPuzzles;
	private List<Puzzle> puzzles;
	private Puzzle currentPuzzle;
	private int subGridSize, mode;
	private String[] modes;

	public SudokuGUI(List<Puzzle> puzzles){
		this.puzzles = puzzles;
		this.numberOfPuzzles = puzzles.size();
		this.currentPuzzle = puzzles.get(0);

		this.subGridSize = currentPuzzle.getSubGridSize();
		this.mode = 0;
		this.modes = new String[]{MODE0, MODE1, MODE2, MODE3};
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
		this.submitButton = new JButton("Submit");
		this.showPossibleSolutionsButton = new JButton("Show possible solutions");
		this.modeSelector = new JComboBox<String>(this.modes);

		this.selectPuzzleButton.addActionListener(provideSelectPuzzleListener());
		this.showPossibleSolutionsButton.addActionListener(provideShowPossibleSolutionsListener());
		this.submitButton.addActionListener(provideSubmitListener());
		this.modeSelector.addItemListener(provideSelectionListener());

		this.menuPanel.add(this.selectPuzzleButton);
		this.menuPanel.add(this.submitButton);
		this.menuPanel.add(this.showPossibleSolutionsButton);
		this.menuPanel.add(this.modeSelector);
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
				this.grid[i][j].getDocument().addDocumentListener(provideDocumentListener());
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
		this.sudokuFrame.setLocationRelativeTo(null);

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
				JFrame thisFrame = (JFrame )(SwingUtilities.getRoot(clickedButton));
				thisFrame.setVisible(false);
			}
		};
		return listener;
	}

	private ActionListener provideShowPossibleSolutionsListener(){
		ActionListener listener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				// if(currentPuzzle.getSolutions().size() == 0) JOptionPane.showMessageDialog(sudokuFrame, "Oh no! There are no solutions!");
				// else{
				// 	System.out.println("NATURAL_SOLUTIONS: "+currentPuzzle.getSolutions().size());
				// 	System.out.println("X_SOLUTIONS: "+currentPuzzle.getXSolutions().size());
				// 	System.out.println("Y_SOLUTIONS: "+currentPuzzle.getYSolutions().size());
				// 	System.out.println("XY_SOLUTIONS: "+currentPuzzle.getXYSolutions().size());
				// 	new SolutionsFrame(currentPuzzle);
				// }
				List<int[][] > solutions = SudokuSolver.solve(grid,subGridSize);
				List<int[][]> solvingModeSolution = new ArrayList<>();
				switch(mode){
					case NORMAL:
						solvingModeSolution = solutions;
						break;
					case SUDOKU_X:
						solvingModeSolution = SudokuSolver.filterXSolutions(solutions,subGridSize);
						break;
					case SUDOKU_Y:
						solvingModeSolution = SudokuSolver.filterYSolutions(solutions,subGridSize);
						break;
					case SUDOKU_XY:
						solvingModeSolution = SudokuSolver.filterXYSolutions(solutions,subGridSize);
						break;
				}
				if(solvingModeSolution.size() == 0){
					JOptionPane.showMessageDialog(null,"No solutions!");
				}else new SolutionsFrame(solvingModeSolution, subGridSize);
			}
		};
		return listener;
	}

	private ActionListener provideSubmitListener(){
		ActionListener listener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				if(SudokuSolver.isALegitimateSolution(grid, subGridSize)){
					JOptionPane.showMessageDialog(sudokuFrame, "You got it right!");
				}else{
					JOptionPane.showMessageDialog(sudokuFrame, "Oh no! Your solution is wrong!");
				}
			}
		};
		return listener;
	}

	private ItemListener provideSelectionListener(){
		ItemListener listener = new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e){
				int boardSize = SudokuGUI.this.subGridSize * SudokuGUI.this.subGridSize;
			 	if (e.getStateChange() == ItemEvent.SELECTED) {
				 	String selectedItem = (String) e.getItem();
				 	if(selectedItem == MODE0)	SudokuGUI.this.mode = NORMAL;
				 	else if(selectedItem == MODE1)	SudokuGUI.this.mode = SUDOKU_X;
				 	else if(selectedItem == MODE2)	SudokuGUI.this.mode = SUDOKU_Y;
				 	else if(selectedItem == MODE3)	SudokuGUI.this.mode = SUDOKU_XY;
				}
				for(int i = 0; i < boardSize; i++)
					for(int j = 0; j < boardSize; j++)
						if(grid[i][j].isEditable()) grid[i][j].setText("");
			}
		};
		return listener;
	}

	private DocumentListener provideDocumentListener(){
		int boardSize = subGridSize*subGridSize;
		DocumentListener listener = new DocumentListener(){
			@Override
			public void insertUpdate(DocumentEvent e) {
				textFieldChanged(e);
		    }
		    @Override
		    public void removeUpdate(DocumentEvent e) {
		    	textFieldChanged(e);
		    }

		    public void changedUpdate(DocumentEvent e) {}

		    public void textFieldChanged(DocumentEvent e){
				Document updatedDocument = e.getDocument();
			    for (int i = 0; i < boardSize; i++) {
					for (int j = 0; j < boardSize; j++) {
					    if (updatedDocument == grid[i][j].getDocument()){
					    	String inputAnswer = grid[i][j].getText();
					        if(inputAnswer.isEmpty()){
					        	grid[i][j].setBackground(Color.WHITE);
					        	showPossibleSolutionsButton.setEnabled(true);
					        }
							else{
								try{
									int answer = Integer.parseInt(inputAnswer);
									if(!(SudokuSolver.isValid(grid, subGridSize, new Cell(i,j), SudokuGUI.this.mode)) || answer > boardSize){
									grid[i][j].setBackground(Color.RED);
										showPossibleSolutionsButton.setEnabled(false);	
									}
									else{
										grid[i][j].setBackground(Color.GREEN);
										showPossibleSolutionsButton.setEnabled(true);	
									}
								}catch(NumberFormatException nfe){
									grid[i][j].setBackground(Color.RED);
									JOptionPane.showMessageDialog(null, "Invalid element! @ ("+i+","+j+")");
								}
								
							}
			     		}
				    }
  				}
			}
		};

		
		return listener;
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
		if(this.currentPuzzle.getSolutions().size() == 0){

			SudokuSolver.solve(currentPuzzle);
			
		}

		this.subGridSize = this.currentPuzzle.getSubGridSize();
		this.buildBoardPanel();

		this.sudokuFrame.add(this.boardPanel,BorderLayout.CENTER);
		
		this.sudokuFrame.revalidate();
		this.sudokuFrame.repaint();
	}



}
