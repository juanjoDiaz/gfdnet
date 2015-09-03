package org.cytoscape.gfdnet.model.businessobjects;

import java.util.List;
import org.cytoscape.gfdnet.model.businessobjects.Enums.Ontology;
import org.cytoscape.gfdnet.model.businessobjects.go.GOTerm;
import org.cytoscape.gfdnet.model.businessobjects.go.Organism;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class GFDnetResult {
    /**
     * Ontology used
     */
    private final Ontology ontology;
    
    /**
     * Organism used
     */
    private Organism organism;
    
    /**
     * Central Node
     */
    private final GOTerm centralNode;
    
    /**
     * Numerical result
     */
    private final double similarity;
    
    /**
     * A graph where the nodes are the genes showing the interactions between them
     */
    private final Graph<GeneInput> network;

    /**
     *
     * @param ontology
     * @param similarity
     * @param centralNode
     * @param network
     */
    public GFDnetResult(Ontology ontology, double similarity, GOTerm centralNode, Graph<GeneInput> network) {
        this.ontology = ontology;
        this.similarity = similarity;
        this.centralNode = centralNode;
        this.network = network;
    }

    /**
     *
     * @return the ontology
     */
    public Ontology getOntology() {
        return ontology;
    }
    
    /**
     *
     * @return the organism
     */
    public Organism getOrganism() {
        return organism;
    }
    
    /**
     *
     * @param organism
     */
    public void setOrganism(Organism organism) {
        this.organism = organism;
    }
   
    /**
     *
     * @return the similarity value
     */
    public double getSimilarity() {
        return similarity;
    }
    
    /**
     *
     * @return the central node
     */
    public GOTerm getCentralNode() {
        return centralNode;
    }

    /**
     *
     * @return the net with all the information of GFD
     */
    public Graph<GeneInput> getNetwork(){
        return network;
    }
    
    /**
     *
     * @return a list of the genes in the net
     */
    public List<GeneInput> getKownGenes(){
        return network.getNodes();
    }
}