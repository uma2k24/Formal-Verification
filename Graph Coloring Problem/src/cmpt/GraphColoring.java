/*
    Name: Utsav Anantbhat
    Student ID: 301446421
    Date: 10/1/2022

    CMPT 477 Programming Assignment 1
 */

// Import packages/libraries
package cmpt;

import com.microsoft.z3.*;

import java.io.*;
import java.util.*;

public class GraphColoring {
    public static void main(String[] args) throws Exception {

        // Create a new context
        Context ctx = new Context();

        // Input file
        Scanner input = new Scanner(new File("src/cmpt/input.txt"));

        // Create the output file (will be created once written to)
        PrintWriter outputFile = new PrintWriter("src/cmpt/output.txt");

        // Create boolean variables
        int n = input.nextInt(); // Number of vertices taken from the first int of input.txt
        int m = input.nextInt(); // Number of colors taken from the second int of input.txt

        //Initialize variables
        int[] v = new int[100]; // Array of v_i,k vertices
        int[] w = new int[100]; // Array of w_i,k vertices
        int[] edges = new int[100]; // 2D array of edges {v_i,k , w_i,k}
        int v_count = 0; // Separate counters for incrementing v, w, and edges
        int w_count = 0;
        int edge_count = 0;

        while(input.hasNext()){ // While loop to check if there is content in the input file
            v[v_count] = input.nextInt(); // Append v_i,k vertices to array v
            w[w_count] = input.nextInt(); // Append w_i,k vertices to array w
            edges[edge_count] = v[v_count]; // Append v_i,k vertices to set of edges
            edge_count++;
            edges[edge_count] = w[w_count]; // Append w_i,k vertices to set of edges
            edge_count++;
            v_count++;
            w_count++;
        }

        // Create boolean expression, similar to the one used in ArrayExample.java provided by Professor Wang
        BoolExpr[][] p_vc = new BoolExpr[n][m]; // n: # of vertices, m: # of colors
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < m; ++j) {
                p_vc[i][j] = ctx.mkBoolConst( (i+1) + " " + (j+1)); // Appends the vertex and its assigned color to the 2D array
            }
        }

        // Create a solver
        Solver solver = ctx.mkSolver();

        // Formula to assign at least one color to each node
        for (int i = 0; i < n; ++i) {
            solver.add(ctx.mkOr(p_vc[i]));
        }

        // Formula to assign at most 1 color to a vertex
        for(int i = 0; i < edge_count-1; i+=2){
            BoolExpr form = ctx.mkTrue();
            for (int j = 0; j < m; ++j){
                int a = edges[i];
                int b = edges[i+1];
                form = ctx.mkAnd(ctx.mkOr((ctx.mkNot(p_vc[a-1][j])), ctx.mkNot(p_vc[b-1][j])), form);
            }
            solver.add(form); // Add formula to the solver
        }

        // Formula to assign the constraint that no two connected nodes have the same color
        for(int i = 0; i < n; i++){
            BoolExpr form = ctx.mkTrue();
            for (int j = 0; j+1 < m; j++){
                form = ctx.mkNot(ctx.mkAnd(p_vc[i][j], p_vc[i][j+1]));
            }
            solver.add(form); // Add formula to the solver
        }

        // Check satisfiability
        Status status = solver.check();

        // Get a model
        if(Objects.equals(status.toString(), "SATISFIABLE")) { // If solvable/satisfiable, output the necessary values
            Model model = solver.getModel();

            // Output the pair of vertex and assigned color to a text file
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    if (model.getConstInterp(p_vc[i][j]).isTrue()) { // Check which pairs are valid (return true)
                        outputFile.println((i + 1) + " " + (j + 1)); // Write the pairs to the output file; increment by 1 since we start with 0 and m,k > 0 for v_m, c_k
                    }
                }
            }
            // Close the output file when done writing
            outputFile.close();
        }
        else if(Objects.equals(status.toString(), "UNSATISFIABLE")){ // If unsolvable/unsatisfiable, print out "No Solution" in output.txt
            outputFile.println("No Solution");
            outputFile.close(); // Close the output file when done writing
        }
    }
}