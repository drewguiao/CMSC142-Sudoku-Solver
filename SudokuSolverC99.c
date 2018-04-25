/*
    CMSC 142 SUDOKU-XY
    2nd Semester AY 2017-2018
    Casion, Claudine
    Guiao, Justine Andrew
    Leano, Dominic

*/

#include <stdio.h>
#include <stdlib.h>
#include <malloc.h>
#include <math.h>

#define TRUE 1
#define FALSE 0
#define EMPTY 0

int** initializeGrid(int subGridSize);
void fillGridWithDummyData(int** sudokuGrid, int subGridSize);
void printGrid(int** sudokuGrid,int subGridSize);
int isFull(int** sudokuGrid, int subGridSize);
int* getPossibleEntries(int** sudokuGrid, int subGridSize, int i, int j);
void solve(int** sudokuGrid, int subGridSize);
void saveToFile(int** sudokuGrid,int subGridSize);

int numberOfSolutions = 0;
//custom data type to store all sudokuPuzzles from txt file
typedef struct{
    int **sudokuGrid;
    int subGridSize;
}ArrayList;

int main(){

    int numOfPuzzles;
    FILE *fp = fopen("input.txt","r"); //read input file
    fscanf(fp,"%d",&numOfPuzzles); //read first number in txt file as number of SudokuPuzzles

    ArrayList* puzzles = malloc(sizeof(puzzles)* numOfPuzzles); //allocate memory for puzzles

    //retrieves all puzzles in the text file
    int i,j,k;
    for(i = 0; i < numOfPuzzles; i++){
        fscanf(fp,"%d",&puzzles[i].subGridSize); //get input from txt file as the size of the puzzle
        puzzles[i].sudokuGrid = initializeGrid(puzzles[i].subGridSize);
        int absoluteSize = puzzles[i].subGridSize * puzzles[i].subGridSize;
        for(j = 0; j < absoluteSize;j++){
            for(k = 0; k < absoluteSize; k++){
                fscanf(fp,"%d",&puzzles[i].sudokuGrid[j][k]);
            }
        }
        printf("PUZZLE #%d: Size: %d\n",i+1,puzzles[i].subGridSize);
        printGrid(puzzles[i].sudokuGrid,puzzles[i].subGridSize);
        solve(puzzles[i].sudokuGrid,puzzles[i].subGridSize);
        printf("Number of solutions: %d\n",numberOfSolutions);
        numberOfSolutions = 0;
    }
    fclose(fp);
}
int** initializeGrid(int subGridSize){
    int absoluteSize = subGridSize * subGridSize;
    int **grid = malloc(sizeof *grid * absoluteSize);
    int i;
    for(i = 0; i < absoluteSize; i++){
        grid[i] = malloc(sizeof *grid[i] * absoluteSize);
    }
    return grid;
}

void fillGridWithDummyData(int** sudokuGrid, int subGridSize){
    int absoluteSize = subGridSize * subGridSize;
    int i,j;
    for(i = 0; i < absoluteSize; i++){
        for(j = 0; j < absoluteSize;j++){
            sudokuGrid[i][j] = 1;
        }
    }
}

void printGrid(int** sudokuGrid, int subGridSize){
    int absoluteSize = subGridSize * subGridSize;
    int separatorCounter = 0;
    int i, j,h;
    for(i = 0; i < absoluteSize; i++){
        for(j = 0; j < absoluteSize; j++){
            printf("%d ",sudokuGrid[i][j]);
            if( (j+1) % subGridSize == 0 && j+1 != absoluteSize) {
                printf("| ");
                separatorCounter++;
            }
        }

        printf("\n");
        if((i+1) % subGridSize == 0 && (i+1) != absoluteSize){
            for(h = 0; h < absoluteSize+separatorCounter; h++){
                printf("- ");
            }
            printf("\n");
        }
        separatorCounter = 0;
    }
}

int isFull(int** sudokuGrid, int subGridSize){
    int absoluteSize = subGridSize * subGridSize;
    int i,j;
    for(i = 0; i < absoluteSize; i++){
        for(j = 0; j < absoluteSize; j++){
            if(sudokuGrid[i][j] == EMPTY) return FALSE;
        }
    }
    return TRUE;
}

int* getPossibleEntries(int** sudokuGrid, int subGridSize, int i, int j){
    int absoluteSize = subGridSize * subGridSize;
    int* possibleEntries = malloc(sizeof possibleEntries * absoluteSize);
    int x,y;
    //initialize possible entries to zero
    for(x = 0; x < absoluteSize; x++) possibleEntries[x] = 0;

    //for horizontal checking
    for(y = 0; y < absoluteSize; y++){
        if(sudokuGrid[i][y] != EMPTY){
            possibleEntries[sudokuGrid[i][y]-1] = 1;
        }
    }

    //for vertical checking
    for(x = 0; x < absoluteSize; x++){
        if(sudokuGrid[x][j] != EMPTY){
            possibleEntries[sudokuGrid[x][j]-1] = 1;
        }
    }

    //for each square box
    //find square box index first
    int boxIndexX = (i / subGridSize) * subGridSize;
    int boxIndexY = (j / subGridSize) * subGridSize;

    //find entries in box
    for(x = boxIndexX; x < subGridSize+boxIndexX; x++){
        for(y = boxIndexY; y < subGridSize+boxIndexY; y++){
            if(sudokuGrid[x][y] != EMPTY){
                possibleEntries[sudokuGrid[x][y]-1] = 1;
            }
        }
    }

    for(x = 0; x < absoluteSize; x++){
        possibleEntries[x] = (possibleEntries[x] == EMPTY) ? (x+1) : 0;
    }

    return possibleEntries;
}

void solve(int** sudokuGrid, int subGridSize){

    if(isFull(sudokuGrid,subGridSize) == TRUE){
        printf("Board solved!\n");
        printGrid(sudokuGrid,subGridSize);
        numberOfSolutions++;
        saveToFile(sudokuGrid,subGridSize);
        //return sudokuGrid;
    }else{
        //find vacant spot first
        int absoluteSize = subGridSize * subGridSize;
        int i = 0;
        int j = 0;
        int x,y;
        int flag = FALSE;
        for(x = 0; x < absoluteSize; x++){
            for(y = 0; y < absoluteSize; y++){
                if(sudokuGrid[x][y] == EMPTY){
                    i = x;
                    j = y;
                    flag = TRUE;
                    break;
                }
            }
            if(flag == TRUE) break;
        }
        int* possibilities = getPossibleEntries(sudokuGrid,subGridSize,i,j);
        for(x = 0; x < absoluteSize; x++){
            if(possibilities[x] != EMPTY){
                sudokuGrid[i][j] = possibilities[x];
                solve(sudokuGrid, subGridSize);
            }
        }
        //backtracking
        sudokuGrid[i][j] = EMPTY;
    }
}

void saveToFile(int** sudokuGrid, int subGridSize){
    FILE *f = fopen("output.txt","a");
    int i,j;
    if(f == NULL){
        printf("Error reading file");
        exit(1);
    }
    int absoluteSize = subGridSize * subGridSize;
    for(i = 0; i < absoluteSize;i++){
        for(j = 0; j < absoluteSize; j++){
            fprintf(f,"%d ",sudokuGrid[i][j]);
        }
        fprintf(f,"\n");
    }
    fprintf(f,"\n");
    fclose(f);
}
