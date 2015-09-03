package org.cytoscape.gfdnet.model.logic.voronoi;

import java.util.Map;
import java.util.concurrent.Callable;
import org.cytoscape.gfdnet.model.businessobjects.GOTree;
import org.cytoscape.gfdnet.model.businessobjects.GOTreeNode;
import org.cytoscape.gfdnet.model.businessobjects.GeneInput;
import org.cytoscape.gfdnet.model.businessobjects.Graph;
import org.cytoscape.gfdnet.model.businessobjects.ThreadResult;
import org.cytoscape.gfdnet.model.logic.utils.SimilarityUtils;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class GFDnetVoronoiThread implements Callable<ThreadResult> {
    private final GOTree goTree;
    private final GOTreeNode centralNode;
    private final Graph<GeneInput> network;
    private final int version;
    
    public GFDnetVoronoiThread(Graph<GeneInput> network, GOTree goTree, GOTreeNode centralNode, int version) {
        this.goTree = goTree;
        this.centralNode = centralNode;
        this.network = network;
        this.version = version;
    }
    
    @Override
    public ThreadResult call() {
        Map<GeneInput, GOTreeNode> selectedAnnotations = goTree.getClosestAnnotationsToLCA(centralNode);
        double dissimilarity = SimilarityUtils.getSimilarity(network, goTree, selectedAnnotations, version);

        return new ThreadResult(centralNode, dissimilarity, selectedAnnotations);
    }
}