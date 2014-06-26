package org.cytoscape.gfdnet.model.businessobjects;

import java.math.BigDecimal;
import java.util.List;
import org.cytoscape.gfdnet.model.businessobjects.go.Organism;
import org.cytoscape.gfdnet.model.logic.utils.GFDnetSimilarityUtils;

/**
 *
 * @author Juan José Díaz Montaña
 */
public class GFDnetResult {
    /**
     * Ontology used
     */
    private final String ontology;
    
    /**
     * Organism used
     */
    private Organism organism;
    
    /**
     * Numerical result
     */
    private final BigDecimal similarity;
    
    /**
     * A graph where the nodes are the genes showing the interactions between them
     */
    private final Graph<GeneInput> net;

    /**
     *
     * @param ontology
     * @param net
     */
    public GFDnetResult(String ontology, Graph<GeneInput> net) {
        this.ontology = ontology;
        this.net = net;
        this.similarity = GFDnetSimilarityUtils.getSimilarity(net);
    }

    /**
     *
     * @return the ontology
     */
    public String getOntology() {
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
    public BigDecimal getSimilarity() {
        return similarity;
    }

    /**
     *
     * @return the net with all the information of GFD
     */
    public Graph<GeneInput> getNet(){
        return net;
    }
    
    /**
     *
     * @return a list of the genes in the net
     */
    public List<GeneInput> getKownGenes(){
        return net.getNodes();
    }
}