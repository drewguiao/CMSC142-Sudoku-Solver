#include<stdio.h>
#include<stdlib.h>
#define TEMP_SIZE 5

int** initializeGrid(int gridSize);
void fillGridWithDummyData(int** sudokuGrid, int gridSize);
void printGrid(int** sudokuGrid,int gridSize);

int main(){

    int** sudokuGrid = initializeGrid(TEMP_SIZE);
    fillGridWithDummyData(sudokuGrid,TEMP_SIZE);
	printGrid(sudokuGrid,TEMP_SIZE);

}
int** initializeGrid(int gridSize){
    int absoluteSize = gridSize * gridSize;
    int **grid = malloc(sizeof *grid * absoluteSize);
    for(int i = 0; i < absoluteSize; i++){
        grid[i] = malloc(sizeof *grid[i] * absoluteSize);
    }
    return grid;
}

void fillGridWithDummyData(int** sudokuGrid, int gridSize){
    int absoluteSize = gridSize * gridSize;
    for(int i = 0; i < absoluteSize; i++){
        for(int j = 0; j < absoluteSize;j++){
            sudokuGrid[i][j] = 1;
        }
    }
}

void printGrid(int** sudokuGrid, int gridSize){
    int absoluteSize = gridSize * gridSize;
    int separatorCounter = 0;
    for(int i = 0; i < absoluteSize; i++){
        for(int j = 0; j < absoluteSize; j++){
            printf("%d ",sudokuGrid[i][j]);
            if( (j+1) % gridSize == 0 && j+1 != absoluteSize) {
                printf("| ");
                separatorCounter++;
            }
        }

        printf("\n");
        if((i+1) % gridSize == 0 && (i+1) != absoluteSize){
            for(int h = 0; h < absoluteSize+separatorCounter; h++){
                printf("- ");
            }
            printf("\n");
        }
        separatorCounter = 0;

    }
}
