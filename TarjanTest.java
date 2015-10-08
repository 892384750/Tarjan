package Tarjan;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.TarjanSCC;

public class TarjanTest {

	public static void main(String[] args) {
		String graphFile = "/Users/shuangwei/Documents/Java/data/tinyDG.txt";
		
		// 1. by princeton algorithm
		// In in = new In(args[0]);
		In in = new In(graphFile);
		Digraph G = new Digraph(in);
		StdOut.println(G);

		TarjanSCC scc = new TarjanSCC(G);

		// number of connected components
		int M = scc.count();
		StdOut.println(M + " components");

		// compute list of vertices in each strong component
		Queue<Integer>[] components = (Queue<Integer>[]) new Queue[M];
		for (int i = 0; i < M; i++) {
			components[i] = new Queue<Integer>();
		}
		for (int v = 0; v < G.V(); v++) {
			components[scc.id(v)].enqueue(v);
		}

		// print results
		for (int i = 0; i < M; i++) {
			for (int v : components[i]) {
				StdOut.print(v + " ");
			}
			StdOut.println();
		}

		// ************************
		// 2. by myself
		SparseMatrix matrix = new SparseMatrix(G);
		int[] indices = (matrix.col_ind);
		int[] indptr = matrix.row_ptr;
		Tarjan tarjan = new Tarjan(indices, indptr);
		tarjan.print();
		
	}

}
