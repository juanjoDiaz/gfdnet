package org.cytoscape.gfdnet.model.logic.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.cytoscape.gfdnet.model.businessobjects.GOTree;
import org.cytoscape.gfdnet.model.businessobjects.GOTreeNode;
import org.cytoscape.gfdnet.model.businessobjects.GeneInput;
import org.cytoscape.gfdnet.model.businessobjects.Graph;

/**
 *
 * @author Juan José Díaz Montaña
 */
public class SimilarityUtils { 
    public static double getSimilarity(Graph<GeneInput> network, GOTree goTree, Map<GeneInput, GOTreeNode> selectedAnnotations, int version) {
        switch (version) {
            case 1:
                return getSimilarity(goTree, selectedAnnotations);
            case 2:
                return getSimilarityNoPenalizing(network, goTree, selectedAnnotations);
            default:
                return getSimilarityPenalizing(network, goTree, selectedAnnotations);
        }
    }
      
    private static double getSimilarityNoPenalizing(Graph<GeneInput> network, GOTree goTree, Map<GeneInput, GOTreeNode> selectedAnnotations) {
        int selectedAnnotationsSize = selectedAnnotations.size();
        if(selectedAnnotationsSize <= 1) {
            return 0.5;
        } else {
            int cont = 0;
            double similarity = 0;      
            List<Map.Entry<GeneInput, GOTreeNode>> entries = new ArrayList<Map.Entry<GeneInput, GOTreeNode>>(selectedAnnotations.entrySet());
            for (int i = 0; i < selectedAnnotationsSize; i++) {
                Map.Entry<GeneInput, GOTreeNode> entry1 = entries.get(i);
                GeneInput gene1 = entry1.getKey();
                GOTreeNode annotation1 = entry1.getValue();
                for (int j = i + 1; j < selectedAnnotationsSize; j++) {
                    Map.Entry<GeneInput, GOTreeNode> entry2 = entries.get(j);
                    GeneInput gene2 = entry2.getKey();
                    GOTreeNode annotation2 = entry2.getValue();
                    if (network.areConnected(gene1.getNodeId(), gene2.getNodeId())) {
                        similarity = similarity + getSimilarity(goTree, annotation1, annotation2);
                        cont++;
                    }
                }
            }
            
            if (cont > 0) {
                return similarity/cont;
            } else {
                return 0.5;
            }
        }
    }
    
    private static double getSimilarityPenalizing(Graph<GeneInput> network, GOTree goTree, Map<GeneInput, GOTreeNode> selectedAnnotations) {
        int selectedAnnotationsSize = selectedAnnotations.size();
        if(selectedAnnotationsSize <= 1) {
            return 0.5;
        } else {
            int cont = 0;
            double similarity = 0;
            List<Map.Entry<GeneInput, GOTreeNode>> entries = new ArrayList<Map.Entry<GeneInput, GOTreeNode>>(selectedAnnotations.entrySet());
            for (int i = 0; i < selectedAnnotationsSize; i++) {
                Map.Entry<GeneInput, GOTreeNode> entry1 = entries.get(i);
                GeneInput gene1 = entry1.getKey();
                GOTreeNode annotation1 = entry1.getValue();
                for (int j = i + 1; j < selectedAnnotationsSize; j++) {
                    Map.Entry<GeneInput, GOTreeNode> entry2 = entries.get(j);
                    GeneInput gene2 = entry2.getKey();
                    GOTreeNode annotation2 = entry2.getValue();               
                    double nodesSimilarity = getSimilarity(goTree, annotation1, annotation2);
 
                    if (!network.areConnected(gene1.getNodeId(), gene2.getNodeId())){
                        nodesSimilarity = 1 - nodesSimilarity;
                    }
                    
                    similarity = similarity + nodesSimilarity;
                    cont++;
                }
            }

            if (cont > 0) {
                return similarity/cont;
            } else {
                return 0.5;
            }
        }
    }
    
    /**
     * Calculates the specificity of a gene set being each gene represented by a representation.
     * This specificity is calculated by averaging the dissimilarity of each pair of gene.
     * 
     * @param goTree
     * @param selectedAnnotations
     * @return The specificity of the representations set
     */
    public static double getSimilarity(GOTree goTree, Map<GeneInput, GOTreeNode> selectedAnnotations) {
        int selectedAnnotationsSize = selectedAnnotations.size();
        if(selectedAnnotationsSize <= 1) {
            return 0.5;
        } else {
            int cont = 0;
            double similarity = 0;
            List<GOTreeNode> annotations = new ArrayList<GOTreeNode>(selectedAnnotations.values());
            for (int i = 0; i < selectedAnnotationsSize; i++) {
                GOTreeNode annotation1 = annotations.get(i);
                for (int j = i + 1; j < selectedAnnotationsSize; j++) {
                    GOTreeNode annotation2 = annotations.get(j); 
                    similarity = similarity + getSimilarity(goTree, annotation1, annotation2);
                    cont++;
                }
            }
            return similarity / cont;
        }
    }
    
    public static double getSimilarity(GOTree goTree, GOTreeNode annotation1, GOTreeNode annotation2) {
        GOTreeNode lca = goTree.getLCA(annotation1, annotation2);
        int distance = lca.getDistanceToAnnotation(annotation1) + lca.getDistanceToAnnotation(annotation2) + 1;
        return (double)distance/(2*lca.getDepth() + distance + 1); 
    }
}