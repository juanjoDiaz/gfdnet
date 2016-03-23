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

    private final double similarity;
    private final GOTerm centralNode;
    private final Graph<GeneInput> network;
    private List<String> unknownGenes;
    private List<GeneInput> unannotatedGenes;
    private Ontology ontology;
    private Organism organism;

    public GFDnetResult(double similarity, GOTerm centralNode, Graph<GeneInput> network) {
        this.similarity = similarity;
        this.centralNode = centralNode;
        this.network = network;
    }
     
    public double getSimilarity() {
        return similarity;
    }

    public GOTerm getCentralNode() {
        return centralNode;
    }
    
    public Graph<GeneInput> getNetwork() {
        return network;
    }
    
    public List<GeneInput> getAnalyzedGenes() {
        return network.getNodes();
    }

    public void setUnkownGenes(List<String> unknownGenes) {
        this.unknownGenes = unknownGenes;
    }
    
    public List<String> getUnkownGenes() {
        return this.unknownGenes;
    }
    
    public void setUnannotatednGenes(List<GeneInput> unannotatedGenes) {
        this.unannotatedGenes = unannotatedGenes;
    }
    
    public List<GeneInput> getUnannotatednGenes() {
        return this.unannotatedGenes;
    }

    public void setOntology(Ontology ontology) {
        this.ontology = ontology;
    }
    
    public Ontology getOntology() {
        return ontology;
    }

    public Organism getOrganism() {
        return organism;
    }

    public void setOrganism(Organism organism) {
        this.organism = organism;
    }
}