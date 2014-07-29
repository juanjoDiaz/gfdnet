package org.cytoscape.gfdnet.model.businessobjects.go;

import java.util.Collections;
import java.util.SortedSet;
import org.cytoscape.gfdnet.model.dataaccess.go.GoTermDAO;

/**
 *
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class GeneProduct implements Comparable {

    private final Integer id;
    private final String description;
    private SortedSet<GOTerm> goTerms;
    public String loadedOntology;

    public GeneProduct(Integer id, String description) {
        this.id = id;
        this.description = description;
    }
    
    public Integer getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
    
    public SortedSet<GOTerm> getGoTerms(String ontology) {
        if (goTerms == null || !(this.loadedOntology == null ? ontology == null : this.loadedOntology.equals(ontology))){
            loadedOntology = ontology;
            goTerms = GoTermDAO.getGoTerms(id, ontology);
        }
        return Collections.unmodifiableSortedSet(goTerms);
    }
        
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GeneProduct other = (GeneProduct) obj;
        return !((this.id == null) ? (other.id != null) : !this.id.equals(other.id));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public int compareTo(Object o) {
        return this.id.compareTo(((GeneProduct) o).id);
    }
}