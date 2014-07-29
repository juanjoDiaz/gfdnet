package org.cytoscape.gfdnet.model.businessobjects.go;

import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.cytoscape.gfdnet.model.businessobjects.GeneInput;
import org.cytoscape.gfdnet.model.businessobjects.ProgressMonitor;
import org.cytoscape.gfdnet.model.dataaccess.go.OrganismDAO;
import org.cytoscape.gfdnet.model.logic.utils.GOUtils;

/**
 *
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class Organism {
    private final String genus;
    private final String species;

    private String ontologyPreloaded;
    private SortedSet<GeneInput> genes;

    public Organism(String genus, String species) {
        this.genus = genus;
        this.species = species;
        genes = new TreeSet<GeneInput>();
    }

    public String getGenus() {
        return genus;
    }

    public String getSpecies() {
        return species;
    }

    public SortedSet<GeneInput> getGenes() {
        return Collections.unmodifiableSortedSet(genes);
    }
    
    public void preloadGenes(String ontology, ProgressMonitor pm){ 
        if (ontologyPreloaded == null || !ontologyPreloaded.equals(ontology)){
            ontologyPreloaded = ontology;
            pm.setStatus("Retrieving genes from GO");
            genes = GOUtils.getGenInputs(this, ontology, pm);
        }
    }
    
    public void unloadGenes(){
        ontologyPreloaded = null;
        genes = null;
    }
    
    public boolean isPreloaded(){
        return ontologyPreloaded != null;
    }
   
    public boolean isValid(){
        return OrganismDAO.isValid(genus, species);
    }
    
    public static List<String> getAllGenera(){
        return OrganismDAO.getGenera();
    }

    public static List<String> getSpeciesFromGenus(String genus){
        return OrganismDAO.getSpeciesFromGenus(genus);
    }
}