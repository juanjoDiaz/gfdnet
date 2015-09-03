package org.cytoscape.gfdnet.model.dataaccess.go;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.cytoscape.gfdnet.model.businessobjects.GeneInput;
import org.cytoscape.gfdnet.model.businessobjects.go.GeneProduct;
import org.cytoscape.gfdnet.model.businessobjects.go.Organism;
import org.cytoscape.gfdnet.model.dataaccess.DBCache;
import org.cytoscape.gfdnet.model.dataaccess.DataBase;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class GeneDAO {    
    public static List<GeneInput> getGenes(Organism organism, List<String> geneNames) {
        StringBuilder sb = new StringBuilder();
        for (String geneName : geneNames) {
            if (sb.length() != 0) {
                sb.append(',');
            }
            sb.append("'").append(geneName).append("'");
        }
        String sql="SELECT DISTINCT gp.symbol, gp.id, gp.full_name, gps.product_synonym " +
            "FROM gene_product gp " +
            "INNER JOIN species s ON gp.species_id = s.id " +
            "AND s.genus=\""+ organism.getGenus() + "\" " +
            "AND s.species=\""+ organism.getSpecies() +"\" " +
            "INNER JOIN gene_product_synonym gps ON gp.id = gps.gene_product_id " +
            "WHERE gp.symbol IN (" + sb + ") " +
            "AND gps.product_synonym IN (" + sb + ");";

        Set<String> genesNamesSet = new HashSet(geneNames.size(), 1);
        for (String geneName : geneNames) {
            genesNamesSet.add(geneName.toLowerCase());
        } 
        
        ResultSet rs = DataBase.executeQuery(sql);

        Map<String, GeneInput> genes = new HashMap<String, GeneInput>(geneNames.size(), 1);
        try {
            while (rs.next()) {
                String symbol = rs.getString("gp.symbol").toUpperCase();
                String synonym = rs.getString("gps.product_synonym").toUpperCase();
                if (genes.containsKey(synonym)) {
                    String aux = symbol;
                    symbol = synonym;
                    synonym = aux;
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
            DataBase.logReadResultException("Error while retrieving genes from the database.", e);
        }
        finally {
            DataBase.closeResultSet(rs);
        }
        
        return new LinkedList<GeneInput>(genes.values());
    }
    
    public static Set<GeneInput> getGenes(Organism organism) {
        String sql="SELECT DISTINCT gp.symbol, gp.id, gp.full_name, gps.product_synonym " +
                "FROM gene_product gp " +
                "INNER JOIN species s ON gp.species_id = s.id " +
                "INNER JOIN gene_product_synonym gps ON gp.id = gps.gene_product_id " +
                "WHERE s.genus=\""+ organism.getGenus() + "\" " +
                "AND s.species=\""+ organism.getSpecies() +"\" ;";
       
        ResultSet rs=DataBase.executeQuery(sql);
        
        Map<String, GeneInput> genes = new HashMap<String, GeneInput>();
        try {
            while (rs.next()) {
                String symbol = rs.getString("gp.symbol").toUpperCase();
                String synonym = rs.getString("gps.product_synonym").toUpperCase();
                if (genes.containsKey(synonym)) {
                    String aux = symbol;
                    symbol = synonym;
                    synonym = aux;
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
            DataBase.logReadResultException("Error while retrieving genes from the database.", e);
        }
        finally {
            DataBase.closeResultSet(rs);
        }

        return new HashSet<GeneInput>(genes.values());
    }
}