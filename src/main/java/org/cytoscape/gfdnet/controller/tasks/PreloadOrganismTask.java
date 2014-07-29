package org.cytoscape.gfdnet.controller.tasks;

import org.cytoscape.gfdnet.controller.CoreController;
import org.cytoscape.gfdnet.controller.ToolBarController;
import org.cytoscape.gfdnet.controller.utils.CySwing;
import org.cytoscape.gfdnet.controller.utils.CytoscapeTaskMonitor;
import org.cytoscape.gfdnet.model.businessobjects.ProgressMonitor;
import org.cytoscape.gfdnet.model.businessobjects.go.Organism;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 *
 * @author Juan José Díaz Montaña
 */
public class PreloadOrganismTask extends AbstractTask{
    private String ontology;
    private Organism organism;
    private CoreController core;
    private Thread taskThread;
    
    public PreloadOrganismTask(Organism organism, String ontology, CoreController core) {
        this.ontology = ontology;
        this.organism = organism;
        this.core = core;
    }

    @Override
    public void run(TaskMonitor tm) {
        taskThread = Thread.currentThread();
        final ToolBarController toolbar = core.getToolbar();
        try{
            toolbar.configDBButtonEnabled(false);
            toolbar.setOntologyButtonEnabled(false);
            toolbar.setOrganismButtonEnabled(false);
            toolbar.executeButtonEnabled(false);
            toolbar.refreshButtonEnabled(false);
            tm.setTitle("Preloading " + organism.getGenus() + " " + organism.getSpecies() + "...");
            ProgressMonitor pm = new CytoscapeTaskMonitor(tm);
            organism.preloadGenes(ontology, pm);
            // TODO Change to observable task callback in 3.1
            if (!taskThread.isInterrupted()){
                core.reset();
                toolbar.executeButtonEnabled(true);
                CySwing.displayPopUpMessage("Organism succesfully set!");
            }
        }
        catch (Exception ex){
            CySwing.displayPopUpMessage(ex.getMessage());
        }
        finally{
            toolbar.configDBButtonEnabled(true);
            toolbar.setOntologyButtonEnabled(true);
            toolbar.setOrganismButtonEnabled(true);
            toolbar.refreshButtonEnabled(true);
        }
    } 
    
    @Override
    public void cancel(){
        taskThread.interrupt();
        ToolBarController toolbar = core.getToolbar();
        toolbar.configDBButtonEnabled(true);
        toolbar.setOntologyButtonEnabled(true);
        toolbar.setOrganismButtonEnabled(true);
        toolbar.refreshButtonEnabled(true); 
        super.cancel();
        CySwing.displayPopUpMessage("Organism preload was cancelled");
    }
}