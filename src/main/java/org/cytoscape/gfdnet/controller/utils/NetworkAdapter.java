package org.cytoscape.gfdnet.controller.utils;

import java.util.List;
import org.cytoscape.gfdnet.model.businessobjects.Graph;
import org.cytoscape.gfdnet.model.businessobjects.GraphImpl;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class NetworkAdapter {
    public static Graph CyNetworkToGraph(CyNetwork network) {
        List<CyNode> nodes = network.getNodeList();
        List<CyEdge> edges = network.getEdgeList();
        if (nodes.isEmpty() || edges.isEmpty()) {
            throw new IllegalArgumentException("The current network view seems to be empty."); 
        }
        Graph<String> g = new GraphImpl<String>(nodes.size());
        for(int i = 0; i < nodes.size(); i++) {
            g.updateNodeValue(i, network.getRow(nodes.get(i)).get(CyNetwork.NAME, String.class));
        }
        for(CyEdge edge : edges) {
            int i = nodes.indexOf(edge.getSource());
            int j = nodes.indexOf(edge.getTarget());
            if (i != j && g.getEdgeWeight(i, j) == -1) {
                g.addEdge(i, j, 1);
            }
        }
        return g;
    }
}