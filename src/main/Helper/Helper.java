import Report.ReportMethods;
import com.aventstack.extentreports.ExtentTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.platform.commons.util.StringUtils;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.time.Duration;
import java.util.*;


public class Helper{

    private WebDriver driver;
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

    // #region PageLoad
    public void pageLoad(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
        wait.until(driver1 -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
    }

    public void pageLoad(WebDriver driver, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        wait.until(driver1 -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
    }

    public void pageLoad(WebDriver driver, By by) {
        pageLoad(driver);
        logger.info("PageLoad; " + by);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
        wait.until(ExpectedConditions.visibilityOfElementLocated(by));
        wait = new WebDriverWait(driver, Duration.ofSeconds(60));
        wait.until(ExpectedConditions.elementToBeClickable(by));
    }

    public void pageLoad(WebDriver driver, String waitForString, int timeOut) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(waitForString)));
        } catch (Exception e) {
            logger.info("Unable to find " + waitForString + " at pageload");
        }
    }
    // #endregion

    // #region Finders
    public void scrollElementIntoView(WebDriver driver, By by) {
        WebElement element = driver.findElement(by);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        // Wait for scrolling to complete
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.visibilityOf(element));
    }

    public boolean elementExists(WebDriver driver, By by, int timeout) {
        try {
            pageLoad(driver); // Assuming pageLoad handles waiting for page load
            findElement(driver, by, timeout); // Use findElement with wait
            return true;
        } catch (NoSuchElementException ex) {
            return false;
        }
    }

    public WebElement findElement(WebDriver driver, By by, int timeout) {
        try {
            logger.info("FindElement by " + by);
            if (timeout > 0) {
                return new WebDriverWait(driver, Duration.ofSeconds(timeout))
                        .until(ExpectedConditions.presenceOfElementLocated(by));
            } else {
                return driver.findElement(by);
            }
        } catch (NoSuchElementException ex) {
            // Handle exception explicitly for clarity
            throw new NoSuchElementException("Element not found: " + by.toString());
        }
    }

    public WebElement findElementEnabled(WebDriver driver, By by, int timeout) {
        try {
            logger.info("FindElement by " + by);
            if (timeout > 0) {
                return new WebDriverWait(driver, Duration.ofSeconds(timeout))
                        .until(ExpectedConditions.elementToBeClickable(by));
            } else {
                return driver.findElement(by);
            }
        } catch (NoSuchElementException ex) {
            throw new NoSuchElementException("Element not found or not clickable: " + by.toString());
        }
    }

    public String xpathDynamicPosition(String generalPath, String text) {
        logger.info("FindElement by " + generalPath);
        return generalPath.replace("$$REPLACE_HERE$$", text);
    }


    // #endregion

// #region Browsers
    public WebDriver setBrowser(String a) {
        switch (a) {
            case "firefox":
                driver = initializeFirefoxDriver();
                logger.info("Driver Set to FireFox");
                break;
            case "chrome":
                driver = initializeChromeDriver();
                logger.info("Driver Set to Chrome");
                break;
            case "chromeIncognito":
                driver = initializeChromeIncognitoDriver();
                logger.info("Driver Set to Chrome Incognito");
                break;
            case "IE":
                // IEDriver is no longer officially supported in Selenium 4
                logger.warn("IEDriver is not officially supported in Selenium 4. Using EdgeDriver instead.");
                driver = initializeEdgeDriver(); // Use EdgeDriver as a replacement
                break;
            default:
                driver = initializeFirefoxDriver();
                logger.info("Driver Set to FireFox");
                break;
        }

        return driver;
    }

    public WebDriver initializeFirefoxDriver() {
        FirefoxOptions options = new FirefoxOptions();
        options.addPreference("xpinstall.signatures.required", false);
        WebDriver driver = new FirefoxDriver(options); // Use WebDriverManager for management
        return driver;
    }

    public WebDriver initializeChromeDriver() {
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation")); // Hide command prompt
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-backgrounding-occluded-windows");
        WebDriver driver = new ChromeDriver(options); // Use WebDriverManager for management
        driver.manage().window().maximize();
        logger.info("Window Maximized");
        return driver;
    }

    public WebDriver initializeChromeIncognitoDriver() {
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation")); // Hide command prompt
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--incognito");
        WebDriver driver = new ChromeDriver(options); // Use WebDriverManager for management
        driver.manage().window().maximize();
        logger.info("Window Maximized");
        return driver;
    }

    public WebDriver initializeEdgeDriver() {
        EdgeOptions options = new EdgeOptions();
        WebDriver driver = new EdgeDriver(options); // Use WebDriverManager for management
        return driver;
    }

    public List<LogEntry> getBrowserLogs() {
        logger.info("getBrowserLogs");
        // Use the updated logs API in Selenium 4
        return driver.manage().logs().get("browser").getAll();
    }

    // This method seems redundant as it switches to the same window
    public void currentWindowHandle() {
        // String current = driver.getWindowHandle(); // Unnecessary
        // driver.switchTo().window(current);         // Unnecessary
    }

    public void changeHelpObjDriver(WebDriver driverNew) {
        logger.info("Changing Driver property for HelpObj");
        driver = driverNew;
    }

    public WebDriver newTab(WebDriver baseDriver) {
        driver.switchTo().newWindow(WindowType.TAB); // Use newWindow for better compatibility
        return driver;
    }



    // #endregion

    // #region Strings
    public String getText(WebDriver driver, By by) {
        pageLoad(driver, by);
        findElement(driver, by, 45);
        scrollElementIntoView(driver, by);
        return driver.findElement(by).getText();
    }

    public String uppercaseFirst(String s) {
        if (s == null || s.isEmpty()) {
            return "";
        }
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public boolean verifyByText(WebDriver driver, By by, String text) {
        return driver.findElement(by).getText().contains(text);
    }
    // #endregion

    // Pop ups
    public void terminatePopup(WebDriver driver) {
        try {
            logger.info("TerminatePopup:");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.alertIsPresent());

            Alert alert = driver.switchTo().alert();
            alert.accept();
            logger.info("Alert Text:" + alert.getText());
            logger.info("Accept");

            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("someId"))); // Adjust as per your needs

        } catch (NoAlertPresentException ex) {
            logger.info(ex.toString());
            logger.info("Error in Terminate POPUP TerminatePopup");
        }
    }

    public void terminatePopup(WebDriver driver, ExtentTest test) {
        try {
            logger.info("TerminatePopup:");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.alertIsPresent());

            Alert alert = driver.switchTo().alert();
            alert.accept();
            logger.info("Alert Text:" + alert.getText());
            logger.info("Accept");

            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("someId"))); // Adjust as per your needs

        } catch (NoAlertPresentException ex) {
            logger.info(ex.toString());
            logger.info("Error in Terminate POPUP TerminatePopup");
        }
    }

    // #endregion

    // #region Loaders
    public boolean loadElement(WebDriver driver, By by, int time) {
        try {
            logger.info("LoadElement; " + by);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(time));
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            wait = new WebDriverWait(driver, Duration.ofSeconds(time));
            wait.until(ExpectedConditions.elementToBeClickable(by));
            logger.info("Element Loaded");
            return true;
        } catch (Exception ex) {
            logger.info("Element not loaded!");
            logger.info(ex.toString());
            return false;
        }
    }
    // #endregion

    //Send Keys
    public WebDriver sendKeysNumeric(WebDriver driver, By by, String keys, ExtentTest node) {
        pageLoad(driver);
        Actions focus = new Actions(driver);

        WebElement sendKeyToElement = findElement(driver, by, 45);
        scrollElementIntoView(driver, by);

        if (StringUtils.isBlank(keys)) {
            logger.info(by + " keys are empty, hence sendkey is not called");
            return driver;
        }

        focus.moveToElement(sendKeyToElement).build().perform();
        focus.moveToElement(sendKeyToElement).click().build().perform();

        try {
            logger.info("Going to Send Keys to :" + by + " keys:" + keys);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOf(sendKeyToElement));

            sendKeyToElement.sendKeys(keys);
            logger.info("Send Keys to :" + by + " keys:" + keys);

        } catch (Exception ex) {
            logger.info("EXCEPTION while Send Keys ");

            if (!(ex instanceof ElementNotInteractableException)) {
                String infoString = "Provided text was not allowed to be entered by HTML: " + keys + " on  XPATH: " + by;
                if (node != null)
                    report.testInfo(node, "", infoString);
                logger.info(infoString);
            } else {
                report.testFail(driver, node, "", ex.getMessage());
            }
            logger.info(ex.getMessage());
        }

        return driver;
    }

    public WebDriver sendKeys(WebDriver driver, By by, String keys, ExtentTest node) {
        pageLoad(driver);
        Actions focus = new Actions(driver);
        WebElement sendKeyToElement = findElement(driver, by, 45);
        scrollElementIntoView(driver, by);
        sendKeyToElement.clear();
        logger.info(by + " value has been cleared");

        if (StringUtils.isBlank(keys)) {
            logger.info(by + " keys are empty, hence sendkey is not called");
            return driver;
        }

        focus.moveToElement(sendKeyToElement).build().perform();
        focus.moveToElement(sendKeyToElement).click().build().perform();

        try {
            logger.info("Going to Send Keys to :" + by + " keys:" + keys);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOf(sendKeyToElement));

            sendKeyToElement.sendKeys(keys);
            logger.info("Send Keys to :" + by + " keys:" + keys);

        } catch (Exception ex) {
            if (!(ex instanceof ElementNotInteractableException)) {
                String infoString = "Provided text was not allowed to be entered by HTML: " + keys + " on  XPATH: " + by;
                if (node != null)
                    report.testInfo(node, "", infoString);
                logger.info(infoString);
            } else {
                report.testFail(driver, node, "", ex.getMessage());
            }
            logger.info(ex.getMessage());
        }

        return driver;
    }


    public WebDriver copyPaste(WebDriver driver, By by, String keys, ExtentTest node) {
        pageLoad(driver);
        Actions focus = new Actions(driver);

        WebElement sendKeyToElement = findElement(driver, by, 45);
        scrollElementIntoView(driver, by);

        if (StringUtils.isBlank(keys)) {
            logger.info(by + " keys are empty, hence sendkey is not called");
            return driver;
        }

        focus.moveToElement(sendKeyToElement).build().perform();
        focus.moveToElement(sendKeyToElement).click().build().perform();

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.elementToBeClickable(sendKeyToElement));

            // Clipboard.SetText(keys);
            sendKeyToElement.sendKeys(Keys.CONTROL + "v");
            report.testPass(node, "", "Paste action perform successfully on " + by);
        } catch (Exception ex) {
            logger.info("EXCEPTION while Pasting Keys ");
            if (!(ex instanceof ElementNotInteractableException)) {
                String infoString = "Provided text was not allowed to be entered by HTML: " + keys + " on  XPATH: " + by;
                if (node != null)
                    report.testInfo(node, "", infoString);
                logger.info(infoString);
            } else {
                report.testFail(driver, node, "", ex.getMessage());
            }
            logger.info(ex.getMessage());
        }

        return driver;
    }

    // #region Clicks
    public WebDriver buttonClick(WebDriver driver, By by) {
        pageLoad(driver);
        logger.info("Going to JavaClick on :" + by);
        WebElement buttonClick = findElement(driver, by, 60);
        try {
            ((JavascriptExecutor)driver).executeScript("arguments[0].click()", buttonClick);
        } catch(Exception ex) {
            ((JavascriptExecutor)driver).executeScript("arguments[0].click()", buttonClick);
        }

        waitForElementClickable(driver, by, 60);
        pageLoad(driver);
        logger.info("Button Clicked On: " + by);

        return driver;
    }

    public WebDriver buttonClickCard(WebDriver driver, By by) {
        pageLoad(driver);
        logger.info("Going to JavaClick on :" + by);
        WebElement buttonClick = findElement(driver, by, 60);
        ((JavascriptExecutor)driver).executeScript("arguments[0].click()", buttonClick);
        ((JavascriptExecutor)driver).executeScript("arguments[0].click()", buttonClick);

        waitForElementClickable(driver, by, 60);
        pageLoad(driver);
        logger.info("Button Clicked On: " + by);

        return driver;
    }

    public WebDriver buttonClickSimple(WebDriver driver, By by) {
        logger.info("Going to Button Clicked On: " + by);
        pageLoad(driver);

        WebElement buttonClick = findElementEnabled(driver, by, 30);
        buttonClick.click();

        waitForElementClickable(driver, by, 30);
        pageLoad(driver);
        logger.info("Button Clicked On: " + by);
        return driver;
    }

    public WebDriver buttonClickCounters(WebDriver driver, By by) {
        pageLoad(driver);

        WebElement buttonClick = findElementEnabled(driver, by, 30);
        buttonClick.click();

        pageLoad(driver);
        return driver;
    }

    public WebDriver buttonClickSimplePopUpWait(WebDriver driver, By by) {
        logger.info("Button Clicked On: " + by);
        pageLoad(driver);

        WebElement buttonClick = findElement(driver, by, 30);
        buttonClick.click();

        waitForElementClickable(driver, by, 30);
        return driver;
    }

    public WebDriver buttonClickSimpleWithoutWait(WebDriver driver, By by) {
        logger.info("Button Clicked On: " + by);

        WebElement buttonClick = driver.findElement(by);
        buttonClick.click();

        waitForElementClickable(driver, by, 30);
        return driver;
    }

    public WebDriver buttonClickAndTerminate(WebDriver driver, By by) {
        logger.info("Button Clicked On: " + by);

        WebElement buttonClick = driver.findElement(by);
        buttonClick.click();

        waitForElementClickable(driver, by, 30);
        terminatePopup(driver);
        return driver;
    }

    public WebDriver buttonClick(WebDriver driver, WebElement element) {
        logger.info("Going to Button Clicked On: " + element);

        element.click();

        waitForElementClickable(driver, element);
        logger.info("Button Clicked On: " + element);

        return driver;
    }

    public WebDriver doubleClick(WebDriver driver, By by) {
        WebElement element = findElement(driver, by, 60);
        Actions dClickAction = new Actions(driver);
        dClickAction.doubleClick(element).build().perform();

        waitForElementClickable(driver, by, 60);
        pageLoad(driver);
        return driver;
    }

    public WebDriver buttonClickAtIncrement(WebDriver driver, By by) {
        try {
            WebElement buttonClick = findElement(driver, by, 30);
            buttonClick.click();
        } catch (Exception ex) {
            // Handle exception
        }
        return driver;
    }

    public WebDriver buttonClickAndHold(WebDriver driver, By byBtn, By byTxt, String amount) {
        int maxTime;

        if (Integer.parseInt(amount) <= 1000) {
            maxTime = 40;
        }
        if (Integer.parseInt(amount) >= 1000) {
            maxTime = 60 * 2;
        }

        WebElement elemByBtn = findElement(driver, byBtn, 30);
        WebElement elemTxt = findElement(driver, byTxt, 30);

        Actions action = new Actions(driver);
        try {
            action
                    .moveToElement(elemByBtn)
                    .clickAndHold()
                    .perform();

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
            wait.until(ExpectedConditions.textToBePresentInElementValue(elemTxt, amount));
            action.release().perform();
        } catch (Exception ex) {
            logger.info("In catch");
            action.release().perform();
        }
        return driver;
    }
    // #endregion


    private void waitForElementClickable(WebDriver driver, By by, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        wait.until(ExpectedConditions.elementToBeClickable(by));
    }

    private void waitForElementClickable(WebDriver driver, WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    // #region DropDown
    public WebDriver selectElementByText(WebDriver driver, By by, String text) {
        logger.info(by + " Text Entered: " + text);
        logger.info(by + " XPath Entered: " + by.toString());

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // Adjust the timeout as needed

        wait.until(ExpectedConditions.presenceOfElementLocated(by));
        wait.until(ExpectedConditions.visibilityOfElementLocated(by));

        Select selectElementByText = new Select(driver.findElement(by));
        selectElementByText.selectByVisibleText(text);

        wait.until(ExpectedConditions.invisibilityOfElementLocated(by)); // Adjust if needed
        wait.until(ExpectedConditions.stalenessOf(driver.findElement(by))); // Adjust if needed

        pageLoad(driver);
        return driver;
    }

    public WebDriver selectElementByValue(WebDriver driver, By by, String value) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // Adjust the timeout as needed

        wait.until(ExpectedConditions.presenceOfElementLocated(by));
        wait.until(ExpectedConditions.visibilityOfElementLocated(by));

        Select selectElementByValue = new Select(driver.findElement(by));
        selectElementByValue.selectByValue(value);

        wait.until(ExpectedConditions.invisibilityOfElementLocated(by)); // Adjust if needed
        wait.until(ExpectedConditions.stalenessOf(driver.findElement(by))); // Adjust if needed

        pageLoad(driver);
        return driver;
    }


    // #region waiters
    public void waitForFrame(WebDriver driver, By by, int timeInSeconds) {
        try {
            logInfo("Waiting For Frame" + by + " for " + timeInSeconds + " seconds");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeInSeconds));
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
        } catch (Exception ex) {
            logError(ex.getMessage());
        }
    }
    // #endregion

    // #region Setup
    public void teardownTest(WebDriver webDriver) {
        try {
            report.flushExtent();
            webDriver.quit();
        } catch (Exception ex) {
            logInfo("Teardown Failed" + ex.toString());
        }
    }
    // #endregion

 // Getting Elements\Locators from Xml, Method is defined in MainManager class
    public Map<String, String> getXmlKeyValueDictionary(String xmlFilePath) {
        Map<String, String> dictionary = new HashMap<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFilePath);

            NodeList elements = document.getElementsByTagName("Element");
            for (int i = 0; i < elements.getLength(); i++) {
                Element element = (Element) elements.item(i);
                String keyword = element.getAttribute("keyword");
                String locator = element.getAttribute("locator");

                System.out.println(keyword + ": " + locator); // Optional: print values to console

                dictionary.put(keyword, locator);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dictionary;
    }

    // DropDown



    // #region Frames
    public WebDriver switchToFrame(WebDriver driver, By by) {
        logInfo("Switch To Frame By: " + by);
        pageLoad(driver);
        driver.switchTo().frame(driver.findElement(by));
        pageLoad(driver);
        return driver;
    }

    public WebDriver switchToFrameIfAvailable(WebDriver driver, By by) {
        logInfo("Switch To Frame By: " + by);
        // pageLoad(driver); // No need to load the page with respect to the driver when switching to iframe
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(by));
        // logger.Info(driver.PageSource);
        // pageLoad(driver); // No need to load the page with respect to the driver after switching to iframe
        // logger.Info(driver.PageSource);
        return driver;
    }

    public WebDriver switchToDefault(WebDriver driver) {
        logInfo("Switching To Default");
        pageLoad(driver);
        driver.switchTo().parentFrame();
        driver.switchTo().defaultContent();
        pageLoad(driver);
        return driver;
    }


}










