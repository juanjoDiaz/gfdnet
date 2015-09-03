package org.cytoscape.gfdnet.model.businessobjects.utils;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public interface ProgressMonitor {
    public String getStatus();
    public void setStatus(String status);
    public float getProgress();
    public void setProgress(float status);
}
