package org.cytoscape.gfdnet.model.dataaccess.go;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.cytoscape.gfdnet.model.businessobjects.GeneInput;
import org.cytoscape.gfdnet.model.businessobjects.GenesSearchResult;
import org.cytoscape.gfdnet.model.businessobjects.go.GeneProduct;
import org.cytoscape.gfdnet.model.businessobjects.go.Organism;
import org.cytoscape.gfdnet.model.dataaccess.DBCache;
import org.cytoscape.gfdnet.model.dataaccess.Database;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class GeneDAO {
    public static List<GeneInput> getGenes(Organism organism) {
        return  getGenes(organism, null).found;
    }
        
    public static GenesSearchResult getGenes(Organism organism, List<String> geneNames) {
        String sql="SELECT DISTINCT gp.symbol, gp.id, gp.full_name, gps.product_synonym " +
            "FROM gene_product gp " +
            "INNER JOIN species s ON gp.species_id = s.id " +
            "AND s.genus= ? " +
            "AND s.species= ? " +
            "INNER JOIN gene_product_synonym gps ON gp.id = gps.gene_product_id ";
        
        Set<String> unknownGenes = new HashSet<String>();
        if (geneNames != null) {
            StringBuilder sb = new StringBuilder();
            for (String geneName : geneNames) {
                unknownGenes.add(geneName);
                if (sb.length() != 0) {
                    sb.append(',');
                }
                sb.append("'").append(geneName.replaceAll("'", "''")).append("'");
            }
            sql += "WHERE gp.symbol IN (" + sb + ") OR gps.product_synonym IN (" + sb + ")";
        }
        
        sql += ";";
        
        PreparedStatement stm = Database.getPreparedStatement(sql);
        Object[] queryParams = {organism.getGenus(), organism.getSpecies()};
        ResultSet rs = Database.executePreparedStatement(stm, queryParams);

        Map<String, GeneInput> genes;
        if (geneNames != null) {
            genes = new HashMap<String, GeneInput>(geneNames.size(), 1);
        } else {
            genes = new HashMap<String, GeneInput>();
        }
        
        try {
            while (rs.next()) {
                String symbol = rs.getString("gp.symbol").toUpperCase();
                String synonym = rs.getString("gps.product_synonym").toUpperCase();
                if (geneNames != null) {
                    if(geneNames.contains(synonym)) {
                        String aux = symbol;
                        symbol = synonym;
                        synonym = aux;
                    }
                    
                    unknownGenes.remove(symbol);
                }
                
                GeneInput gene = genes.get(symbol);
                if (gene == null) {
                    gene = new GeneInput(symbol);
                    genes.put(symbol, gene);
                }
                gene.addSynonym(synonym);
                GeneProduct geneProduct = DBCache.geneProducts.getOrAdd(new GeneProduct(rs.getInt("gp.id"), rs.getString("gp.full_name")));
                gene.addGeneProduct(geneProduct);
            }
        } catch (SQLException e) {
            Database.logReadResultException("Error while retrieving genes from the database.", e);
        }
        finally {
            Database.closeResultSet(rs);
        }
        
        return new GenesSearchResult(new ArrayList(genes.values()), new ArrayList(unknownGenes));
    }
}