import Common.DatabaseConnection;
import Excel.DataCollection;
import Excel.ExcelLib;
import Log.LogJ;
import Report.ReportMethods;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainManager {
    private static final Logger logger = LogManager.getLogger(MainManager.class);
    private LogJ jl;
    private String environment;

    private String buildUsername;

    private String buildId;
    private String buildNumber;
    private String path;
    private String target;
    private String logPath;
    private String logName;

    private Helper helpObj;
    private ExtentReports extent;
    private ReportMethods reportMethods;
    private  String AppConfiguration_ExcelFileName;
    private DatabaseConnection DataConnection = new DatabaseConnection();

    // Implementation for loading parameters from Excel
    //private ExcelLib excelLib = new ExcelLib();
    private String pathToExcelFiles = "path/to/excel/files"; // Replace with Excel file actual path
    private String AppConfiguration_ExcelFileName = "config.xlsx"; // Replace with actual Excel filename
    private String Environment_ExcelSheetName = "Environment";
    private String DBConfig_ExcelSheetName = "DBConfig";


    public static void main(String[] args) {
        MainManager mainManager = new MainManager();
        mainManager.run();
    }

    public void run() {
        // Initialization
        initializeLogger();
        loadParamsFromAppConfig();
        loadParamsFromExcel();
        initializeReportingFramework();
        initializeHelper();
        initializeElementFactory();

        // Test Connections
        testConnectivity();

        // Execution Set Manager
        ExecutionSetManager executionSetManager = new ExecutionSetManager(/* pass required parameters */);
        executionSetManager.run();
    }

    private void initializeLogger() {
        // Log4j2 is used for logging , initializing  logger

        path = System.getProperty("user.dir");
        target = path + "\\Automation_Report\\" + new SimpleDateFormat("ddMMyyyyHHmmss").format(new Date());
        logPath = target + "\\Logs";
        jl = new LogJ();
        jl.initializeLogger(logPath, logName);
        logger.info("Logger initialized");
    }

    private void initializeHelper() {
        //initializing helper class
        helpObj = new Helper(logger,reportMethods);

    }

    private void initializeReportingFramework() {
        // Extent reporting is used fot reporting , initializing extent reporting

        logger.info("InitializeReportFrameWork : " + new SimpleDateFormat("HH:mm:ss:SSS").format(new Date()));

        String reportPath = target + "\\" + "Report.html";
        String screenshotPath = target + "";
        reportMethods = new ReportMethods(logger, screenshotPath);

        ExtentSparkReporter  sparkReporter = new ExtentSparkReporter(new File(reportPath));
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);

        reportMethods.initializeExtent(target, reportPath, buildUsername, buildId, buildNumber, environment);

        logger.info("InitializeReportFrameWork - Completed : " + new SimpleDateFormat("HH:mm:ss:SSS").format(new Date()));

    }

    private void loadParamsFromAppConfig() {
        // Implementation for loading parameters from AppConfig

        AppConfiguration_ExcelFileName = Helper.initializeVariable("ExcelFileName");
    }

    private void loadParamsFromExcel() {

        // Implementation for loading parameters from Excel
        try {
            List<DataCollection> dataCol = ExcelLib.populateInCollection(pathToExcelFiles + AppConfiguration_ExcelFileName, Environment_ExcelSheetName);
            environment = ExcelLib.readData(dataCol, 1, "Environment");
            buildUsername = ExcelLib.readData(dataCol, 1, "BuildUser");
            buildId = ExcelLib.readData(dataCol, 1, "BuildID");
            buildNumber = ExcelLib.readData(dataCol, 1, "BuildNumber");
            logName = ExcelLib.readData(dataCol, 1, "LogName");

            dataCol = ExcelLib.populateInCollection(pathToExcelFiles + AppConfiguration_ExcelFileName, DBConfig_ExcelSheetName);
            DataConnection.DbName = "Iris";
            DataConnection.DbSid = ExcelLib.readData(dataCol, 1, "IrisServiceName");
            DataConnection.DbUser = ExcelLib.readData(dataCol, 1, "IrisDbUser");
            DataConnection.DbPassword = ExcelLib.readData(dataCol, 1, "IrisDbPassword");
            DataConnection.DbHost = ExcelLib.readData(dataCol, 1, "IrisDbHost");
            DataConnection.DBPort = ExcelLib.readData(dataCol, 1, "IrisDbPort");
            DataConnection.UseSID = ExcelLib.readData(dataCol, 1, "IrisUseSid");
            db = new DatabaseConnection(DataConnection.DbHost, DataConnection.DbSid, DataConnection.DbUser, DataConnection.DbPassword, DataConnection.UseSID, DataConnection.DBPort);

        } catch (Exception e) {
            logger.info("Exception in loadParamsFromExcel: " + e.getMessage());
        }
    }

    private void initializeElementFactory() {
        // Implementation for initializing element factory
    }

    private void testConnectivity() {
        // Implementation for testing connectivity
    }
}