package org.cytoscape.gfdnet.model.logic.heuristic;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cytoscape.gfdnet.model.businessobjects.GeneInput;
import org.cytoscape.gfdnet.model.businessobjects.Graph;
import org.cytoscape.gfdnet.model.businessobjects.Representation;

/**
 * This class manage the threads generated in order to evaluate the
 * input gene network.
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class GFDnetVoronoiMetaThread extends Thread {
    private final GFDnetVoronoi voronoi;
            
    private final int coresCount = 128;
    
    private int currentThread = 0;
    private int executedThreadCount = 0;
    
    private final List<Representation> treeNodes;
    private final Graph<GeneInput> network;
    private final int version;
    private List<Representation> usedRepresentations;
    private BigDecimal lowestDissimilarity = BigDecimal.valueOf(Float.MAX_VALUE);

    
    public GFDnetVoronoiMetaThread(List<Representation> treeNodes, Graph<GeneInput> network,
            int version, GFDnetVoronoi voronoi) {
        Thread.currentThread().setPriority(Thread.NORM_PRIORITY + 2);
        this.treeNodes = treeNodes;
        this.voronoi=voronoi;
        this.network = network;
        this.version = version;
    }

    @Override
    public void run() {
        int cont = 1;
        int numThreadtoExecute = treeNodes.size();
        synchronized (this) {
            while (executedThreadCount < numThreadtoExecute) {
                if ((coresCount > currentThread) && (executedThreadCount + currentThread) < numThreadtoExecute) {
                    incrementCurrentThread(1);
                    Representation centralNode = treeNodes.get(executedThreadCount + currentThread - 1);
                    new GFDnetVoronoiThread(this, centralNode, network, version).start();
                    voronoi.incProgress();
                    cont++;
                } else {
                    try {
                        wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(GFDnetVoronoiMetaThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        voronoi.notifyMetaThreadCompleted();
    }

    public BigDecimal getBestSpecificity() {
        return lowestDissimilarity;
    }

    public List<Representation> getUsedRepresentations() {
        return Collections.unmodifiableList(usedRepresentations);
    }

    synchronized void notifyThreadCompleted() {
        executedThreadCount++;
        incrementCurrentThread(-1);
        notify();
    }

    private synchronized void incrementCurrentThread(int value) {
        currentThread += value;
    }

    synchronized void compareSolution(BigDecimal dissimilarity, List<Representation> usedRepresentations, Representation centralNode) {
        if (lowestDissimilarity.compareTo(dissimilarity) > 0) {
            this.lowestDissimilarity = new BigDecimal(dissimilarity.doubleValue());
            this.usedRepresentations = usedRepresentations;
        }
    }
}