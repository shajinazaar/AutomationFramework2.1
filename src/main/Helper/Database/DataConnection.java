package Database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataConnection {
    private static final Logger logger = LogManager.getLogger();

    private String dataSource;
    private boolean useSID;
    private String dbUserId;
    private String dbPassword;
    private String dbHost;
    private String dbPort;
    private String connectionString;
    private Connection conn;
    private PreparedStatement preparedStatement;

    public DataConnection(String dbHost, String dataSource, String dbUserId, String dbPassword, String useSID, String port) {
        this.dataSource = dataSource;
        this.dbUserId = dbUserId;
        this.dbPassword = dbPassword;
        this.dbHost = dbHost;
        this.dbPort = port;

        if (Boolean.parseBoolean(useSID)) {
            this.useSID = true;
        } else {
            this.useSID = false;
        }

        this.connectionString = generateConnectionString();
    }

    public void executeQuery(String query) {
        logger.info("Executing Query:" + System.lineSeparator() + query);

        try {
            conn = DriverManager.getConnection(connectionString);
            runQuery(query);
        } catch (SQLException e) {
            logger.error(e + "Unable to execute query in DB");
        } finally {
            closeConnection();
        }
    }

    public List<String> connectToData(String query) {
        logger.info("Executing Query:" + System.lineSeparator() + query + " at Database SID:" + dataSource + " userid:" + dbUserId + " password:" + dbPassword);

        List<String> result = new ArrayList<>();

        try {
            conn = DriverManager.getConnection(connectionString);
            preparedStatement = conn.prepareStatement(query);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(resultSet.getString(1)); // Assuming a single column in the result set
            }
        } catch (SQLException e) {
            logger.error(e + "Unable to connect to data");
        } finally {
            closeConnection();
        }

        for (String s : result) {
            System.out.print(s + " ");
        }
        logger.info(System.lineSeparator());

        return result;
    }

    private String generateConnectionString() {
        return "jdbc:oracle:thin:@" + dbHost + ":" + dbPort + ":" + (useSID ? "SID=" + dataSource : "SERVICE_NAME=" + dataSource);
    }

    private void runQuery(String query) {
        try {
            Statement statement = conn.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            logger.error(e + "Unable to execute query in DB");
        }
    }

    private void closeConnection() {
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            logger.error(e + "Error closing connection");
        }
    }

    public void insertAlert(String DATETIME, String INSTITUTION, String MODULE, String UI_ERROR, String MESSAGE_BODY, String RECIPIENTS) {
        DATETIME = DATETIME.replace("'", "''");
        INSTITUTION = INSTITUTION.replace("'", "''");
        MODULE = MODULE.replace("'", "''");
        UI_ERROR = UI_ERROR.replace("'", "''");
        MESSAGE_BODY = MESSAGE_BODY.replace("'", "''");
        RECIPIENTS = RECIPIENTS.replace("'", "''");

        String query = "INSERT INTO TBL_NI_AUTOMATION_ALERT (ALERT_ID, DATETIME, INSTITUTION, MODULE, UI_ERROR, MESSAGE_BODY, RECIPIENTS, IS_FETCH) " +
                "VALUES (AUTOMATION_ALERT_ID.NEXTVAL, '" + DATETIME + "','" + INSTITUTION + "','" + MODULE + "','" +
                UI_ERROR + "','" + MESSAGE_BODY + "','" + RECIPIENTS + "', '0')";

        executeQuery(query);
    }
}
