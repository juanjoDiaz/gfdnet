package org.cytoscape.gfdnet.model.businessobjects.go;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.cytoscape.gfdnet.model.businessobjects.GeneInput;
import org.cytoscape.gfdnet.model.dataaccess.go.GeneDAO;
import org.cytoscape.gfdnet.model.dataaccess.go.OrganismDAO;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class Organism {
    private final String genus;
    private final String species;

    private Set<GeneInput> genes;
    
    private boolean isPreloaded;

    public Organism(String genus, String species) {
        this.genus = genus;
        this.species = species;
        genes = new HashSet<GeneInput>();
    }

    public String getGenus() {
        return genus;
    }

    public String getSpecies() {
        return species;
    }
    
    public boolean isValid() {
        return OrganismDAO.isValid(genus, species);
    }
    
    public Set<GeneInput> getAllGenes() {
        if (!isPreloaded){
            genes = GeneDAO.getGenes(this); 
            isPreloaded = true;
        }
        return Collections.unmodifiableSet(genes);
    }
    
    /**
     * Receives a list containing gene names and return a equivalent list containing genes
     * 
     * @param geneNames List of gene names
     * @return List of genes extracted from GO
     */
    public List<GeneInput> getGenes(List<String> geneNames) {
        List<GeneInput> loadedGenes = new LinkedList<GeneInput>();
        List<String> notFoundGeneNames = new LinkedList<String>();
        for (String geneName : geneNames){
            GeneInput gene = new GeneInput(geneName);
            GeneInput selectedGene = null;
            for (GeneInput preloadedGene : genes){
                if(preloadedGene.equals(gene)){
                    selectedGene = preloadedGene;
                    break;
                }
                else if (preloadedGene.isSynonym(gene)) {
                    selectedGene = preloadedGene;
                }
            }
            if(selectedGene != null) {
                if (!selectedGene.getName().equals(geneName)) {
                    if (loadedGenes.contains(selectedGene)){
                        selectedGene = selectedGene.clone();
                    }
                    selectedGene.convertSynonymInName(geneName);
                }
                loadedGenes.add(selectedGene);
            } else {
                notFoundGeneNames.add(geneName);
            }
        }
        if (!isPreloaded && !notFoundGeneNames.isEmpty()) {
            List<GeneInput> notFoundGenes = GeneDAO.getGenes(this, notFoundGeneNames);
            loadedGenes.addAll(notFoundGenes);
            genes.addAll(notFoundGenes);  
        }
        return loadedGenes;
    }
        
    public static List<String> getAllGenera() {
        return OrganismDAO.getGenera();
    }

    public static List<String> getSpeciesFromGenus(String genus){
        return OrganismDAO.getSpeciesFromGenus(genus);
    }
}