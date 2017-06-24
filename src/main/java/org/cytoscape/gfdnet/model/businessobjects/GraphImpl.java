package org.cytoscape.gfdnet.model.businessobjects;

import java.util.Arrays;
import java.util.List;

/**
 * Implementation of the interface Graph using arrays
 * @param <T>
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class GraphImpl<T> implements Graph<T>{
	
    /**
     * Set of all the nodes of the graph
     */
    private T[] nodes;
	
    /**
     * Adjacency matrix of the graph
     * It is a matrix where each position represent the edge of the nodes identified by these row and column
     * It is a matrix where each position represent the edge of the nodes identified by these row and column
     * -1 -> There is no edge
     * >-1 -> The weight of the edge
     */
    private double[][] adjMat; 
    
    /**
     * Empty constructor.
     * Creates an empty graph with n nodes
     * 
     * @param n number of nodes of the graph
     */
    public GraphImpl (int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("The graph should contains at least one node.");
        }
        this.nodes =(T[]) new Object[n];
        this.adjMat = new double[n-1][];
        for (int i=0; i < n-1; i++) {
            this.nodes[i] = null;
            this.adjMat[i] = new double[n-i-1];
            for (int j= i+1; j < n; j++) {
                this.adjMat[i][j-i-1] = -1;
            }
        }
    }
    
    /**
     * Returns the node which id is passed as parameter
     * 
     * @param i id of the node to search for
     * @return the node which id is passed as parameter
     * @throws IllegalArgumentException if the specified index doesn't exist
     */
    @Override
    public T getNode(int i) {
        assertNodeExist(i);
        return nodes[i];
    }

    /**
     * Get all the nodes of the graph
     * 
     * @return a list containing all the nodes of the graph
     */
    @Override
    public List<T> getNodes() {
        return Arrays.asList(this.nodes);
    }
    
    /**
     * Set the value of a node which id is passed as parameter with and object
     * 
     * @param i id of the node to be changed
     * @param object new value of the node
     * @throws IllegalArgumentException if the specified index doesn't exist
     */
    @Override
    public void updateNodeValue(Integer i, T object) {
        assertNodeExist(i);
        nodes[i] = object;
    }
    
    @Override
    public boolean areConnected(int i, int j) {
        assertNodesExist(i,j);
        if (i>j) {
            int aux = j;
            j = i;
            i = aux;
        }
        return adjMat[i][j-i-1] != -1;
    }
    
    @Override
    public void setEdgeWeight(int i, int j, double weight) {
        assertNodesExist(i,j);
        if (i>j) {
            int aux = j;
            j = i;
            i = aux;
        } 
        if (this.adjMat[i][j-i-1] == -1) {
            throw new IllegalArgumentException("The nodes are not joined by an edge.");
        }
        this.adjMat[i][j-i-1] = weight;
    }
    
    /**
     * Returns the weight of the edge between the nodes which ids are passed
     * as parameters
     * 
     * @param i id of the node at one side of the edge
     * @param j id of the node at the other side of the edge
     * @return the weight of the edge between the nodes which ids are passed
     * as parameters or -1 if it doesn't exist
     * @throws IllegalArgumentException if one of the specified index doesn't exist
     */
    @Override
    public double getEdgeWeight(int i, int j) {
        assertNodesExist(i,j);
        if (i>j) {
            int aux = j;
            j = i;
            i = aux;
        }
        return (adjMat[i][j-i-1]);
    }
    
    /**
     * Add an edge if it does not exist before
     * 
     * @param i id of the node at one side of the edge
     * @param j id of the node at the other side of the edge
     * @param weight weight of the new edge
     * @throws IllegalArgumentException if one of the specified index doesn't exist
     */
    @Override
    public void addEdge(int i, int j, double weight) {
        assertNodesExist(i,j);
        if (i>j) {
            int aux = j;
            j = i;
            i = aux;
        }
        if (this.adjMat[i][j-i-1] != -1) {
            throw new IllegalArgumentException("The nodes are already joined by an edge.");
        }
        this.adjMat[i][j-i-1] = weight;
    }
    
    private void assertNodeExist(int i) {
        if (i < 0 || i >= nodes.length) {
            throw new IllegalArgumentException("Node doesn't exist.");
        }
    }
    
    private void assertNodesExist(int i, int j) {
        try {
            assertNodeExist(i);
            assertNodeExist(j);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("The nodes should exist.");
        }
    }
}