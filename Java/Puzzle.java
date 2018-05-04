class Puzzle{

	private int subGridSize;
	private int boardSize;
	public int[][] board;
	private int numOfSolutions = 0;

	public Puzzle(int subGridSize, int boardSize, int[][]  board){
		this.subGridSize = subGridSize;
		this.boardSize = boardSize;
		this.board = board;
	}

	public int getSubGridSize(){
		return this.subGridSize;
	}

	public int getBoardSize(){
		return this.boardSize;
	}

	

	public void updateBoard(int[][] newBoard){
		this.board = newBoard;
	}

	public void updateNumberOfSolutions(){
		this.numOfSolutions++;
	}

	public int getNumberOfSolutions(){
		return this.numOfSolutions;
	}

	@Override
	public String toString(){
		String retVal = "Size: "+this.subGridSize+"\n";
		for(int i = 0; i < boardSize; i++){
			for(int j = 0; j < boardSize; j++){
				retVal += ""+board[i][j]+" ";
			}
			retVal+="\n";
		}
		return retVal;
	}
}