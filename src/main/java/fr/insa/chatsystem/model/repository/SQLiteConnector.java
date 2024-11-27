package fr.insa.chatsystem.model.repository;

import fr.insa.chatsystem.model.logger.message.ErrorMessages;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

import static fr.insa.chatsystem.Main.self;

/**
 * The SQLiteConnector class provides methods for connecting to an SQLite database, executing SQL statements,
 * and managing database connections and transactions.
 */
public class SQLiteConnector {

    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement preparedStatement;
    private static final Logger LOGGER = LogManager.getLogger(SQLiteConnector.class);

    /**
     * Connects to the SQLite database using the JDBC driver and the specified database URL.
     * Exits the system if an error occurs during the connection.
     */
    public void connectDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            String databaseUrl = "jdbc:sqlite:database-" + self.getAddress().getHostAddress() + ".db";
            connection = DriverManager.getConnection(databaseUrl);
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.error(ErrorMessages.DATABASE_CONNECTION_MESSAGE_ERROR + e.getMessage());
            System.exit(0);
        }
    }

    /**
     * Disconnects from the SQLite database. Logs an error message if disconnection fails.
     */
    public void disconnectDatabase() {
        try {
            connection.close();
        } catch (SQLException e) {
            LOGGER.error(ErrorMessages.DATABASE_CONNECTION_MESSAGE_ERROR + e.getMessage());
        }
    }

    /**
     * Creates an SQL statement for executing queries or updates.
     * Logs an error message if statement creation fails.
     */
    public void createStatement() {
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            LOGGER.error(ErrorMessages.CAN_NOT_EXECUTE_SQL_STATEMENT + e.getMessage());
        }
    }

    /**
     * Executes an SQL update statement. Logs an error message if execution fails.
     *
     * @param query The SQL query to be executed.
     */
    public void statementExecuteUpdate(String query) {
        try {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            LOGGER.error(ErrorMessages.CAN_NOT_EXECUTE_SQL_STATEMENT + e.getMessage());
        }
    }

    /**
     * Executes an SQL query and returns the result set. Logs an error message if execution fails.
     *
     * @param query The SQL query to be executed.
     * @return The result set obtained from executing the query.
     */
    public ResultSet statementExecuteQuery(String query) {
        try {
            return statement.executeQuery(query);
        } catch (SQLException e) {
            LOGGER.error(ErrorMessages.CAN_NOT_EXECUTE_SQL_STATEMENT + e.getMessage());
        }
        return null;
    }

    /**
     * Closes the SQL statement. Logs an error message if closure fails.
     */
    public void closeStatement() {
        try {
            statement.close();
        } catch (SQLException e) {
            LOGGER.error(ErrorMessages.DATABASE_CONNECTION_MESSAGE_ERROR + e.getMessage());
        }
    }

    /**
     * Creates a prepared statement for executing parameterized queries.
     * Logs an error message if statement creation fails.
     *
     * @param query The parameterized SQL query.
     */
    public void createPreparedStatement(String query) {
        try {
            preparedStatement = connection.prepareStatement(query);
        } catch (SQLException e) {
            LOGGER.error(ErrorMessages.CAN_NOT_EXECUTE_SQL_STATEMENT + e.getMessage());
        }
    }

    /**
     * Sets a string parameter in the prepared statement.
     *
     * @param index The parameter index.
     * @param value The string value to be set.
     */
    public void preparedStatementSetString(Integer index, String value) {
        try {
            preparedStatement.setString(index, value);
        } catch (SQLException e) {
            LOGGER.error(ErrorMessages.CAN_NOT_EXECUTE_SQL_STATEMENT + e.getMessage());
        }

    }

    /**
     * Executes an SQL update statement using the prepared statement.
     * Logs an error message if execution fails.
     */
    public void preparedStatementExecuteUpdate() {
        try {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(ErrorMessages.CAN_NOT_EXECUTE_SQL_STATEMENT + e.getMessage());
        }
    }

    /**
     * Executes an SQL query using the prepared statement and returns the result set.
     * Logs an error message if execution fails.
     *
     * @return The result set obtained from executing the prepared statement.
     */
    public ResultSet preparedStatementExecuteQuery() {
        try {
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            LOGGER.error(ErrorMessages.CAN_NOT_EXECUTE_SQL_STATEMENT + e.getMessage());
        }
        return null;
    }

    /**
     * Closes the prepared statement. Logs an error message if closure fails.
     */
    public void closePreparedStatement() {
        try {
            preparedStatement.close();
        } catch (SQLException e) {
            LOGGER.error(ErrorMessages.DATABASE_CONNECTION_MESSAGE_ERROR + e.getMessage());
        }
    }

}
