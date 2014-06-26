package org.cytoscape.gfdnet.model.logic.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Stack;
import org.cytoscape.gfdnet.model.businessobjects.Representation;
import org.cytoscape.gfdnet.model.businessobjects.go.GoTerm;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class SimilarityUtils {

    /**
     * Calculates the specificity of a gene set being each gene represented by a representation.
     * This specificity is calculated by averaging the dissimilarity of each pair of gene.
     * 
     * @param representations List of Representation
     * @return The specificity of the representations set
     */
    public static BigDecimal getSimilarity(List<Representation> representations) {
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
                    similarity = similarity.add(getSimilarity(rep1, rep2));
                    cont++;
                }
            }
            return similarity.divide(new BigDecimal(cont),10,RoundingMode.HALF_UP);
        }
    }
    
    /**
     * Calculates the specificity of a gene set being each gene represented by a representation.
     * This specificity is calculated by averaging the dissimilarity of each pair of gene.
     * 
     * @param representations List of Representation
     * @return The specificity of the representations set
     */
    public static BigDecimal getSimilarity(Representation rep1, Representation rep2) {
        BigDecimal distance = new BigDecimal(getDistance(rep1.getPath(), rep2.getPath()));
        BigDecimal levels = new BigDecimal(rep1.getPath().size() + rep2.getPath().size());
        return distance.divide(levels,10,RoundingMode.HALF_UP);
    }
    
    /**
     * Calculates the distance between two GO-Terms in the GO-Tree.
     * If there they share any Go-Term in their path to the root, 
     * the distance is the sum of the previous elements in each stack.
     * Otherwise, it is the sum of both of the whole paths plus 1 
     * because both share the root element (GO:000001- all).
     * 
     * @param pathGen1 Stack containing all the GO-Terms between a gene representation (a GO-Term) and the ontology root
     * @param pathGen2 Stack containing all the GO-Terms between a gene representation (a GO-Term) and the ontology root
     * @return The distance between the two GO-Term
     */
    private static int getDistance(Stack<GoTerm> pathGen1, Stack<GoTerm> pathGen2) {
        int pathGen1Size = pathGen1.size();
        int pathGen2Size = pathGen2.size();
        if (pathGen1Size > pathGen2Size) {
            Stack<GoTerm> aux = pathGen1;
            pathGen1 = pathGen2;
            pathGen2 = aux;
        }
        int cont = 0;
        for (GoTerm gt1 : pathGen1) {
            int elementPosition = pathGen2.indexOf(gt1);
            if (elementPosition != -1) {
                return cont + elementPosition + 1;
            }
            cont++;
        }
        return pathGen1Size + pathGen2Size + 1;
    }
}