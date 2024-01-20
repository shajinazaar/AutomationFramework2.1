package Common;

public class DatabaseConnection {

    private String dbName;
    private String dbSid;
    private String dbUser;
    private String dbPassword;
    private String dbHost;
    private String dbPort;
    private String useSid;

    // Constructors
    public DatabaseConnection(String dbHost, String dbSid, String dbUser, String dbPassword, String useSid, String dbPort) {
        this.dbHost = dbHost;
        this.dbSid = dbSid;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.useSid = useSid;
        this.dbPort = dbPort;
    }

    public DatabaseConnection() {

    }

    // Getter and Setter methods

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbSid() {
        return dbSid;
    }

    public void setDbSid(String dbSid) {
        this.dbSid = dbSid;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String getDbHost() {
        return dbHost;
    }

    public void setDbHost(String dbHost) {
        this.dbHost = dbHost;
    }

    public String getDbPort() {
        return dbPort;
    }

    public void setDbPort(String dbPort) {
        this.dbPort = dbPort;
    }

    public String getUseSid() {
        return useSid;
    }

    public void setUseSid(String useSid) {
        this.useSid = useSid;
    }

}
