package org.cytoscape.gfdnet.model.logic;

import org.cytoscape.gfdnet.model.businessobjects.GFDnetResult;
import org.cytoscape.gfdnet.model.businessobjects.GeneInput;
import org.cytoscape.gfdnet.model.businessobjects.Graph;
import org.cytoscape.gfdnet.model.businessobjects.go.Ontology;
import org.cytoscape.gfdnet.model.businessobjects.go.Organism;
import org.cytoscape.gfdnet.model.logic.heuristic.GFDnetVoronoi;
import org.cytoscape.gfdnet.model.logic.utils.GOUtils;
import org.cytoscape.work.TaskMonitor;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class GFDnet {   
    /**
     * Returns the result object that contains all the information retrieved
     * by the GFD-Net approximation.
     * 
     * @param network network to be evaluated
     * @param genus genus of the organism that the network belongs to
     * @param species species of the organism that the network belongs to
     * @param ontology ontology to use in order to run the analysis
     * @param version version of the algorithm to be run (soon to be changed) 
     * @return the result object that contains all the information retrieved
     */
    public static GFDnetResult evaluateGeneNames(Graph<String> network, String genus, String species, String ontology, int version, TaskMonitor tm){
        Organism organism = new Organism(genus, species);
        if (!organism.isValid()){
            throw new IllegalArgumentException("The organism cannot be recognised");
        }
        return evaluateGeneNames(network, organism, ontology, version, tm);
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
    public static GFDnetResult evaluateGeneNames(Graph<String> network, Organism organism, String ontology, int version, TaskMonitor tm){
        tm.setTitle("Executing GFD-Net analysis");
        if (!Ontology.isOntology(ontology)){
            throw new IllegalArgumentException("The ontology cannot be recognised");
        }
        if (version < 1 && version > 3) {
            throw new IllegalArgumentException("Wrong version of GFD-Net");
        }
        tm.setStatusMessage("Retrieving genes from GO");
        Graph<GeneInput> geneInputsNetwork = GOUtils.getGenInputNetwork(network, organism, ontology);
        GFDnetVoronoi voronoi = new GFDnetVoronoi();
        for (GeneInput gene : geneInputsNetwork.getNodes()){
            gene.clearRepresentationSelected();
        }
        tm.setStatusMessage("Analyzing the network");
        GFDnetResult result = voronoi.evaluateRepresentations(geneInputsNetwork, ontology, version, tm);
        result.setOrganism(organism);
        return result;
    }
}