package org.cytoscape.gfdnet.model.logic;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.cytoscape.gfdnet.model.businessobjects.go.Enums.Ontology;
import org.cytoscape.gfdnet.model.businessobjects.GFDnetResult;
import org.cytoscape.gfdnet.model.businessobjects.GOTree;
import org.cytoscape.gfdnet.model.businessobjects.GeneInput;
import org.cytoscape.gfdnet.model.businessobjects.GenesSearchResult;
import org.cytoscape.gfdnet.model.businessobjects.Graph;
import org.cytoscape.gfdnet.model.businessobjects.go.Organism;
import org.cytoscape.gfdnet.model.businessobjects.utils.ProgressMonitor;
import org.cytoscape.gfdnet.model.dataaccess.Database;
import org.cytoscape.gfdnet.model.logic.utils.GOUtils;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public abstract class GFDnet {
    protected ProgressMonitor pm;
    protected boolean isInterrupted = false;
    
    public GFDnet(ProgressMonitor pm) {
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
    public GFDnetResult evaluateGeneNames(Graph<String> network, String genus, String species, String ontologyName, int version) {
        Organism organism = new Organism(genus, species);
        if (!organism.isValid()) {
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
    public GFDnetResult evaluateGeneNames(Graph<String> network, Organism organism, Ontology ontology, int version) {
        Database.openConnection();
        pm.setStatus("Retrieving genes from GO");
        List<String> nodes = network.getNodes();
        GenesSearchResult genesSearchResult = organism.getGenes(nodes);
        List<GeneInput> genes = genesSearchResult.found;
        List<String> unknownGenes = genesSearchResult.unknown;
        
        pm.setStatus("Retrieving gene annotations");
        List<GeneInput> unannotatedGenes = new LinkedList<GeneInput>();
        Iterator<GeneInput> genesItr = genes.iterator();
        while(genesItr.hasNext()) {
           GeneInput gene = genesItr.next();
            if (isInterrupted) {
                Database.closeConnection();
                return null;
            }
            pm.setStatus("Retrieving gene annotations (" + gene.getName() + ")");
            if(!gene.isAnnotated(ontology)) {
                unannotatedGenes.add(gene);
                genesItr.remove();
            }
            gene.clearGOTerm();
        }
        Database.closeConnection();
        
        if (isInterrupted) {
            return null;
        }
        
        if (genes.isEmpty()) {
            throw new IllegalArgumentException("There isn't any gene in the network that is annotated in the selected ontology.");
        }
        
        pm.setStatus("Annotating the network");
        Graph<GeneInput> geneInputsNetwork = GOUtils.getGenInputNetwork(network, genes);

        if (isInterrupted) {
            return null;
        }
        
        pm.setStatus("Building the GO-Tree");
        GOTree goTree = new GOTree(geneInputsNetwork.getNodes(), ontology);
        
        if (isInterrupted) {
            return null;
        }

        pm.setStatus("Analyzing the network");
        GFDnetResult result = evaluateGenes(geneInputsNetwork, goTree, organism, ontology, version);
        result.setUnkownGenes(unknownGenes);
        result.setUnannotatednGenes(unannotatedGenes);
        return result;
    }
    
    public GFDnetResult evaluateGenes(Graph<GeneInput> network, GOTree goTree, Organism organism, Ontology ontology, int version) {
        if (version < 1 && version > 3) {
            throw new IllegalArgumentException("Wrong version of GFD-Net");
        }
        for (GeneInput gene : network.getNodes()) {
            gene.clearGOTerm();
        }
        GFDnetResult result = evaluateGenes(goTree, network, version);
        result.setOntology(ontology);
        result.setOrganism(organism);
        return result;
    }
    
    protected abstract GFDnetResult evaluateGenes(GOTree goTree, Graph<GeneInput> network, int version);
}