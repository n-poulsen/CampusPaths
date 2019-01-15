package n.poulsen.campuspaths.model;

import java.util.*;

/**
 * <b>DLMGraph</b> is a mutable representation of the mathematical concept of a directed,
 * labeled multi-graph. A graph consists of nodes of generic parameter E, and edges of
 * generic parameters E and F, between nodes. In a multigraph, there can be any number
 * of edges between a pair of nodes. In a labeled graph, every edge has a label containing
 * information of some sort. Labels are not unique: multiple edges may have the same label.
 * This DLMGraph does not allow two edges with the same parent node, child node and label
 * (i.e. identical edges).
 *
 * <b>Specification fields</b>:
 *   @spec.specfield nodes: the nodes contained in this graph
 *   @spec.specfield edges: the edges contained in this graph
 *
 * <b>Abstract invariant</b>:
 *   All edges in a graph must be between two nodes that are also present in it. A graph
 *   must not contain two equal edges or nodes.
 */
public class DLMGraph<E extends Object, F extends Object> {

    /** Holds all Nodes and edges of the graph, in an adjacency list */
    private Map<Node<E>, Set<DEdge<E, F>>> adjacencyList;

    /** When TRUE, this variable enables checkReps() at the beginning and end of every method*/
    private final static boolean DEBUGGING = false;

    // Definitions:
    //    For a DLMGraph g, let
    //        N(g) = g.adjacencyList.keySet()
    //        E(g) = g.adjacencyList.values()
    //    For a Node n in N(g), let
    //        C(g, n) = adjacencyList.get(n)
    //
    // Abstraction function:
    //    DLMGraph g represents the directed, labeled multi-graph whose the set of nodes
    //    is the set of keys of adjacencyList, and whose set of edges is the union of all
    //    sets that are values in adjacencyList. If a key n in N(g) has an empty list as
    //    value, then that node has no children. If there are no keys in adjacencyList,
    //    then the DLMGraph represents an empty graph.
    //
    // Representation invariant for every DLMGraph g:
    //    g.adjacencyList != null &&
    //    forall n in N(g): n != null &&
    //    forall n in N(g): C(g, n) != null &&
    //    forall n in N(g), forall e in C(g, n):
    //                (e != null &&
    //                e.getParentNode() = n &&
    //                N(g).contains(e.getChildNode()))
    //

    /** @spec.effects Constructs a new empty graph */
    public DLMGraph(){
        adjacencyList = new HashMap<Node<E>, Set<DEdge<E, F>>>();
        if (DEBUGGING) checkRep();
    }

    /**
     * Returns the shortest path from a start node to a destination, in
     * a given graph.
     *
     * @spec.requires marvel != null
     * @spec.requires start != null
     * @spec.requires dest != null
     * @spec.requires marvel contains start and marvel contains dest
     * @spec.requires marvel contains only edges with positive weights
     * @param <E> the type of label in the nodes (i.e, Node is of generic type E)
     * @param marvel the graph in which to find the path
     * @param start the node from which to start the path
     * @param dest the node to reach from start
     * @return the shortest path from start to dest in marvel, and null if
     *    dest cannot be reached from start.
     */
    public static <E extends Object> List<DEdge<E, Double>> dijkstra(DLMGraph<E, Double> marvel, Node<E> start, Node<E> dest){
        if (start.equals(dest)) return new ArrayList<>();
        // Queue of pairs of (path, totalCost) where totalCost is the total cost of path.
        Queue<Pair<List<DEdge<E, Double>>, Double>> q = new PriorityQueue<>(1,
                (p1, p2) -> p1.getValue().compareTo(p2.getValue()));
        Set<Node<E>> finished = new HashSet<>();

        List<DEdge<E, Double>> pathToItself = new ArrayList<>();
        pathToItself.add(new DEdge<>(start, start, 0.0));
        q.add(new Pair<>(pathToItself, 0.0));

        while(!q.isEmpty()){
            Pair<List<DEdge<E, Double>>, Double> head = q.remove();
            List<DEdge<E, Double>> path = head.getKey();
            Node<E> reached = path.get(path.size() - 1).getChildNode();
            double distance = head.getValue();
            if (reached.equals(dest)){
                return path;
            }
            if (!finished.contains(reached)){
                for (DEdge<E, Double> e: marvel.outEdges(reached)){
                    if (!finished.contains(e.getChildNode())){
                        List<DEdge<E, Double>> nextPath;
                        if (e.getParentNode().equals(start)){
                            nextPath = new ArrayList<>();
                        }else {
                            nextPath = new ArrayList<>(path);
                        }
                        nextPath.add(e);
                        q.add(new Pair<>(nextPath, distance + e.getLabel()));
                    }
                }
            }
            finished.add(reached);
        }
        return null;
    }

    /**
     * Returns the lexicographically least shortest path from a start node to a destination, in
     * a given graph.
     *
     * @spec.requires marvel != null
     * @spec.requires start != null
     * @spec.requires dest != null
     * @spec.requires marvel contains start and marvel contains dest
     * @param marvel the graph in which to find the path
     * @param start the node from which to start the path
     * @param dest the node to reach from start
     * @return the lexicographically least shortest path from start to dest in marvel, and null if
     *    dest cannot be reached from start.
     */
    public static List<DEdge<String, String>> shortestPath(DLMGraph<String, String> marvel, Node<String> start, Node<String> dest){
        Map<Node<String>, List<DEdge<String, String>>> paths = new HashMap<Node<String>, List<DEdge<String, String>>>();
        Queue<Node<String>> q = new ArrayDeque<Node<String>>();
        paths.put(start, new ArrayList<DEdge<String, String>>());
        q.add(start);
        while(!q.isEmpty()){
            Node<String> head = q.remove();
            if (head.equals(dest)){
                return paths.get(head);
            }
            List<DEdge<String, String>> children = new ArrayList<DEdge<String, String>>(marvel.outEdges(head));
            Collections.sort(children, (e1, e2) -> {
                // Orders the next nodes to add to the queue by node name first, and edge name second
                int firstTest = e1.getChildNode().getLabel().compareTo(e2.getChildNode().getLabel());
                return firstTest == 0 ? e1.getLabel().compareTo(e2.getLabel()) : firstTest;
            });
            for(DEdge<String, String> e: children){
                // Here I need to guarantee that paths.get(head) will not return null. We are guaranteed this as all elements
                // in the queue are keys for the map, and the map only points to NonNull lists. This cannot be expressed using
                // @KeyFor, as me and a TA noticed.
                List<DEdge<String, String>> path = new ArrayList<DEdge<String, String>>(paths.get(head));
                path.add(e);
                if (paths.putIfAbsent(e.getChildNode(), path) == null) {
                    q.add(e.getChildNode());
                }
            }
        }
        return null;
    }

    /**
     * Adds a node to this directed labeled multi-graph.
     *
     * @param n the node to add to the graph
     * @spec.requires n != null
     * @spec.requires n isn't already contained in this graph
     * @spec.modifies this
     * @spec.effects Adds the node n to this graph
     * @return true iff this node was successfully added to this graph
     */
    public boolean addNode(Node<E> n){
        if (DEBUGGING) checkRep();
        Set<DEdge<E, F>> oldValue = adjacencyList.putIfAbsent(n, new HashSet<DEdge<E, F>>());
        if (DEBUGGING) checkRep();
        return oldValue == null;
    }

    /**
     * Adds a set of nodes to this directed labeled multi-graph.
     *
     * @param s the set of nodes to add to the graph
     * @spec.requires s != null
     * @spec.requires forall n in s, n != null
     * @spec.requires forall n in s, n isn't already contained in this graph
     * @spec.modifies this
     * @spec.effects Adds all nodes n in s to this graph
     * @return true iff all nodes were successfully added to this graph
     */
    public boolean addNodeSet(Set<Node<E>> s){
        if (DEBUGGING) checkRep();
        boolean allAdded = true;
        for (Node<E> n: s){
            allAdded &= addNode(n);
        }
        if (DEBUGGING) checkRep();
        return allAdded;
    }

    /**
     * Add an edge to this directed labeled multi-graph.
     *
     * @param e the edge to add to the graph
     * @spec.requires e != null
     * @spec.requires e isn't already contained in this graph
     * @spec.requires e is between two nodes contained in this graph
     * @spec.modifies this
     * @spec.effects Adds the edge e to this graph
     * @return true iff this edge was successfully added to this graph
     */
    public boolean addEdge(DEdge<E, F> e) {
        if (DEBUGGING) checkRep();
        Set<DEdge<E, F>> s = adjacencyList.get(e.getParentNode());
        boolean added = false;
        // s == null iff e.getParentNode() isn't in the graph.
        if (s == null || !contains(e.getChildNode())){
            throw new IllegalArgumentException("This edge can't be part of the graph, as at least one of its nodes isn't in it");
        }else if (!s.contains(e)){
            added = s.add(e);
        }
        if (DEBUGGING) checkRep();
        return added;
    }

    /**
     * Adds a set of edges to this directed labeled multi-graph.
     *
     * @param s the set of edges to add to the graph
     * @spec.requires s != null
     * @spec.requires forall e in s, e != null
     * @spec.requires forall e in s, e isn't already contained in this graph
     * @spec.requires forall e in s, e is between two nodes contained in this graph
     * @spec.modifies this
     * @spec.effects Adds all edges e in s to this graph
     * @return true iff all edges were successfully added to this graph
     */
    public boolean addEdgeSet(Set<DEdge<E, F>> s){
        if (DEBUGGING) checkRep();
        boolean allAdded = true;
        for (DEdge<E, F> e: s){
            allAdded &= addEdge(e);
        }
        if (DEBUGGING) checkRep();
        return allAdded;
    }

    /**
     * Returns true if this graph contains the specified node, false otherwise.
     *
     * @param n the node to be checked for membership in this graph
     * @spec.requires n != null
     * @return true iff this graph contains node n
     */
    public boolean contains(Node<E> n){
        if (DEBUGGING) checkRep();
        boolean c = adjacencyList.containsKey(n);
        if (DEBUGGING) checkRep();
        return c;
    }

    /**
     * Returns true if this graph contains the specified edge, false otherwise.
     *
     * @param e the edge to be checked for membership in this graph.
     * @spec.requires e != null
     * @return true iff this graph contains edge e
     */
    public boolean contains(DEdge<E, F> e){
        if (DEBUGGING) checkRep();
        boolean c = false;
        Set<DEdge<E, F>> s = adjacencyList.get(e.getParentNode());
        // s == null iff e.getParentNode() isn't in the graph.
        if (s != null && contains(e.getChildNode())){
            c = s.contains(e);
        }
        if (DEBUGGING) checkRep();
        return c;
    }

    /**
     * Removes the specified node, and all edges connected to that node, from this graph.
     *
     * @param n the node to remove from this graph
     * @spec.requires n != null
     * @spec.requires this contains n
     * @spec.modifies this
     * @spec.effects removes n, and all edges connected to n, from this graph
     * @return true iff the specified node, and all attached edges, were successfully removed
     */
    public boolean removeNode(Node<E> n){
        if (DEBUGGING) checkRep();
        boolean removed = false;
        if (contains(n)){
            adjacencyList.remove(n);
            // I could have used edgeIterator() here, but it would have been
            // less efficient
            for (Node<E> a: adjacencyList.keySet()) {
                Iterator<DEdge<E, F>> i = adjacencyList.get(a).iterator();
                while(i.hasNext()){
                    if (i.next().getChildNode().equals(n)) i.remove();
                }
            }
            removed = !contains(n);
        }
        if (DEBUGGING) checkRep();
        return removed;
    }

    /**
     * Removes the specified edge from this graph.
     *
     * @param e the edge to remove from this graph
     * @spec.requires e != null
     * @spec.requires this contains e
     * @spec.modifies this
     * @spec.effects removes e from this graph
     * @return true iff the specified edge was successfully removed
     */
    public boolean removeEdge(DEdge<E, F> e){
        if (DEBUGGING) checkRep();
        boolean removed = false;
        Set<DEdge<E, F>> s = adjacencyList.get(e.getParentNode());
        // s == null iff e.getParentNode() isn't in the graph.
        if (s != null){
            removed = s.remove(e);
        }
        if (DEBUGGING) checkRep();
        return removed;
    }

    /**
     * Returns the number of nodes that this graph contains.
     *
     * @return the number of nodes in this graph
     */
    public int numberOfNodes(){
        if (DEBUGGING) checkRep();
        int size = adjacencyList.size();
        if (DEBUGGING) checkRep();
        return size;
    }

    /**
     * Returns the number of edges that this graph contains.
     *
     * @return the number of edges in this graph
     */
    public int numberOfEdges(){
        if (DEBUGGING) checkRep();
        int size = 0;
        for (Node<E> n: adjacencyList.keySet()){
            size += adjacencyList.get(n).size();
        }
        if (DEBUGGING) checkRep();
        return size;
    }

    /**
     * Returns an iterator on this graph's nodes.
     *
     * @return an iterator on this graph's nodes
     */
    public Iterator<Node<E>> nodeIterator(){
        if (DEBUGGING) checkRep();
        Iterator<Node<E>> i = getNodes().iterator();
        if (DEBUGGING) checkRep();
        return i;
    }

    /**
     * Returns an iterator on this graph's edges.
     *
     * @return an iterator on this graph's edges
     */
    public Iterator<DEdge<E, F>> edgeIterator(){
        if (DEBUGGING) checkRep();
        Iterator<DEdge<E, F>> i = getEdges().iterator();
        if (DEBUGGING) checkRep();
        return i;
    }

    /**
     * Returns all children of the specified node.
     *
     * @param n the node to return the children's from
     * @spec.requires n != null
     * @spec.requires n is contained in this graph
     * @return all children of the specified node
     */
    public Set<Node<E>> children(Node<E> n){
        if (DEBUGGING) checkRep();
        Set<Node<E>> s = new HashSet<Node<E>>();
        if (n != null) {
            Set<DEdge<E, F>> oldSet = adjacencyList.get(n);
            // oldSet == null iff n isn't in the graph
            if (oldSet != null) {
                for (DEdge<E, F> e : oldSet) {
                    s.add(e.getChildNode());
                }
            }
        }
        if (DEBUGGING) checkRep();
        return s;
    }

    /**
     * Returns all parents of the specified node.
     *
     * @param n the node to return the parent's from
     * @spec.requires n != null
     * @spec.requires n is contained in this graph
     * @return all parents of the specified node
     */
    public Set<Node<E>> parents(Node<E> n){
        if (DEBUGGING) checkRep();
        Set<Node<E>> s = new HashSet<Node<E>>();
        if (n != null && contains(n)) {
            // I could have used edgeIterator() here, but it would have been
            // less efficient
            for (Node<E> a: adjacencyList.keySet()) {
                Iterator<DEdge<E, F>> i = adjacencyList.get(a).iterator();
                while(i.hasNext()){
                    if (i.next().getChildNode().equals(n)) s.add(a);
                }
            }
        }
        if (DEBUGGING) checkRep();
        return s;
    }

    /**
     * Returns all edges which have the specified node as the parent.
     *
     * @param n the node to return the children's from
     * @spec.requires n != null
     * @spec.requires n is contained in this graph
     * @return a set containing all edges which have the specified node as the parent.
     */
    public Set<DEdge<E, F>> outEdges(Node<E> n){
        if (DEBUGGING) checkRep();
        Set<DEdge<E, F>> copy;
        Set<DEdge<E, F>> s = adjacencyList.get(n);
        if (n != null && s != null) {
            copy = new HashSet<DEdge<E, F>>(s);
        }else{
            copy = new HashSet<DEdge<E, F>>();
        }
        if (DEBUGGING) checkRep();
        return copy;
    }

    /**
     * Returns all edges which have the specified node as the child.
     *
     * @param n the node to return the parent's from
     * @spec.requires n != null
     * @spec.requires n is contained in this graph
     * @return a set containing all edges which have the specified node as the child.
     */
    public Set<DEdge<E, F>> inEdges(Node<E> n){
        if (DEBUGGING) checkRep();
        Set<DEdge<E, F>> s = new HashSet<DEdge<E, F>>();
        if (n != null && contains(n)) {
            // I could have used edgeIterator() here, but it would have been
            // less efficient
            for (Node<E> a: adjacencyList.keySet()) {
                Iterator<DEdge<E, F>> i = adjacencyList.get(a).iterator();
                while(i.hasNext()){
                    DEdge<E, F> nextEdge = i.next();
                    if (nextEdge.getChildNode().equals(n)) s.add(nextEdge);
                }
            }
        }
        if (DEBUGGING) checkRep();
        return s;
    }


    /**
     * Returns a set containing all nodes in this graph.
     *
     * @return a set containing all nodes in this graph
     */
    public Set<Node<E>> getNodes(){
        if (DEBUGGING) checkRep();
        Set<Node<E>> s = new HashSet<Node<E>>(adjacencyList.keySet());
        if (DEBUGGING) checkRep();
        return s;
    }

    /**
     * Returns a set containing all edges in this graph.
     *
     * @return a set containing all edges in this graph
     */
    public Set<DEdge<E, F>> getEdges(){
        if (DEBUGGING) checkRep();
        Set<DEdge<E, F>> s = new HashSet<DEdge<E, F>>();
        for (Node<E> a: adjacencyList.keySet()) {
            s.addAll(adjacencyList.get(a));
        }
        if (DEBUGGING) checkRep();
        return s;
    }

    /**
     * Returns a string representation of this graph.
     *
     * @return A string representation of this graph, as two lists, surrounded by brackets,
     *     on two lines: one of all the nodes in this graph, and one of all the edges in this
     *     graph. Each node and each edge is surrounded by parentheses. If this graph is empty,
     *     the "[]\n[]" string is returned.
     *
     *     As an example, if a graph contains nodes with labels "n1", "n2" and "n3", and edges
     *     from n1 to n2 with label "e1" and from n2 to n3 with label "e2", a valid output is
     *     "[(n1)(n3)(n2)]\n[(n2, n3, e2)(n1, n2, e1)]"
     */
    @Override
    public String toString(){
        if (DEBUGGING) checkRep();
        StringBuilder nodes = new StringBuilder();
        StringBuilder edges = new StringBuilder();
        nodes.append('[');
        edges.append('[');
        for (Node<E> a: adjacencyList.keySet()) {
            nodes.append('(');
            nodes.append(a.toString());
            nodes.append(')');
            Iterator<DEdge<E, F>> i = adjacencyList.get(a).iterator();
            while(i.hasNext()){
                edges.append(i.next().toString());
            }
        }
        if (DEBUGGING) checkRep();
        return nodes.toString() + "]\n" + edges.toString() + "]";
    }

    /**
     * Standard equality operation.
     *
     * @param obj The object to be compared for equality.
     * @return true iff 'obj' is an instance of a DLMGraph and 'this' and 'obj' represent
     *     the same directed labeled multi-graph.
     */
    @Override
    public boolean equals(Object obj){
        if (obj instanceof DLMGraph<?, ?>) {
            DLMGraph<?, ?> g = (DLMGraph<?, ?>) obj;
            if (this.hashCode() == g.hashCode() && this.numberOfNodes() == g.numberOfNodes()){
                return this.adjacencyList.equals(g.adjacencyList);
            }
        }
        return false;
    }

    /**
     * Standard hashCode function.
     *
     * @return an int that all objects equal to this will also return.
     */
    @Override
    public int hashCode(){
        return Objects.hashCode(adjacencyList);
    }

    /** Checks that the representation invariant holds (if any). */
    private void checkRep(){
        assert (adjacencyList != null);
        for (Node<E> n: adjacencyList.keySet()){
            assert(n != null);
            assert(adjacencyList.get(n) != null);
            for (DEdge<E, F> e: adjacencyList.get(n)){
                assert(e != null);
                assert(e.getParentNode().equals(n));
                assert(adjacencyList.containsKey(e.getChildNode()));
            }
        }
    }

    /**
     * <b>Node</b> is an immutable representation of the concept of a node,
     * which is a component of a graph. It contains a label of immutable type E.
     *
     * <b>Specification fields</b>:
     *   @spec.specfield label: some information about this node.
     *
     * <b>Abstract invariant</b>:
     *   Two nodes are equal if they contain the same information.
     */
    public static class Node<E extends Object> {

        /** This edge's label */
        private final E label;

        /** When TRUE, this variable enables checkReps() at the beginning and end of every method*/
        private final static boolean DEBUGGING = false;

        // Abstraction function:
        //    Node n represents a node with label n.label
        //
        // Representation invariant for every Node n:
        //    n.label != null
        //

        /**
         * @param label the information contained in this node
         * @spec.requires label != null
         * @spec.requires label != ""
         * @spec.effects Constructs a new node containing the
         *   information "label"
         */
        public Node(E label){
            this.label = label;
            if (DEBUGGING) checkRep();
        }

        /**
         * Returns the information contained in this node.
         *
         * @return the information contained in this node
         */
        public E getLabel(){
            if (DEBUGGING) checkRep();
            return label;
        }

        /**
         * Returns a string representation of this node.
         *
         * @return A string representation of this node, as a the information it
         * contains: the string representation of E label.
         */
        @Override
        public String toString(){
            if (DEBUGGING) checkRep();
            return label.toString();
        }

        /**
         * Standard equality operation.
         *
         * @param obj The object to be compared for equality.
         * @return true iff 'obj' is an instance of a Node and 'this' and 'obj' represent
         *     the same Node, i.e. their labels are of the same type and equal.
         */
        @Override
        public boolean equals(Object obj){
            if (obj instanceof Node<?>){
                Node<?> e = (Node<?>) obj;
                return e.label.equals(this.label);
            }
            return false;
        }

        /**
         * Standard hashCode function.
         *
         * @return an int that all objects equal to this will also return.
         */
        @Override
        public int hashCode(){
            return Objects.hashCode(label);
        }

        /** Checks that the representation invariant holds (if any). */
        private void checkRep(){
            assert(label != null);
        }

    }

    /**
     * <b>DEdge</b> is an immutable representation of the concept of a directed edge,
     * which is a component of a directed graph. A directed edge DEdge, with generic
     * parameters E and F, connects two nodes of type Node of E, and has a label of
     * type F from that graph, starts at a parent node and end at a child node. Each
     * edge has a label of type F, which contains some information about it.
     *
     * <b>Specification fields</b>:
     *   @spec.specfield parent: the node this edge starts at.
     *   @spec.specfield child: the node this edge ends at.
     *   @spec.specfield label: the information this edge contains.
     *
     * <b>Abstract invariant</b>:
     *   The child node is directly reachable from the parent node. An edge can start at
     *   the same node it ends at (i.e., the parent node can be the same as the child node).
     *   Two edges are equal if the parent and child nodes are the same and they contain
     *   the same information.
     */
    public static class DEdge<E extends Object, F extends Object> {

        /** The node this edge starts at, the parent node */
        private final Node<E> parent;

        /** The node this edge ends at, the child node */
        private final Node<E> child;

        /** This edge's label */
        private final F label;

        /** When TRUE, this variable enables checkReps() at the beginning and end of every method*/
        private final static boolean DEBUGGING = false;

        // Abstraction function:
        //    DEdge e represents a directed, labeled edge, whose parent node is e.parent,
        //    whose child node is e.child and with label e.label
        //
        // Representation invariant for every DEdge e:
        //    e.parent != null &&
        //    e.child != null &&
        //    label != null
        //

        /**
         * @param parent the node this edge starts at
         * @param child the node this edge ends at
         * @param label the label containing information about this edge
         * @spec.requires parent != null
         * @spec.requires child != null
         * @spec.requires label != null
         * @spec.requires label != ""
         * @spec.effects Constructs a new edge that starts at the parent node,
         *   ends at the child node and contains the information "label".
         */
        public DEdge(Node<E> parent, Node<E> child, F label){
            this.parent = parent;
            this.child = child;
            this.label = label;
            if (DEBUGGING) checkRep();
        }

        /**
         * Returns the information this edge contains.
         *
         * @return the information this edge contains
         */
        public F getLabel(){
            if (DEBUGGING) checkRep();
            return label;
        }

        /**
         * Returns this edge's parent node.
         *
         * @return this edge's parent node
         */
        public Node<E> getParentNode(){
            if (DEBUGGING) checkRep();
            return parent;
        }

        /**
         * Returns this edge's child node.
         *
         * @return this edge's child node
         */
        public Node<E> getChildNode(){
            if (DEBUGGING) checkRep();
            return child;
        }

        /**
         * Returns a string representation of this edge.
         *
         * @return A string representation of this edge, as a tuple surrounded by parenthesis
         * (parent, child, label), where child is the child's label, parent is the parent's label,
         * and label is the string representation of this edge's label.
         */
        @Override
        public String toString(){
            if (DEBUGGING) checkRep();
            return "(" + parent.toString() + ", " + child.toString() + ", " + label + ")";
        }

        /**
         * Standard equality operation.
         *
         * @param obj The object to be compared for equality.
         * @return true iff 'obj' is an instance of a DEdge and 'this' and 'obj' represent
         *     the same edge.
         */
        @Override
        public boolean equals(Object obj){
            if (obj instanceof DEdge<?, ?>){
                DEdge<? ,?> e = (DEdge<?, ?>) obj;
                return e.child.equals(this.child) && e.parent.equals(this.parent) && e.label.equals(this.label);
            }
            return false;
        }

        /**
         * Standard hashCode function.
         *
         * @return an int that all objects equal to this will also return.
         */
        @Override
        public int hashCode(){
            return Objects.hashCode(child) + Objects.hashCode(parent) + Objects.hashCode(label);
        }

        /** Checks that the representation invariant holds (if any). */
        private void checkRep(){
            assert(parent != null);
            assert(child != null);
            assert(label != null);
        }

    }

    /**
     * A class representing a pair of objects. A pair contains a key of
     * generic type K and a value of generic type V.
     *
     * @param <K> the type of the key
     * @param <V> the type of the value
     */
    private static class Pair<K extends Object, V extends Object> {

        /** The key of the pair */
        private K key;

        /** the value of the pair */
        private V value;

        // Abstraction function:
        //    Pair p represents a pair of values p = (k, v), where k is
        //    of type K and v is of type V.
        //
        // Representation invariant for every Pair p:
        //    p.key != null &&
        //    p.value != null

        /**
         * Creates a new pair with key K and value V
         *
         * @param key
         * @param value
         */
        private Pair(K key, V value){
            this.key = key;
            this.value = value;
            checkRep();
        }

        /**
         * Returns this pair's key
         *
         * @return the key in this
         */
        private K getKey(){
            checkRep();
            return key;
        }

        /**
         * Returns this pair's value
         *
         * @return the value in this
         */
        private V getValue(){
            checkRep();
            return value;
        }

        /** Checks that the representation invariant holds (if any). */
        private void checkRep(){
            assert(key != null);
            assert(value != null);
        }

    }

}
