import Common.DatabaseConnection;
import Database.DataConnection;
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
    private DatabaseConnection Data = new DatabaseConnection(); //DatabaseConnection class object
    private DataConnection db;                   // DataConnection class object

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
            Data.setDbName("Iris");
            Data.setDbSid(ExcelLib.readData(dataCol, 1, "IrisServiceName"));
            Data.setDbUser(ExcelLib.readData(dataCol, 1, "IrisDbUser"));
            Data.setDbPassword (ExcelLib.readData(dataCol, 1, "IrisDbPassword"));
            Data.setDbHost (ExcelLib.readData(dataCol, 1, "IrisDbHost"));
            Data.setDbPort(ExcelLib.readData(dataCol, 1, "IrisDbPort"));
            Data.setUseSid(ExcelLib.readData(dataCol, 1, "IrisUseSid"));
            db = new DataConnection(Data.getDbHost(),
                    Data.getDbSid(),
                    Data.getDbUser(),
                    Data.getDbPassword(),
                    Data.getUseSid(),
                    Data.getDbPort());

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