package org.cytoscape.gfdnet.model.dataaccess.go;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cytoscape.gfdnet.model.businessobjects.go.GoTerm;
import org.cytoscape.gfdnet.model.dataaccess.DataBase;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class GoTermDAO {

    public static SortedSet<GoTerm> getGoTerms(int geneProductId, String ontology){
        SortedSet<GoTerm> goTerms=new TreeSet();

        String sql="SELECT term.id,name,acc,term_type FROM term,association "
                + "WHERE association.term_id=term.id "
                + "AND association.gene_product_id=\"" + Integer.toString(geneProductId) + "\" "
                + "AND term_type =\"" + ontology + "\" "
                + "AND is_obsolete=false AND is_relation=false;";
        ResultSet rs=DataBase.executeQuery(sql);
        try {
            while (rs.next()) {
                GoTerm goTerm = new GoTerm(rs.getInt("term.id"), rs.getString("acc"),rs.getString("name"), ontology);
                goTerms.add(goTerm);
            }
        } catch (SQLException ex) {
            Logger.getLogger(GoTermDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        DataBase.closeConnection(rs);
        
        return goTerms;
    }
}