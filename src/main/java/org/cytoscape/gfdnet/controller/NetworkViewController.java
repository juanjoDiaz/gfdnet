package org.cytoscape.gfdnet.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.cytoscape.gfdnet.controller.utils.OSGiManager;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public final class NetworkViewController {
    private CyNetworkView networkView;
    
    // Store the hiden elements and theirs positions so they can be restored
    private List<CyNode> hiddenNodes = new LinkedList<CyNode>();
    private List<CyEdge> hiddenEdges = new LinkedList<CyEdge>();
    private Map<Long, Double> xPosition = new HashMap<Long, Double>();
    private Map<Long, Double> yPosition = new HashMap<Long, Double>();
    
    public NetworkViewController() throws Exception{
        networkView = OSGiManager.getCyApplicationManager().getCurrentNetworkView();
        if (networkView == null){
            throw new Exception("A valid network view should be loaded.");
        }
    }
    
    public void dispose(){
        restoreNetwork();
    }
             
    public void hideNodesNotInList(List<String> nodes){
        CyNetwork network = networkView.getModel();
        List<CyNode> nodesView = network.getNodeList();
        for (CyNode node : nodesView){
            boolean hide = true;
            for (String gen : nodes){
                if (gen.equals(network.getRow(node).get(CyNetwork.NAME, String.class))){
                    hide = false;
                    break;
                }
            }
            if(hide){
                hideNode(node);
            }
        }
    }
    
    public void hideNode(CyNode node){
        Long nodeIndex = node.getSUID();
        // Store the hidden node and its position
        hiddenNodes.add(node);
        View<CyNode> nodeView = networkView.getNodeView(node);
        xPosition.put(nodeIndex, nodeView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION));
        yPosition.put(nodeIndex, nodeView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION));
        // Store the hidden edges
        Iterable<CyEdge> edges = networkView.getModel().getAdjacentEdgeIterable(node, CyEdge.Type.ANY);
        for (CyEdge edge : edges){
            if(!hiddenEdges.contains(edge)){
                hiddenEdges.add(edge);
                View<CyEdge> edgeView = networkView.getEdgeView(edge);
                edgeView.setVisualProperty(BasicVisualLexicon.NODE_VISIBLE, false);
            }
        }
        nodeView.setVisualProperty(BasicVisualLexicon.NODE_VISIBLE, false);
    }
    
    public void restoreNetwork(){
        // Restore the nodes and the edges
        for (CyNode node: hiddenNodes){
            Long nodeIndex = node.getSUID();
            View<CyNode> nodeView = networkView.getNodeView(node);
            nodeView.setVisualProperty(BasicVisualLexicon.NODE_VISIBLE, true);
            nodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, xPosition.get(nodeIndex));
            nodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, yPosition.get(nodeIndex));
        }
        for (CyEdge edge : hiddenEdges)
        {
            networkView.getEdgeView(edge).setVisualProperty(BasicVisualLexicon.EDGE_VISIBLE, true);
        }
        // Restore the visual style
//        TODO
//        VisualMappingManager vmmServiceRef = getService(VisualMappingManager.class);
//        VisualStyle vi_style = vmm.getVisualStyle(networkView);
//        vi_style.apply(networkView);
//        networkView.updateView();
        hiddenNodes = new LinkedList<CyNode>();
        xPosition = new HashMap<Long, Double>();
        yPosition = new HashMap<Long, Double>();
        hiddenEdges = new LinkedList<CyEdge>();
    }
            
    public void selectEdges(String n1, String n2){
        CyNetwork network = networkView.getModel();
        CyTable nodeTable = network.getDefaultNodeTable();
        List<CyEdge> edges = network.getEdgeList();
        
        for(CyRow row : nodeTable.getAllRows()){
            row.set(CyNetwork.SELECTED, false);
        }
        for(CyEdge edge : edges){
            if((network.getRow(edge.getSource()).get(CyNetwork.NAME, String.class).equals(n1) && network.getRow(edge.getTarget()).get(CyNetwork.NAME, String.class).equals(n2)) ||
                    (network.getRow(edge.getSource()).get(CyNetwork.NAME, String.class).equals(n2) && network.getRow(edge.getTarget()).get(CyNetwork.NAME, String.class).equals(n1))){
                network.getRow(edge).set(CyNetwork.SELECTED, true);
            }
            else{
                network.getRow(edge).set(CyNetwork.SELECTED, false);
            }
        }
        networkView.updateView();
    }

    public void selectNode(String nodeName){
        CyNetwork network = networkView.getModel();
        CyTable nodeTable = network.getDefaultNodeTable();
        CyTable edgeTable = network.getDefaultEdgeTable();
        for (CyRow row : edgeTable.getAllRows()) {
            row.set(CyNetwork.SELECTED, false);
        }
        for(CyRow row : nodeTable.getAllRows()){
            if(row.get(CyNetwork.NAME, String.class).equals(nodeName)){
                row.set(CyNetwork.SELECTED, true);
            }
            else{
                row.set(CyNetwork.SELECTED, false);
            }
        }
        networkView.updateView();
    }
    
    public List<String> getSelectedElements(){
        CyNetwork network = networkView.getModel();
        List<CyNode> nodes = CyTableUtil.getNodesInState(network,"selected",true);
        List<CyEdge> edges = CyTableUtil.getEdgesInState(network,"selected",true);
        if(nodes.size() == 1 && edges.isEmpty()){
            return Arrays.asList(network.getRow(nodes.get(0)).get(CyNetwork.NAME, String.class));
        } 
        else if (nodes.isEmpty() && edges.size() == 1){
            CyEdge edge = edges.get(0);
            String n1 = network.getRow(edge.getSource()).get(CyNetwork.NAME, String.class);
            String n2 = network.getRow(edge.getTarget()).get(CyNetwork.NAME, String.class);
            return Arrays.asList(n1, n2);
        }
        return null;
    }

    public CyNetworkView getNetworkView(){
        return networkView;
    }
}