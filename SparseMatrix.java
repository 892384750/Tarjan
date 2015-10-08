package Tarjan;

import java.util.Iterator;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;

public class SparseMatrix {

	public int[] col_ind;
	public int[] row_ptr;

	public SparseMatrix(Digraph graph) {
		col_ind = new int[graph.E()];
		row_ptr = new int[graph.V() + 1];
		int iter = 0, v;
		Iterable<Integer> adj;
		for (v = 0; v < graph.V(); v++) {

			row_ptr[v] = iter;
//			if (v != 0 && row_ptr[v] == row_ptr[v-1]) {
//				row_ptr[v-1] = 0; //???
//			}
			
			adj = graph.adj(v);
			for (int w : adj) {
				col_ind[iter] = w;
				iter++;
			}
		}
		row_ptr[v] = graph.E(); // just a convention

	}

	

	public void print(int[] vec) {
		for (int i = 0; i < vec.length; i++) {
			System.out.print(vec[i] + " ");
		}
		System.out.println();
	}

	public static void main(String[] args) {

		String graphFile = "/Users/shuangwei/Documents/Java/data/tinyDG.txt";
		// In in = new In(args[0]);
		In in = new In(graphFile);
		Digraph G = new Digraph(in);

		SparseMatrix matrix = new SparseMatrix(G);
		matrix.print(matrix.col_ind);
		matrix.print(matrix.row_ptr);
	}

}
