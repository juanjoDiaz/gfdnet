package org.cytoscape.gfdnet.model.businessobjects;

import java.math.BigDecimal;
import java.util.List;

/**
 * Graph interface ad hoc for GFD-Net.
 * The nodes are created at the beginning and only their values can be changed.
 * @param <T> 
 * @author Juan José Díaz Montaña
 */
public interface Graph<T> {

    /**
     * Returns the node which id is passed as parameter
     * 
     * @param i id of the node to search for
     * @return the node which id is passed as parameter
     * @throws IllegalArgumentException if the specified index doesn't exist
     */
    T getNode(int i);
    
    /**
     * Get all the nodes of the graph
     * 
     * @return a list containing all the nodes of the graph
     */
    List<T> getNodes();
    
    /**
     * Set the value of a node which id is passed as parameter with and object
     * 
     * @param i id of the node to be changed
     * @param object new value of the node
     * @throws IllegalArgumentException if the specified index doesn't exist
     */
    void updateNodeValue(Integer i, T object);
    
    /**
     * Returns if two nodes are connected or not
     * 
     * @param i id of one node
     * @param j id the other node
     * @return a boolean indicating whether two nodes are connected or not
     */
    boolean areConnected(int i, int j);
    
    /**
     * Set the weight of the edge between the nodes which ids are passed
     * as parameters
     * 
     * @param i id of the node at one side of the edge
     * @param j id of the node at the other side of the edge
     * @throws IllegalArgumentException if one of the specified index doesn't exist
     */
    void setEdgeWeight(int i, int j, BigDecimal weight);
        
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
    BigDecimal getEdgeWeight(int i, int j);
    
    /**
     * Add an edge if it does not exist before
     * 
     * @param i id of the node at one side of the edge
     * @param j id of the node at the other side of the edge
     * @param weight weight of the new edge
     * @throws IllegalArgumentException if one of the specified index doesn't exist
     * or the edge doesn't exist
     */
    void addEdge(int i, int j, BigDecimal weight);
}
