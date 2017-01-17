package org.cytoscape.gfdnet.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.gfdnet.controller.utils.NetworkAdapter;
import org.cytoscape.gfdnet.model.businessobjects.go.Enums.Ontology;
import org.cytoscape.gfdnet.model.businessobjects.GFDnetResult;
import org.cytoscape.gfdnet.model.businessobjects.GeneInput;
import org.cytoscape.gfdnet.model.businessobjects.Graph;
import org.cytoscape.gfdnet.model.businessobjects.go.GOTerm;
import org.cytoscape.gfdnet.model.businessobjects.go.Organism;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public final class NetworkController {
    private static CyApplicationManager applicationManager;
    private static VisualMappingManager visualMappingManager;
    
    public static final String OrganismColumn = "GFD-Net Organism";
    public static final String OntologyColumn = "GFD-Net Ontology";
    public static final String GOTermColumn = "GFD-Net GO Term";
    public static final String GOTermDescColumn = "GFD-Net GO Term Description";   
    public static final String GOTermsColumn = "GO Terms";
    public static final String DissimilairtyColumn = "GFD-Net Dissimilarity";
    public static final String Unknown = "Unkown";
    public static final String Unnanotated = "Unnanotated";
    private final CyNetwork network;
    private final CyNetworkView networkView;
    
    private Object[][] geneRows;
    private Object[][] edgeRows;
    
    public static void init(CyApplicationManager applicationManager, VisualMappingManager visualMappingManager) {
        NetworkController.applicationManager = applicationManager;
        NetworkController.visualMappingManager = visualMappingManager;
    }
    
    public NetworkController() throws InstantiationException {
        networkView = applicationManager.getCurrentNetworkView();
        if (networkView != null) {
            network = networkView.getModel();
        } else {
            network = applicationManager.getCurrentNetwork();
        }
        
        if (network == null) {
            throw new InstantiationException("No network selected.");
        }
    }

    public void dispose() {
    }
 
    public CyNetwork getCyNetwork() {
       return network;
    }
       
    public Graph<String> getGraph() {
       return NetworkAdapter.CyNetworkToGraph(network);
    }
 
    public Object[][] getGeneRows() {
       return geneRows;
    }
    
    public Object[][] getEdgesRows() {
       return edgeRows;
    }
    
    public void addGFDnetInfo(GFDnetResult result) {
        addNetworkInfo(result.getSimilarity(), result.getOrganism(), result.getOntology());
        addNodeInfo(result);
        addEdgeInfo(result.getNetwork());
        if (networkView != null) {
//            TODO Fix applyVisualStyle();
        }
    }
    
    private void addNetworkInfo(Double dissimilarity, Organism organism, Ontology ontology) {
        CyTable networkTable = network.getDefaultNetworkTable();

        try {
            networkTable.createColumn(DissimilairtyColumn, Double.class, false);
        } catch (IllegalArgumentException ex) {
            // Do nothing and just override the values in the collumn
        }
        try {
            networkTable.createColumn(OrganismColumn, String.class, false);
        } catch (IllegalArgumentException ex) {
            // Do nothing and just override the values in the collumn
        }
        try {
            networkTable.createColumn(OntologyColumn, String.class, false);
        } catch (IllegalArgumentException ex) {
            // Do nothing and just override the values in the collumn
        }

        try {
            CyRow row = networkTable.getAllRows().get(0);
            row.set(DissimilairtyColumn, dissimilarity);
            row.set(OrganismColumn, organism.toString());
            row.set(OntologyColumn, ontology.getDescription());
        } catch(Exception e) {
            // Do nothing, Cytoscape is buggy
        }
    }
    
    private void addNodeInfo(GFDnetResult result) {
        CyTable nodeTable = network.getDefaultNodeTable();
        
        try {
            nodeTable.createColumn(GOTermColumn, String.class, false);
        } catch (IllegalArgumentException ex) {
            // Do nothing and just override the values in the collumn
        }
        try {
            nodeTable.createColumn(GOTermDescColumn, String.class, false);
        } catch (IllegalArgumentException ex) {
            // Do nothing and just override the values in the collumn
        }
        try {
            nodeTable.createListColumn(GOTermsColumn, String.class, false);
        } catch (IllegalArgumentException ex) {
            // Do nothing and just override the values in the collumn
        }
        
        geneRows = new Object[result.getAnalyzedGenes().size() + result.getUnkownGenes().size() + result.getUnannotatednGenes().size()][3];
        int i = 0;
        for (GeneInput gene : result.getAnalyzedGenes()) {
            for(CyRow row : nodeTable.getAllRows()) {
                if(row.get(CyNetwork.NAME, String.class).equalsIgnoreCase(gene.getName())) {
                    row.set(GOTermColumn, gene.getSelectedGOTerm().getName());
                    row.set(GOTermDescColumn, gene.getSelectedGOTerm().getDescription());
                    
                    Set<GOTerm>  goTerms = gene.getGoTerms(result.getOntology());
                    List<String> goTermsDisplayList = new ArrayList<String>(goTerms.size());
                    for (GOTerm goTerm : goTerms) {
                        goTermsDisplayList.add(goTerm.getName());
                    }
                    row.set(GOTermsColumn, goTermsDisplayList);
                    break;
                }
            }
            
            Object[] row = {
                gene.getName(),
                gene.getSelectedGOTerm().getName(),
                gene.getSelectedGOTerm().getDescription()
            };
            geneRows[i] = row;
            i++;
        }
        for (String gene : result.getUnkownGenes()) {
            for(CyRow row : nodeTable.getAllRows()) {
                if(row.get(CyNetwork.NAME, String.class).equalsIgnoreCase(gene)) {
                    row.set(GOTermColumn, Unknown);
                    break;
                }
            }
            
            Object[] row = {
                gene,
                Unknown,
                Unknown
            };
            geneRows[i] = row;
            i++;
        }
        for (GeneInput gene : result.getUnannotatednGenes()) {
            for(CyRow row : nodeTable.getAllRows()) {
                if(row.get(CyNetwork.NAME, String.class).equalsIgnoreCase(gene.getName())) {
                    row.set(GOTermColumn, Unnanotated);
                    break;
                }
            }

            Object[] row = {
                gene.getName(),
                Unnanotated,
                Unnanotated
            };
            geneRows[i] = row;
            i++;
        }
    }
    
    private void addEdgeInfo(Graph<GeneInput> gfdNetNetwork) {
        CyTable edgeTable = network.getDefaultEdgeTable();
        try {
            edgeTable.createColumn(DissimilairtyColumn, Double.class, false);
        } catch (IllegalArgumentException ex) {
            // Do nothing and just override the values in the collumn
        }
        
        List<CyEdge> edges = network.getEdgeList();
        int noNodes = gfdNetNetwork.getNodes().size();
        List<Object[]> relationshipList = new ArrayList<Object[]>();
        for (int i = 0; i < noNodes; i++) {
            String n1 = gfdNetNetwork.getNode(i).getName();
            for (int j = i + 1; j < noNodes; j++) {
                String n2 = gfdNetNetwork.getNode(j).getName();
                Double weight = gfdNetNetwork.getEdgeWeight(i, j);
                if (weight != -1) {
                    for(CyEdge edge : edges) {
                        String sourceName = network.getRow(edge.getSource()).get(CyNetwork.NAME, String.class);
                        String targetName = network.getRow(edge.getTarget()).get(CyNetwork.NAME, String.class);
                        
                        if((sourceName.equalsIgnoreCase(n1) && targetName.equalsIgnoreCase(n2)) ||
                                (sourceName.equalsIgnoreCase(n2) && targetName.equalsIgnoreCase(n1))) {
                            edgeTable.getRow(edge.getSUID()).set(DissimilairtyColumn, weight);
                            Object[] row = new Object[3];
                            row[0] = sourceName;
                            row[1] = targetName;
                            row[2] = weight;
                            relationshipList.add(row);
                            break;
                        }
                    }
                }
            }
            edgeRows = relationshipList.toArray(new Object[relationshipList.size()][3]);
        }
    }

    private void applyVisualStyle() {
        for (VisualStyle style : visualMappingManager.getAllVisualStyles()) {
            if (style.getTitle().equals("GFD-Net Style")) {
                style.apply(networkView);
                break;
            }
        }
    }
    
    public void selectNode(String nodeName) {
        CyTable nodeTable = network.getDefaultNodeTable();
        CyTable edgeTable = network.getDefaultEdgeTable();
        
        for (CyRow row : edgeTable.getAllRows()) {
            row.set(CyNetwork.SELECTED, false);
        }
        
        for(CyRow row : nodeTable.getAllRows()) {
            if(row.get(CyNetwork.NAME, String.class).equalsIgnoreCase(nodeName)) {
                row.set(CyNetwork.SELECTED, true);
            }
            else{
                row.set(CyNetwork.SELECTED, false);
            }
        }
        
        if (networkView != null) {
            networkView.updateView();
            applicationManager.setCurrentNetworkView(networkView);
        }
    }

    public void selectEdges(String n1, String n2) {
        CyTable nodeTable = network.getDefaultNodeTable();
        List<CyEdge> edges = network.getEdgeList();
        
        for(CyRow row : nodeTable.getAllRows()) {
            row.set(CyNetwork.SELECTED, false);
        }
        
        for(CyEdge edge : edges) {
            if((network.getRow(edge.getSource()).get(CyNetwork.NAME, String.class).equalsIgnoreCase(n1) && network.getRow(edge.getTarget()).get(CyNetwork.NAME, String.class).equalsIgnoreCase(n2)) ||
                    (network.getRow(edge.getSource()).get(CyNetwork.NAME, String.class).equalsIgnoreCase(n2) && network.getRow(edge.getTarget()).get(CyNetwork.NAME, String.class).equalsIgnoreCase(n1))) {
                network.getRow(edge).set(CyNetwork.SELECTED, true);
            }
            else{
                network.getRow(edge).set(CyNetwork.SELECTED, false);
            }
        }
        
        if (networkView != null) {
            networkView.updateView();
            applicationManager.setCurrentNetworkView(networkView);
        }
    }
    
    public List<String> getSelectedElements() {
        List<CyNode> nodes = CyTableUtil.getNodesInState(network, CyNetwork.SELECTED, true);
        List<CyEdge> edges = CyTableUtil.getEdgesInState(network, CyNetwork.SELECTED, true);
        if(nodes.size() == 1 && edges.isEmpty()) {
            CyNode node = nodes.get(0);
            String nodeGOTerm = network.getRow(node).get(GOTermColumn, String.class);
            
            if (Unknown.equals(nodeGOTerm) || Unnanotated.equals(nodeGOTerm)) {
               return null;
            }
            
            return Arrays.asList(network.getRow(node).get(CyNetwork.NAME, String.class));
        } 
        else if (nodes.isEmpty() && edges.size() == 1) {
            CyEdge edge = edges.get(0);
            CyRow sourceRow = network.getRow(edge.getSource());
            CyRow targetRow = network.getRow(edge.getTarget());
            String sourceGOTerm = sourceRow.get(GOTermColumn, String.class);
            String targetGOTerm = targetRow.get(GOTermColumn, String.class);
            
            if (Unknown.equals(sourceGOTerm) || Unnanotated.equals(sourceGOTerm)
                    || Unknown.equals(targetGOTerm) || Unnanotated.equals(targetGOTerm)) {
               return null;
            }
            
            return Arrays.asList(sourceRow.get(CyNetwork.NAME, String.class), targetRow.get(CyNetwork.NAME, String.class));
        }
        return null;
    }
}