package org.cytoscape.gfdnet.model.dataaccess.go;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import org.cytoscape.gfdnet.model.businessobjects.go.Enums.Ontology;
import org.cytoscape.gfdnet.model.businessobjects.go.Enums.RelationshipType;
import org.cytoscape.gfdnet.model.businessobjects.go.GOTerm;
import org.cytoscape.gfdnet.model.businessobjects.go.Relationship;
import org.cytoscape.gfdnet.model.dataaccess.DBCache;
import org.cytoscape.gfdnet.model.dataaccess.Database;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class RelationshipDAO {

    private static PreparedStatement retrieveAncestorStatement = null;
    
    public static PreparedStatement getRetrieveAncestorsStatement() {
        if (retrieveAncestorStatement == null || Database.isPreparedStatementClosed(retrieveAncestorStatement)) {
            retrieveAncestorStatement = Database.getPreparedStatement(
                    "SELECT t.id as termId, t.name, " +
                        "t.term_type, t.acc, t2t.id as relationshipId " +
                    "FROM term t, term2term t2t " +
                    "WHERE t2t.term1_id = t.id " +
                        "AND t.is_obsolete = false " +
                        "AND (SELECT name FROM term t2, term2term t2t2 " +
                            "WHERE t2.id = t2t2.relationship_type_id " +
                                "AND t2t2.id = t2t.id) IN ('" + RelationshipType.is_a.getDBString() + "', '" +
                                    RelationshipType.part_of.getDBString() + "', '" + RelationshipType.occurs_in.getDBString() + "') " +
                        "AND t2t.term2_id = ? " +
                        "AND t.term_type = ?;");
        }
        return retrieveAncestorStatement;
    }
    
    public static List<Relationship> loadAncestors(int goTermId, Ontology ontology) {      
        Object[] queryParams = {goTermId, ontology.getDBString()};
        ResultSet rs = Database.executePreparedStatement(getRetrieveAncestorsStatement(), queryParams);
        
        List<Relationship> relationships = new LinkedList();
        try {
            while (rs.next()) {
                GOTerm parentGOTerm = DBCache.goTerms.getOrAdd(new GOTerm(rs.getInt("termId"),rs.getString("acc"),rs.getString("name"), ontology));
                Relationship relationship = new Relationship(
                        rs.getInt("relationshipId"), RelationshipType.is_a,
                        parentGOTerm);
                relationships.add(relationship);
            }
        } catch (SQLException e) {
            Database.logReadResultException("Error while loading ancestors from the database.", e);
        }
        finally {
            Database.closeResultSet(rs);
        }
        // Has to be out of the previous loop so the prepared statement doesn't get re-used.
        for (Relationship relationship : relationships) {
            relationship.getGoTerm().loadAncestors();
        }
        return relationships;
    }
}