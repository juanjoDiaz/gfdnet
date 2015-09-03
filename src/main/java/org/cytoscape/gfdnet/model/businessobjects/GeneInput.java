package org.cytoscape.gfdnet.model.businessobjects;

import org.cytoscape.gfdnet.model.businessobjects.Enums.Ontology;
import org.cytoscape.gfdnet.model.businessobjects.go.GOTerm;
import org.cytoscape.gfdnet.model.businessobjects.go.Gene;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class GeneInput extends Gene {
    private int nodeId;
    private GOTerm seletedGOTerm;

    public GeneInput(String name) {
        super(name);
    }
      
    public boolean isKnown(Ontology ontology) {
        return !getGoTerms(ontology).isEmpty();
    }
      
    public GOTerm getSelectedGOTerm(){
        return seletedGOTerm;
    }
    
    public void selectGOTerm(GOTerm goTerm) {
        if (!getGoTerms(loadedOntology).contains(goTerm)) {
            throw new IllegalArgumentException("The selected GO-Term \"" + goTerm.getDescription() + "\" is not present in the selected ontology");
        }
        seletedGOTerm = goTerm;
    }
    
    public void clearGOTerm() {
        seletedGOTerm = null;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public int getNodeId() {
        return nodeId;
    }
    
    @Override
    public GeneInput clone() {
        GeneInput clone = new GeneInput(this.name);
        clone.synonyms.addAll(this.synonyms);
        clone.geneProducts.addAll(this.geneProducts);
        clone.goTerms.addAll(this.goTerms);
        clone.loadedOntology = this.loadedOntology;
        return clone;
    }
}