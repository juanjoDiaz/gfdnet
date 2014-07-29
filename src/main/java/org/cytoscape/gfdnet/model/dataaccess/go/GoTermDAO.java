package org.cytoscape.gfdnet.model.dataaccess.go;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.SortedSet;
import java.util.TreeSet;
import org.cytoscape.gfdnet.model.businessobjects.go.GOTerm;
import org.cytoscape.gfdnet.model.dataaccess.DataBase;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class GoTermDAO {

    private static PreparedStatement getGoTermsStatement = null;
    
    public static PreparedStatement getPrepareGetGoTermsStatement() {
        try {
            if (getGoTermsStatement == null || getGoTermsStatement.isClosed()) {
                getGoTermsStatement = DataBase.getPreparedStatement(
                        "SELECT term.id, name, acc " +
                        "FROM term, association " +
                        "WHERE association.term_id = term.id " +
                            "AND association.gene_product_id = ? " +
                            "AND term_type = ? " +
                            "AND is_obsolete = false " +
                            "AND is_relation = false;");
            }
        } catch (SQLException e) {
            System.err.println("Error preparing a statement.\n" + e.getMessage());
        }
        return getGoTermsStatement;
    }

    public static SortedSet<GOTerm> getGoTerms(int geneProductId, String ontology){      
        Object[] queryParams = {geneProductId, ontology};
        ResultSet rs = DataBase.executePreparedStatement(getPrepareGetGoTermsStatement(), queryParams);
        
        SortedSet<GOTerm> goTerms = new TreeSet();
        try {
            while (rs.next()) {
                GOTerm goTerm = new GOTerm(rs.getInt("term.id"), rs.getString("acc"),rs.getString("name"), ontology);
                goTerms.add(goTerm);
            }
        } catch (SQLException e) {
            System.err.println("Error while retrieving GOTerms from the database.\n" + e.getMessage());
        } finally {
            DataBase.closeResultSet(rs);
        }
        
        return goTerms;
    }
}