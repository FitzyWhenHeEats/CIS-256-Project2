
package graph;

import dict.HashTable;

public class WUGraph {
    private final HashTable<Object, Vertex> vertices;
    private final HashTable<VertexPair, Edge> edges;
    private final DList<Vertex> vertexList;

    private static class Node<T> {
        private final T item;
        private Node<T> previous;
        private Node<T> next;

        /** Constructs a list node containing item. */
        private Node(T item) {
            this.item = item;
        }
    }

    private static class DList<T> {
        private Node<T> first;
        private int size;

        /** Adds item to the front and returns its new list node. */
        private Node<T> add(T item) {
            Node<T> node = new Node<>(item);
            node.next = first;

            if (first != null) {
                first.previous = node;
            }

            first = node;
            size++;
            return node;
        }

        /** Removes node, which must belong to this list. */
        private void remove(Node<T> node) {
            if (node.previous == null) {
                first = node.next;
            } else {
                node.previous.next = node.next;
            }

            if (node.next != null) {
                node.next.previous = node.previous;
            }

            node.previous = null;
            node.next = null;
            size--;
        }
    }

    private static class Vertex {
        private final Object item;
        private final DList<Edge> adjacencyList = new DList<>();
        private Node<Vertex> listNode;

        /** Constructs an isolated internal vertex representing item. */
        private Vertex(Object item) {
            this.item = item;
        }
    }

    private static class Edge {
        private final Vertex u;
        private final Vertex v;
        private int weight;
        private Node<Edge> nodeU;
        private Node<Edge> nodeV;

        /** Constructs an edge between u and v with the specified weight. */
        private Edge(Vertex u, Vertex v, int weight) {
            this.u = u;
            this.v = v;
            this.weight = weight;
        }

        /** Returns the other endpoint given an incident vertex. */
        private Vertex other(Vertex vertex) {
            return vertex == u ? v : u;
        }
    }

    /** Constructs an empty weighted undirected graph. */
    public WUGraph() {
        vertices = new HashTable<>();
        edges = new HashTable<>();
        vertexList = new DList<>();
    }

    /** Returns the number of vertices in the graph. */
    public int vertexCount() {
        return vertexList.size;
    }

    /** Returns the number of undirected edges in the graph. */
    public int edgeCount() {
        return edges.size();
    }

    /** Returns a new array containing every original vertex object. */
    public Object[] getVertices() {
        Object[] result = new Object[vertexCount()];
        Node<Vertex> current = vertexList.first;
        int i = 0;

        while (current != null) {
            result[i++] = current.item.item;
            current = current.next;
        }

        return result;
    }

    /** Adds vertex if an equal vertex is not already present. */
    public void addVertex(Object vertex) {
        if (isVertex(vertex)) {
            return;
        }

        Vertex internal = new Vertex(vertex);
        internal.listNode = vertexList.add(internal);
        vertices.put(vertex, internal);
    }

    /** Returns true if a vertex equal to vertex is present. */
    public boolean isVertex(Object vertex) {
        return vertices.get(vertex) != null;
    }

    /** Returns the degree of vertex, counting self-edges once, or 0 if absent. */
    public int degree(Object vertex) {
        Vertex internal = vertices.get(vertex);

        if (internal == null) {
            return 0;
        }

        return internal.adjacencyList.size;
    }

    /** Returns a new Neighbors object for vertex, or null if absent or isolated. */
    public Neighbors getNeighbors(Object vertex) {
        Vertex internal = vertices.get(vertex);

        if (internal == null || internal.adjacencyList.size == 0) {
            return null;
        }

        int degree = internal.adjacencyList.size;

        Neighbors result = new Neighbors();
        result.neighborList = new Object[degree];
        result.weightList = new int[degree];

        Node<Edge> current = internal.adjacencyList.first;
        int i = 0;

        while (current != null) {
            Edge edge = current.item;

            result.neighborList[i] = edge.other(internal).item;
            result.weightList[i] = edge.weight;

            i++;
            current = current.next;
        }

        return result;
    }

    /** Adds or updates the edge between u and v with weight, if both exist. */
    public void addEdge(Object u, Object v, int weight) {
        Vertex first = vertices.get(u);
        Vertex second = vertices.get(v);

        if (first == null || second == null) {
            return;
        }

        VertexPair pair = new VertexPair(first.item, second.item);
        Edge edge = edges.get(pair);

        if (edge != null) {
            edge.weight = weight;
            return;
        }

        edge = new Edge(first, second, weight);
        edge.nodeU = first.adjacencyList.add(edge);

        if (first != second) {
            edge.nodeV = second.adjacencyList.add(edge);
        }

        edges.put(pair, edge);
    }

    /** Returns true if an edge exists between u and v. */
    public boolean isEdge(Object u, Object v) {
        return edges.get(new VertexPair(u, v)) != null;
    }

    /** Returns the weight of the edge between u and v, or 0 if absent. */
    public int weight(Object u, Object v) {
        Edge edge = edges.get(new VertexPair(u, v));

        if (edge == null) {
            return 0;
        }

        return edge.weight;
    }

    /** Removes the edge between u and v if it exists. */
    public void removeEdge(Object u, Object v) {
        Edge edge = edges.remove(new VertexPair(u, v));

        if (edge == null) {
            return;
        }

        edge.u.adjacencyList.remove(edge.nodeU);

        if (edge.nodeV != null) {
            edge.v.adjacencyList.remove(edge.nodeV);
        }
    }

    /** Removes vertex and all incident edges if vertex exists. */
    public void removeVertex(Object vertex) {
        Vertex internal = vertices.get(vertex);

        if (internal == null) {
            return;
        }

        while (internal.adjacencyList.first != null) {
            Edge edge = internal.adjacencyList.first.item;
            removeEdge(edge.u.item, edge.v.item);
        }

        vertexList.remove(internal.listNode);
        vertices.remove(internal.item);
    }
}
