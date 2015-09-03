package org.cytoscape.gfdnet.model.dataaccess.go;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import org.cytoscape.gfdnet.model.businessobjects.Enums.Ontology;
import org.cytoscape.gfdnet.model.businessobjects.go.GOTerm;
import org.cytoscape.gfdnet.model.dataaccess.DBCache;
import org.cytoscape.gfdnet.model.dataaccess.DataBase;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class GOTermDAO {

    private static PreparedStatement getGoTermsStatement = null;
    
    public static PreparedStatement getPrepareGetGoTermsStatement() {
        if (getGoTermsStatement == null || DataBase.isPreparedStatementClosed(getGoTermsStatement)) {
            getGoTermsStatement = DataBase.getPreparedStatement(
                    "SELECT term.id, name, acc " +
                    "FROM term, association " +
                    "WHERE association.term_id = term.id " +
                        "AND association.gene_product_id = ? " +
                        "AND term_type = ? " +
                        "AND is_obsolete = false " +
                        "AND is_relation = false;");
        }
        return getGoTermsStatement;
    }

    public static Set<GOTerm> getGoTerms(int geneProductId, Ontology ontology) {      
        Object[] queryParams = {geneProductId, ontology.getDBString()};
        ResultSet rs = DataBase.executePreparedStatement(getPrepareGetGoTermsStatement(), queryParams);
        
        Set<GOTerm> goTerms = new HashSet(32, 1);
        try {
            while (rs.next()) {
                GOTerm goTerm = DBCache.goTerms.getOrAdd(new GOTerm(rs.getInt("term.id"), rs.getString("acc"),rs.getString("name"), ontology));
                goTerm.loadAncestors();
                goTerms.add(goTerm);
            }
        } catch (SQLException e) {
            DataBase.logReadResultException("Error while retrieving GOTerms from the database.", e);
        } finally {
            DataBase.closeResultSet(rs);
        }
        
        return goTerms;
    }
}