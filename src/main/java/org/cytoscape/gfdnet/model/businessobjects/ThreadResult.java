package org.cytoscape.gfdnet.model.businessobjects;

import java.util.Map;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class ThreadResult {
    public GOTreeNode centralNode;
    public double dissimilarity;
    public Map<GeneInput, GOTreeNode> selectedAnnotations;
    
    public ThreadResult(GOTreeNode centralNode, double dissimilarity, Map<GeneInput, GOTreeNode> selectedAnnotations) {
        this.centralNode = centralNode;
        this.dissimilarity = dissimilarity;
        this.selectedAnnotations =  selectedAnnotations;
    }
}
