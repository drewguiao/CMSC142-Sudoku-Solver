typedef struct{
    int **sudokuGrid;
    int subGridSize;
}ArrayList;

typedef struct{
    int x;
    int y;
}Cell;

int** initializeGrid(int subGridSize);
void resetOutputFile();
ArrayList* retrievePuzzlesFromInputFile(int numberOfPuzzles, FILE *fp);
void startSolving(ArrayList* puzzles, int numOfPuzzles);
void printGrid(int** sudokuGrid,int subGridSize);
void appendNumberOfSolutionsToTextFile();
int isFull(int** sudokuGrid, int subGridSize);
int* getPossibleEntries(int** sudokuGrid, int subGridSize, Cell cell);
void solve(int** sudokuGrid, int subGridSize);
Cell findEmptyCell(int** sudokuGrid, int absoluteSize);
void saveToFile(int** sudokuGrid,int subGridSize);
int* initializeEntries(int absoluteSize);
void checkRowForUnwantedEntries(int* possibleEntries, int absoluteSize, int** sudokuGrid, int xIndex);
void checkColForUnwantedEntries(int* possibleEntries, int absoluteSize, int** sudokuGrid, int yIndex);

