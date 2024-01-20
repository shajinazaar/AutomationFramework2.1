package Report;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportMethods{
    private static final String DATE_FORMAT = "yyyyMMddHHmmss";
    private static final String SCREENSHOT_FORMAT = "png";

    private Logger logger;
    private ExtentReports extent;
    private String screenshotPath;

    public ReportMethods(Logger logger, String screenshotPath) {
        this.logger = logger;
        this.screenshotPath = screenshotPath;
    }

    public ExtentReports getExtent() {
        return extent;
    }

    public void flushExtent() {
        extent.flush();
    }

    public ExtentTest createTest(String desc) {
        return extent.createTest(desc);
    }

    public void initializeExtent(String target, String reportPath, String buildUsername, String buildId, String buildNumber, String environment) {
        ExtentSparkReporter reporter = new ExtentSparkReporter (reportPath);
        extent = new ExtentReports();
        extent.setSystemInfo("Build username", buildUsername);
        extent.setSystemInfo("Build id", buildId);
        extent.setSystemInfo("Build number", buildNumber);
        extent.setSystemInfo("Report Folder", target);
        extent.setSystemInfo("Environment", environment);
        extent.attachReporter(reporter);
    }

    public void testScreenShotInfo(WebDriver driver, ExtentTest test, String testCaseId, String testInfoDetails) {
        try {
            if (testCaseId == null || testCaseId.isEmpty()) {
                testCaseId = "ex";
            }
            logger.info("Test Info: Test Case ID: " + testCaseId + " <br>" + testInfoDetails);

            if (testInfoDetails != null && !testInfoDetails.isEmpty()) {
                test.log(Status.INFO, "Test Case ID: " + testCaseId + " <br>" + testInfoDetails);
            }

            String screenShotPath = capture(driver, "screenshot_" + new SimpleDateFormat(DATE_FORMAT).format(new Date()) + testCaseId);
            logger.info("Test Info: Screenshot Path " + screenShotPath);
            // Attach screenshot using addScreenCaptureFromPath()
            test.addScreenCaptureFromPath(screenShotPath);
        } catch (IOException e) {
            logger.error("Error capturing screenshot: " + e.getMessage(), e);
        }
        extent.flush();
    }

    public void testFail(WebDriver driver, ExtentTest test, String testCaseId, String testFailDetails) {
        try {
            if (testCaseId == null || testCaseId.isEmpty()) {
                testCaseId = "ex";
            }
            logger.info("Test Failed: Test Case ID: " + testCaseId + " <br>" + testFailDetails);
            test.log(Status.FAIL, "Test Case ID: " + testCaseId + " <br>" + testFailDetails);

            String screenShotPath = capture(driver, "screenshot_" + new SimpleDateFormat(DATE_FORMAT).format(new Date()) + testCaseId);
            logger.info("Test Failed: Screenshot Path " + screenShotPath);
            // Attach screenshot using addScreenCaptureFromPath()
            test.addScreenCaptureFromPath(screenShotPath);
        } catch (IOException e) {
            logger.error("Error capturing screenshot: " + e.getMessage(), e);
        }
        extent.flush();
    }

    public void testFail(ExtentTest test, String testCaseId, String testFailDetails) {
        logger.error("Test Failed: Test Case: " + testCaseId + " <br>" + testFailDetails);
        test.log(Status.FAIL, "Test Case: " + testCaseId + " <br>" + testFailDetails);
        extent.flush();
    }

    public void testInfo(ExtentTest test, String testCaseId, String testFailDetails) {
        logger.info("Test Info: Test Case: " + testCaseId + " <br>" + testFailDetails);
        test.log(Status.INFO, "Test Case: " + testCaseId + " <br>" + testFailDetails);
        extent.flush();
    }

    public void testPass(ExtentTest test, String testCaseId, String testPassDetails) {
        logger.info("Test Passed: Test Case: " + testCaseId + " <br>" + testPassDetails);
        test.log(Status.PASS, "Test Case: " + testCaseId + " <br>" + testPassDetails);
        extent.flush();
    }

    private String capture(WebDriver driver, String screenShotName) throws IOException {
        logger.info("===========CAPTURING SCREENSHOT========");
        logger.info("SCREENSHOT NAME: " + screenShotName);
        TakesScreenshot ts = (TakesScreenshot) driver;
        File screenshotFile = new File(Paths.get(screenshotPath, screenShotName + "." + SCREENSHOT_FORMAT).toString());
        Files.write(screenshotFile.toPath(), ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
        return screenshotFile.getAbsolutePath(); // returns absolute path
    }
}
