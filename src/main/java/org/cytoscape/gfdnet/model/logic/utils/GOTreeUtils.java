package org.cytoscape.gfdnet.model.logic.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import org.cytoscape.gfdnet.model.businessobjects.GeneInput;
import org.cytoscape.gfdnet.model.businessobjects.Graph;
import org.cytoscape.gfdnet.model.businessobjects.Representation;
import org.cytoscape.gfdnet.model.businessobjects.RepresentationNode;
import org.cytoscape.gfdnet.model.businessobjects.go.GoTerm;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class GOTreeUtils {
    
    /**
     * Get all the distinct representations relevant for the analysis of a specific network in an ontology
     * 
     * @param representationsList List of lists ofRepresentation
     * @return List of all the distinct representations in representationsList
     */
    public static LinkedList<Representation> getNodes(Graph<GeneInput> network, String ontology) {
        Set<Representation> rNodes = new HashSet();
        for(GeneInput gene : network.getNodes())
        {
            rNodes.addAll(GOTreeUtils.getRepresentationsNodes(gene.getRepresentations(ontology)));
        }
        return new LinkedList<Representation>(rNodes);
    }
    
    /**
     * Get all the existing nodes for all the given representations and generate a 
     * RepresentationNode object for each of them
     * 
     * @param representations List of Representation
     * @return List of all the nodes related to each of the given representation
     */
    private static List<RepresentationNode> getRepresentationsNodes(List<Representation> representations){
        List<RepresentationNode> rNodes = new LinkedList();
        for(Representation representation : representations) {
            rNodes.addAll(getRepresentationNodes(representation));
        }
        return rNodes;
    }

    /**
     * Get all the existing nodes for a given representations and generate a 
     * RepresentationNode object for each of them
     * 
     * @param representacion Representation
     * @return List of all the nodes related to the given representation
     */
    private static List<RepresentationNode> getRepresentationNodes(Representation representation){
        Stack<GoTerm> path = representation.getPath();
        int pathSize = path.size();
        List<RepresentationNode> rNodes = new ArrayList<RepresentationNode>(pathSize);
        for(int i=0; i < pathSize; i++) {
            RepresentationNode rNode = new RepresentationNode(path.get(i), representation.getGen());
            rNode.setPath(path.subList(i, pathSize));
            rNodes.add(rNode);
        }
        return rNodes;
    }
}