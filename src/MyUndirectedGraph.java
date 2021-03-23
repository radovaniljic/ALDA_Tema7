import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;


public class MyUndirectedGraph<T> implements UndirectedGraph<T> {

	private static class Node<T> {
		T data;
		ArrayList<T> connectedNodes = new ArrayList<>();;
		boolean visited = false;
		T previous = null;

		public Node(T data) {
			this.data = data;
		}
	}

	private static class Edge<T> implements Comparable<Edge<T>> {
		Node<T> node1;
		Node<T> node2;
		int cost;

		public Edge(Node<T> node1, Node<T> node2, int cost) {
			this.node1 = node1;
			this.node2 = node2;
			this.cost = cost;
		}

		@Override
		public int compareTo(Edge<T> other) {
			if (other.cost > cost) {
				return -1;
			} else if (other.cost < cost) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	private HashMap<T, Node<T>> nodeMap = new HashMap<>();
	private ArrayList<Edge<T>> edgeList = new ArrayList<>();

	@Override
	public int getNumberOfNodes() {
		return nodeMap.size();
	}

	@Override
	public int getNumberOfEdges() {
		return edgeList.size();
	}

	@Override
	public boolean add(T newNode) {
		if (nodeMap.containsKey(newNode)) {
			return false;
		}
		Node<T> node = new Node<>(newNode);
		nodeMap.put(newNode, node);
		return true;
	}

	@Override
	public boolean connect(T node1, T node2, int cost) {
		if (!nodeMap.containsKey(node1) || !nodeMap.containsKey(node2) || cost < 1) {
			return false;
		}
		if (!isConnected(node1, node2)) {
			Node<T> firstNode = nodeMap.get(node1);
			Node<T> secondNode = nodeMap.get(node2);
			firstNode.connectedNodes.add(node2);
			secondNode.connectedNodes.add(node1);
			edgeList.add(new Edge<>(firstNode, secondNode, cost));
			return true;
		}
		if (isConnected(node1, node2)) {
			getEdge(node1, node2).cost = cost;
			return true;
		}
		return false;
	}

	private void disConnect(T node1, T node2) {
		Node<T> firstNode = nodeMap.get(node1);
		Node<T> secondNode = nodeMap.get(node2);
		firstNode.connectedNodes.remove(node2);
		secondNode.connectedNodes.remove(node1);	
		edgeList.remove(getEdge(node1, node2));
	}

	private Edge<T> getEdge(T node1, T node2) {
		if (!nodeMap.containsKey(node1) || !nodeMap.containsKey(node2)) {
			return null;
		}
		for (Edge<T> e : edgeList) {
			if (e.node1.data.equals(node1) && e.node2.data.equals(node2) ||
				e.node1.data.equals(node2) && e.node2.data.equals(node1)) {
				return e;
			}
		}
		return null;
	}

	@Override
	public boolean isConnected(T node1, T node2) {
		if (!nodeMap.containsKey(node1) || !nodeMap.containsKey(node2)) {
			return false;
		}
		Node<T> firstNode = nodeMap.get(node1);
		for (T t : firstNode.connectedNodes) {
			if (t.equals(node2)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getCost(T node1, T node2) {
		if (!nodeMap.containsKey(node1) || !nodeMap.containsKey(node2)) {
			return -1;
		}
		for (Edge<T> e : edgeList) {
			if (e.node1.data.equals(node1) && e.node2.data.equals(node2) ||
				e.node1.data.equals(node2) && e.node2.data.equals(node1)) {
				return e.cost;
			}
		}
		return -1;
	}

	@Override
	public List<T> depthFirstSearch(T start, T end) {
		if (!nodeMap.containsKey(start) || !nodeMap.containsKey(end)) {
			return null;
		}
		Stack<T> stack = new Stack<>();
		LinkedList<T> output = new LinkedList<>();
		Node<T> startNode = nodeMap.get(start);

		stack.push(start);
		startNode.visited = true;

		if (start.equals(end)) {
			return stack;
		}

		while (!stack.isEmpty()) {
			T current = stack.pop();
			Node<T> currentNode = nodeMap.get(current);

			for (T t : currentNode.connectedNodes) {
				Node<T> tNode = nodeMap.get(t);
				if (tNode.previous == null) {
					tNode.previous = current;
				}
				if (tNode.visited == false) {
					stack.push(t);
					tNode.visited = true;
				}
			}
		}
		output.addLast(end);
		for (T temp = end; !nodeMap.get(temp).previous.equals(start); temp = nodeMap.get(temp).previous) {
			output.addFirst(nodeMap.get(temp).previous);
		}
		output.addFirst(start);
		return output;
	}

	@Override
	public List<T> breadthFirstSearch(T start, T end) {
		if (!nodeMap.containsKey(start) || !nodeMap.containsKey(end)) {
			return null;
		}
		LinkedList<T> queue = new LinkedList<>();
		LinkedList<T> output = new LinkedList<>();
		Node<T> startNode = nodeMap.get(start);

		queue.addFirst(start);
		startNode.visited = true;

		if (start.equals(end)) {
			return queue;
		}

		while (!queue.isEmpty()) {
			T current = queue.poll();
			Node<T> currentNode = nodeMap.get(current);

			for (T t : currentNode.connectedNodes) {
				Node<T> tNode = nodeMap.get(t);
				if (tNode.previous == null) {
					tNode.previous = current;
				}
				if (tNode.visited == false) {
					queue.addFirst(t);
					tNode.visited = true;
				}
			}
		}
		output.addLast(end);
		for (T temp = end; !nodeMap.get(temp).previous.equals(start); temp = nodeMap.get(temp).previous) {
			output.addFirst(nodeMap.get(temp).previous);
		}
		output.addFirst(start);
		return output;
	}

	@Override
	public UndirectedGraph<T> minimumSpanningTree() {
		Collections.sort(edgeList);
		ArrayList<Edge<T>> sortedEdgeList = new ArrayList<>(edgeList);
		MyUndirectedGraph<T> MST = new MyUndirectedGraph<>();

		removeLoops(sortedEdgeList);

		ArrayList<Edge<T>> mstList = new ArrayList<>();
		while (mstList.size() < getNumberOfNodes() - 1) {

			Edge<T> edge = sortedEdgeList.remove(0);
			T t1 = edge.node1.data;
			T t2 = edge.node2.data;
			MST.add(t1);
			MST.add(t2);
			MST.connect(t1, t2, edge.cost);
			mstList.add(edge);

			if (isCycle(edge.node1.data, MST)) {
				mstList.remove(edge);
				MST.disConnect(t1, t2);
			}
		}
		mstList.clear();
		return MST;
	}
	
	private void removeLoops(ArrayList<Edge<T>> sortedEdgeList) {
		ArrayList<Edge<T>> toRemove = new ArrayList<>();
		for (Edge<T> e : sortedEdgeList) {
			if (e.node1.equals(e.node2)) {
				toRemove.add(e);
			}
		}
		sortedEdgeList.removeAll(toRemove);
	}

	private boolean isCycle(T start, MyUndirectedGraph<T> MST) {
		Stack<T> stack = new Stack<>();
		Node<T> startNode = MST.nodeMap.get(start);

		for (Node<T> n : MST.nodeMap.values()) {
			n.visited = false;
		}

		stack.push(start);
		startNode.visited = true;

		while (!stack.isEmpty()) {
			T current = stack.pop();
			Node<T> currentNode = MST.nodeMap.get(current);

			for (T t : currentNode.connectedNodes) {
				Node<T> tNode = MST.nodeMap.get(t);
				if (tNode.visited == true && !t.equals(currentNode.previous)) {
					return true;
				} else {
					if (tNode.visited == false) {
						stack.push(t);
						tNode.visited = true;
						tNode.previous = current;
					}
				}
			}
		}
		return false;
	}
}
