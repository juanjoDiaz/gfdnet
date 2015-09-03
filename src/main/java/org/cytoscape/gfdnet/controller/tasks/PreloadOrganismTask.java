package org.cytoscape.gfdnet.controller.tasks;

import org.cytoscape.gfdnet.controller.CoreController;
import org.cytoscape.gfdnet.controller.ToolBarController;
import org.cytoscape.gfdnet.controller.utils.CySwing;
import org.cytoscape.gfdnet.controller.utils.CytoscapeTaskMonitor;
import org.cytoscape.gfdnet.model.businessobjects.Enums.Ontology;
import org.cytoscape.gfdnet.model.businessobjects.utils.ProgressMonitor;
import org.cytoscape.gfdnet.model.businessobjects.exceptions.DataBaseException;
import org.cytoscape.gfdnet.model.businessobjects.go.Organism;
import org.cytoscape.gfdnet.model.logic.OrganismPreloader;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 *
 * @author Juan José Díaz Montaña
 */
public class PreloadOrganismTask extends AbstractTask{
    private final Ontology ontology;
    private final Organism organism;
    private final CoreController core;
    private OrganismPreloader organismPreloader;
    
    public PreloadOrganismTask(Organism organism, Ontology ontology, CoreController core) {
        this.ontology = ontology;
        this.organism = organism;
        this.core = core;
    }

    @Override
    public void run(TaskMonitor tm) {
        final ToolBarController toolbar = core.getToolbar();
        try{
            toolbar.enableDBButton(false);
            toolbar.enableOntologyButton(false);
            toolbar.enableOrganismButton(false);
            toolbar.enableExecuteButton(false);
            toolbar.enableRefreshButton(false);
            tm.setTitle("Preloading " + organism.getGenus() + " " + organism.getSpecies() + "...");
            ProgressMonitor pm = new CytoscapeTaskMonitor(tm);
            organismPreloader = new OrganismPreloader(pm);
            organismPreloader.preloadGenes(organism, ontology);
            // TODO Change to observable task callback in 3.1
            if (organismPreloader.isInterrupted()){
                core.reset();
                toolbar.enableExecuteButton(true);
                CySwing.displayPopUpMessage("Organism succesfully set!");
            }
        } catch (DataBaseException ex) {
            CySwing.displayPopUpMessage("There was a problem accesing the database.");
        }
        catch (Exception ex){
            CySwing.displayPopUpMessage(ex.getMessage());
        }
        finally{
            toolbar.enableDBButton(true);
            toolbar.enableOntologyButton(true);
            toolbar.enableOrganismButton(true);
            toolbar.enableRefreshButton(true);
        }
    } 
    
    @Override
    public void cancel(){
        organismPreloader.interrupt();
        super.cancel();
        CySwing.displayPopUpMessage("Organism preload was cancelled");
    }
}