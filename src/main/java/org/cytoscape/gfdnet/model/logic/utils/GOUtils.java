package org.cytoscape.gfdnet.model.logic.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cytoscape.gfdnet.model.businessobjects.GeneInput;
import org.cytoscape.gfdnet.model.businessobjects.Graph;
import org.cytoscape.gfdnet.model.businessobjects.GraphImpl;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class GOUtils {
    /**
     * Receives a graph containing gene names as nodes and return a equivalent graph containing genes
     * 
     * @param network
     * @param genes
     * @return Graph formed by the genes retrieved from GO
     */
    public static Graph<GeneInput> getGenInputNetwork(Graph<String> network, List<GeneInput> genes) {
        List<String> nodes = network.getNodes();
        Map<String, Integer> nodesMap = new HashMap<String, Integer>(nodes.size());
        int i = 0;
        for (String nodeName : nodes) {
            nodesMap.put(nodeName.toUpperCase(), i);
            i++;
        }

        Graph<GeneInput> geneInputsNetwork = new GraphImpl<GeneInput>(genes.size());
        int genesSize = genes.size();
        for(i = 0; i < genesSize; i++) {
            GeneInput gene1 = genes.get(i);
            int posI = nodesMap.get(gene1.getName());
            gene1.setNodeId(i);
            geneInputsNetwork.updateNodeValue(i, gene1);
            for(int j = i + 1; j < genesSize; j++) {
                int posJ = nodesMap.get(genes.get(j).getName());
                
                geneInputsNetwork.addEdge(j, i, network.getEdgeWeight(posJ, posI));
            }
        }
        return geneInputsNetwork; 
    }
}