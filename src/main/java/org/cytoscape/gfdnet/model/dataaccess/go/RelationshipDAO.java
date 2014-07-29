package org.cytoscape.gfdnet.model.dataaccess.go;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.cytoscape.gfdnet.model.businessobjects.go.GOTerm;
import org.cytoscape.gfdnet.model.businessobjects.go.Relationship;
import org.cytoscape.gfdnet.model.dataaccess.Cache;
import org.cytoscape.gfdnet.model.dataaccess.DataBase;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class RelationshipDAO {

    private static PreparedStatement retrieveAncestorStatement = null;
    
    public static PreparedStatement getRetrieveAncestorsStatement() {
        try {
            if (retrieveAncestorStatement == null || retrieveAncestorStatement.isClosed()) {
                retrieveAncestorStatement = DataBase.getPreparedStatement(
                        "SELECT t.id as termId, t.name, " +
                            "t.term_type, t.acc, t2t.id as relationshipId " +
                        "FROM term t, term2term t2t " +
                        "WHERE t2t.term1_id = t.id " +
                            "AND t.is_obsolete = false " +
                            "AND (SELECT name FROM term t2, term2term t2t2 " +
                                "WHERE t2.id = t2t2.relationship_type_id " +
                                    "AND t2t2.id = t2t.id) = \"is_a\" " +
                            "AND t2t.term2_id = ? " +
                            "AND term_type = ?;");
            }
        } catch (SQLException e) {
            System.err.println("Error preparing a statement.\n" + e.getMessage());
        }
        return retrieveAncestorStatement;
    }
    
    public static List<Relationship> loadAncestors(int goTermId, String ontology){      
        Object[] queryParams = {goTermId, ontology};
        ResultSet rs = DataBase.executePreparedStatement(getRetrieveAncestorsStatement(), queryParams);
        
        List<Relationship> relationships=new ArrayList();
        try {
            while (rs.next()) {
                GOTerm parentGOTerm = Cache.getOrAddGoTerm(new GOTerm(rs.getInt("termId"),rs.getString("acc"),rs.getString("name"), ontology));
                Relationship relationship = new Relationship(
                        rs.getInt("relationshipId"), Relationship.is_a,
                        parentGOTerm);
                relationships.add(relationship);
            }
        } catch (SQLException ex) {
            System.err.println("Error while loading ancestors from the database.\n" + ex);
        }
        finally {
            DataBase.closeResultSet(rs);
        }
        return relationships;
    }
}