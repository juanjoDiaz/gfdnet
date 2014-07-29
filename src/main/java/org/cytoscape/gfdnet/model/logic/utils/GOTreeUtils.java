package org.cytoscape.gfdnet.model.logic.utils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import org.cytoscape.gfdnet.model.businessobjects.GOTreeNode;
import org.cytoscape.gfdnet.model.businessobjects.GeneInput;
import org.cytoscape.gfdnet.model.businessobjects.Graph;
import org.cytoscape.gfdnet.model.businessobjects.Representation;
import org.cytoscape.gfdnet.model.businessobjects.go.GOTerm;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class GOTreeUtils {

    /**
     * Get all the distinct representations relevant for the analysis of a specific network in an ontology
     * 
     * @param network
     * @param ontology
     * @return List of all the distinct representations in representationsList
     */
    public static LinkedList<GOTreeNode> getNodes(Graph<GeneInput> network, String ontology) {
        Set<GOTreeNode> rNodes = new HashSet();
        for(GeneInput gene : network.getNodes())
        {
            List<Representation> representations = gene.getRepresentations(ontology);
            for(Representation representation : representations) {
                Stack<GOTerm> path = representation.getPath();
                int pathSize = path.size();
                for(int i=0; i < pathSize; i++) {
                    GOTreeNode rNode = new GOTreeNode(path.get(i), path.subList(i, pathSize));
                    rNodes.add(rNode);
                }
            }
        }
        return new LinkedList<GOTreeNode>(rNodes);
    }
}