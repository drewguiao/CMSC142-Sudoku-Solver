/*
    CMSC 142 SUDOKU-XY
    2nd Semester AY 2017-2018
    Casion, Claudine
    Guiao, Justine Andrew
    Leano, Dominic

*/

#include<stdio.h>
#include<stdlib.h>

#define TRUE 1
#define FALSE 0
#define EMPTY 0

typedef struct{
    int **sudokuGrid;
    int subGridSize;
}ArrayList;

int** initializeGrid(int subGridSize);
void resetOutputFile();
ArrayList* retrievePuzzlesFromInputFile(int numberOfPuzzles, FILE *fp);
void startSolving(ArrayList* puzzles, int numOfPuzzles);
void printGrid(int** sudokuGrid,int subGridSize);
void appendNumberOfSolutionsToTextFile();
int isFull(int** sudokuGrid, int subGridSize);
int* getPossibleEntries(int** sudokuGrid, int subGridSize, int i, int j);
void solve(int** sudokuGrid, int subGridSize);
void saveToFile(int** sudokuGrid,int subGridSize);


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

int* getPossibleEntries(int** sudokuGrid, int subGridSize, int i, int j){
    int absoluteSize = subGridSize * subGridSize;
    int* possibleEntries = malloc(sizeof possibleEntries * absoluteSize);

    //initialize possible entries to zero
    for(int x = 0; x < absoluteSize; x++) possibleEntries[x] = 0;

    //for horizontal checking
    for(int y = 0; y < absoluteSize; y++){
        if(sudokuGrid[i][y] != EMPTY){
            possibleEntries[sudokuGrid[i][y]-1] = 1;
        }
    }

    //for vertical checking
    for(int x = 0; x < absoluteSize; x++){
        if(sudokuGrid[x][j] != EMPTY){
            possibleEntries[sudokuGrid[x][j]-1] = 1;
        }
    }

    //find bounding box
    int boxIndexX = findBoundingBox(i,subGridSize);
    int boxIndexY = findBoundingBox(j,subGridSize);

    //find entries in box
    for(int x = boxIndexX; x < subGridSize+boxIndexX; x++){
        for(int y = boxIndexY; y < subGridSize+boxIndexY; y++){
            if(sudokuGrid[x][y] != EMPTY){
                possibleEntries[sudokuGrid[x][y]-1] = 1;
            }
        }
    }

    for(int x = 0; x < absoluteSize; x++){
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

    }else{

        int absoluteSize = subGridSize * subGridSize;
        int i = 0;
        int j = 0;

        int flag = FALSE;
        for(int x = 0; x < absoluteSize; x++){
            for(int y = 0; y < absoluteSize; y++){
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
        for(int x = 0; x < absoluteSize; x++){
            if(possibilities[x] != EMPTY){
                sudokuGrid[i][j] = possibilities[x];
                solve(sudokuGrid, subGridSize);
            }
        }

        sudokuGrid[i][j] = EMPTY;
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
