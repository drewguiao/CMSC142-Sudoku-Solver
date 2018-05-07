import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.text.DefaultCaret;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

class SudokuGUI{
	private JFrame sudokuFrame;
	private JFrame solutionsFrame;
	private JPanel menuPanel, boardPanel,solutionsPanel;
	private JButton selectPuzzle, solveButton, getSolutionsButton;
	private JTextField grid[][];
	private int boardSize;
	private int subGridSize;
	private int[][] board;

	private static final String TITLE = "SUDOKU";
	private static final int JTEXTFIELD_NUM_OF_COLS = 2;
	private static final int JFRAME_SIZE = 700;

	public SudokuGUI(int subGridSize, int[][] board){
		this.subGridSize = subGridSize;
		this.boardSize = subGridSize * subGridSize;
		this.grid = new JTextField[boardSize][boardSize];
		this.board = board;

		this.buildMenuPanel();
		this.buildBoardPanel();
		this.buildSudokuFrame();
		this.buildSolutionsFrame();
	}

	public void render(){
		this.sudokuFrame.setVisible(true);
	}

	private void buildMenuPanel(){
		this.menuPanel = new JPanel();

		this.selectPuzzle = new JButton("Select Puzzle");
		this.solveButton = new JButton("Solve");
		this.getSolutionsButton = new JButton("Get possible solutions");

		this.selectPuzzle.addActionListener(
			provideSolutionListenerSelect()
			);

		this.getSolutionsButton.addActionListener(
			provideSolutionListenerGet()
			);

		this.menuPanel.add(this.selectPuzzle);
		this.menuPanel.add(this.solveButton);
		this.menuPanel.add(this.getSolutionsButton);

	}

	private void buildBoardPanel(){
		this.boardPanel = new JPanel();
		this.boardPanel.setLayout(new GridLayout(this.boardSize,this.boardSize));
		for(int i = 0; i < this.boardSize; i++){
			for(int j = 0; j < this.boardSize; j++){
				this.grid[i][j] = new JTextField(JTEXTFIELD_NUM_OF_COLS);
				this.grid[i][j].setHorizontalAlignment(JTextField.CENTER);
				this.grid[i][j].setText(""+ ((board[i][j] == 0) ? "" : ""+board[i][j]));
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

	private ActionListener provideSolutionListenerSelect(){
		ActionListener action = new ActionListener(){
			@Override
				public void actionPerformed(ActionEvent e){
					getFrame().setVisible(true);
				}
		};
		return action;
	}


	private ActionListener provideSolutionListenerGet(){
		ActionListener action = new ActionListener(){
			@Override
				public void actionPerformed(ActionEvent e){
					getFrame().setVisible(true);
				}
		};
		return action;
	}

	private void buildSolutionsFrame(){
		this.solutionsFrame = new JFrame("Solutions");
		this.solutionsPanel = new JPanel(new GridLayout(1,1));
		JTextArea possibleSolutions = new JTextArea();

		DefaultCaret caret = (DefaultCaret)possibleSolutions.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.add(possibleSolutions);
		scrollPane.setViewportView(possibleSolutions);

		this.solutionsPanel.add(scrollPane);
		this.solutionsFrame.add(solutionsPanel);
		this.solutionsFrame.setSize(JFRAME_SIZE/2,JFRAME_SIZE);
	}

	public JFrame getFrame(){
		JFrame retrieved = this.solutionsFrame;
		return retrieved;
	}


}