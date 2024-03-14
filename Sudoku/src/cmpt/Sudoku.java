/*
    Name: Utsav Anantbhat
    Student ID: 301446421
    Date: 10/28/2022

    CMPT 477 Programming Assignment 2
 */

// Import packages/libraries
package cmpt;

import com.microsoft.z3.*;

import java.io.*;
import java.util.*;

public class Sudoku {

    // Declare private variables rows and columns since we know it is a fixed size of 9
    private static final int rows_ = 9;
    private static final int columns_ = 9;

    public static void main(String[] args) throws Exception {

        // Create a new context
        Context ctx = new Context();

        // Input file
        Scanner input = new Scanner(new File("src/cmpt/input.txt"));

        // Create the output file (will be created once written to)
        PrintWriter outputFile = new PrintWriter("src/cmpt/output.txt");

        // Create a solver
        Solver solver = ctx.mkSolver();

        // Initialize the 9x9 sudoku grid
        int[][] sudokugrid = new int[rows_][columns_];

        // Read input from the file
        while(input.hasNext()){ // While loop to check if there is still more content to read in the file; "As long as there is content in the file..."
            for(int i = 0; i < sudokugrid.length; i++) { // Read characters in the line for as long as the line is in characters
                String[] line = input.nextLine().trim().split(" "); // Ignore the spaces
                for (int j = 0; j < line.length; j++) { // Double for loop for 2D array
                    sudokugrid[i][j] = Integer.parseInt(line[j]); // Pass the int into the sudoku grid in its respective [i][j] position
                }
            }

        // Create 9x9 int matrix for the sudoku grid
        IntExpr[][] grid = new IntExpr[rows_][columns_]; // 9x9 grid
        for (int i = 0; i < rows_; i++)
        {
            for (int j = 0; j < columns_; j++)
                grid[i][j] = (IntExpr) ctx.mkConst(ctx.mkSymbol("" + (i+1) + "" + (j+1)), ctx.getIntSort()); // Create the grid
        }

        // Constraint that cell has a value between 1 and 9 (0 represents empty space)
        BoolExpr[][] cells = new BoolExpr[rows_][columns_];
        for (int i = 0; i < rows_; i++)
        {
            for (int j = 0; j < columns_; j++){
                cells[i][j] = ctx.mkAnd(ctx.mkGe(grid[i][j], ctx.mkInt(1)), ctx.mkGe((ctx.mkInt(9)), grid[i][j])); // Formula for the constraint
                solver.add(cells[i][j]); // Add the cells 2D array to the solver
            }
        }

        // Constraint that each row can have a number at most once
        BoolExpr[] rows = new BoolExpr[rows_];
        for(int i = 0; i < rows_; i++){
            rows[i] = ctx.mkDistinct(grid[i]); // Make each number in the row distinct
            solver.add(rows[i]); // Add the rows array to solver
        }

        // Constraint that each column can have a number at most once
        BoolExpr[] cols = new BoolExpr[columns_];
        for(int j = 0; j < columns_; j++)
        {
            IntExpr[] column = new IntExpr[columns_];
            for(int i = 0; i < columns_; i++)
                column[i] = grid[i][j];
            cols[j] = ctx.mkDistinct(column); // Make each number in column distinct
            solver.add(cols[j]); // Add columns array to solver
        }

        // Each 3x3 block has a number at most once

        // Initialize boolean expression for 3x3 block
        BoolExpr[][] block_3 = new BoolExpr[3][3];
        // Block increments (go from top left 3x3 block, top center, top right, middle left, ...)
        int block_increment_i = 3;
        for(int a = 0; a < 3; a++) // Double for loop for rows and columns
        {
            int block_increment_a = 3*a; // Block increments for rows
            for(int b = 0; b < 3; b++)
            {
                int block_increment_b = 3*b; // Block increments for columns
                IntExpr[] block = new IntExpr[9]; // IntExpr for the numbers
                for(int i = 0; i < 3; i++)
                    for(int j = 0; j < 3; j++)
                        block[block_increment_i*i + j] = grid[block_increment_a + i][block_increment_b + j]; // Go to the next section of 3x3 blocks
                block_3[a][b] = ctx.mkDistinct(block); // Unique numbers from 1-9 in each cell of the block
                solver.add(block_3[a][b]); // Add the block to the solver
            }
        }

        // Initialize puzzle instance
        BoolExpr sudoku_instance;
        for(int i = 0; i < rows_; i++)
            for(int j = 0; j < columns_; j++)
                if(sudokugrid[i][j] != 0){ // Check for non-empty spaces
                    sudoku_instance = ctx.mkEq(grid[i][j], ctx.mkInt(sudokugrid[i][j])); // Assign the numbers to the main sudoku grid (int(sudokugrid[i][j] = grid[i][j])
                    solver.add(sudoku_instance); // Add puzzle instance to the solver
                }

        // Satisfiability checker
        if(solver.check() == Status.SATISFIABLE)
        {
            Model model = solver.getModel(); // Acquire the model
            IntExpr[][] model_expression = new IntExpr[rows_][columns_]; // Create a model expression
            for(int i = 0; i < rows_; i++){
                for(int j = 0; j < columns_; j++)
                    model_expression[i][j] = (IntExpr) model.evaluate(grid[i][j], true); // Evaluate the model
            }
            for(int i = 0; i < rows_; i++){
                for(int j = 0; j < columns_; j++){
                    outputFile.print(" " + model_expression[i][j]); // Print solution to the output file (space for spacing in between the numbers)
                }
                outputFile.println(); // Print in the 9x9 grid format (after the 9th int, go to the next line)
                //System.out.println("Done!"); // Output for confirmation (prints out "Done!" 9 times if everything is in order)
            }
            outputFile.close(); // Close the output file after the writing is done
        }
        else // Condition if not satisfiable
        {
            outputFile.println("No Solution");
            outputFile.close(); // Close output file
        }
     }
   }
}