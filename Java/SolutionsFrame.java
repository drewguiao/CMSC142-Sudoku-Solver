import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

class SolutionsFrame extends JFrame implements ActionListener{
  private static final int JFRAME_SIZE = 500;

  private JPanel boardPanel	  = new JPanel();
  private JPanel buttonsPanel	= new JPanel();

  private JButton prev 	= new JButton("Previous");
	private JButton next 	= new JButton("Next");
  private JButton[][] board;

  private JLabel solLabel = new JLabel();

  private Puzzle puzzle;
  private int boardSize;
  private int solIndex;

  public SolutionsFrame(Puzzle puzzle){
    super("Puzzle #" + puzzle.getPuzzleNumber() + " Solutions");
    this.puzzle = puzzle;
    this.boardSize = puzzle.getBoardSize();
    this.board = new JButton[this.boardSize][this.boardSize];
    this.solIndex = 0;

    this.setSize(JFRAME_SIZE, JFRAME_SIZE);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLayout(new BorderLayout());
		this.setComponents();
		this.pack();
    this.setLocationRelativeTo(null);
		this.setVisible(true);
  }

  private void setComponents(){
    this.boardPanel.setLayout(new GridLayout(this.boardSize,this.boardSize));
		this.setBoard();
    
    this.prev.addActionListener(this);
		this.next.addActionListener(this);

    this.solLabel.setText("Solution #" + (this.solIndex+1));
    this.buttonsPanel.add(this.solLabel);
    this.buttonsPanel.add(this.prev);
    this.buttonsPanel.add(this.next);

    this.add(boardPanel, BorderLayout.CENTER);
		this.add(buttonsPanel, BorderLayout.PAGE_END);
  }

  private void setBoard(){
    for(int i=0; i<this.boardSize; i++){
      for(int j=0; j<this.boardSize; j++){
        this.board[i][j] = new JButton(Integer.toString(this.puzzle.getSolutions().get(this.solIndex)[i][j]));
        this.board[i][j].setFocusPainted(false);
        this.board[i][j].setPreferredSize(new Dimension(400/this.boardSize, 300/this.boardSize));
    		this.board[i][j].setBackground(Color.WHITE);
        this.boardPanel.add(this.board[i][j]);
      }
		}
  }

  private void update(){
    for(int i=0; i<this.boardSize; i++)
      for(int j=0; j<this.boardSize; j++)
        this.board[i][j].setText(Integer.toString(this.puzzle.getSolutions().get(this.solIndex)[i][j]));
    this.solLabel.setText("Solution #" + (solIndex+1));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
  	JButton button = ((JButton)(e.getSource()));

  	if(button==this.prev && this.solIndex-1>=0) this.solIndex--;
  	else if(button==this.next && this.solIndex+1<this.puzzle.getNumberOfSolutions()) this.solIndex++;
  	this.update();
  }

}
