package org.cytoscape.gfdnet.controller.tasks;

import org.cytoscape.gfdnet.controller.CoreController;
import org.cytoscape.gfdnet.controller.ToolBarController;
import org.cytoscape.gfdnet.controller.utils.CySwing;
import org.cytoscape.gfdnet.controller.utils.CytoscapeTaskMonitor;
import org.cytoscape.gfdnet.model.businessobjects.Enums.Ontology;
import org.cytoscape.gfdnet.model.businessobjects.GFDnetResult;
import org.cytoscape.gfdnet.model.businessobjects.Graph;
import org.cytoscape.gfdnet.model.businessobjects.utils.ProgressMonitor;
import org.cytoscape.gfdnet.model.businessobjects.exceptions.DataBaseException;
import org.cytoscape.gfdnet.model.businessobjects.go.Organism;
import org.cytoscape.gfdnet.model.logic.GFDnet;
import org.cytoscape.gfdnet.model.logic.voronoi.GFDnetVoronoi;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 *
 * @author Juan José Díaz Montaña
 */
public class ExecuteGFDnetTask extends AbstractTask{
    private final Graph<String> network;
    private final Organism organism;
    private final Ontology ontology;
    private final CoreController core;
    private GFDnet gfdnet;
    
    public ExecuteGFDnetTask(Graph<String> network, Organism organism, Ontology ontology, CoreController core){
        this.network = network;
        this.organism = organism;
        this.ontology = ontology; 
        this.core = core;
    }
    
    @Override
    public void run(TaskMonitor tm){
        final ToolBarController toolbar = core.getToolbar();
        try {
            toolbar.enableDBButton(false);
            toolbar.enableOntologyButton(false);
            toolbar.enableOrganismButton(false);
            toolbar.enableExecuteButton(false);
            toolbar.enableRefreshButton(false);
            ProgressMonitor pm = new CytoscapeTaskMonitor(tm);
            GFDnet gfdNet = new GFDnetVoronoi(pm);
            tm.setTitle("Executing GFD-Net analysis");
            GFDnetResult result = gfdNet.evaluateGeneNames(network, organism, ontology, 2);
            // TODO Change to observable task callback in 3.1
            if (!gfdNet.isInterrupted()){
                core.showResults(result);
                CySwing.displayPopUpMessage("GFD-Net succesfully finished!");
            }
            else{
                toolbar.enableExecuteButton(true);
            }
        } catch (DataBaseException ex) {
            CySwing.displayPopUpMessage("There was a problem accesing the database.");
        }
        catch (Exception ex) {
            toolbar.enableOrganismButton(true);
            toolbar.enableExecuteButton(true);
            CySwing.displayPopUpMessage(ex.getMessage());
        }
        finally {
            toolbar.enableDBButton(true);
            toolbar.enableOntologyButton(true);
            toolbar.enableOrganismButton(true);
            toolbar.enableRefreshButton(true);
        }
    } 
    
    @Override
    public void cancel(){
        gfdnet.interrupt();
        super.cancel();
        CySwing.displayPopUpMessage("GFD-Net execution was cancelled");
    }
}