package org.cytoscape.gfdnet.model.businessobjects.go;

/**
 *
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class Relationship {
    private int id;
    private String relationshipType;
    private GoTerm goTerm;
    public static String is_a="is_a",
                         part_of="part_of",
                         regulate="regulates",
                         positive_regulate="positively_regulates", 
                         negative_regulate="negatively_regulates";

    public Relationship(int id, String tipoRelacion, GoTerm goTerm) {
        this.id = id;
        this.relationshipType = tipoRelacion;
        this.goTerm = goTerm;
    }

    public Relationship(int id, GoTerm goTerm) {
        this(id,"",goTerm);
    }


    public int getId() {
        return id;
    }

    public GoTerm getGoTerm() {
        return goTerm;
    }

    public void setGoTerm(GoTerm goTerm) {
        this.goTerm = goTerm;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }
}