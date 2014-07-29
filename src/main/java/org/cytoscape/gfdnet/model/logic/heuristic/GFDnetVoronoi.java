package org.cytoscape.gfdnet.model.logic.heuristic;

import java.math.BigDecimal;
import java.util.List;
import org.cytoscape.gfdnet.model.businessobjects.GFDnetResult;
import org.cytoscape.gfdnet.model.businessobjects.GOTreeNode;
import org.cytoscape.gfdnet.model.businessobjects.GeneInput;
import org.cytoscape.gfdnet.model.businessobjects.Graph;
import org.cytoscape.gfdnet.model.businessobjects.ProgressMonitor;
import org.cytoscape.gfdnet.model.businessobjects.Representation;
import org.cytoscape.gfdnet.model.logic.GFDnet;
import org.cytoscape.gfdnet.model.logic.utils.GOTreeUtils;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class GFDnetVoronoi extends GFDnet{

    private int numCores = Runtime.getRuntime().availableProcessors();
    
    private int currentThread = 0;
    private int executedThreadCount = 0;
    
    private List<Representation> usedRepresentations;
    private BigDecimal lowestDissimilarity = BigDecimal.valueOf(Float.MAX_VALUE);

    private int progress = 0;
    private int treeNodesSize;
        
    public synchronized void incProgress() {
        progress++; 
        pm.setProgress((float)progress/treeNodesSize);
    }
    
    public GFDnetVoronoi(ProgressMonitor pm) {
        super(pm);
    }

    /**
     * This method is the bottle neck of the analysis and it is use a Divide and Conquer approach
     * to overcome that. It gets the relevant section of the GO-Tree for the given network
     * and execute several MetaThreads dividing the nodes of the tree in several sets
     * 
     * @param nodes
     * @param network
     * @param ontology
     * @param version
     * @return The MethaThread that got the best result
     */
    @Override
    public GFDnetResult evaluateRepresentations(Graph<GeneInput> network, String ontology, int version) {
        List<GOTreeNode> treeNodes = GOTreeUtils.getNodes(network, ontology);      
        treeNodesSize = treeNodes.size();
        
        synchronized (this) {
            while (executedThreadCount + currentThread < treeNodesSize) {
                if (isInterrupted){
                    return null;
                }
                if ((numCores > currentThread)) {
                    currentThread++;
                    GOTreeNode centralNode = treeNodes.get(executedThreadCount + currentThread - 1);
                    new GFDnetVoronoiThread(this, network, ontology, centralNode, version).start();
                    incProgress();
                } else {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        System.err.println("Error while waiting for a threads.\n" + e.getLocalizedMessage());
                    }
                }
            }
        }
        
        for(Representation representation : usedRepresentations) {
            representation.setSelected(true);
        }

        GFDnetResult result = new GFDnetResult(ontology, network);
        System.gc();
        return result;
    }

    public synchronized void notifyThreadCompleted(BigDecimal dissimilarity, List<Representation> usedRepresentations) {
        if (lowestDissimilarity.compareTo(dissimilarity) > 0) {
            this.lowestDissimilarity = dissimilarity;
            this.usedRepresentations = usedRepresentations;
        }
        executedThreadCount++;
        currentThread--;
        notify();
    }
}