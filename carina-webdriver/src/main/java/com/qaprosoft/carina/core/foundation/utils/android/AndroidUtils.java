package com.qaprosoft.carina.core.foundation.utils.android;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.qaprosoft.carina.core.foundation.utils.mobile.MobileUtils;
import com.qaprosoft.carina.core.foundation.webdriver.DriverPool;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

import io.appium.java_client.PressesKeyCode;

/**
 * Useful Android utilities. For usage: import
 * com.qaprosoft.carina.core.foundation.utils.android.AndroidUtils;
 *
 */
public class AndroidUtils extends MobileUtils {

    protected static final Logger LOGGER = Logger.getLogger(AndroidUtils.class);

    /**
     * execute Key Event
     *
     * @param keyCode int
     */
    public static void executeKeyEvent(int keyCode) {
        WebDriver driver = DriverPool.getDriver();
        LOGGER.info("Execute key event: " + keyCode);
        HashMap<String, Integer> keyCodeMap = new HashMap<String, Integer>();
        keyCodeMap.put("keycode", keyCode);
        ((JavascriptExecutor) driver).executeScript("mobile: keyevent", keyCodeMap);

    }

    /**
     * press Key Code
     *
     * @param keyCode int
     * @return boolean
     */
    public static boolean pressKeyCode(int keyCode) {
        try {
            LOGGER.info("Press key code: " + keyCode);
            ((PressesKeyCode) DriverPool.getDriver()).pressKeyCode(keyCode);
            return true;
        } catch (Exception e) {
            LOGGER.error("Exception during pressKeyCode:", e);
            try {
                LOGGER.info("Press key code by javaScript: " + keyCode);
                executeKeyEvent(keyCode);
            } catch (Exception err2) {
                LOGGER.error("Exception during pressKeyCode with JavaScript:", err2);
            }
        }
        return false;
    }

    /**
     * swipe Until Element Presence
     *
     * @param element ExtendedWebElement
     * @return boolean
     */
    public static boolean swipeUntilElementPresence(final ExtendedWebElement element) {
        int swipeTimes = 20;
        WebDriver driver = DriverPool.getDriver();
        Dimension scrSize;
        int x;
        int y;
        boolean isPresent = element.isElementPresent(MINIMUM_TIMEOUT);
        LOGGER.info("Swipe down to element: ".concat(element.toString()));
        while (!isPresent && swipeTimes-- > 0) {
            LOGGER.debug("Element not present! Swipe down will be executed.");
            scrSize = driver.manage().window().getSize();
            x = scrSize.width / 2;
            y = scrSize.height / 2;
            swipe(x, y, x, y / 2, 500);
            LOGGER.info("Swipe was executed. Attempts remain: " + swipeTimes);
            isPresent = element.isElementPresent(1);
            LOGGER.info("Result: " + isPresent);
        }
        if (!isPresent) {
            LOGGER.info("Swipe up to element: ".concat(element.toString()));
            swipeTimes = 20;
            while (!isPresent && swipeTimes-- > 0) {
                LOGGER.debug("Element not present! Swipe up will be executed.");
                scrSize = driver.manage().window().getSize();
                x = scrSize.width / 2;
                y = scrSize.height / 2;
                swipe(x, y / 2, x, y, 500);
                LOGGER.info("Swipe was executed. Attempts remain: " + swipeTimes);
                isPresent = element.isElementPresent(1);
                LOGGER.info("Result: " + isPresent);
            }
        }
        return isPresent;
    }

    /**
     * swipe In Container
     *
     * @param elem - scrollable container
     * @param times - swipe times
     * @param direction -Direction {LEFT, RIGHT, UP, DOWN}
     * @param duration - duration in msec.
     */
    public static void swipeInContainer(ExtendedWebElement elem, int times, Direction direction, int duration) {

        // Default direction left
        double directMultX1 = 0.9;
        double directMultX2 = 0.1;
        double directMultY1 = 0.5;
        double directMultY2 = 0.5;

        WebDriver driver = DriverPool.getDriver();

        if (direction.equals(Direction.RIGHT)) {
            directMultX1 = 0.2;
            directMultX2 = 0.9;
            directMultY1 = 0.5;
            directMultY2 = 0.5;
            LOGGER.info("Swipe right");
        } else if (direction.equals(Direction.LEFT)) {
            directMultX1 = 0.9;
            directMultX2 = 0.2;
            directMultY1 = 0.5;
            directMultY2 = 0.5;
            LOGGER.info("Swipe left");
        } else if (direction.equals(Direction.UP)) {
            directMultX1 = 0.1;
            directMultX2 = 0.1;
            directMultY1 = 0.2;
            directMultY2 = 0.9;
            LOGGER.info("Swipe up");
        } else if (direction.equals(Direction.DOWN)) {
            directMultX1 = 0.1;
            directMultX2 = 0.1;
            directMultY1 = 0.9;
            directMultY2 = 0.2;
            LOGGER.info("Swipe down");
        } else if (direction.equals(Direction.VERTICAL) || direction.equals(Direction.HORIZONTAL)
                || direction.equals(Direction.HORIZONTAL_RIGHT_FIRST) || direction.equals(Direction.VERTICAL_DOWN_FIRST)) {
            LOGGER.info("Incorrect swipe direction: " + direction.toString());
            return;
        }

        int x = elem.getElement().getLocation().getX();
        int y = elem.getElement().getLocation().getY();
        int width = elem.getElement().getSize().getWidth();
        int height = elem.getElement().getSize().getHeight();
        int screen_size_x = driver.manage().window().getSize().getWidth();
        int screen_size_y = driver.manage().window().getSize().getHeight();

        LOGGER.debug("x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + ", screen width=" + screen_size_x + ", screen height="
                + screen_size_y);
        LOGGER.info("Swiping in container:" + elem.getNameWithLocator());
        for (int i = 0; i <= times; i++) {
            int pointX1 = (int) (x + (width * directMultX1));
            int pointY1 = (int) (y + (height * directMultY1));
            int pointX2 = (int) (x + (width * directMultX2));
            int pointY2 = (int) (y + (height * directMultY2));

            LOGGER.debug(
                    "Direction:" + direction + ". Try #" + i + ". Points: X1Y1=" + pointX1 + ", " + pointY1 + ", X2Y2=" + pointX2 + ", " + pointY2);
            try {
                swipe(pointX1, pointY1, pointX2, pointY2, duration);
            } catch (Exception e) {
                LOGGER.error("Exception: " + e);
            }
        }
    }

    /**
     * Quick solution for scrolling To Button or element.
     *
     * @param extendedWebElement ExtendedWebElement
     * @return boolean
     */
    @Deprecated
    public static boolean scrollTo(final ExtendedWebElement extendedWebElement) {
        int i = 0;
        try {
            WebDriver driver = DriverPool.getDriver();
            int x = driver.manage().window().getSize().getWidth();
            int y = driver.manage().window().getSize().getHeight();
            LOGGER.info("Swipe down");
            while (!extendedWebElement.isElementPresent(1) && ++i <= 10) {
                LOGGER.debug("Swipe down. Attempt #" + i);
                swipe((int) (x * 0.1), (int) (y * 0.9), (int) (x * 0.1), (int) (y * 0.2), 2000);

            }
            if (!extendedWebElement.isElementPresent(1)) {
                LOGGER.info("Swipe up");
                i = 0;
                x = driver.manage().window().getSize().getWidth();
                y = driver.manage().window().getSize().getHeight();
                while (!extendedWebElement.isElementPresent(1) && ++i <= 10) {
                    LOGGER.debug("Swipe up. Attempt #" + i);
                    swipe((int) (x * 0.1), (int) (y * 0.2), (int) (x * 0.1), (int) (y * 0.9), 2000);
                }
            }
            return extendedWebElement.isElementPresent(1);
        } catch (Exception e) {
            LOGGER.info("Error happen during scrollTo ExtendedWebElement: " + e);
            return true;
        }
    }

    /**
     * swipe Coordinates
     *
     * @param startX int
     * @param startY int
     * @param endX int
     * @param endY int
     * @param duration int
     */
    public static void swipeCoord(int startX, int startY, int endX, int endY, int duration) {
        swipe(startX, startY, endX, endY, duration);
    }

    /**
     * swipe Coordinates
     *
     * @param startX int
     * @param startY int
     * @param endX int
     * @param endY int
     */
    public static void swipeCoord(int startX, int startY, int endX, int endY) {
        swipeCoord(startX, startY, endX, endY, DEFAULT_SWIPE_TIMEOUT);
    }

    /**
     * swipe In Container To required Element
     *
     * @param extendedWebElement - expected element
     * @param container - scrollable container
     * @param direction - Direction {LEFT, RIGHT, UP, DOWN, HORIZONTAL, VERTICAL
     * }
     * @param duration - duration
     * @param times - times
     * @return boolean
     */
    @Deprecated
    public static boolean swipeInContainerToElement(final ExtendedWebElement extendedWebElement, ExtendedWebElement container, Direction direction,
            int duration, int times) {
        int i = 0;
        boolean bothWay = false;
        Direction oppositeDirection = Direction.DOWN;
        try {
            if (extendedWebElement.isElementPresent(1)) {
                LOGGER.info("Element already present");
                return true;
            }

            if (direction.equals(Direction.HORIZONTAL)) {
                bothWay = true;
                direction = Direction.LEFT;
                oppositeDirection = Direction.RIGHT;
            } else if (direction.equals(Direction.HORIZONTAL_RIGHT_FIRST)) {
                bothWay = true;
                direction = Direction.RIGHT;
                oppositeDirection = Direction.LEFT;
            } else if (direction.equals(Direction.VERTICAL_DOWN_FIRST)) {
                bothWay = true;
                direction = Direction.DOWN;
                oppositeDirection = Direction.UP;
            } else if (direction.equals(Direction.VERTICAL)) {
                bothWay = true;
                direction = Direction.UP;
                oppositeDirection = Direction.DOWN;
            }

            while (!extendedWebElement.isElementPresent(1) && ++i <= times) {
                LOGGER.debug("Swipe " + direction.toString());
                swipeInContainer(container, 1, direction, duration);
            }
            if (!extendedWebElement.isElementPresent(1) && bothWay) {
                LOGGER.info("Swipe in opposite direction");
                i = 0;

                while (!extendedWebElement.isElementPresent(1) && ++i <= times) {
                    LOGGER.debug("Swipe " + direction.toString());
                    swipeInContainer(container, 1, oppositeDirection, duration);
                }
            }
            return extendedWebElement.isElementPresent(1);
        } catch (Exception e) {
            LOGGER.info("Error happened during swipe in container for element: " + e);
            return true;
        }
    }

    /**
     * wait Until Element Not Present
     *
     * @param locator By
     * @param timeout long
     * @param pollingTime long
     */
    public static void waitUntilElementNotPresent(final By locator, final long timeout, final long pollingTime) {
        LOGGER.info(String.format("Wait until element %s disappear", locator.toString()));
        WebDriver driver = DriverPool.getDriver();
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        try {
            if (new WebDriverWait(driver, timeout, pollingTime).until(ExpectedConditions.invisibilityOfElementLocated(locator))) {
                LOGGER.info(String.format("Element located by: %s not present.", locator.toString()));
            } else {
                LOGGER.info(String.format("Element located by: %s is still present.", locator.toString()));
            }
        } catch (TimeoutException e) {
            LOGGER.debug(e.getMessage());
            LOGGER.info(String.format("Element located by: %s is still present.", locator.toString()));
        }
        driver.manage().timeouts().implicitlyWait(IMPLICIT_TIMEOUT, TimeUnit.SECONDS);
    }

    // TODO temporary decision. If it works it should be moved to carina
    public static boolean swipeUntilElementPresence(final ExtendedWebElement element, int times) {
        WebDriver driver = DriverPool.getDriver();
        Dimension scrSize;
        int x;
        int y;
        boolean isPresent = element.isElementPresent(1);
        LOGGER.info("Swipe down to element: ".concat(element.toString()));
        while (!isPresent && times-- > 0) {
            LOGGER.debug("Element not present! Swipe down will be executed.");
            scrSize = driver.manage().window().getSize();
            x = scrSize.width / 2;
            y = scrSize.height / 2;
            swipe(x, y, x, y / 2, 500);
            LOGGER.info("Swipe was executed. Attempts remain: " + times);
            isPresent = element.isElementPresent(1);
            LOGGER.info("Result: " + isPresent);
        }
        if (!isPresent) {
            LOGGER.info("Swipe up to element: ".concat(element.toString()));
            while (!isPresent && times-- > 0) {
                LOGGER.debug("Element not present! Swipe up will be executed.");
                scrSize = driver.manage().window().getSize();
                x = scrSize.width / 2;
                y = scrSize.height / 2;
                swipe(x, y / 2, x, y, 500);
                LOGGER.info("Swipe was executed. Attempts remain: " + times);
                isPresent = element.isElementPresent(1);
                LOGGER.info("Result: " + isPresent);
            }
        }
        return isPresent;
    }

    /**
     * change Android Device Language
     * <p>
     * Url: <a href=
     * "http://play.google.com/store/apps/details?id=net.sanapeli.adbchangelanguage&hl=ru&rdid=net.sanapeli.adbchangelanguage">
     * ADBChangeLanguage apk </a> Change locale (language) of your device via
     * ADB (on Android OS version 6.0, 5.0, 4.4, 4.3, 4.2 and older). No need to
     * root your device! With ADB (Android Debug Bridge) on your computer, you
     * can fast switch the device locale to see how your application UI looks on
     * different languages. Usage: - install this app - setup adb connection to
     * your device (http://developer.android.com/tools/help/adb.html) - Android
     * OS 4.2 onwards (tip: you can copy the command here and paste it to your
     * command console): adb shell pm grant net.sanapeli.adbchangelanguage
     * android.permission.CHANGE_CONFIGURATION
     * <p>
     * English: adb shell am start -n
     * net.sanapeli.adbchangelanguage/.AdbChangeLanguage -e language en Russian:
     * adb shell am start -n net.sanapeli.adbchangelanguage/.AdbChangeLanguage
     * -e language ru Spanish: adb shell am start -n
     * net.sanapeli.adbchangelanguage/.AdbChangeLanguage -e language es
     *
     * @param language to set. Can be es, en, etc.
     * @return boolean
     */
    public static boolean setDeviceLanguage(String language) {

        AndroidService executor = AndroidService.getInstance();

        boolean status = executor.setDeviceLanguage(language);

        return status;
    }

}
