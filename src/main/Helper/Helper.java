import Report.ReportMethods;
import com.aventstack.extentreports.ExtentTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import java.util.Properties;

public class Helper{

    public Helper(Logger logger, ReportMethods report){

        this.logger = logger;
        this.report= report;

    }

   //Logging methods
    private static Logger logger = LogManager.getLogger();

    public void logInfo(String info) {
        logger.info(info);
    }

    public void logError(String error) {
        logger.error(error);
    }

    public Logger getLogger() {
        return logger;
    }


    // Reporting methods
    private ReportMethods report;

    public ReportMethods getReportObj() {
        return report;
    }

    public void reportInfo(String info, ExtentTest test, String testCaseID) {
        logger.info(info);
        report.testInfo(test, testCaseID, info);
    }

    public void reportInfo(WebDriver driver, String info, ExtentTest test, String testCaseID) {
        logger.info(info);
        report.testScreenShotInfo(driver, test, testCaseID, info);
    }

    public void reportPass(String info, ExtentTest test, String testCaseID) {
        logger.info(info);
        report.testPass(test, testCaseID, info);
    }

    public void reportFail(String error, ExtentTest test, String testCaseID) {
        // Consider using logger.error(error) for error reporting
        report.testFail(test, testCaseID, error);
    }

    public void reportFail(WebDriver driver, String error, ExtentTest test, String testCaseID) {
        // Consider using logger.error(error) for error reporting
        report.testFail(driver, test, testCaseID, error);
    }

    public void reportFlush() {
        report.flushExtent();
    }


    //Loading Parameters from App config\properties file
    public static String initializeVariable(String variableName) {
        try {
            logger.info("Initialize: " + variableName);
            return System.getenv(variableName); // Get from environment variables
        } catch (Exception ex) {
            Properties properties = new Properties();
            try {
                properties.load(Helper.class.getResourceAsStream("/app.properties")); // Load properties file
                return properties.getProperty(variableName); // Get from properties file
            } catch (Exception e) {
                logger.error("Unable to initialize variable: " + variableName, e);
                throw new IllegalArgumentException("Variable not found: " + variableName);
            }
        }








    }



}





