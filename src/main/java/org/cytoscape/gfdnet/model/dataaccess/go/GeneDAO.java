package org.cytoscape.gfdnet.model.dataaccess.go;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cytoscape.gfdnet.model.businessobjects.GeneInput;
import org.cytoscape.gfdnet.model.businessobjects.go.GeneProduct;
import org.cytoscape.gfdnet.model.businessobjects.go.Organism;
import org.cytoscape.gfdnet.model.dataaccess.DataBase;
import org.cytoscape.gfdnet.model.logic.utils.CollectionUtil;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class GeneDAO {
    
    public static List<GeneInput> getGenes(Organism organism, List<String> geneNames) {
        SortedSet<String> genesNamesSet = new TreeSet();
        for (String geneName : geneNames) {
            genesNamesSet.add(geneName.toLowerCase());
        } 
        SortedSet<GeneInput> genes = new TreeSet();
        SortedSet<GeneProduct> geneProducts = new TreeSet();

        String sql="SELECT DISTINCT gp.symbol, gp.id, gp.full_name, gps.product_synonym " +
                "FROM gene_product gp " +
                "INNER JOIN species s ON gp.species_id = s.id " +
                "INNER JOIN gene_product_synonym gps ON gp.id = gps.gene_product_id " +
                "WHERE s.genus=\""+ organism.getGenus() + "\" " +
                "AND s.species=\""+ organism.getSpecies() +"\" ";
        int geneNamesSize = geneNames.size();
        if (geneNamesSize == 1){
            String geneName = geneNames.get(0);
            sql += " AND (gp.symbol = \"" + geneName+ "\" OR gps.product_synonym = \"" + geneName+ "\")";
        }
        else if (geneNamesSize > 0){
            for (int i = 0; i < geneNamesSize; i++){
                String geneName = geneNames.get(i);
                if (i == 0){
                    sql += " AND ((gp.symbol = \"" + geneName+ "\" OR gps.product_synonym = \"" + geneName+ "\") OR ";
                }
                else if (i == geneNamesSize-1){
                    sql += "(gp.symbol = \"" + geneName+ "\" OR gps.product_synonym = \"" + geneName+ "\"))";
                }
                else {
                    sql += "(gp.symbol = \"" + geneName+ "\" OR gps.product_synonym = \"" + geneName+ "\") OR ";
                }
            }
        }
        
        sql += ";";
        ResultSet rs=DataBase.executeQuery(sql);
        try {
            while (rs.next()) {
                String symbol = rs.getString("gp.symbol");
                String synonym = rs.getString("gps.product_synonym");
                if (CollectionUtil.search(genesNamesSet, symbol.toLowerCase()) == null){
                    String aux = symbol;
                    symbol = synonym;
                    synonym = aux;
                }
                GeneInput gene = new GeneInput(symbol);
                GeneInput existingGene = CollectionUtil.search(genes, gene);
                if (existingGene == null) {
                    genes.add(gene);
                }
                else
                {
                    gene = existingGene;
                }
                
                GeneProduct geneProduct = new GeneProduct(Integer.parseInt(rs.getString("gp.id")), rs.getString("gp.full_name"));               
                GeneProduct existingGeneProduct = CollectionUtil.search(geneProducts, geneProduct);
                if (existingGeneProduct == null) {
                    geneProducts.add(geneProduct);
                }
                else
                {
                    geneProduct = existingGeneProduct;
                }
                
                gene.addSynonym(synonym);
                gene.addGeneProduct(geneProduct);                
            }
        } catch (SQLException ex) {
            Logger.getLogger(GeneDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        DataBase.closeConnection(rs);
        
        return new LinkedList<GeneInput>(genes);
    }
    
    public static SortedSet<GeneInput> getGenes(Organism organism){
        SortedSet<GeneInput> genes = new TreeSet();
        SortedSet<GeneProduct> geneProducts = new TreeSet();

        String sql="SELECT DISTINCT gp.symbol, gp.id, gp.full_name, gps.product_synonym " +
                "FROM gene_product gp " +
                "INNER JOIN species s ON gp.species_id = s.id " +
                "INNER JOIN gene_product_synonym gps ON gp.id = gps.gene_product_id " +
                "WHERE s.genus=\""+ organism.getGenus() + "\" " +
                "AND s.species=\""+ organism.getSpecies() +"\" ;";
       
        ResultSet rs=DataBase.executeQuery(sql);
        try {
            while (rs.next()) {
                GeneInput gene = new GeneInput(rs.getString("gp.symbol"));
                GeneInput existingGene = CollectionUtil.search(genes, gene);
                if (existingGene == null) {
                    genes.add(gene);
                }
                else
                {
                    gene = existingGene;
                }
                gene.addSynonym(rs.getString("gps.product_synonym"));
                
                GeneProduct geneProduct = new GeneProduct(Integer.parseInt(rs.getString("gp.id")), rs.getString("gp.full_name"));               
                GeneProduct existingGeneProduct = CollectionUtil.search(geneProducts, geneProduct);
                if (existingGeneProduct == null) {
                    geneProducts.add(geneProduct);
                }
                else
                {
                    geneProduct = existingGeneProduct;
                }
                
                gene.addGeneProduct(geneProduct);                
            }
        } catch (SQLException ex) {
            Logger.getLogger(GeneDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        DataBase.closeConnection(rs);

        return genes;
    }
}