package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yzosin on 01-Sep-17.
 */
public class DriverManager {
    static String OS_EXTENTION = (System.getProperty("os.name").toLowerCase().contains("win")) ? ".exe" : "_mac";
    static String FIREFOX_PATH = "drivers/geckodriver" + OS_EXTENTION;
    static String CHROME_PATH = "drivers/chromedriver" + OS_EXTENTION;

    public static ThreadLocal<WebDriver> instance = new ThreadLocal<WebDriver>();
    static String BROWSER_TYPE;

    static public FirefoxDriver getFirefox() {

        System.setProperty("webdriver.gecko.driver", FIREFOX_PATH);

        DesiredCapabilities capabilities = new DesiredCapabilities().firefox();
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
                "image/jpeg, application/pdf, application/octet-stream, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        capabilities.setCapability(FirefoxDriver.PROFILE, profile);

        return new FirefoxDriver(capabilities);
    }

    static public ChromeDriver getChrome() {

        System.setProperty("webdriver.chrome.driver", CHROME_PATH);
        ChromeOptions options = new ChromeOptions();
        Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);

        options.addArguments("--test-type");
        options.addArguments("--start-maximized");
        options.addArguments("--disable-saveAndQuit-password-bubble");
        DesiredCapabilities capabilities = new DesiredCapabilities().chrome();
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

        capabilities.setCapability("chrome.switches", Arrays.asList("--no-default-browser-check"));
        HashMap<String, Object> chromePreferences = new HashMap<>();
        chromePreferences.put("profile.password_manager_enabled", "false");
        chromePreferences.put("credentials_enable_service", "false");
        capabilities.setCapability("chrome.prefs", chromePreferences);

        return new ChromeDriver(capabilities);
    }

    public static WebDriver getDriver() {
        if (instance.get() == null)
            if (getCurrentBrowserName().equals(BrowserType.FIREFOX)) {
                instance.set(getFirefox());
            } else {
                instance.set(getChrome());
            }
        return instance.get();
    }

    public static void closeDriver() {
        instance.get().quit();
        instance.set(null);
    }

    public static String getCurrentBrowserName() {
        if (BROWSER_TYPE == null)
            if (PropertiesReader.getConfigProperty("driver").equalsIgnoreCase("firefox")) {
                BROWSER_TYPE = BrowserType.FIREFOX;
            } else {
                BROWSER_TYPE = BrowserType.CHROME;
            }
        return BROWSER_TYPE;
    }
}
