//For File
import java.io.*;
//For Scanner, Priority Queue and HashSet
import java.util.*;

public class Main {

    //As IDDFS recursively calls itself to find a solution, its solution path is initialized outside and built upon when a solution is found within the function
    String IDDFSpath = "";
    public Main(){
        try{
            //Look for Puzzles.txt, as a user you must supply the text document with the puzzles; I have supplied examples
            File file = new File("Puzzles");
            Scanner sc = new Scanner(file);

            //Keep track of which puzzle the program is on
            int puzzleNum = 1;

            //While there are more puzzles to solve
            while(sc.hasNext()){
                //Initialize a representation of the puzzle as a 2d integer array
                int[][] puzzle = getPuzzle(sc);

                System.out.println("Puzzle " + puzzleNum + "\n\n");

                //All implementations

                //IDDFS
                //Only works with quite basic puzzles, so I would recommend commenting out for the complex examples
                if(puzzleNum == 1) {
                    IDDFS(puzzle);
                }

                //A* Solutions, each using a different heuristic, they should be used for complex examples and should not be commented out
                //A* with Manhattan Distance Heuristic
                AStar(puzzle, 1);
                //A* with Numbers-out-of-Place Heuristic
                AStar(puzzle, 2);
                //A* with Custom Heuristic (Linear Conflict)
                AStar(puzzle, 3);

                //Iterate puzzleNum
                puzzleNum++;
            }
        }
        //Catch error of File not being found
        catch (FileNotFoundException e){
            System.out.println("File was not found");
        }
    }
    //Take String input from file and parse the puzzle into an int[][]
    public int[][] getPuzzle(Scanner sc){
        //Get the size of the puzzle, then create an array for the grid of the puzzle
        int size = Integer.parseInt(sc.nextLine());
        int[][] grid = new int[size][size];

        //For the specified size of the puzzle
        for(int i = 0; i<size; i++){
            String row = sc.nextLine();
            for(int j = 0; j<size; j++){
                //Split along the spaces to get each column in the row
                String[] columns = row.split(" ");
                //Convert any blank spaces into 0
                if(columns[j].equals("X")){
                    grid[i][j] = 0;
                }
                else {
                    grid[i][j] = Integer.parseInt(columns[j]);
                }
            }
        }
        return grid;
    }



    //Iterative Deepening Depth First Search
    public void IDDFS(int[][] grid){
        //Start the timer
        long startTime = System.nanoTime();
        //The max depth before IDDFS gives up, contingency if the puzzle has no solution
        //20 is chosen because beyond it, the algorithm becomes unreasonably long (though the longest puzzle solution is 30, beyond this scope)
        int maxDepth = 21;
        //Reset IDDFS path for each new puzzle
        IDDFSpath = "";

        //Initialize the starting node, heuristic value doesn't matter and there are no children or parent nodes yet
        Node n = new Node(0, 0, grid, null);

        //Repeat until maxDepth is reached
        for(int depth = 0; depth < maxDepth; depth++){
            //Call recursive function and if a solution is found, print it
            if(DFS(n, depth)){
                System.out.println("IDDFS:");
                System.out.println("Solution found at depth: " + depth);
                long endTime = System.nanoTime();
                System.out.println("IDDFS took: " + (endTime-startTime)/1000000 + " milliseconds\n");
                //Print initial puzzle
                printInitial(grid);
                System.out.println("Path: " + getPath(IDDFSpath));
                printGoal(n.state.length);
                System.out.println();
                return;
            }
        }
        //If after reaching maxDepth, nothing was found, return statement
        System.out.println("Solution was not found with a max depth of " + maxDepth);
    }
    //Recursively called Depth First Search
    public boolean DFS(Node n, int depth){
        //Check if goal state has been reached
        if(goalCheck(n.state)){
            return true;
        }
        //Check if at maxDepth
        if (depth == 0){
            return false;
        }
        //Check to make sure swap in a direction is valid ot create children
        //Each i is a different direction following the order 0 = left, 1 = up, 2 = right, 3 = down
        //Check through every direction
        for (int i = 0; i < 4; i++){
            if (checkSwap(n, i)){
                int[][] newGrid = copyGrid(n.state);
                newGrid = swap(newGrid, i);
                Node child = new Node(0, 0, newGrid, n.path + i);
                //Recursively check children to see if the path was the correct to a solution, while decreasing the depth to get closer to 0
                if (DFS(child, depth-1)){
                    //Append the path to include this direction, because it is a recursive function, put the new move at the front
                    IDDFSpath = i + IDDFSpath;
                    return true;
                }
            }
        }
        //If we never find a solution, the initial call will return back false
        return false;
    }



    //AStar Search, parameter to define which heuristic should be calculated
    public void AStar(int[][] grid, int heuristicVal){

        //Priority queue that uses the heuristic value to compare nodes
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparing(node -> node.heur+node.cost));

        //HashSet to store a list of every state already visited (cut down on branches that can be easily pruned)
        Set<Long> visited = new HashSet<>();
        //Start the timer
        long startTime = System.nanoTime();

        //The max depth before A* gives up, contingency if the puzzle has no solution
        //Number is mathematically arbitrary but encases all example questions
        int maxDepth = 32;
        int depth = 0;

        //Initialize the starting node, heuristic value is calculating with Manhattan distance and no cost function because it is the initial node, there are no children or parent nodes yet
        Node n = new Node(calcHeur(grid, heuristicVal), depth, grid, null);
        //Initially add the starting node to the priority queue and visited states
        pq.add(n);
        visited.add(toBitMap(n.state));

        //While there are states to check within the given maxDepth, keep running
        while(!pq.isEmpty()){
            //Pull the top node from the priority queue
            Node currentNode = pq.poll();

            //Check if that Node's state is the goal state, if so, print solution
            if (goalCheck(currentNode.state)){
                //switch case to print the right heuristic name
                switch(heuristicVal){
                    case 1:
                        System.out.println("A*MANHATTAN:");
                        break;
                    case 2:
                        System.out.println("A*NUMBERS-OUT-OF-PLACE:");
                        break;
                    case 3:
                        System.out.println("A*CUSTOM:");
                        break;
                }
                System.out.println("Solution found at depth: " + currentNode.cost);
                long endTime = System.nanoTime();
                System.out.println("Search took: " + (endTime-startTime)/1000000 + " milliseconds\n");
                //Print initial puzzle
                printInitial(grid);
                System.out.println("Path: " + getPath(currentNode.path));
                printGoal(currentNode.state.length);
                System.out.println();
                return;
            }

            //Check if the "cost" which represents depth is not larger than the maxDepth
            if(!(currentNode.cost > maxDepth)){
                //Check to make sure swap in a direction is valid to create children
                //Each i is a different direction following the order 0 = left, 1 = up, 2 = right, 3 = down
                for (int i = 0; i < 4; i++){
                    if (checkSwap(currentNode, i)) {
                        //If the direction is swappable, create a newGrid that copies all values from the current and swap the two values
                        int[][] newGrid = copyGrid(currentNode.state);
                        newGrid = swap(newGrid, i);

                        //If we have not visited this state before
                        if(!(visited.contains(toBitMap(newGrid)))) {
                            //Add the new child node to the priority queue and HashSet with calculated heuristic value
                            Node child = new Node(calcHeur(newGrid, heuristicVal), currentNode.cost + 1, newGrid, currentNode.path + i);
                            pq.add(child);
                            visited.add(toBitMap(child.state));
                        }
                    }
                }
            }
        }
        //If unable to find a solution within the given maxDepth
        switch(heuristicVal){
            case 1:
                System.out.println("A*MANHATTAN:");
                break;
            case 2:
                System.out.println("A*NUMBERS-OUT-OF-PLACE:");
                break;
            case 3:
                System.out.println("A*CUSTOM:");
                break;
        }
        System.out.println("Was unable to find a solution with the given maxDepth of " + maxDepth);
        long endTime = System.nanoTime();
        System.out.println("Search took: " + (endTime-startTime)/1000000 + " milliseconds");
        System.out.println();
    }
    //Print the initial puzzle before any moves
    public void printInitial(int[][] grid){
        System.out.println("Initial State");
        //For each value in the puzzle
        for (int i = 0; i < grid.length; i++){
            for (int j = 0; j < grid.length; j++){
                if(grid[i][j] == 0){
                    //If the value is the blank, replace it with X for better understanding
                    System.out.print("[X] ");
                }
                else {
                    //If not a blank space, print the value of the array stylized with "[n] "
                    System.out.print("[" + grid[i][j] + "] ");
                }
            }
            System.out.println();
        }
    }
    //Print the goal state, finished state if a solution was found
    public void printGoal(int length){
        //Count is the value printed in each box in the puzzle, it iterates by one with each box until the last which is empty
        int count = 1;
        System.out.println("Finished State");
        for (int i = 0; i < length; i++){
            for (int j = 0; j < length; j++){
                if (i == length - 1 && j == length - 1){
                    //Check if the last space is blank
                    System.out.print("[X] ");
                }
                else{
                    System.out.print("[" + count + "] ");
                }
                count++;
            }
            System.out.println();
        }
    }
    //Calculate the heuristic value of a given state
    public int calcHeur(int[][] grid, int heuristicVal){
        //Keep commonly used variables out of switch
        int h;
        int i;
        int j;
        switch(heuristicVal){
            //Manhattan Distance
            case 1:
                //Initialize heuristic value at 0
                h = 0;
                //For every value in the puzzle
                for (i = 0; i < grid.length; i++) {
                    for (j = 0; j < grid.length; j++) {
                        //
                        int value = grid[i][j];
                        //Skip the empty value as skipping it makes the heuristic admissible
                        if (value != 0){
                            //Get the goal position of every value on the given state
                            int[] goalPosi = findGoalPosition(value, grid);
                            //Use the Manhattan Distance function to add the distance from the correct spot each value is
                            h = h + Math.abs(i - goalPosi[0]) + Math.abs(j - goalPosi[1]);
                        }
                    }
                }
                return h;
            //Numbers-out-of-Place
            case 2:
                h = 0;
                for (i = 0; i < grid.length; i++){
                    for (j = 0; j < grid.length; j++) {
                        //Check if the number at any given slot is out of place (similar to how goalCheck() is done)
                        //Order is the correct number that should be in the spot
                        int order = 1;
                        for (i = 0; i < grid.length; i++) {
                            for (j = 0; j < grid.length; j++) {
                                //Check exclusively if we are checking the last value in the array
                                if (i == grid.length - 1 && j == grid.length - 1) {
                                    //If the empty space is not at the bottom right
                                    if (grid[i][j] != 0) {
                                        h = h + 1;
                                    }
                                }
                                //If not at the end of the array, check if the value is in the given array is in the right spot
                                else if (grid[i][j] != order) {
                                    h = h + 1;
                                }
                                //Increment order
                                order++;
                            }
                        }
                    }
                }
                return h;
                //Custom Heuristic (Linear Conflict)
            case 3:
                //Start with the Manhattan Distance Heuristic
                //Initialize heuristic value at 0
                h = 0;
                //For every value in the puzzle
                for (i = 0; i < grid.length; i++) {
                    for (j = 0; j < grid.length; j++) {
                        //
                        int value = grid[i][j];
                        //Skip the empty value as skipping it makes the heuristic admissible
                        if (value != 0){
                            //Get the goal position of every value on the given state
                            int[] goalPosi = findGoalPosition(value, grid);
                            //Use the Manhattan Distance function to add the distance from the correct spot each value is
                            h = h + Math.abs(i - goalPosi[0]) + Math.abs(j - goalPosi[1]);

                            //Check for column linear conflict
                            //If the row is correct, check if the sibling nodes in the column are in linear conflict
                            if(goalPosi[0] == i){
                                //Check every value past the initial value for Linear Conflicts
                                for(int k = j + 1; k < grid.length; k++){
                                    int checkVal = grid[i][k];
                                    //While the other value is not an empty value
                                    if (checkVal != 0){
                                        int[] otherGoalPos = findGoalPosition(checkVal, grid);
                                        //If a Linear Conflict exists, both goals are where eachother are
                                        if (i == otherGoalPos[0] && goalPosi[1] > otherGoalPos[1]){
                                            //Add the 2 necessary moves to the heuristic value to more realistically show the moves away
                                            h = h + 2;
                                        }
                                    }
                                }
                            }
                            //Check for row linear conflict
                            //If the column is correct, check if the sibling nodes in the row are in linear conflict
                            if(goalPosi[1] == j){
                                //Check every value past the initial value for Linear Conflicts
                                for(int k = i + 1; k < grid.length; k++){
                                    int checkVal = grid[k][j];
                                    //While the other value is not an empty value
                                    if (checkVal != 0){
                                        int[] otherGoalPos = findGoalPosition(checkVal, grid);
                                        //If a Linear Conflict exists, both goals are where eachother are
                                        if (j == otherGoalPos[1] && goalPosi[0] > otherGoalPos[0]){
                                            //Add the 2 necessary moves to the heuristic value to more realistically show the moves away
                                            h = h + 2;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return h;
        }
        return 0;
    }
    //Finds the row and column of a requested value within the goal state, to judge how far off the given state's value is
    public int[] findGoalPosition(int value, int[][] grid){
        int counter =  1;
        //Initialize a grid with the proportions of the grid being judged
        int[][] goalGrid = new int[grid.length][grid.length];
        for (int i = 0; i < grid.length; i++){
            for (int j = 0; j < grid.length; j++){
                //If the last node is being considered, then the spot should be blank, not 9 or 16
                if (i == grid.length-1 && j == grid.length-1){
                    goalGrid[i][j] = 0;
                }
                else{
                    goalGrid[i][j] = counter;
                }
                if(goalGrid[i][j] == value){
                    //Return an array with the row and column that the value is found in the goal state
                    return new int[]{i, j};
                }
                counter++;
            }
        }
        return null;
    }
    //Check if the given grid is the goal state
    public boolean goalCheck(int[][] grid){
        int order = 1;
        //Check through every node, if one is wrong, return false, if it makes it through every node, return true
        for(int i = 0; i< grid.length; i++){
            for(int j = 0; j< grid.length; j++){
                if(i == grid.length-1 && j == grid.length-1){
                    //Check if the last space is blank
                    if(grid[i][j] != 0){
                        return false;
                    }
                }
                //Check if the value is where it should be, given the goal's value goes from 1 to goal.length-1 with a blank space at the end
                else if(grid[i][j] != order){
                    return false;
                }
                order++;
            }
        }
        return true;
    }
    //Copies all attributes of a grid to a new one, as to not write over a previously defined grid
    public int[][] copyGrid(int[][] origGrid){
        //Make new array with the same proportions
        int[][] copy = new int[origGrid.length][origGrid.length];
        //For every value, copy to new grid and return the copy
        for(int i = 0; i < origGrid.length; i++){
            for(int j = 0; j < origGrid.length; j++){
                copy[i][j] = origGrid[i][j];
            }
        }
        return copy;
    }
    //Check if swapping the blank value in a direction leads to a valid state (not out of bounds)
    public boolean checkSwap(Node n, int direction){
        //Initialize values out of range so if there is no blank spot, nothing can be moved
        int blankRow = -2;
        int blankCol = -2;

        //Linearly search for the blank space to assess validity
        for(int i = 0; i < n.state.length; i++){
            for(int j = 0; j < n.state.length; j++){
                //When we've foung the blank space, save the coordinates
                if (n.state[i][j] == 0){
                    blankRow = i;
                    blankCol = j;
                }
            }
        }
        //Check to make sure the spot to be swapped to is valid in the array
        switch (direction){
            //Swapped left
            case 0:
                if (blankCol - 1 >= 0){
                    return true;
                }
                return false;
            //Swapped up
            case 1:
                if (blankRow - 1 >= 0){
                    return true;
                }
                return false;
            //Swapped right
            case 2:
                if (blankCol + 1 < n.state.length){
                    return true;
                }
                return false;
            //Swapped down
            case 3:
                if (blankRow + 1 < n.state.length){
                    return true;
                }
                return false;
        }
        return false;
    }
    //Swap the blank value with another value in a given direction (has already been checked that this swap is valid)
    public int[][] swap(int[][] grid, int direction){
        //Initialize values out of range so if there is no blank spot, nothing can be moved
        int blankRow = -2;
        int blankCol = -2;
        int swapVal;

        //Create a copy of the parent puzzle
        int[][] swapGrid = grid;

        //Linearly search for the blank space to assess validity
        for(int i = 0; i < swapGrid.length; i++){
            for(int j = 0; j < swapGrid.length; j++){
                //When we've foung the blank space, save the coordinates
                if (swapGrid[i][j] == 0){
                    blankRow = i;
                    blankCol = j;
                }
            }
        }
        switch (direction){
            //Swapped left
            case 0:
                swapVal = swapGrid[blankRow][blankCol-1];
                swapGrid[blankRow][blankCol-1] = 0;
                swapGrid[blankRow][blankCol] = swapVal;
                return swapGrid;
            //Swapped up
            case 1:
                swapVal = swapGrid[blankRow-1][blankCol];
                swapGrid[blankRow-1][blankCol] = 0;
                swapGrid[blankRow][blankCol] = swapVal;
                return swapGrid;
            //Swapped right
            case 2:
                swapVal = swapGrid[blankRow][blankCol+1];
                swapGrid[blankRow][blankCol+1] = 0;
                swapGrid[blankRow][blankCol] = swapVal;
                return swapGrid;
            //Swapped down
            case 3:
                swapVal = swapGrid[blankRow+1][blankCol];
                swapGrid[blankRow+1][blankCol] = 0;
                swapGrid[blankRow][blankCol] = swapVal;
                return swapGrid;
        }
        return swapGrid;
    }
    //Take a grid state and turn it into long to be put into the HashSet of rvisited states
    public long toBitMap(int[][] grid){
        //Take the state and put each number one after the other into a long
        long bitMap = 0;
        for (int i = 0; i < grid.length; i++){
            for (int j = 0; j < grid.length; j++){
                //Multiply by 10 to create a new 10 bit slot to put the next number
                bitMap = bitMap*10;
                bitMap = bitMap + grid[i][j];
            }
        }
        //The returned bitMap should be in-order an long representation of the grid
        return bitMap;
    }
    //Convert the String of directions swapped in into a path listing with chars
    public String getPath(String pathList){
        //Initialize an empty path
        String path = "";
        //While there are moves still made, go through one char at a time
        while(!(pathList.isEmpty())){
            switch (pathList.charAt(0)){
                case '0':
                    path = path + "L ";
                    break;
                case '1':
                    path = path + "U ";
                    break;
                case '2':
                    path = path + "R ";
                    break;
                case '3':
                    path = path + "D ";
                    break;
            }
            //Take out the char converted from the string
            pathList = pathList.substring(1);
        }
        return path;
    }

    public static void main(String[] args) {new Main();}
}