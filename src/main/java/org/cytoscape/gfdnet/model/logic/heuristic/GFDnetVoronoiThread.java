package org.cytoscape.gfdnet.model.logic.heuristic;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cytoscape.gfdnet.model.businessobjects.GeneInput;
import org.cytoscape.gfdnet.model.businessobjects.Graph;
import org.cytoscape.gfdnet.model.businessobjects.Representation;
import org.cytoscape.gfdnet.model.logic.utils.GFDnetSimilarityUtils;
import org.cytoscape.gfdnet.model.logic.utils.SimilarityUtils;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class GFDnetVoronoiThread extends Thread{
    private final GFDnetVoronoiMetaThread metaThread;  
    private final Representation centralNode;   
    private final List<Representation> selectedRepresentations;
    private final Graph<GeneInput> network;
    private final int version;
    
    public GFDnetVoronoiThread(GFDnetVoronoiMetaThread metaThread, Representation centralNode,
            Graph<GeneInput> network, int version) {
        super();
        this.metaThread = metaThread;
        Thread.currentThread().setPriority(Thread.NORM_PRIORITY+1);
        this.centralNode = centralNode;
        this.network = network;
        this.version = version;
        this.selectedRepresentations = new LinkedList<Representation>();
        
    }
    
    @Override
    public void run(){
        try {
            BigDecimal similarity = getClusterSimilarity(network, centralNode, version);
            metaThread.compareSolution(similarity, selectedRepresentations, centralNode);
            metaThread.notifyThreadCompleted();
        } catch (Throwable ex) {
            Logger.getLogger(GFDnetVoronoiThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /*
     * Search the closest representation of each gene to the central node which will
     * provide a solution candidate
     * 
     * @return the dissimilarity of the cluster formed by the closes representations
     */
    private  BigDecimal getClusterSimilarity(Graph<GeneInput> network, Representation centralNode, int version) { 
        for(GeneInput gene : network.getNodes()) {
            List<Representation> geneRepresentations = gene.getRepresentations();
            Representation representation = getClosestRepresentation(centralNode, geneRepresentations);
            selectedRepresentations.add(representation);
        }
        return GFDnetSimilarityUtils.getSimilarity(network, selectedRepresentations, version);
    }
    
    private  Representation getClosestRepresentation(Representation centralNode, 
            List<Representation> representations) {
        BigDecimal bestSimilarity=new BigDecimal(Float.MAX_VALUE);
        Representation closestRrepresentation=null;
        
        for(Representation representation : representations){
            BigDecimal currentSimilarity = SimilarityUtils.getSimilarity(representation, centralNode);
            if(currentSimilarity.compareTo(bestSimilarity)<0){
                bestSimilarity = currentSimilarity;
                closestRrepresentation = representation;
            }
        }
        return closestRrepresentation;
    }
}