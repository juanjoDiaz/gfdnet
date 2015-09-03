package org.cytoscape.gfdnet.model.logic;

import java.util.LinkedList;
import java.util.List;
import org.cytoscape.gfdnet.model.businessobjects.Enums.Ontology;
import org.cytoscape.gfdnet.model.businessobjects.GFDnetResult;
import org.cytoscape.gfdnet.model.businessobjects.GeneInput;
import org.cytoscape.gfdnet.model.businessobjects.Graph;
import org.cytoscape.gfdnet.model.businessobjects.go.Organism;
import org.cytoscape.gfdnet.model.businessobjects.utils.ProgressMonitor;
import org.cytoscape.gfdnet.model.dataaccess.DataBase;
import org.cytoscape.gfdnet.model.logic.utils.GOUtils;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public abstract class GFDnet {
    protected ProgressMonitor pm;
    protected boolean isInterrupted = false;
    
    public GFDnet(ProgressMonitor pm){
        this.pm = pm;
    }
    
    public void interrupt() {
        isInterrupted = true;
    }
    
    public boolean isInterrupted() {
        return isInterrupted;
    }
    /**
     * Returns the result object that contains all the information retrieved
     * by the GFD-Net approximation.
     * 
     * @param network network to be evaluated
     * @param genus genus of the organism that the network belongs to
     * @param species species of the organism that the network belongs to
     * @param ontologyName ontology to use in order to run the analysis
     * @param version version of the algorithm to be run (soon to be changed) 
     * @return the result object that contains all the information retrieved
     */
    public GFDnetResult evaluateGeneNames(Graph<String> network, String genus, String species, String ontologyName, int version){
        Organism organism = new Organism(genus, species);
        if (!organism.isValid()){
            throw new IllegalArgumentException("The organism cannot be recognised");
        }
        Ontology ontology;
        try {
            ontology = Ontology.getEnum(ontologyName);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The ontology cannot be recognised");
        }
        return evaluateGeneNames(network, organism, ontology, version);
    }
    
    /**
     * Returns the result object that contains all the information retrieved
     * by the GFD-Net approximation.
     * 
     * @param network network to be evaluated
     * @param organism organism that the network belongs to
     * @param ontology ontology to use in order to run the analysis
     * @param version version of the algorithm to be run (soon to be changed) 
     * @return the result object that contains all the information retrieved
     */
    public GFDnetResult evaluateGeneNames(Graph<String> network, Organism organism, Ontology ontology, int version){
        if (version < 1 && version > 3) {
            throw new IllegalArgumentException("Wrong version of GFD-Net");
        }
        
        DataBase.openConnection();
        this.pm.setStatus("Retrieving genes from GO");
        List<String> nodes = network.getNodes();
        
        List<GeneInput> genes = organism.getGenes(nodes);
        List<GeneInput> genesToRemove = new LinkedList<GeneInput>();
        for(GeneInput gene : genes){
            if (isInterrupted) {
                DataBase.closeConnection();
                return null;
            }
            if(!gene.isKnown(ontology)){
                genesToRemove.add(gene);
            }
        }
        DataBase.closeConnection();
        genes.removeAll(genesToRemove);
        
        if (genes.isEmpty()) {
            throw new IllegalArgumentException("There isn't any gene in the network that is annotated in the selected ontology.");
        }
        
        Graph<GeneInput> geneInputsNetwork = GOUtils.getGenInputNetwork(network, genes);

        for (GeneInput gene : geneInputsNetwork.getNodes()){
            gene.clearGOTerm();
        }
        this.pm.setStatus("Analyzing the network");
        GFDnetResult result = evaluateGenes(geneInputsNetwork, ontology, version);
        if (isInterrupted) {
            return null;
        }
        result.setOrganism(organism);
        return result;
    }
    
    public abstract GFDnetResult evaluateGenes(Graph<GeneInput> network, Ontology ontology, int version);
}