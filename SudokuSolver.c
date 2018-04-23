/*
    CMSC 142 SUDOKU-XY
    2nd Semester AY 2017-2018
    Casion, Claudine
    Guiao, Justine Andrew
    Leano, Dominic

*/
#include<stdio.h>
#include<stdlib.h>
#include<malloc.h>

int** initializeGrid(int subGridSize);
void fillGridWithDummyData(int** sudokuGrid, int subGridSize);
void printGrid(int** sudokuGrid,int subGridSize);

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
    for(int i = 0; i < numOfPuzzles; i++){
        fscanf(fp,"%d",&puzzles[i].subGridSize); //get input from txt file as the size of the puzzle
        puzzles[i].sudokuGrid = initializeGrid(puzzles[i].subGridSize);
        int absoluteSize = puzzles[i].subGridSize * puzzles[i].subGridSize;
        for(int j = 0; j < absoluteSize;j++){
            for(int k = 0; k < absoluteSize; k++){
                fscanf(fp,"%d",&puzzles[i].sudokuGrid[j][k]);
            }
        }
        printf("PUZZLE #%d: Size: %d\n",i+1,puzzles[i].subGridSize);
        printGrid(puzzles[i].sudokuGrid,puzzles[i].subGridSize);
    }

}
int** initializeGrid(int subGridSize){
    int absoluteSize = subGridSize * subGridSize;
    int **grid = malloc(sizeof *grid * absoluteSize);
    for(int i = 0; i < absoluteSize; i++){
        grid[i] = malloc(sizeof *grid[i] * absoluteSize);
    }
    return grid;
}

void fillGridWithDummyData(int** sudokuGrid, int subGridSize){
    int absoluteSize = subGridSize * subGridSize;
    for(int i = 0; i < absoluteSize; i++){
        for(int j = 0; j < absoluteSize;j++){
            sudokuGrid[i][j] = 1;
        }
    }
}

void printGrid(int** sudokuGrid, int subGridSize){
    int absoluteSize = subGridSize * subGridSize;
    int separatorCounter = 0;
    for(int i = 0; i < absoluteSize; i++){
        for(int j = 0; j < absoluteSize; j++){
            printf("%d ",sudokuGrid[i][j]);
            if( (j+1) % subGridSize == 0 && j+1 != absoluteSize) {
                printf("| ");
                separatorCounter++;
            }
        }

        printf("\n");
        if((i+1) % subGridSize == 0 && (i+1) != absoluteSize){
            for(int h = 0; h < absoluteSize+separatorCounter; h++){
                printf("- ");
            }
            printf("\n");
        }
        separatorCounter = 0;

    }
}
