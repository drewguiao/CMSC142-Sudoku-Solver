/*
    CMSC 142 SUDOKU-XY
    2nd Semester AY 2017-2018
    Casion, Claudine
    Guiao, Justine Andrew
    Leano, Dominic

*/

#include <stdio.h>
#include <stdlib.h>
#include "SudokuUtils.h"

#define TRUE 1
#define FALSE 0
#define EMPTY 0

int numberOfSolutions = 0;

int main(){

    int numOfPuzzles;

    FILE *fp = fopen("input.txt","r");

    fscanf(fp,"%d",&numOfPuzzles);
    resetOutputFile();
    ArrayList* puzzles = retrievePuzzlesFromInputFile(numOfPuzzles,fp);
    startSolving(puzzles, numOfPuzzles);

    fclose(fp);
}

void resetOutputFile(){
    remove("output.txt");
}

ArrayList* retrievePuzzlesFromInputFile(int numOfPuzzles, FILE *fp){
    ArrayList* puzzles = malloc(sizeof(puzzles)* numOfPuzzles);
    for(int i = 0; i < numOfPuzzles; i++){
        fscanf(fp,"%d",&puzzles[i].subGridSize);
        puzzles[i].sudokuGrid = initializeGrid(puzzles[i].subGridSize);
        int absoluteSize = puzzles[i].subGridSize * puzzles[i].subGridSize;
        for(int j = 0; j < absoluteSize;j++){
            for(int k = 0; k < absoluteSize; k++){
                fscanf(fp,"%d",&puzzles[i].sudokuGrid[j][k]);
            }
        }
    }
    return puzzles;
}

void startSolving(ArrayList* puzzles, int numOfPuzzles){
    for(int i = 0; i < numOfPuzzles; i++){
        printf("Puzzle #%d\n Sub-grid size: %d\n",(i+1),puzzles[i].subGridSize);
        printGrid(puzzles[i].sudokuGrid,puzzles[i].subGridSize);
        solve(puzzles[i].sudokuGrid,puzzles[i].subGridSize);
        appendNumberOfSolutionsToTextFile(i, numberOfSolutions);
        numberOfSolutions = 0;
    }
}

void appendNumberOfSolutionsToTextFile(int index, int numberOfSolutions){
    FILE *fp = fopen("output.txt","a");
    if(fp == NULL) printf("File not found!");
    else{
        fprintf(fp,"SOLUTIONS FOR PUZZLE #%d: %d\n\n",(index+1),numberOfSolutions);
    }
    fclose(fp);
}

int** initializeGrid(int subGridSize){
    int absoluteSize = subGridSize * subGridSize;
    int **grid = malloc(sizeof *grid * absoluteSize);
    for(int i = 0; i < absoluteSize; i++){
        grid[i] = malloc(sizeof *grid[i] * absoluteSize);
    }
    return grid;
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

int isFull(int** sudokuGrid, int subGridSize){
    int absoluteSize = subGridSize * subGridSize;
    for(int i = 0; i < absoluteSize; i++){
        for(int j = 0; j < absoluteSize; j++){
            if(sudokuGrid[i][j] == EMPTY) return FALSE;
        }
    }
    return TRUE;
}

int findBoundingBox(int index, int sudokuGrid){
    return (index /sudokuGrid) * sudokuGrid;
}

int* initializeEntries(int absoluteSize){
    int* possibleEntries = malloc(sizeof possibleEntries * absoluteSize);
    for(int i = 0 ; i < absoluteSize; i++){
        possibleEntries[i] = 0;
    }
    return possibleEntries;
}

void findEntriesInRow(int* possibleEntries, int absoluteSize, int** sudokuGrid,int xIndex){
    for(int y = 0; y < absoluteSize; y++){
        if(sudokuGrid[xIndex][y] != EMPTY){
            possibleEntries[sudokuGrid[xIndex][y]-1] = 1;
        }
    }
}

void findEntriesInCol(int* possibleEntries, int absoluteSize, int** sudokuGrid,int yIndex){
    for(int x = 0; x < absoluteSize; x++){
        if(sudokuGrid[x][yIndex] != EMPTY){
            possibleEntries[sudokuGrid[x][yIndex]-1] = 1;
        }
    }
}

void findEntriesInSubGrid(int* possibleEntries, int subGridSize, int** sudokuGrid, Cell emptyCell){
    int boxIndexX = findBoundingBox(emptyCell.x,subGridSize);
    int boxIndexY = findBoundingBox(emptyCell.y,subGridSize);

     for(int x = boxIndexX; x < subGridSize+boxIndexX; x++){
        for(int y = boxIndexY; y < subGridSize+boxIndexY; y++){
            if(sudokuGrid[x][y] != EMPTY){
                possibleEntries[sudokuGrid[x][y]-1] = 1;
            }
        }
    }
}

int* getPossibleEntries(int** sudokuGrid, int subGridSize, Cell emptyCell){
    int absoluteSize = subGridSize * subGridSize;

    int* possibleEntries = initializeEntries(absoluteSize);

    findEntriesInRow(possibleEntries,absoluteSize,sudokuGrid,emptyCell.x);
    findEntriesInCol(possibleEntries,absoluteSize,sudokuGrid,emptyCell.y);
    findEntriesInSubGrid(possibleEntries,subGridSize,sudokuGrid,emptyCell);

    //To do: add here, find entries in X
    //findEntriesInX()
    //To do: add here, find entries in Y
    //findEntriesInY()
    //To do: add here, find entries in XY
    //findEntriesInXY()

    for(int x = 0; x < absoluteSize; x++){
        possibleEntries[x] = (possibleEntries[x] == EMPTY) ? (x+1) : 0;
    }

    return possibleEntries;
}

Cell findEmptyCell(int** sudokoGrid, int absoluteSize){
    Cell cell;
    int flag = FALSE;
    for(int i = 0; i < absoluteSize; i++){
        for(int j = 0; j < absoluteSize; j++){
            if(sudokoGrid[i][j] == EMPTY){
                cell.x = i;
                cell.y = j;
                flag = TRUE;
                break;
            }
        }
        if(flag == TRUE) break;
    }
    return cell;
}

void solve(int** sudokuGrid, int subGridSize){

    if(isFull(sudokuGrid,subGridSize) == TRUE){
        printf("Board solved!\n");
        printGrid(sudokuGrid,subGridSize);
        numberOfSolutions++;
        saveToFile(sudokuGrid,subGridSize);

    }else{
        int absoluteSize = subGridSize * subGridSize;
        Cell emptyCell = findEmptyCell(sudokuGrid,absoluteSize);

        int* possibilities = getPossibleEntries(sudokuGrid,subGridSize,emptyCell);
        for(int x = 0; x < absoluteSize; x++){
            if(possibilities[x] != EMPTY){
                sudokuGrid[emptyCell.x][emptyCell.y] = possibilities[x];
                solve(sudokuGrid, subGridSize);
            }
        }

        sudokuGrid[emptyCell.x][emptyCell.y] = EMPTY;
    }
}

void saveToFile(int** sudokuGrid, int subGridSize){
    FILE *f = fopen("output.txt","a");
    if(f == NULL){
        printf("Error reading file");
        exit(1);
    }
    int absoluteSize = subGridSize * subGridSize;
    for(int i = 0; i < absoluteSize;i++){
        for(int j = 0; j < absoluteSize; j++){
            fprintf(f,"%d ",sudokuGrid[i][j]);
        }
        fprintf(f,"\n");
    }
    fprintf(f,"\n");
    fclose(f);
}
