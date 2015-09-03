package org.cytoscape.gfdnet.model.businessobjects.go;

import org.cytoscape.gfdnet.model.businessobjects.Enums.RelationshipType;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class Relationship {
    private int id;
    private RelationshipType relationshipType;
    private GOTerm goTerm;

    public Relationship(int id, RelationshipType relationshipType, GOTerm goTerm) {
        this.id = id;
        this.relationshipType = relationshipType;
        this.goTerm = goTerm;
    }

    public Relationship(int id, GOTerm goTerm) {
        this(id, null, goTerm);
    }

    public int getId() {
        return id;
    }

    public GOTerm getGoTerm() {
        return goTerm;
    }

    public RelationshipType getRelationshipType() {
        return relationshipType;
    }
}