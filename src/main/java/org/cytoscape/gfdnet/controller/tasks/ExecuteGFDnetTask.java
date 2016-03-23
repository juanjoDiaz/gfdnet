package org.cytoscape.gfdnet.controller.tasks;

import org.cytoscape.gfdnet.controller.NetworkController;
import org.cytoscape.gfdnet.controller.ResultPanelController;
import org.cytoscape.gfdnet.controller.utils.CySwing;
import org.cytoscape.gfdnet.controller.utils.CytoscapeTaskMonitor;
import org.cytoscape.gfdnet.model.businessobjects.Enums.Ontology;
import org.cytoscape.gfdnet.model.businessobjects.GFDnetResult;
import org.cytoscape.gfdnet.model.businessobjects.exceptions.DatabaseException;
import org.cytoscape.gfdnet.model.businessobjects.go.Organism;
import org.cytoscape.gfdnet.model.businessobjects.utils.ProgressMonitor;
import org.cytoscape.gfdnet.model.logic.GFDnet;
import org.cytoscape.gfdnet.model.logic.voronoi.GFDnetVoronoi;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class ExecuteGFDnetTask extends AbstractTask {
    private GFDnet gfdnet;
    private final Organism organism;
    private final Ontology ontology;
    
    public ExecuteGFDnetTask(Organism organism, Ontology ontology) {
        this.organism = organism;
        this.ontology = ontology;
    }
    
    @Override
    public void run(TaskMonitor tm) {
        NetworkController network;
        try {
            network = new NetworkController();
        } catch (InstantiationException ex) {
            CySwing.displayPopUpMessage(ex.getMessage());
            return;
        }
        if (ontology == null) {
            CySwing.displayPopUpMessage("No ontology selected.");
            return;
        }
        if (organism == null) {
            CySwing.displayPopUpMessage("No organism selected.");
            return;
        }
        try {
            ProgressMonitor pm = new CytoscapeTaskMonitor(tm);
            GFDnet gfdNet = new GFDnetVoronoi(pm);
            tm.setTitle("Executing GFD-Net analysis");
            GFDnetResult result = gfdNet.evaluateGeneNames(network.getGraph(), organism, ontology, 2);
            if (!gfdNet.isInterrupted()) {
                pm.setStatus("Displaying the  results.");
                new ResultPanelController(network, result);
                CySwing.displayPopUpMessage("GFD-Net anlysis succesfully completed!");
            }
        } catch (DatabaseException ex) {
            CySwing.displayPopUpMessage("There was a problem accesing the database.");
        }
        catch (Exception ex) {
            CySwing.displayPopUpMessage(ex.getMessage());
        }
    } 
    
    @Override
    public void cancel() {
        gfdnet.interrupt();
        super.cancel();
        CySwing.displayPopUpMessage("GFD-Net execution was cancelled");
    }
}