package org.cytoscape.gfdnet.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.cytoscape.gfdnet.controller.utils.ClickOnViewListener;
import org.cytoscape.gfdnet.controller.utils.CySwing;
import org.cytoscape.gfdnet.controller.utils.OSGiManager;
import org.cytoscape.gfdnet.model.businessobjects.GFDnetResult;
import org.cytoscape.gfdnet.model.businessobjects.GeneInput;
import org.cytoscape.gfdnet.view.resultPanel.MainResultsView;
import org.cytoscape.model.events.RowsSetListener;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class ResultPanelsController {
    private final NetworkController networkView;
    private GFDnetResult result;
    private MainResultsView rv;
    private ClickOnViewListener clickListerner;
    
    public ResultPanelsController(GFDnetResult result, NetworkController networkView){
        this.networkView = networkView;
        this.result = result;
        List<GeneInput> nodes = result.getNetwork().getNodes();
        List<String> genesToKeep = new ArrayList<String>(nodes.size());
        for (GeneInput node : nodes){
           genesToKeep.add(node.getName()); 
        }
        networkView.hideNodesNotInList(genesToKeep);
        registerClickOnViewListener();
        rv = new MainResultsView(result, this);
        CySwing.addPanel(rv);
    }
    
    public void dispose(){
        removeResultsPanel();
    }
    
    public void removeResultsPanel(){
        unregisterClickOnViewListener();
        result = null;
        if (rv != null){
            CySwing.removePanel(rv);
            rv = null;
        }
    }
    
    public void showSelectedElementInfo(){
        List<String> elements = networkView.getSelectedElements();
        if(elements == null){
            showDefaultInfo();
        }else if(elements.size() == 1){
            showNodeInfo(elements.get(0));
        } 
        else if (elements.size() == 2){
            showEdgeInfo(elements.get(0), elements.get(1));
        }
    }
    
    public void showDefaultInfo(){
        rv.ShowDefaultInfo();
    }
    
    public void selectEdge(String n1, String n2){
        disableClickOnViewListener();
        networkView.selectEdges(n1, n2);
        enableClickOnViewListener();
    }
    
    public void showEdgeInfo(String n1, String n2){
        int i = -1;
        int j = -1;
        List<GeneInput> genes = result.getNetwork().getNodes();
        int cont = 0;
        for (GeneInput gene : genes){
            String geneName = gene.getName();
            if (i < 0 && j < 0){
                if (geneName.equals(n1)){
                    i = cont;
                }
                else if (geneName.equals(n2)){
                    j = cont;
                }
            }
            else if (i < 0){
                if (geneName.equals(n1)){
                    i = cont;
                }
            }
            else if (j < 0){
                if (geneName.equals(n2)){
                    j = cont;
                }
            }
            else{
                break;
            }
            cont++;
        }
        rv.showEdgeInfo(result.getNetwork().getNode(i), result.getNetwork().getNode(j), result.getNetwork().getEdgeWeight(i, j));
    }
    
    public void selectNode(String node){
        disableClickOnViewListener();
        networkView.selectNode(node);
        enableClickOnViewListener();
    }

    public void showNodeInfo(String node){
        List<GeneInput> genes = result.getNetwork().getNodes();
        for (GeneInput gene : genes){
            if (gene.getName().equals(node)){
                rv.showGeneInfo(gene);
                break;
            }
        }
    }
    
    public final void registerClickOnViewListener(){
        clickListerner = new ClickOnViewListener(this);
        OSGiManager.registerService(clickListerner, RowsSetListener.class, new Properties());  
    }
    
    public void unregisterClickOnViewListener(){
        if (clickListerner != null) {
            OSGiManager.unregisterService(clickListerner, RowsSetListener.class);
            clickListerner = null; 
        }    
    }
    
    private void enableClickOnViewListener(){
        clickListerner.enable();
    }
    
    private void disableClickOnViewListener(){
        clickListerner.disable();
    }
}