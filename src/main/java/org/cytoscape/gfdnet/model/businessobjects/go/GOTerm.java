package org.cytoscape.gfdnet.model.businessobjects.go;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.cytoscape.gfdnet.model.businessobjects.Enums.Ontology;
import org.cytoscape.gfdnet.model.dataaccess.go.RelationshipDAO;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class GOTerm implements Comparable{
    private final int id;
    private final String name;
    private final String description;
    private final Ontology ontology;
    private final Set<Relationship> ancestors;
    private final Set<Relationship> children;
    
    public GOTerm(int id, String name, String description, Ontology ontology) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ontology = ontology;
        this.ancestors = new HashSet();
        this.children = new HashSet();
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }
    
    public boolean isRoot() {
        return Ontology.isOntology(description);
    }
    
    public void loadAncestors() {
        if (ancestors.isEmpty() && !isRoot()) {
            List<Relationship> retrievedAncestors = RelationshipDAO.loadAncestors(id, ontology); 
            for (Relationship ancestor : retrievedAncestors) {
                addAncestor(ancestor);
            }   
        }
    }
    
    public Set<Relationship> getAncestors() {
        return Collections.unmodifiableSet(ancestors);
    }

    public Set<Relationship> getChildren() {
        return Collections.unmodifiableSet(children);
    }

    private boolean addAncestor(Relationship relation) {
        boolean result = ancestors.add(relation);
        result &= addChildren(relation);
        return result;
    }

    private boolean addChildren(Relationship relation) {
        Relationship rel = new Relationship(-1, relation.getRelationshipType(), this);
        return relation.getGoTerm().children.add(rel);
    }

    @Override
    public int compareTo(Object o) {
        return id-((GOTerm)o).id;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GOTerm other = (GOTerm) obj;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
}