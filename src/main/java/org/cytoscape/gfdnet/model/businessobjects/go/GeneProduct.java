package org.cytoscape.gfdnet.model.businessobjects.go;

import org.cytoscape.gfdnet.model.businessobjects.go.Enums.Ontology;
import java.util.Collections;
import java.util.Set;
import org.cytoscape.gfdnet.model.dataaccess.go.GOTermDAO;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class GeneProduct implements Comparable {

    private final Integer id;
    private final String description;
    private Set<GOTerm> goTerms;
    public Ontology loadedOntology;

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
    
    public Set<GOTerm> getGoTerms(Ontology ontology) {
        if (loadedOntology != ontology) {
            goTerms = GOTermDAO.getGoTerms(id, ontology);
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
        final GeneProduct other = (GeneProduct) obj;
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public int compareTo(Object o) {
        return this.id.compareTo(((GeneProduct) o).id);
    }
}