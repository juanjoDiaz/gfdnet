package org.cytoscape.gfdnet.model.dataaccess;

import java.sql.*;
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
    private static int opsCount = 0;
    private static final int maxOps = 2000;
    private static Connection connection = null;

    public static void setConnection(String newUrl, String newUser, String newPassword) {
        url = "jdbc:mysql://" + newUrl;
        user = newUser;
        password = newPassword;
    }

    public static void testConnection() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        if (opsCount == 0 || connection.isClosed()) {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection(url, user, password);
            closeConnection();
        }
    }

    /**
     * The database connection is implemented using a singleton.
     * This way, there can be one connection at the most
     */
    private static void createConnection()
    {
        try
        {
            if ((opsCount == 0) || (connection.isClosed()))
            {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                connection = DriverManager.getConnection(url, user, password);
            }
        } catch (SQLException e) {
            System.err.println("Can't connect to the database.\n" + e);
            System.exit(1);
        } catch (ClassNotFoundException ex) {
            System.err.println("Can't find a MySQL JDBC driver.\n" + ex);
            System.exit(2);
        } catch (Exception ex) {
            System.err.println("Error opening the connection: \n" + ex);
            System.exit(3);
        }
    }

    public static void closeConnection(ResultSet rs) {
        try {
            Statement s = rs.getStatement();
            rs.close();
            s.close();
        } catch (SQLException e) {
            System.out.println("Error closing the connection.\n" + e);
        }
        closeConnection();
    }
    
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed() && opsCount >= maxOps) {
                connection.close();
                opsCount = 0;
            }
        } catch (SQLException e) {
            System.out.println("Error closing the connection.\n" + e);
        }
    }

    /**
     * Execute the SQL command sent as parameter
     * 
     * @param sql SQL query to be executed
     * @return The resulting ResultSet
     */
    public static ResultSet executeQuery(String sql) {
        ResultSet res = null;

        createConnection();

        try {
            Statement statement = connection.createStatement();
            res = statement.executeQuery(sql);
        } catch (SQLException e) {
            System.err.println("Error in the execution of the SQL query.\n" +
                    "Wrong query: \n" + sql + "\n" + e);
        } catch (Exception e2) {
            System.err.println("Error in the execution of the SQL query'.\n" + e2);
        }
        opsCount++;
        return res;
    }

    /**
     * Execute a stored procedure using the element of the list as parameters
     * 
     * @param nombreSP Name of the stored procedure
     * @param param List of parameter for the stored procedure
     * @return The resulting ResultSet
     */
    public static ResultSet executeSP(String nombreSP, List param) {
        
        ResultSet res = null;
        String query = "";
        Object o;

        createConnection();

        try {
            int i;
            query = "{ CALL " + nombreSP + "(";

            for (i = 0; i < param.size() - 1; i++) {
                o = param.get(i);
                if (o.getClass().equals(String.class)) {
                    query += "\"" + param.get(i) + "\"";
                } else {
                    query += param.get(i);
                }

                query += ", ";
            }

            o = param.get(i);
            if (o.getClass().equals(String.class)) {
                query += "\"" + param.get(i) + "\"";
            } else {
                query += param.get(i);
            }

            query += ") }";
            CallableStatement cStatement = connection.prepareCall(query);
            res = cStatement.executeQuery();
        } catch (SQLException e) {
            System.err.println("Error in the execution of the SQL stored procedure.\n" +
                    "Wrong stored procedure: \n" + query + "\n" + e);
        } catch (Exception e2) {
            System.err.println("Error in the execution of the SQL stored procedure.\n" + e2);
        }
        opsCount++;
        return res;
    }
}