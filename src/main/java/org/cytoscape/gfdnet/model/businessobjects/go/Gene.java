package org.cytoscape.gfdnet.model.businessobjects.go;

import org.cytoscape.gfdnet.model.businessobjects.Enums.Ontology;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class Gene implements Comparable {
    protected String name;
    protected final Set<String> synonyms;
    protected final Set<GeneProduct> geneProducts;
    protected final Set<GOTerm> goTerms;
    protected Ontology loadedOntology;
    
    public Gene(String name) {
        this.name = name;
        synonyms = new HashSet(); //Need to be ordered for hash 
        geneProducts = new HashSet();
        goTerms = new HashSet();
    }
    
    public String getName() {
        return name;
    }
    
    public void convertSynonymInName(String synonym){
        if (synonyms.remove(synonym)){
            synonyms.add(name);
            name = synonym;
        }
    }

    public void addSynonym(String synonym){
        synonyms.add(synonym);
    }
    
    public boolean isSynonym(String geneName) {
        return this.name == null
                ? geneName == null 
                : (this.name.equalsIgnoreCase(geneName) || synonyms.contains(geneName));
    }

    public boolean isSynonym(Gene gene) {
        return this.name == null
                ? gene.name == null 
                : (this.name.equalsIgnoreCase(gene.name) || synonyms.contains(gene.name));
    }
    
    public Ontology getLoadedOntology(){
        return loadedOntology;
    }
    
    public void addGeneProduct(GeneProduct geneProduct) {
        geneProducts.add(geneProduct);
    }
    
    public Set<GOTerm> getGoTerms(Ontology ontology) {
        if (loadedOntology == null || !loadedOntology.equals(ontology)) {
            for(GeneProduct geneProduct : geneProducts) {
                goTerms.clear();
                goTerms.addAll(geneProduct.getGoTerms(ontology));
            }
            loadedOntology = ontology;
        }
        return Collections.unmodifiableSet(goTerms);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Gene other = (Gene) obj;
        return(this.name == null) ? (other.name == null) : this.name.equalsIgnoreCase(other.name);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    @Override
    public int compareTo(Object o) {
        return this.name.compareToIgnoreCase(((Gene)o).name);
    }

    @Override
    public String toString(){
        return name;
    }
}
