package org.cytoscape.gfdnet.model.dataaccess.go;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.cytoscape.gfdnet.model.dataaccess.DataBase;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class OrganismDAO {
    public static boolean isValid(String genus, String species) {
        DataBase.openConnection();
        String sql = "SELECT * FROM species " +
                "WHERE genus= \"" + genus + "\" " +
                "AND species= \"" + species + "\" LIMIT 1";
        boolean exists = false;
        ResultSet rs = DataBase.executeQuery(sql);
        try {
            exists = rs.next();
        } catch (SQLException e) {
            DataBase.logReadResultException("Error validating organism on the database.", e);
        }
        finally {
            DataBase.closeQuery(rs);
            DataBase.closeConnection();
        }
        return exists;
    }
    
    public static List<String> getGenera() {
        DataBase.openConnection();
        String sql="SELECT s.genus AS genus FROM species s GROUP BY s.genus ORDER BY s.genus;";
        List<String> genus = new ArrayList();
        ResultSet rs = DataBase.executeQuery(sql);
        try {
            while (rs.next()) {
                genus.add(rs.getString("genus"));
            }
        } catch (SQLException e) {
            DataBase.logReadResultException("Error retrieving all existing genera from the database.", e);
        }
        finally {
            DataBase.closeQuery(rs);
            DataBase.closeConnection();
        }
        return genus;
    }

    public static List<String> getSpeciesFromGenus(String genus) {
        DataBase.openConnection();
        String sql = "SELECT species FROM species s WHERE genus=\""+genus+"\" ORDER BY species;";
        List<String> species = new ArrayList();
        ResultSet rs = DataBase.executeQuery(sql);
        try {
            while (rs.next()) {
                species.add(rs.getString("species"));
            }
        } catch (SQLException e) {
            DataBase.logReadResultException("Error retrieving all " + genus + " species from the database.", e);
        }
        finally {
            DataBase.closeQuery(rs);
            DataBase.closeConnection();
        }
        return species;
    }
}