#include<stdio.h>
#include<stdlib.h>

int** initializeGrid(int gridSize);
void fillGridWithDummyData(int** sudokuGrid, int gridSize);
void printGrid(int** sudokuGrid,int gridSize);

int main(){
    int** sudokuGrid = initializeGrid(5);
    fillGridWithDummyData(sudokuGrid,5);
	printGrid(sudokuGrid,5);
}
int** initializeGrid(int gridSize){
    int **grid = malloc(sizeof *grid * gridSize);
    for(int i = 0; i < gridSize; i++){
        grid[i] = malloc(sizeof *grid[i] * gridSize);
    }
    return grid;
}

void fillGridWithDummyData(int** sudokuGrid, int gridSize){
    for(int i = 0; i < gridSize; i++){
        for(int j = 0; j < gridSize;j++){
            sudokuGrid[i][j] = 1;
        }
    }
}

void printGrid(int** sudokuGrid, int gridSize){
    for(int i = 0; i < gridSize; i++){
        for(int j = 0; j < gridSize; j++){
            printf("%d ",sudokuGrid[i][j]);
        }
        printf("\n");
    }
}
