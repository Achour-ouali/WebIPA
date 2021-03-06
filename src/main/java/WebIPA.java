import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WebIPA {
    package common;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.LogStatus;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;
import reporting.ExtentManager;
import reporting.ExtentTestManager;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
    public class WebAPI {

        //ExtentReport
        public static ExtentReports extent;
        @BeforeSuite
        public void extentSetup(ITestContext context) {
            ExtentManager.setOutputDirectory(context);
            extent = ExtentManager.getInstance(); }

        @BeforeMethod
        public void startExtent(Method method) {
            String className = method.getDeclaringClass().getSimpleName();
            String methodName = method.getName().toLowerCase();
            ExtentTestManager.startTest(method.getName());
            ExtentTestManager.getTest().assignCategory(className);}

        protected String getStackTrace(Throwable t) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            return sw.toString();}

        @AfterMethod
        public void afterEachTestMethod(ITestResult result) {
            ExtentTestManager.getTest().getTest().setStartedTime(getTime(result.getStartMillis()));
            ExtentTestManager.getTest().getTest().setEndedTime(getTime(result.getEndMillis()));
            for (String group : result.getMethod().getGroups()) {
                ExtentTestManager.getTest().assignCategory(group); }
            if (result.getStatus() == 1) {
                ExtentTestManager.getTest().log(LogStatus.PASS, "Test Passed");
            } else if (result.getStatus() == 2) {
                ExtentTestManager.getTest().log(LogStatus.FAIL, getStackTrace(result.getThrowable()));
            } else if (result.getStatus() == 3) {
                ExtentTestManager.getTest().log(LogStatus.SKIP, "Test Skipped"); }
            ExtentTestManager.endTest();
            extent.flush();
            if (result.getStatus() == ITestResult.FAILURE) {
                captureScreenshot(driver, result.getName()); }
            driver.quit(); }

        @AfterSuite
        public void generateReport() {
            extent.close();
        }

        //calender
        private Date getTime(long millis) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(millis);
            return calendar.getTime(); }

        //Browser SetUp
        public static WebDriver driver = null;
        public String browserstack_username = "";
        public String browserstack_accesskey = "";
        public String saucelabs_username = "";
        public String saucelabs_accesskey = "";
        @Parameters({"useCloudEnv", "cloudEnvName", "os", "os_version", "browserName", "browserVersion", "url"})
        //browser step for single module
        @BeforeMethod
        public void setUp(@Optional("false") boolean useCloudEnv, @Optional("false") String cloudEnvName,
                          @Optional("os") String os, @Optional("x") String os_version, @Optional("chrome") String browserName, @Optional("83")
                                  String browserVersion, @Optional("https://www.netflix.com") String url) throws IOException {
            if (useCloudEnv == true) {
                if (cloudEnvName.equalsIgnoreCase("browserstack")) {
                    getCloudDriver(cloudEnvName, browserstack_username, browserstack_accesskey, os, os_version, browserName, browserVersion);
                } else if (cloudEnvName.equalsIgnoreCase("saucelabs")) {
                    getCloudDriver(cloudEnvName, saucelabs_username, saucelabs_accesskey, os, os_version, browserName, browserVersion); }
            } else {
                getLocalDriver(os, browserName); }
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            //driver.manage().timeouts().pageLoadTimeout(25, TimeUnit.SECONDS);
            driver.get(url);
            //driver.manage().window().maximize();
        }

        //driver setup the driver on the machine
        public WebDriver getLocalDriver(@Optional("mac") String OS, String browserName) {
            if (browserName.equalsIgnoreCase("chrome")) {
                if (OS.equalsIgnoreCase("OS X")) {
                    System.setProperty("webdriver.chrome.driver", "../Generic/BrowserDriver/mac/chromedriver");
                } else if (OS.equalsIgnoreCase("Windows")) {
                    System.setProperty("webdriver.chrome.driver", "../Generic/BrowserDriver/windows/chromedriver.exe"); }
                driver = new ChromeDriver();
            } else if (browserName.equalsIgnoreCase("chrome-options")) {
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--disable-notifications");
                if (OS.equalsIgnoreCase("OS X")) {
                    System.setProperty("webdriver.chrome.driver", "../Generic/BrowserDriver/mac/chromedriver");
                } else if (OS.equalsIgnoreCase("Windows")) {
                    System.setProperty("webdriver.chrome.driver", "../Generic/BrowserDriver/windows/chromedriver.exe"); }
                driver = new ChromeDriver(options);
            } else if (browserName.equalsIgnoreCase("firefox")) {
                if (OS.equalsIgnoreCase("OS X")) {
                    System.setProperty("webdriver.gecko.driver", "../Generic/BrowserDriver/mac/geckodriver");
                } else if (OS.equalsIgnoreCase("Windows")) {
                    System.setProperty("webdriver.gecko.driver", "../Generic/BrowserDriver/windows/geckodriver.exe"); }
                driver = new FirefoxDriver();
            } else if (browserName.equalsIgnoreCase("ie")) {
                System.setProperty("webdriver.ie.driver", "../Generic/BrowserDriver/windows/IEDriverServer.exe");
                driver = new InternetExplorerDriver(); }
            return driver; }

        //setup depends on the machine system
        public WebDriver getCloudDriver(String envName, String envUsername, String envAccessKey, String os, String os_version, String browserName,
                                        String browserVersion) throws IOException {
            DesiredCapabilities caps = new DesiredCapabilities();
            caps.setCapability("os", "OS X");
            caps.setCapability("os_version", "Catalina");
            caps.setCapability("browser", "Safari");
            caps.setCapability("browser_version", "11.0");
            caps.setCapability("build", "1");
            caps.setCapability("browserstack.local", "false");
            caps.setCapability("browserstack.timezone", "New York");
            caps.setCapability("browserstack.selenium_version", "3.5.2");
            caps.setCapability("browserstack.safari.enablePopups", "true");
            caps.setCapability("browserstack.safari.allowAllCookies", "true");
            if (envName.equalsIgnoreCase("Saucelabs")) {
                //resolution for Saucelabs
                driver = new RemoteWebDriver(new URL("http://" + envUsername + ":" + envAccessKey +
                        "@ondemand.saucelabs.com:80/wd/hub"), caps);
            } else if (envName.equalsIgnoreCase("Browserstack")) {
                caps.setCapability("resolution", "1024x768");
                driver = new RemoteWebDriver(new URL("http://" + envUsername + ":" + envAccessKey +
                        "@hub-cloud.browserstack.com/wd/hub"), caps); }
            return driver; }
        //clean up after methode
        @AfterMethod(alwaysRun = true)
        public void cleanUp() {
            //driver.close();
            driver.quit(); }

        /* _______________________________________________ HELPER METHODES  ___________________________________________________*/


        //convert to string
        public static String convertToString(String st) {
            String splitString = "";
            splitString = StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(st), ' ');
            return splitString; }

        //--------------------------------------Clicking methodes---------------------------------------------------------
        //click on element by locator(css,or class name,or id ,or xpath)
        public void clickOnElement(String locator) {
            try {
                driver.findElement(By.cssSelector(locator)).click();
            } catch (Exception ex) {
                try {
                    driver.findElement(By.className(locator)).click();
                } catch (Exception ex2) {
                    try {
                        driver.findElement(By.id(locator)).click();
                    } catch (Exception ex3) {
                        driver.findElement(By.xpath(locator)).click();
                    } } } }

        //click on element css.xpath,or id and driver(locator,driver)
        public static void clickOnElement(String locator, WebDriver driver1) {
            try {
                driver1.findElement(By.cssSelector(locator)).click();
            } catch (Exception ex1) {
                try {
                    driver1.findElement(By.xpath(locator)).click();
                } catch (Exception ex2) {
                    driver1.findElement(By.id(locator)).click(); } } }

        // click by xpath
        public void clickByXpath(String locator) {
            driver.findElement(By.xpath(locator)).click(); }

        //click and find by css and give the value at the page it will continue by it self
        public void takeEnterKeys(String locator) {
            driver.findElement(By.cssSelector(locator)).sendKeys(Keys.ENTER); }

        //************************************Get text list url title*************************************
        //get list by id locator
        public List<WebElement> getListOfWebElementsById(String locator) {
            List<WebElement> list = new ArrayList<WebElement>();
            list = driver.findElements(By.id(locator));
            System.out.println(list);
            return list; }

        //get text by css
        public static List<String> getTextFromWebElements(String locator) {
            List<WebElement> element = new ArrayList<WebElement>();
            List<String> text = new ArrayList<String>();
            element = driver.findElements(By.cssSelector(locator));
            for (WebElement web : element) {
                String st = web.getText();
                text.add(st);
                System.out.println(text);
            }

            return text;
        }

        //get textby locator ,driver
        public static List<String> getTextFromWebElements(String locator, WebDriver driver1) {
            List<WebElement> element = new ArrayList<WebElement>();
            List<String> text = new ArrayList<String>();
            element = driver1.findElements(By.cssSelector(locator));
            for (WebElement web : element) {
                String st = web.getText();
                text.add(st);
            }

            return text;
        }

        //get list text
        public static List<WebElement> getListOfWebElementsByXPath(String locator) {
            List<WebElement> text = new ArrayList<WebElement>();
            text = driver.findElements(By.xpath(locator));
            System.out.println(text);
            return text;
        }

        //get list
        public static List<WebElement> getListOfWebElementsByCss(String locator, WebDriver driver1) {
            List<WebElement> list = new ArrayList<WebElement>();
            list = driver1.findElements(By.cssSelector(locator));
            return list;
        }

        //get list
        public List<WebElement> getListOfWebElementsByXpath(String locator) {
            List<WebElement> list = new ArrayList<WebElement>();
            list = driver.findElements(By.xpath(locator));
            System.out.println(list);
            return list;
        }

        //get current url
        public String getCurrentPageUrl() {
            String url = driver.getCurrentUrl();
            return url;
        }

        //get text by css
        public String getTextByCss(String locator) {
            String st = driver.findElement(By.cssSelector(locator)).getText();
            return st;
        }

        // get text by xpath
        public String getTextByXpath(String locator) {
            String st = driver.findElement(By.xpath(locator)).getText();
            return st;
        }

        //get text by id
        public String getTextById(String locator) {
            return driver.findElement(By.id(locator)).getText();
        }

        //get text by name
        public String getTextByName(String locator) {
            String st = driver.findElement(By.name(locator)).getText();
            return st;
        }

        //get list of string
        public List<String> getListOfString(List<WebElement> list) {
            List<String> items = new ArrayList<String>();
            for (WebElement element : list) {
                items.add(element.getText());
            }
            return items;
        }

//**************************** Handling New Tabs *******************************************************

        public static WebDriver handleNewTab(WebDriver driver1) {
            String oldTab = driver1.getWindowHandle();
            List<String> newTabs = new ArrayList<String>(driver1.getWindowHandles());
            newTabs.remove(oldTab);
            driver1.switchTo().window(newTabs.get(0));
            return driver1; }
        public static boolean isPopUpWindowDisplayed(WebDriver driver1, String locator) {
            boolean value = driver1.findElement(By.cssSelector(locator)).isDisplayed();
            return value; }

        // Customer Made Helper Methods for Amex.com
        public void brokenLink() throws IOException {
            //Step:1-->Get the list of all the links and images
            List<WebElement> linkslist = driver.findElements(By.tagName("a"));
            linkslist.addAll(driver.findElements(By.tagName("img")));

            System.out.println("Total number of links and images--------->>> " + linkslist.size());

            List<WebElement> activeLinks = new ArrayList<WebElement>();
            //Step:2-->Iterate linksList: exclude all links/images which does not have any href attribute
            for (int i = 0; i < linkslist.size(); i++) {
                //System.out.println(linkslist.get(i).getAttribute("href"));
                if (linkslist.get(i).getAttribute("href") != null && (!linkslist.get(i).getAttribute("href").contains("javascript") && (!linkslist.get(i).getAttribute("href").contains("mailto")))) {
                    activeLinks.add(linkslist.get(i));
                }
            }
            System.out.println("Total number of active links and images-------->>> " + activeLinks.size());

            //Step:3--> Check the href url, with http connection api
            for (int j = 0; j < activeLinks.size(); j++) {

                HttpURLConnection connection = (HttpURLConnection) new URL(activeLinks.get(j).getAttribute("href")).openConnection();

                connection.connect();
                String response = connection.getResponseMessage();
                connection.disconnect();
                System.out.println(activeLinks.get(j).getAttribute("href") + "--------->>> " + response); } }
        //handling Alert
        public void okAlert() {
            Alert alert = driver.switchTo().alert();
            alert.accept();
        }

        public void cancelAlert() {
            Alert alert = driver.switchTo().alert();
            alert.dismiss();
        }

        //iFrame Handle
        public void iframeHandle(WebElement element) {
            driver.switchTo().frame(element);
        }

        //get Links
        public void getLinks(String locator) {
            driver.findElement(By.linkText(locator)).findElement(By.tagName("a")).getText();
        }

        //Taking Screen shots
        public void takeScreenShot() throws IOException {
            File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            //FileUtils.copyFile(file, new File("screenShots.png"));
        }

        public static void captureScreenshot(WebDriver driver, String screenshotName) {
            DateFormat df = new SimpleDateFormat("M-d-y");
            Date date = new Date();
            df.format(date);
            System.setProperty("current.date", date.toString().replace(" ", "_").replace(":", "_"));
            Date d = new Date();
            File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            try {
                FileUtils.copyFile(file,
                        new File(System.getProperty("user.dir") + "/Screenshots/" + screenshotName + " " + df.format(date) + ".png"));
                System.out.println("Screenshot captured");
            } catch (Exception e) {
                System.out.println("Exception while taking screenshot " + e.getMessage()); } }

//**************************   Synchronization   **********************************************************

        public void waitUntilClickAble(By locator) {
            WebDriverWait wait = new WebDriverWait(driver, 10);
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        }

        public void waitUntilSelectable(By locator) {
            WebDriverWait wait = new WebDriverWait(driver, 10);
            boolean element = wait.until(ExpectedConditions.elementToBeSelected(locator));
        }

        public void upLoadFile(String locator, String path) {
            driver.findElement(By.cssSelector(locator)).sendKeys(path);
        /* path example to upload a file/image
           path= "C:\\Users\\rrt\\Pictures\\ds1.png";
         */
        }

        public void selectOptionByVisibleText(WebElement element, String value) {
            Select select = new Select(element);
            select.selectByVisibleText(value);
        }

        public void selectOptionByIndex(WebElement element, int value) {
            Select select = new Select(element);
            select.selectByIndex(value);
        }

        //wait the page until the locator section will be vissible
        public void waitUntilVisible(By locator) {
            WebDriverWait wait = new WebDriverWait(driver, 10);
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator)); }


//*************************************** clear field ********************************

        //clear input field
        public void clearInputField(String locator) {
            driver.findElement(By.cssSelector(locator)).clear(); }
        public void clearInput(String locator) {
            driver.findElement(By.cssSelector(locator)).clear();
        }
        public void clearField(String locator) {
            driver.findElement(By.id(locator)).clear(); }public void clearInputBox(WebElement webElement) {
            webElement.clear();
        }
        public String getTextByWebElement(WebElement webElement) {
            String text = webElement.getText();
            return text; }

//*************************************  typing methodes ********************************************

        //type on element css or idd locator and the key value(locator,value)
        public void typeOnInputField(String locator, String value) {
            try {
                driver.findElement(By.cssSelector(locator)).sendKeys(value);
            } catch (Exception ex) {
                driver.findElement(By.id(locator)).sendKeys(value); } }

        //type by css
        public void typeByCss(String locator, String value) {
            driver.findElement(By.cssSelector(locator)).sendKeys(value); }
        //type by css end enter value
        public void typeByCssNEnter(String locator, String value) {
            driver.findElement(By.cssSelector(locator)).sendKeys(value, Keys.ENTER); }
        //type by xpath
        public void typeByXpath(String locator, String value) {
            driver.findElement(By.xpath(locator)).sendKeys(value); }

        //type on element by using css and give value during the test on the window running
        public void keysInput(String locator) {
            driver.findElement(By.cssSelector(locator)).sendKeys(Keys.ENTER); }

        //type on element (webelement,value)
        public void inputValueInTextBoxByWebElement(WebElement webElement, String value) {
            webElement.sendKeys(value + Keys.ENTER); }
        //type on element by xpath or css and sendkey value
        public void typeOnElement(String locator, String value) {
            try {
                driver.findElement(By.cssSelector(locator)).sendKeys(value);
            } catch (Exception ex) {
                driver.findElement(By.xpath(locator)).sendKeys(value); } }
        //type on element css locator ,sendkey value
        public static void typeOnElementNEnter(String locator, String value) {
            try {
                driver.findElement(By.cssSelector(locator)).sendKeys(value, Keys.ENTER);
            } catch (Exception ex1) {
                try {
                    System.out.println("First Attempt was not successful");
                    driver.findElement(By.name(locator)).sendKeys(value, Keys.ENTER);
                } catch (Exception ex2) {
                    try {
                        System.out.println("Second Attempt was not successful");
                        driver.findElement(By.xpath(locator)).sendKeys(value, Keys.ENTER);
                    } catch (Exception ex3) {
                        System.out.println("Third Attempt was not successful");
                        driver.findElement(By.id(locator)).sendKeys(value, Keys.ENTER); } } } }

        //type on element by locator css/id/or name and passe the value(locator,value)
        public void typeOnInputBox(String locator, String value) {
            try {
                driver.findElement(By.id(locator)).sendKeys(value, Keys.ENTER);
            } catch (Exception ex1) {
                System.out.println("ID locator didn't work");
            }
            try {
                driver.findElement(By.name(locator)).sendKeys(value, Keys.ENTER);
            } catch (Exception ex2) {
                System.out.println("Name locator didn't work");
            }
            try {
                driver.findElement(By.cssSelector(locator)).sendKeys(value, Keys.ENTER);
            } catch (Exception ex3) {
                System.out.println("CSS locator didn't work");
            } }

        //type on element (csslocator, sendKey value ,driver)
        public static void typeOnElementNEnter(String locator, String value, WebDriver driver1) {
            try {
                driver1.findElement(By.cssSelector(locator)).sendKeys(value, Keys.ENTER);
            } catch (Exception ex1) {
                try {
                    System.out.println("First Attempt was not successful");
                    driver1.findElement(By.id(locator)).sendKeys(value, Keys.ENTER);
                } catch (Exception ex2) {
                    try {
                        System.out.println("Second Attempt was not successful");
                        driver1.findElement(By.name(locator)).sendKeys(value, Keys.ENTER);
                    } catch (Exception ex3) {
                        System.out.println("Third Attempt was not successful");
                        driver1.findElement(By.xpath(locator)).sendKeys(value, Keys.ENTER); } } } }


//***************************************** Mouse hover**************************************************

        // method to hover mouse and click*
        public static void HoverMouseAndClickt(WebDriver driver, WebElement element) {
            Actions action = new Actions(driver);
            action.moveToElement(element).perform(); }

        //mouse hover by xpath and perform*
        public void mouseHoverByXpath(String locator) {
            try {
                WebElement element = driver.findElement(By.xpath(locator));
                Actions action = new Actions(driver);
                Actions hover = action.moveToElement(element);
            } catch (Exception ex) {
                System.out.println("First attempt has been done, This is second try");
                WebElement element = driver.findElement(By.xpath(locator));
                Actions action = new Actions(driver);
                action.moveToElement(element).perform(); }}

        //mouse hover by css and perform*
        public void mouseHoverByCSS(String locator) {
            try {
                WebElement element = driver.findElement(By.cssSelector(locator));
                Actions action = new Actions(driver);
                Actions hover = action.moveToElement(element);
            } catch (Exception ex) {
                System.out.println("First attempt has been done, This is second try");
                WebElement element = driver.findElement(By.cssSelector(locator));
                Actions action = new Actions(driver);
                action.moveToElement(element).perform(); } }

        //********************************Assertation to get(text/title/compare/url**************************

        //Assert method to get title
        public static void getTitle(String expectedTitle) {
            String title = driver.getTitle();
            Assert.assertEquals(title, expectedTitle, "title doesn't match");
            System.out.println(title); }

        //Assert method to compare Text
        public static void compare_Text(String expectedText, WebElement element) {
            String actualText = element.getText();
            Assert.assertEquals(actualText, expectedText, "The text doesn't match");
            System.out.println(actualText); }

        //Assert method to get url
        public static void getUrl(String expectedUrl) {
            String url = driver.getCurrentUrl();
            Assert.assertEquals(url, expectedUrl, "URL doesn't match");
            System.out.println(url); }

        //************************************scroll methodes***********************************************
        //method  Scroll Up
        public static void Page_Scroll_Up(WebDriver driver) {
            Actions actions = new Actions(driver);
            // Scroll Up using Actions class
            actions.keyDown(Keys.CONTROL).sendKeys(Keys.HOME).perform(); }

        //method  Scroll Down
        public static void Page_Scroll_Down(WebDriver driver) {
            Actions actions = new Actions(driver);
            // Scroll Down using Actions class
            actions.keyDown(Keys.CONTROL).sendKeys(Keys.END).perform(); }

        //method  Scroll Down
        public static void PageScrollDown(WebDriver driver) {
            Actions actions = new Actions(driver);
            // Scroll Down using Actions class
            actions.keyDown(Keys.CONTROL).sendKeys(Keys.END).perform(); }

        //method  Scroll Down to The webElement
        public static void Page_Scroll_DownToElement(WebElement element) {
            Actions actions = new Actions(driver);
            // Scroll Down using Actions class
            actions.keyDown(Keys.CONTROL).moveToElement(element).click(); }

        //method  Scroll left Or Right to The webElement
        public static void PageScrollToElement(WebElement element) {
            Actions actions = new Actions(driver);
            // Scroll Down using Actions class
            actions.moveToElement(element).click(); }

        //********************************* windows setup ****************************************

        // window page loading maximaze and delete cookies
        public static void setUpWindow(WebDriver driver){
            driver.manage().timeouts().pageLoadTimeout(10,TimeUnit.SECONDS);
            driver.manage().window().maximize();
            driver.manage().deleteAllCookies(); }

        //windows setup maximaze and delete cookies
        public static void setUpWindow() {
            driver.manage().window().maximize();
            driver.manage().deleteAllCookies();
            driver.manage().window().fullscreen(); }
        // window maximazing
        public static void expendWindow() {
            driver.manage().window().maximize(); }

//************************************* Waiting time************************************

        //sleepFor by passing the value
        public static void sleepFor(int sec) throws InterruptedException {
            Thread.sleep(sec * 1000); }
        //method to wait
        public static void waitForSeconds(long time) throws InterruptedException {
            long Time = time * 1000;
            Thread.sleep(Time); }
        //implicite wait time
        public void ImplicitWaitTime(long WaitTime) {
            driver.manage().timeouts().implicitlyWait(WaitTime, TimeUnit.SECONDS); }


//*****************************  Navigating methodes  **************************************

        //navigate to url after closing one step passing to another
        public static void navigatetourl(String url){
            driver.get(url); }
        //back to home page
        public void goBackToHomeWindow() {
            driver.switchTo().defaultContent();
        }
        //mavigate back to page
        public void navigateBack() {
            driver.navigate().back(); }
        //navigate back
        public void navigateForward() {
            driver.navigate().forward();
        }

    }
}
