package fr.insa.chatsystem.model.repository;

import fr.insa.chatsystem.model.logger.message.ErrorMessages;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The Repository interface provides methods for interacting with databases.
 * It includes a default method for checking the existence of a table in the database.
 */
public class Repository {

    private final Logger LOGGER = LogManager.getLogger(Repository.class);
    public SQLiteConnector sqLiteConnector;

    public Repository() {
        sqLiteConnector = new SQLiteConnector();
    }

    /**
     * Default method to check if a table exists in the database by its table name.
     *
     * @param tableName The name of the table to check for existence.
     * @return true if the table exists, false otherwise.
     */
    public boolean tableExistsByTableName(String tableName) {
        ResultSet rs = null;
        try {
            sqLiteConnector.connectDatabase();

            String query = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";
            sqLiteConnector.createPreparedStatement(query);
            sqLiteConnector.preparedStatementSetString(1, tableName);

            rs = sqLiteConnector.preparedStatementExecuteQuery();
            return rs.next();
        } catch (SQLException | NullPointerException e) {
            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException | NullPointerException e) {
                LOGGER.error(ErrorMessages.CAN_NOT_EXECUTE_SQL_STATEMENT + e.getMessage());
            }
            sqLiteConnector.closePreparedStatement();
            sqLiteConnector.disconnectDatabase();
        }
    }

}
