package org.cytoscape.gfdnet.model.businessobjects.go;

import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.cytoscape.gfdnet.model.businessobjects.GeneInput;
import org.cytoscape.gfdnet.model.dataaccess.go.GeneDAO;
import org.cytoscape.gfdnet.model.dataaccess.go.OrganismDAO;
import org.cytoscape.work.TaskMonitor;

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
    
    public void preloadGenes(String ontology, TaskMonitor tm){ 
        tm.setTitle("Preloading " + genus + " " + species + "...");
        if (ontologyPreloaded == null || !ontologyPreloaded.equals(ontology)){
            ontologyPreloaded = ontology;
            tm.setStatusMessage("Retrieving genes from GO");
            genes = GeneDAO.getGenes(this);
            int cont = 1;
            int genesSize = genes.size();
            for (Gene gene : genes){
                tm.setStatusMessage("Loading " + gene.getName());
                ((GeneInput)gene).isKnown(ontology);
                tm.setProgress((double)cont/genesSize);
                cont++;
            }
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