package org.cytoscape.gfdnet.model.businessobjects.go;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import org.cytoscape.gfdnet.model.dataaccess.go.GoTermDAO;
import org.cytoscape.gfdnet.model.logic.utils.Cache;

/**
 *
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class GeneProduct implements Comparable {

    private final Integer id;
    private final String description;
    private final SortedSet<GoTerm> goTerms;
    public String locadedOntology;

    public GeneProduct(Integer id, String description) {
        this.id = id;
        this.description = description;
        this.goTerms = new TreeSet();
    }
    
    public Integer getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
    
    public SortedSet<GoTerm> getGoTerms(String ontology) {
        if (goTerms.isEmpty() ){
            loadGoTerms(ontology);
        }
        return Collections.unmodifiableSortedSet(goTerms);
    }

    private void loadGoTerms(String ontology){
        SortedSet<GoTerm> goTermsAux = GoTermDAO.getGoTerms(id, ontology);       
        for (GoTerm gt : goTermsAux){
            GoTerm cachedGT = Cache.getGoTerm(gt);
            if(cachedGT != null){
                goTerms.add(cachedGT);
            } else {
                goTerms.add(gt); 
                if(!gt.isRoot()){
                    gt.loadAncestors();
                }
                Cache.addGoTerm(gt);
            }
        }
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