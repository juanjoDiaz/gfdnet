package org.cytoscape.gfdnet.model.logic.utils;

import java.util.List;
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
    public static Graph<GeneInput> getGenInputNetwork(Graph<String> network, List<GeneInput> genes){
        List<String> nodes = network.getNodes();
        Graph<GeneInput> geneInputsNetwork = new GraphImpl<GeneInput>(genes.size());
        int genesSize = genes.size();
        for(int i = 0; i < genesSize; i++){
            GeneInput gene1 = genes.get(i);
            gene1.setNodeId(i); 
            String gene1Name = gene1.getName();
            geneInputsNetwork.updateNodeValue(i, gene1);
            for(int j = 0; j < i; j++){
                String gene2Name = genes.get(j).getName();

                int posI = -1;
                int posJ = -1;
                int cont = 0;
                for (String node : nodes) {
                    if (node.equalsIgnoreCase(gene1Name)){
                        posI = cont;
                    }
                    else if (node.equalsIgnoreCase(gene2Name)){
                        posJ = cont;
                    }
                    if(posI != -1 && posJ != -1){
                        break;
                    }
                    cont++;
                }
                geneInputsNetwork.addEdge(j, i, network.getEdgeWeight(posJ, posI));
            }
        }
        return geneInputsNetwork; 
    }
}