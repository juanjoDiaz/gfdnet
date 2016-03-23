package org.cytoscape.gfdnet.model.dataaccess;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import org.cytoscape.gfdnet.model.businessobjects.exceptions.DatabaseException;
import org.cytoscape.gfdnet.model.businessobjects.exceptions.DatabaseNotConfiguredException;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Norberto Díaz-Díaz
 * @author Juan José Díaz Montaña
 */
public class Database {
    
    public enum Errors {
        Generic("There was a problem accesing the database."),
        DriverNotFound("Can't find a MySQL JDBC driver."),
        ConnetionFailed("Can't connect to the database."),
        OpenConnectionFailed("Error opening the connection."),
        CloseConnectionFailed("Error closing the connection."),
        PrepareStatementFailed("Error preparing a statement."),
        PrepareStatementIsCloseFailed("Error cheking if a prepared statement is closed."),
        ExecuteQueryFailed("Error in the execution of a SQL query."),
        CloseQueryFailed("Error cleaning the resources for query."),
        ReadResultSetFailed("Error reading the record set."),
        CloseResultSetFailed("Error closing the record set.");
        
        private Errors(String message) {
            this.message = message;
        }

        private final String message;

        public String getMessage() { return message; }
    }

    private static String url;
    private static String user;
    private static String password;
    private static Connection connection = null;
    private static List<Statement> openedStatements = null;

   public static void setConnection(String newUrl, String newUser, String newPassword) {
        url = "jdbc:mysql://" + newUrl;
        user = newUser;
        password = newPassword;
    }

    public static void testConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                connection = DriverManager.getConnection(url, user, password);
                connection.close();
                connection = null;
            }
        } catch (ClassNotFoundException ex) {
            throw new DatabaseException(Errors.DriverNotFound.getMessage(), ex);
        } catch (SQLException ex) {
            throw new DatabaseException(Errors.ConnetionFailed.getMessage(), ex);
        } catch (InstantiationException ex) {
            throw new DatabaseException(Errors.OpenConnectionFailed.getMessage(), ex);
        } catch (IllegalAccessException ex) {
            throw new DatabaseException(Errors.OpenConnectionFailed.getMessage(), ex);
        }
    }

    /**
     * The database connection is implemented using a singleton.
     * This way, there can be one connection at the most
     */
    public static void openConnection()
    {
        if (url == null) {
            throw new DatabaseNotConfiguredException();
        }
        
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
        } catch (ClassNotFoundException ex) {
            System.out.println(Errors.DriverNotFound.getMessage());
            throw new DatabaseException(Errors.DriverNotFound.getMessage(), ex);
        } catch (SQLException ex) {
            System.out.println(Errors.ConnetionFailed.getMessage());
            throw new DatabaseException(Errors.ConnetionFailed.getMessage(), ex);
        } catch (InstantiationException ex) {
            System.out.println(Errors.OpenConnectionFailed.getMessage());
            throw new DatabaseException(Errors.OpenConnectionFailed.getMessage(), ex);
        } catch (IllegalAccessException ex) {
            System.out.println(Errors.OpenConnectionFailed.getMessage());
            throw new DatabaseException(Errors.OpenConnectionFailed.getMessage(), ex);
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
        } catch (SQLException ex) {
            System.out.println(Errors.CloseConnectionFailed.getMessage());
            throw new DatabaseException(ex);
        }
    }
    
    public static PreparedStatement getPreparedStatement(String sql) {
        PreparedStatement stm = null;
        try {
            stm = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            openedStatements.add(stm);
        } catch (SQLException ex) {
            System.out.println(Errors.PrepareStatementFailed.getMessage());
            throw new DatabaseException(ex);
        }
        return stm;
    }
    
    public static boolean isPreparedStatementClosed(PreparedStatement stm) {
        try {
            return stm.isClosed();
        } catch (SQLException ex) {
            System.out.println(Errors.PrepareStatementIsCloseFailed.getMessage());
            throw new DatabaseException(ex);
        }
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
        } catch (SQLException ex) {
            System.out.println(Errors.ExecuteQueryFailed.getMessage());
            throw new DatabaseException(ex);
        }
        return rs;
    }
    
    public static void closeResultSet(ResultSet rs) {
        try {
            rs.close();
        } catch (SQLException ex) {
            System.out.println(Errors.CloseResultSetFailed.getMessage());
            throw new DatabaseException(Errors.CloseResultSetFailed.getMessage(), ex);
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
        } catch (SQLException ex) {
            System.out.println(Errors.ExecuteQueryFailed.getMessage() + "\n" + sql);
            throw new DatabaseException(Errors.ExecuteQueryFailed.getMessage(), ex);
        }
        return rs;
    }
    
    public static void logReadResultException(String message, SQLException ex) {
        System.out.println(message);
        throw new DatabaseException(message, ex);
    }
    
    public static void closeQuery(ResultSet rs) {
        try {
            Statement stm = rs.getStatement();
            rs.close();
            stm.close();
        } catch (SQLException ex) {
            System.out.println(Errors.CloseQueryFailed.getMessage());
            throw new DatabaseException(ex);
        }
    }
}