package org.cytoscape.gfdnet.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cytoscape.gfdnet.controller.listener.ClickOnViewListener;
import org.cytoscape.gfdnet.controller.utils.CySwing;
import org.cytoscape.gfdnet.model.businessobjects.GFDnetResult;
import org.cytoscape.gfdnet.model.businessobjects.GeneInput;
import org.cytoscape.gfdnet.view.resultPanel.MainResultsView;
import org.cytoscape.model.CyNetwork;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class ResultPanelController {
    public static final Map<CyNetwork, ResultPanelController> panels = new HashMap<CyNetwork, ResultPanelController>();
    
    private final NetworkController network;
    private GFDnetResult result;
    private MainResultsView rv;
    
    @SuppressWarnings("LeakingThisInConstructor")
    public ResultPanelController(NetworkController network, GFDnetResult result) {
        this.network = network;
        this.result = result;

        network.addGFDnetInfo(result);
        
        rv = new MainResultsView(this);
        CySwing.addPanel(rv);
        
        if(panels.containsKey(network.getCyNetwork())) {
            CySwing.removePanel(panels.get(network.getCyNetwork()).rv);
        }
        
        panels.put(network.getCyNetwork(), this);
    }
    
    public void dispose() {
        result = null; 
        
        CySwing.removePanel(rv);
        rv = null;
        panels.remove(network.getCyNetwork());
    }
    
    public GFDnetResult getResult() {
        return result;
    }
    
    public Object[][] getGeneRows() {
       return network.getGeneRows();
    }
    
    public Object[][] getEdgesRows() {
       return network.getEdgesRows();
    }
    
    public void showSelectedElementInfo() {
        List<String> elements = network.getSelectedElements();
        if(elements == null) {
            showSummarytInfo();
        }else if(elements.size() == 1) {
            showNodeInfo(elements.get(0));
        } 
        else if (elements.size() == 2) {
            showEdgeInfo(elements.get(0), elements.get(1));
        }
        CySwing.selectPanel(rv);
    }
    
    public void showSummarytInfo() {
        rv.showSummarytInfo();
    }
    
    public void selectEdge(String n1, String n2) {
        ClickOnViewListener.disable();
        network.selectEdges(n1, n2);
        ClickOnViewListener.enable();
    }
    
    public void showEdgeInfo(String n1, String n2) {
        int i = -1;
        int j = -1;
        List<GeneInput> genes = result.getNetwork().getNodes();
        int cont = 0;
        for (GeneInput gene : genes) {
            String geneName = gene.getName();
            if (i < 0 && j < 0) {
                if (geneName.equalsIgnoreCase(n1)) {
                    if (gene.getSelectedGOTerm() == null) {
                        return;
                    }
                    i = cont;
                }
                else if (geneName.equalsIgnoreCase(n2)) {
                    if (gene.getSelectedGOTerm() == null) {
                        return;
                    }
                    j = cont;
                }
            }
            else if (i < 0) {
                if (geneName.equalsIgnoreCase(n1)) {
                    if (gene.getSelectedGOTerm() == null) {
                        return;
                    }
                    i = cont;
                }
            }
            else if (j < 0) {
                if (geneName.equalsIgnoreCase(n2)) {
                    if (gene.getSelectedGOTerm() == null) {
                        return;
                    }
                    j = cont;
                }
            }
            else{
                break;
            }
            cont++;
        }
        if (i == -1 || j == -1) {
            return;
        }
        rv.showEdgeInfo(result.getNetwork().getNode(i), result.getNetwork().getNode(j), result.getNetwork().getEdgeWeight(i, j));
    }
    
    public void selectNode(String node) {
        ClickOnViewListener.disable();
        network.selectNode(node);
        ClickOnViewListener.enable();
    }

    public void showNodeInfo(String node) {
        List<GeneInput> genes = result.getNetwork().getNodes();
        for (GeneInput gene : genes) {
            if (gene.getName().equalsIgnoreCase(node)) {
                rv.showGeneInfo(gene);
                break;
            }
        }
    }
}