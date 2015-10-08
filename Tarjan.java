package Tarjan;

public class Tarjan {

	// VOID: not visited; END: done
	private final int VOID = -1, END = -2;

	// SCC labels of each node; the key result
	private int[] labels;

	// stack_f and stack_b = explicit stack (doubly linked) for DFS search
	private int[] stack_f;
	private int[] stack_b;

	// stack for strongly connected components
	private int[] SS;

	// lowlinks[v] = low number of v
	private int[] lowlinks;

	// head iterators of two stacks
	private int stack_head, SS_head;

	// two counters: index for lowlinks; label for labels
	private int index, label;

	/*
	 * We now pick an arbitrary node as a starting point and perform a depth
	 * first search (DFS) traversal of our graph. At each node we assign the
	 * current value of index to the node and increment index by one. In this
	 * way we assign incrementing numbers to each node which is weakly connected
	 * to our starting node. We call the number assigned to each node its link
	 * number.
	 */

	public Tarjan(int[] indices, int[] indptr) {

		int N = indptr.length - 1; // number of nodes in graph
		initialization(N);

		// visit each node iteratively
		// Count SCC labels backwards so as not to class with lowlinks values.
		for (int v = 0; v < N; v++) { // cover isolated nodes
			if (lowlinks[v] == VOID) {
				DFS(indices, indptr, v);
			}
		}

		// labels count down from N-1 to zero. Modify them so they count upward
		// from 0
		reverseLabels(N);
	}

	// depth-first search for each node
	private void DFS(int[] indices, int[] indptr, int v) {
		stack_head = v;
		stack_f[v] = END;
		stack_b[v] = END;
		while (stack_head != END) {
			v = stack_head; // new iteration
			if (lowlinks[v] == VOID) { // not visited yet
				lowlinks[v] = index;
				index += 1;

				// add successor nodes
				// stack_f, stack_b and stack_head will be updated
				addAdjacencyNodes(indices, indptr, v);

			} else {
				// DFS-stack pop
				popDFSstack(v);

				boolean root = isRoot(indices, indptr, v);

				if (root) { // found a root node
					// SS, labels, SS_head and label will be updated
					recordSCC(v);
				} else {
					// only SS and SS_head will be updated
					pushBackTrackstack(v);
				}
			}
		}
	}

	private void addAdjacencyNodes(int[] indices, int[] indptr, int v) {
		for (int j = indptr[v]; j < indptr[v + 1]; j++) {
			int w = indices[j]; // index of successor nodes
			if (lowlinks[w] == VOID) {
				// DFS-stack push

				// If w is already inside the stack, excise it.
				// This means we move w to the top of the stack.
				if (stack_f[w] != VOID) {
					exciseExistingNode(w);
				}

				// we then push w into the DFS stack
				pushDFSstack(w);
			}
		}
	}

	// not fully understood
	private void exciseExistingNode(int existingNode) {
		int f = stack_f[existingNode];
		int b = stack_b[existingNode];
		if (b != END) {
			stack_f[b] = f;
		}
		if (f != END) {
			stack_b[f] = b;
		}
	}

	// not fully understood
	private void pushDFSstack(int w) {
		stack_f[w] = stack_head;
		stack_b[stack_head] = w;
		stack_b[w] = END;
		stack_head = w; // next
	}

	private void popDFSstack(int v) {
		stack_head = stack_f[v];
		if (stack_head >= 0) {
			stack_b[stack_head] = END;
		}
		stack_f[v] = VOID;
		stack_b[v] = VOID;
	}

	/*
	 * The clever part of the algorithm comes when we have processed all the
	 * children of a node and are ready to backtrack up the tree to continue the
	 * DFS traversal. When backtracking past a node, we consider all of its
	 * children and find the smallest link number of these child nodes. If this
	 * is smaller than the link number of the current node, we update the link
	 * number of the current node and put the node onto a stack.
	 * 
	 * If none of the children have a smaller link number then we have found a
	 * root node. None of the children of this node point to a node with a
	 * smaller link number than the current one, which means that they can’t
	 * possibly be strongly connected to any nodes “lower” in the graph.
	 */
	private boolean isRoot(int[] indices, int[] indptr, int v) {
		boolean root = true;
		int low_v = lowlinks[v];
		for (int j = indptr[v]; j < indptr[v + 1]; j++) {
			int low_w = lowlinks[indices[j]]; // check each successor node
			if (low_w < low_v) { // root shall have the smallest lowlink value
				low_v = low_w;
				root = false;
			}
		}
		lowlinks[v] = low_v; // a little strange

		return root;
	}

	/*
	 * In this case we assign the value of label to the root node. We also
	 * assign label to all the nodes in the stack with a link number greater
	 * than or equal to the root’s link number, removing them from the stack at
	 * the same time. Once we’ve done all this we decrease label by 1 and
	 * decrease index by the number of nodes we just updated.
	 */
	private void recordSCC(int v) {
		index -= 1;

		// while S not empty and rindex[v] <= rindex[top[S]]
		while (SS_head != END && lowlinks[v] <= lowlinks[SS_head]) {
			int w = SS_head; // not the same w as defined before
			SS_head = SS[w];
			SS[w] = VOID;

			labels[w] = label;
			index -= 1;
		}
		labels[v] = label;
		label -= 1;
	}

	// push v into the SCC stack, which is implemented by a singly linked list
	private void pushBackTrackstack(int v) {
		SS[v] = SS_head;
		SS_head = v;
	}

	private void initialization(int N) {
		labels = new int[N];
		SS = new int[N];

		// the following setting is one key of the algorithm!
		// for the consideration of saving memory
		lowlinks = labels; // share the same memory
		stack_f = SS; // share the same memory

		stack_b = new int[N];

		// The stack of nodes which have been backtracked and are in the
		// current SCC
		fill(SS, VOID);
		SS_head = END;

		// The array containing the lowlinks of nodes not yet assigned an SCC.
		// Shares memory with the labels array, since they are not used at the
		// same time.
		fill(lowlinks, VOID);

		// The DFS stack. Stored with both forwards and backwards pointers to
		// allow us to move a node up to the top of the stack, as we only need
		// to visit each node once. stack_f shares memory with SS, as nodes
		// aren't put on the SS stack until after they've been popped from the
		// DFS stack.
		stack_head = END;
		fill(stack_f, VOID);
		fill(stack_b, VOID);

		index = 0;
		label = N - 1;
	}

	private void reverseLabels(int N) {
		for (int t = 0; t < labels.length; t++) {
			labels[t] *= -1;
			labels[t] += (N - 1);
		}
	}

	private void fill(int[] vec, int value) {
		for (int i = 0; i < vec.length; i++) {
			vec[i] = value;
		}
	}

	public void print() {
		printArray(labels);
	}

	private void printArray(int[] array) {
		for (int i = 0; i < array.length; i++) {
			System.out.print(array[i] + " ");
		}
		System.out.println();
	}

	public int id(int v) {
		return labels[v];
	}

	public boolean stronglyConnected(int v, int w) {
		return labels[v] == labels[w];
	}

	// returns the number of strong components.
	// public int count() {
	// return count;
	// }
}
