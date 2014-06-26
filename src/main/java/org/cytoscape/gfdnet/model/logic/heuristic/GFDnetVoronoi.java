package org.cytoscape.gfdnet.model.logic.heuristic;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cytoscape.gfdnet.model.businessobjects.GFDnetResult;
import org.cytoscape.gfdnet.model.businessobjects.GeneInput;
import org.cytoscape.gfdnet.model.businessobjects.Graph;
import org.cytoscape.gfdnet.model.businessobjects.Representation;
import org.cytoscape.gfdnet.model.logic.utils.GOTreeUtils;
import org.cytoscape.work.TaskMonitor;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class GFDnetVoronoi {

    private int metaThreads = 4;
    private int threadsExecuted = 0;
    
    private TaskMonitor tm;
    private int progress = 0;
    private int treeNodesSize;
        
    public synchronized void incProgress() {
        progress++; 
        tm.setProgress((double)progress/treeNodesSize);
    }

    /**
     * This method is the bottle neck of the analysis and it is use a Divide and Conquer approach
     * to overcome that. It gets the relevant section of the GO-Tree for the given network
     * and execute several MetaThreads dividing the nodes of the tree in several sets
     * 
     * @param nodes
     * @param network
     * @param ontology
     * @param version
     * @return The MethaThread that got the best result
     */
    public GFDnetResult evaluateRepresentations(Graph<GeneInput> network, String ontology, int version, TaskMonitor tm) {
        this.tm = tm;
        List<Representation> treeNodes = GOTreeUtils.getNodes(network, ontology);
        
        treeNodesSize = treeNodes.size();
        int metaThreadSize = treeNodesSize / metaThreads;
        List<GFDnetVoronoiMetaThread> mts = new ArrayList<GFDnetVoronoiMetaThread>(metaThreads);
        for (int i = 0; i < metaThreads; i++){
            int end = (i+1) * metaThreadSize;
            end = end > treeNodesSize ? treeNodesSize : end;
            GFDnetVoronoiMetaThread mt = new GFDnetVoronoiMetaThread(treeNodes.subList(i, end), network, version, this);
            mts.add(mt);
        }
        for(GFDnetVoronoiMetaThread mt : mts){
            mt.start();
        }        
        waitToCompletelyExecute();
        threadsExecuted = 0;

        GFDnetVoronoiMetaThread bestMt = null;
        for (GFDnetVoronoiMetaThread mti : mts){
            if (bestMt == null || bestMt.getBestSpecificity().compareTo(mti.getBestSpecificity()) > 0) {
                bestMt = mti;
            }
        }

        for(Representation representation : bestMt.getUsedRepresentations()) {
            representation.setSelected(true);
        }
        GFDnetResult result = new GFDnetResult(ontology, network);
        System.gc();
        return result;
    }

    private synchronized void waitToCompletelyExecute() {
        while (threadsExecuted < metaThreads) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(GFDnetVoronoi.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public synchronized void notifyMetaThreadCompleted() {
        threadsExecuted++;
        notify();
    }
}