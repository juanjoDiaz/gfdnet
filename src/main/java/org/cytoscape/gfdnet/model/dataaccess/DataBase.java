package org.cytoscape.gfdnet.model.dataaccess;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class DataBase {

    private static String url = "jdbc:mysql://localhost/go";
    private static String user = "root";
    private static String password = "root";
    private static Connection connection = null;
    private static List<Statement> openedStatements = null;

    public static void setConnection(String newUrl, String newUser, String newPassword) {
        url = "jdbc:mysql://" + newUrl;
        user = newUser;
        password = newPassword;
    }

    public static void testConnection() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        if (connection == null || connection.isClosed()) {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection(url, user, password);
            openedStatements = new LinkedList<Statement>();
            closeConnection();
        }
    }

    /**
     * The database connection is implemented using a singleton.
     * This way, there can be one connection at the most
     */
    public static void openConnection()
    {
        try
        {
            if (connection == null || connection.isClosed())
            {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                connection = DriverManager.getConnection(url, user, password);
                connection.setAutoCommit (false);
                connection.setReadOnly(true);
                openedStatements = new LinkedList<Statement>();
            }
            else {
                System.err.println("There is already a connection to a database.");
            }
        } catch (SQLException e) {
            System.err.println("Can't connect to the database.\n" + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Can't find a MySQL JDBC driver.\n" + e.getMessage());
        } catch (InstantiationException e) {
            System.err.println("Error opening the connection: \n" + e.getMessage());
        } catch (IllegalAccessException e) {
            System.err.println("Error opening the connection: \n" + e.getMessage());
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                for (Statement stm : openedStatements) {
                    stm.close();
                }
                openedStatements = null;
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            System.err.println("Error closing the connection.\n" + e.getMessage());
        }
    }
    
    public static PreparedStatement getPreparedStatement(String sql) {
        PreparedStatement stm = null;
        try {
            stm = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            openedStatements.add(stm);
        } catch (SQLException e) {
            System.err.println("Error preparing a statement.\n" + e.getMessage());
        }
        return stm;
    }
    
    public static ResultSet executePreparedStatement(PreparedStatement stm, Object[] params) {
        ResultSet rs = null; 
        try {
            for (int i = 0; i < params.length; i++) {
                Object obj = params[i];
                if (obj instanceof Integer) {
                    stm.setInt(i+1, (Integer)obj);
                }
                else if (obj instanceof String) {
                    stm.setString(i+1, (String)obj);
                }                
            }
            rs = stm.executeQuery();
        } catch (SQLException e) {
            System.err.println("Error executing database query.\n" + e.getMessage());
        }
        return rs;
    }
    
    public static void closeResultSet(ResultSet rs) {
        try {
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error closing the record set.\n" + e.getMessage());
        }
    }

   /**
     * Execute the SQL command sent as parameter
     * 
     * @param sql SQL query to be executed
     * @return The resulting ResultSet
     */
    public static ResultSet executeQuery(String sql) {
        ResultSet rs = null;
        try {
            Statement statement = connection.createStatement();
            rs = statement.executeQuery(sql);
        } catch (SQLException e) {
            System.err.println("Error in the execution of the SQL query.\n" +
                    "Wrong query: \n" + sql + "\n" + e.getMessage());
        }
        return rs;
    }
    
    public static void closeQuery(ResultSet rs) {
        try {
            Statement stm = rs.getStatement();
            rs.close();
            stm.close();
        } catch (SQLException e) {
            System.err.println("Error cleaning the resources for query." + "\n" + e.getMessage());
        }
    }
}