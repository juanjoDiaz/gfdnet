package org.cytoscape.gfdnet.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.cytoscape.gfdnet.controller.utils.OSGiManager;
import org.cytoscape.gfdnet.model.businessobjects.GeneInput;
import org.cytoscape.gfdnet.model.businessobjects.Graph;
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
public final class NetworkController {
    private final String GOTermColumn = "GO-Term";
    private final String GFDNetColumn = "GFD-Net";
    private final CyNetwork network;
    private final CyNetworkView networkView;
    
    // Store the hiden elements and theirs positions so they can be restored
    private List<CyNode> hiddenNodes = new LinkedList<CyNode>();
    private List<CyEdge> hiddenEdges = new LinkedList<CyEdge>();
    private Map<Long, Double> xPosition = new HashMap<Long, Double>();
    private Map<Long, Double> yPosition = new HashMap<Long, Double>();
    
    public NetworkController() throws InstantiationException {
        network = OSGiManager.getCyApplicationManager().getCurrentNetwork();
        networkView = OSGiManager.getCyApplicationManager().getCurrentNetworkView();
        if (networkView == null){
            throw new InstantiationException("A valid network view should be loaded.");
        }
    }
    
    public void dispose(){
        restoreNetwork();
    }
    
    public void addGFDnetInfo(Graph<GeneInput> gfdNetNetwork) {
        addSelectedGoTerms(gfdNetNetwork);
        addEdgeWeights(gfdNetNetwork);
    }
    
    public void clearGFDnetInfo() {
        if (network.getDefaultNodeTable().getColumn(GOTermColumn) != null) {
            network.getDefaultNodeTable().deleteColumn(GOTermColumn);
        }
        if (network.getDefaultEdgeTable().getColumn(GFDNetColumn) != null) {
            network.getDefaultEdgeTable().deleteColumn(GFDNetColumn);
        }
    }
    
    private void addSelectedGoTerms(Graph<GeneInput> gfdNetNetwork) {
        CyTable nodeTable = network.getDefaultNodeTable();
        try {
            nodeTable.createColumn(GOTermColumn, String.class, false);
        } catch (IllegalArgumentException ex) {
            // Do nothing and just override the values in the collumn
        }
        
        for (GeneInput gene : gfdNetNetwork.getNodes()){
            for(CyRow row : nodeTable.getAllRows()){
                if(row.get(CyNetwork.NAME, String.class).equals(gene.getName())){
                    row.set(GOTermColumn, gene.getSelectedGOTerm().getName());
                    break;
                }
            }
        }
    }
    
    private void addEdgeWeights(Graph<GeneInput> gfdNetNetwork) {
        CyTable edgeTable = network.getDefaultEdgeTable();
        try {
            edgeTable.createColumn(GFDNetColumn, Double.class, false);
        } catch (IllegalArgumentException ex) {
            // Do nothing and just override the values in the collumn
        }
        
        List<CyEdge> edges = network.getEdgeList();
        int noNodes = gfdNetNetwork.getNodes().size();
        for (int i = 0; i < noNodes; i++) {
            String n1 = gfdNetNetwork.getNode(i).getName();
            for (int j = i + 1; j < noNodes; j++) {
                String n2 = gfdNetNetwork.getNode(j).getName();
                Double weight = gfdNetNetwork.getEdgeWeight(i, j);
                if (weight != -1) {
                    for(CyEdge edge : edges){
                        if((network.getRow(edge.getSource()).get(CyNetwork.NAME, String.class).equals(n1) && network.getRow(edge.getTarget()).get(CyNetwork.NAME, String.class).equals(n2)) ||
                                (network.getRow(edge.getSource()).get(CyNetwork.NAME, String.class).equals(n2) && network.getRow(edge.getTarget()).get(CyNetwork.NAME, String.class).equals(n1))){
                            edgeTable.getRow(edge.getSUID()).set(GFDNetColumn, weight);
                            break;
                        }
                    }
                }
            }
        }
    }
     
    public void hideNodesNotInList(List<String> nodes){
        List<CyNode> nodesView = network.getNodeList();
        for (CyNode node : nodesView){
            boolean hide = true;
            for (String gene : nodes){
                if (gene.equals(network.getRow(node).get(CyNetwork.NAME, String.class))){
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