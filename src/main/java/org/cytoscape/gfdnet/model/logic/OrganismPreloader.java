package org.cytoscape.gfdnet.model.logic;

import java.util.Set;
import org.cytoscape.gfdnet.model.businessobjects.Enums.Ontology;
import org.cytoscape.gfdnet.model.businessobjects.GeneInput;
import org.cytoscape.gfdnet.model.businessobjects.utils.ProgressMonitor;
import org.cytoscape.gfdnet.model.businessobjects.go.Organism;
import org.cytoscape.gfdnet.model.dataaccess.DataBase;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class OrganismPreloader {
    
    protected ProgressMonitor pm;
    protected boolean isInterrupted = false;
    
    public OrganismPreloader(ProgressMonitor pm){
        this.pm = pm;
    }
    
    public void interrupt() {
        isInterrupted = true;
    }
    
    public boolean isInterrupted() {
        return isInterrupted;
    }
    
    public void preloadGenes(Organism organism, Ontology ontology) {
        DataBase.openConnection();
        pm.setStatus("Retrieving genes from GO");
        // Load genes
        Set<GeneInput> genes = organism.getAllGenes();
        // Load related GOTerms
        int cont = 1;
        int genesSize = genes.size();
        for(GeneInput gene : genes) {
            if (isInterrupted){
                DataBase.closeConnection();
                return;
            }
            pm.setStatus("Loading " + gene.getName());
            gene.isKnown(ontology);
            pm.setProgress((float)cont/genesSize);
            cont++;
        }
        DataBase.closeConnection();
    }
}
