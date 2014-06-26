package org.cytoscape.gfdnet.model.dataaccess.go;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cytoscape.gfdnet.model.businessobjects.go.GoTerm;
import org.cytoscape.gfdnet.model.businessobjects.go.Relationship;
import org.cytoscape.gfdnet.model.dataaccess.DataBase;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class RelationshipDAO {

    public static List<Relationship> loadAncestors(int goTermId, String ontology){
        List<Relationship> relationships = retrieveAncestor(goTermId, ontology);
        for(Relationship relationship : relationships) {
            updateRelationType(relationship);
        }
        return relationships;
    }

    private static List<Relationship> retrieveAncestor(int goTermId, String ontology){
        List<Relationship> relationships=new ArrayList();

        String sql = "SELECT t.id,t.name,t.term_type,t.acc,t2t.id FROM term t, term2term t2t"
                + " WHERE t2t.term1_id=t.id"
                + " AND t.is_obsolete=false"
                + " AND t2t.term2_id=\""+ Integer.toString(goTermId) +"\""
                + " AND term_type=\""+ ontology +"\";";
        ResultSet rs = DataBase.executeQuery(sql);
        try {
            while (rs.next()) {

                GoTerm goTermParent = new GoTerm(rs.getInt("t.id"),rs.getString("t.acc"),rs.getString("t.name"), ontology);
                Relationship relationship = new Relationship(rs.getInt("t2t.id"),goTermParent);
                relationships.add(relationship);
            }
        } catch (SQLException ex) {
            Logger.getLogger(RelationshipDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        DataBase.closeConnection(rs);

        return relationships;
    }

    private static void updateRelationType(Relationship relationship){
        String sql = "SELECT name from term t, term2term t2t WHERE t.id=t2t.relationship_type_id and" +
                " t2t.id="+relationship.getId()+";";
        ResultSet rs = DataBase.executeQuery(sql);
        try {
            if (rs.next()) {
                relationship.setRelationshipType(rs.getString("name"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(RelationshipDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        DataBase.closeConnection(rs);
    }
}