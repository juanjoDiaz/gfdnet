package org.cytoscape.gfdnet.model.dataaccess.go;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cytoscape.gfdnet.model.dataaccess.DataBase;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class OrganismDAO {
    public static boolean isValid(String genus, String species){
        boolean exists = false;
        String sql="SELECT * FROM species "
                + "WHERE genus= \"" + genus + "\" "
                + "AND species= \"" + species + "\" LIMIT 1";
        ResultSet rs = DataBase.executeQuery(sql);
        try {
            exists = rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(RelationshipDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        DataBase.closeConnection(rs);

        return exists;
    }
    
    public static List<String> getGenera(){
        List<String> genus = new ArrayList();

        String sql="select s.genus AS genus from species s group by s.genus order by s.genus;";
        ResultSet rs=DataBase.executeQuery(sql);
        try {
            while (rs.next()) {
                genus.add(rs.getString("genus"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(RelationshipDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        DataBase.closeConnection(rs);

        return genus;
    }

    public static List<String> getSpeciesFromGenus(String genus){
         List<String> species = new ArrayList();

        String sql="SELECT species FROM species s where genus=\""+genus+"\" order by species;";
        ResultSet rs=DataBase.executeQuery(sql);
        try {
            while (rs.next()) {
                species.add(rs.getString("species"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(RelationshipDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        DataBase.closeConnection(rs);

        return species;
    }
}