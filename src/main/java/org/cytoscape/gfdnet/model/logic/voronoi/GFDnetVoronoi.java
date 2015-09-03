package org.cytoscape.gfdnet.model.logic.voronoi;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.cytoscape.gfdnet.model.businessobjects.Enums.Ontology;
import org.cytoscape.gfdnet.model.businessobjects.GFDnetResult;
import org.cytoscape.gfdnet.model.businessobjects.GOTree;
import org.cytoscape.gfdnet.model.businessobjects.GOTreeNode;
import org.cytoscape.gfdnet.model.businessobjects.GeneInput;
import org.cytoscape.gfdnet.model.businessobjects.Graph;
import org.cytoscape.gfdnet.model.businessobjects.ThreadResult;
import org.cytoscape.gfdnet.model.businessobjects.exceptions.AnalysisErrorException;
import org.cytoscape.gfdnet.model.businessobjects.utils.ProgressMonitor;
import org.cytoscape.gfdnet.model.logic.GFDnet;
import org.cytoscape.gfdnet.model.logic.utils.SimilarityUtils;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class GFDnetVoronoi extends GFDnet {
    private final int threads;
    
    private GOTreeNode selectedCentralNode;
    private Map<GeneInput, GOTreeNode> selectedAnnotations;
    private double lowestDissimilarity = 1;

    private int progress = 0;
    private int goTreeSize;
        
    public void incProgress() {
        progress++; 
        pm.setProgress((float)progress/goTreeSize);
    }
    
    public GFDnetVoronoi(ProgressMonitor pm) {
        this(Runtime.getRuntime().availableProcessors(), pm);
    }
    
    public GFDnetVoronoi(int threads, ProgressMonitor pm) {
        super(pm);
        this.threads = threads < 1 ? Runtime.getRuntime().availableProcessors() :threads;
    }

    /**
     * This method is the bottle neck of the analysis and it is use a Divide and Conquer approach
     * to overcome that. It gets the relevant section of the GO-Tree for the given network
     * and execute several MetaThreads dividing the nodes of the tree in several sets
     * 
     * @param network
     * @param ontology
     * @param version
     * @return The MethaThread that got the best result
     */
    @Override
    public GFDnetResult evaluateGenes(Graph<GeneInput> network, Ontology ontology, int version) {
        GOTree goTree = new GOTree(network.getNodes(), ontology);
        return evaluateGenes(goTree, network, ontology, version);
    }
    
    public GFDnetResult evaluateGenes(GOTree goTree, Graph<GeneInput> network, Ontology ontology, int version){
        Set<GOTreeNode> nodesIterator = goTree.getNodes();
        if (threads == 1) {
            for (GOTreeNode centralNode : nodesIterator) {
                ThreadResult result = new GFDnetVoronoiThread(network, goTree, centralNode, version).call();
                checkResult(result.centralNode, result.dissimilarity, result.selectedAnnotations);
            }
        }
        else {        
            ExecutorService executor = Executors.newFixedThreadPool(threads);
            CompletionService<ThreadResult> completionService = new ExecutorCompletionService<ThreadResult>(executor);
            int cont = 0;
            for (GOTreeNode centralNode : nodesIterator) {
                completionService.submit(new GFDnetVoronoiThread(network, goTree, centralNode, version));
                cont++;
            }
            executor.shutdown();
            for(int i = cont; i > 0; i--) {
                try {
                    if (isInterrupted) {
                        executor.shutdownNow();
                        return null;
                    }
                    ThreadResult result = completionService.take().get();
                    checkResult(result.centralNode, result.dissimilarity, result.selectedAnnotations);
                } catch (InterruptedException ex) {
                    executor.shutdownNow();
                    throw new AnalysisErrorException(ex);
                } catch (ExecutionException ex) {
                    executor.shutdownNow();
                    throw new AnalysisErrorException(ex);
                }
            }
        }

        // Set data into the network    
        int noNodes = network.getNodes().size();
        switch (version) {
            case 1:
                double similarity = 0;
                int cont = 0;
                for (int i = 0; i < noNodes; i++){
                    GeneInput g1 = network.getNode(i);
                    GOTreeNode annotation1 = selectedAnnotations.get(g1);
                    g1.selectGOTerm(annotation1.getGoTerm());
                    network.updateNodeValue(i, g1);
                    for (int j = i + 1; j < noNodes; j++){
                        if (network.getEdgeWeight(i, j) != -1){
                            GeneInput g2 = network.getNode(j);
                            GOTreeNode annotation2 = selectedAnnotations.get(g2);
                            double dissimilarity = SimilarityUtils.getSimilarity(goTree, annotation1, annotation2);
                            network.setEdgeWeight(i, j, dissimilarity);
                            similarity = similarity + dissimilarity;
                            cont++;
                        }
                    }
                }
                if (cont == 0) {
                    lowestDissimilarity = 0.5;
                } else {
                    lowestDissimilarity = similarity/cont;
                }
                break;
            case 2:
            case 3:
                for (int i = 0; i < noNodes; i++){
                    GeneInput g1 = network.getNode(i);
                    GOTreeNode annotation1 = selectedAnnotations.get(g1);
                    g1.selectGOTerm(annotation1.getGoTerm());
                    network.updateNodeValue(i, g1);
                    for (int j = i + 1; j < noNodes; j++){
                        if (network.getEdgeWeight(i, j) != -1){
                            GeneInput g2 = network.getNode(j);
                            GOTreeNode annotation2 = selectedAnnotations.get(g2);
                            double dissimilarity = SimilarityUtils.getSimilarity(goTree, annotation1, annotation2);
                            network.setEdgeWeight(i, j, dissimilarity);
                        }
                    }
                }
                break;
        }

        return new GFDnetResult(ontology, lowestDissimilarity, selectedCentralNode.getGoTerm(), network);
    }
    
    public void checkResult(GOTreeNode centralNode, double dissimilarity, Map<GeneInput, GOTreeNode> selectedAnnotations) {
        if (dissimilarity < lowestDissimilarity || (dissimilarity == lowestDissimilarity && 
                (centralNode.getDepth() > selectedCentralNode.getDepth() || (
                    centralNode.getDepth() == selectedCentralNode.getDepth() && centralNode.getGoTerm().getName().compareTo(selectedCentralNode.getGoTerm().getName()) < 0))
                )
            ) {
            this.selectedCentralNode = centralNode;
            this.lowestDissimilarity = dissimilarity;
            this.selectedAnnotations = selectedAnnotations;
        }
        incProgress();
    }
}