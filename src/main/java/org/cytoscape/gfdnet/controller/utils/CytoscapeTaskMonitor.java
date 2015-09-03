package org.cytoscape.gfdnet.controller.utils;

import org.cytoscape.gfdnet.model.businessobjects.utils.ProgressMonitor;
import org.cytoscape.work.TaskMonitor;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class CytoscapeTaskMonitor implements ProgressMonitor {
    private final TaskMonitor tm;
    
    public CytoscapeTaskMonitor(TaskMonitor tm) {
        this.tm = tm;
    }
    @Override
    public String getStatus() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void setStatus(String status) {
        tm.setStatusMessage(status);
    }

    @Override
    public float getProgress() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void setProgress(float progress) {
        tm.setProgress(progress);
    }
    
}
