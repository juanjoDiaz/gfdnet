package org.cytoscape.gfdnet.model.logic.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.cytoscape.gfdnet.model.businessobjects.GeneInput;
import org.cytoscape.gfdnet.model.businessobjects.Graph;
import org.cytoscape.gfdnet.model.businessobjects.Representation;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class GFDnetSimilarityUtils extends SimilarityUtils{
    
    //TODO convert int version in boolean penalizing
    public static BigDecimal getSimilarity(Graph<GeneInput> network, List<Representation> representations, int version) {
         switch (version) {
             case 1:
                 return SimilarityUtils.getSimilarity(representations);
             case 2:
                 return getSimilarityNoPenalizing(network, representations);
             default:
                 return getSimilarityPenalizing(network, representations);
         }
     }
      
    private static BigDecimal getSimilarityNoPenalizing(Graph<GeneInput> network, List<Representation> representations) {
        int representationsSize = representations.size();
        if(representationsSize <= 1) {
            return BigDecimal.valueOf(0.5);
        } else {
            int cont = 0;
            BigDecimal similarity = new BigDecimal(0);            
            for (int i = 0; i < representationsSize; i++) {
                Representation rep1 = representations.get(i);
                for (int j = i + 1; j < representationsSize; j++) {
                    Representation rep2 = representations.get(j);
                    if (network.areConnected(rep1.getGen().getNodeId(), rep2.getGen().getNodeId())){
                        similarity = similarity.add(SimilarityUtils.getSimilarity(rep1, rep2));
                        cont++;
                    }
                }
            }
            if (cont > 0) {
                return similarity.divide(new BigDecimal(cont),10,RoundingMode.HALF_UP);
            } else {
                return BigDecimal.valueOf(0.5);
            }
        }
    }
    
    private static BigDecimal getSimilarityPenalizing(Graph<GeneInput> network, List<Representation> representations) {
        int representationsSize = representations.size();
        if(representationsSize <= 1) {
            return BigDecimal.valueOf(0.5);
        } else {
            int cont = 0;
            BigDecimal similarity = new BigDecimal(0);        
            for (int i = 0; i < representationsSize; i++) {
                Representation rep1 = representations.get(i);
                for (int j = i + 1; j < representationsSize; j++) {
                    Representation rep2 = representations.get(j);
                    BigDecimal nodesSimilarity = SimilarityUtils.getSimilarity(rep1, rep2);
                    if (network.areConnected(rep1.getGen().getNodeId(), rep2.getGen().getNodeId())){
                        similarity = similarity.add(nodesSimilarity);
                    }
                    else {
                        similarity = similarity.add(BigDecimal.ONE.subtract(nodesSimilarity));
                    }
                    cont++;
                }
            }
            if (cont > 0) {
                return similarity.divide(new BigDecimal(cont),10,RoundingMode.HALF_UP);
            } else {
                return BigDecimal.valueOf(0.5);
            }
        }
    }
      
    /**
     * Calculates the dissimilarity of a network that has already selected a representation for each gene.
     * This specificity is calculated by averaging the dissimilarity between each pair of genes connected by an edge.
     * 
     * @param representations List of Representation
     * @return The specificity of the representations set
     */
    public static BigDecimal getSimilarity(Graph<GeneInput> network) {
        int noNodes = network.getNodes().size();
        if (noNodes == 0){
            return BigDecimal.valueOf(0.5);
        } else if (noNodes == 1) {
            return BigDecimal.ZERO;  
        } else {
            BigDecimal r;
            BigDecimal sum = BigDecimal.ZERO;
            int cont = 0;
            for (int i = 0; i< noNodes; i++){
                GeneInput g1 = network.getNode(i);
                network.updateNodeValue(i, g1);
                for (int j = i+1; j<noNodes; j++){
                    if (!network.getEdgeWeight(i, j).equals(new BigDecimal(-1))){
                        GeneInput g2 = network.getNode(j);
                        Representation r1 = g1.getRepresentationSelected();
                        Representation r2 = g2.getRepresentationSelected();
                        r = SimilarityUtils.getSimilarity(r1, r2);
                        network.setEdgeWeight(i, j, r);
                        sum = sum.add(r);
                        cont++;
                    }
                }
            }
            if (cont > 0) {
                return sum.divide(new BigDecimal(cont),10,RoundingMode.HALF_UP);
            } else {
                return BigDecimal.valueOf(0.5);
            }
        }
    }
}