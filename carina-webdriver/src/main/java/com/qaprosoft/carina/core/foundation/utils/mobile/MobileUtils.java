package com.qaprosoft.carina.core.foundation.utils.mobile;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;

import com.google.common.base.Function;
import com.qaprosoft.carina.core.foundation.utils.Configuration;
import com.qaprosoft.carina.core.foundation.utils.Configuration.Parameter;
import com.qaprosoft.carina.core.foundation.webdriver.DriverPool;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileDriver;
import io.appium.java_client.TouchAction;

public class MobileUtils {
    protected static final Logger LOGGER = Logger.getLogger(MobileUtils.class);
    
    /**
     * With interval of 2 seconds swipe action speed (preliminary for half
     * screen) is somewhere in the middle - not so fast, not so long.
     * <p>
     * Tested at Nexus 6P Android 8.0.0.
     */
    private static final int SWIPE_ACTION_DEFAULT_INTERVAL = 2000;
    
    private static final int SWIPE_TO_ELEMENT_DEFAULT_TIMEOUT = 30000;

    private static final int SWIPE_TO_ELEMENT_DEFAULT_PULLING_TIMEOUT = 2500;

    public enum Direction {
        LEFT, RIGHT, UP, DOWN, VERTICAL, HORIZONTAL, VERTICAL_DOWN_FIRST, HORIZONTAL_RIGHT_FIRST
    }

    protected static final long IMPLICIT_TIMEOUT = Configuration.getLong(Parameter.IMPLICIT_TIMEOUT);

    protected static final long EXPLICIT_TIMEOUT = Configuration.getLong(Parameter.EXPLICIT_TIMEOUT);

    protected static final int MINIMUM_TIMEOUT = 2;

    protected static final int DEFAULT_SWIPE_TIMEOUT = 1000;


	/**
	 * Hide keyboard if needed
	 */
	public static void hideKeyboard() {
		try {
			((AppiumDriver<?>) DriverPool.getDriver()).hideKeyboard();
		} catch (Exception e) {
			LOGGER.info("Keyboard was already hided or error occurs: " + e);
		}
	}
	
    /**
     * Tap element (using TouchAction)
     * 
     * @param element WebElement
     */
    public static void tapElement(ExtendedWebElement element) {
        Point point = element.getLocation();
        Dimension size = element.getSize();
        tap(point.getX() + size.getWidth() / 2, point.getY() + size.getHeight() / 2);
    }
    
    /**
     * Tap with TouchAction by coordinates
     * 
     * @param startx int
     * @param starty int
     */
    public static void tap(int startx, int starty) {
        TouchAction touchAction = new TouchAction((AppiumDriver<?>) DriverPool.getDriver());
        touchAction.tap(startx, starty).perform();
    }
    
    /**
     * Tap and Hold (LongPress) on element
     *
     * @param element ExtendedWebElement
     * @return boolean
     */
    public static boolean longPress(ExtendedWebElement element) {
        try {
            WebDriver driver = DriverPool.getDriver();
            TouchAction action = new TouchAction((MobileDriver<?>) driver);
            action.longPress(element.getElement()).release().perform();
            return true;
        } catch (Exception e) {
            LOGGER.info("Error occurs: " + e);
        }
        return false;
    }


    /**
     * Swipe up
     * 
     * @param duration int
     */
    public static void swipeUp(final int duration) {
        WebDriver driver = DriverPool.getDriver();
        int x = driver.manage().window().getSize().width / 2;
        int y = driver.manage().window().getSize().height;
        LOGGER.info("Swipe up will be executed.");
        swipe(x, y / 2, x, y * 4 / 5, duration);
    }

    /**
     * Swipe up several times
     * 
     * @param times int
     * @param duration int
     */
    public static void swipeUp(final int times, final int duration) {
        for (int i = 0; i < times; i++) {
            swipeUp(duration);
        }
    }

    /**
     * Swipe down several times
     * 
     * @param times int
     * @param duration int
     */
    public static void swipeDown(final int times, final int duration) {
        for (int i = 0; i < times; i++) {
            swipeDown(duration);
        }
    }

    /**
     * Swipe down
     * 
     * @param duration int
     */
    public static void swipeDown(final int duration) {
        WebDriver driver = DriverPool.getDriver();
        int x = driver.manage().window().getSize().width / 2;
        int y = driver.manage().window().getSize().height / 2;
        LOGGER.info("Swipe down will be executed.");
        swipe(x, y, x, y / 2, duration);
    }
	
	/**
     * Swipe in specified direction preliminary half a screen.
     * <p>
     * Tested at Nexus 6P Android 8.0.0.
     * <p>
     * The behavior of "moveTo" is different for some reason with/without "wait"
     * step: 1. with "wait": Y coordinate in second "moveTo" is not relative,
     * but obsolete: e.g. 100 will move to Y = 100 point; 2. without "wait": Y
     * coordinate in second "moveTo" is relative: e.g. "100" will add 100 to
     * current touch position. Seems issue against Appium should be reported here
     * <p>
     * Without "wait" step in general swipe is performed too fast and goes too
     * much down - "wait" action slows down the swiping and allow to swipe
     * only small part of screen
     * <p>
     * waitAction(Duration.ofSeconds(1)) - such syntax only starting from java 8
     *
     * @param direction direction of swiping
     */
    public static void swipe(Direction direction) {
    	WebDriver driver = DriverPool.getDriver();
        int screenWidth = driver.manage().window().getSize().getWidth();
        int screenHeight = driver.manage().window().getSize().getHeight();

        int screenCenterX = Double.valueOf(screenWidth / 2).intValue();
        int screenCenterY = Double.valueOf(screenHeight / 2).intValue();

        int offsetX = 0;
        int offsetY = 0;

        switch (direction) {
        case UP: {
            offsetY = Double.valueOf(screenHeight - screenHeight * 0.05).intValue();
            break;
        }
        case DOWN: {
            offsetY = Double.valueOf(screenHeight * 0.05).intValue();
            break;
        }
        case RIGHT: {
            offsetX = Double.valueOf(screenWidth - screenWidth * 0.05).intValue();
            break;
        }
        case LEFT: {
            offsetX = Double.valueOf(screenWidth * 0.05).intValue();
            break;
        }
		default:
			throw new RuntimeException("Unknown direction!");
        }

        swipe(screenCenterX, screenCenterY, offsetX, offsetY, SWIPE_ACTION_DEFAULT_INTERVAL);
    }

    
    /**
     * @see MobileUtils#swipeToElement(By, Direction, int, int)
     */
    public static void swipeToElement(final By elementLocator) {
        swipeToElement(elementLocator, Direction.UP, SWIPE_TO_ELEMENT_DEFAULT_TIMEOUT, SWIPE_TO_ELEMENT_DEFAULT_PULLING_TIMEOUT);
    }
    
    /**
     * @see MobileUtils#swipeToElement(By, Direction, int, int)
     */
    public static void swipeToElement(final By elementLocator, final Direction direction) {
        swipeToElement(elementLocator, direction, SWIPE_TO_ELEMENT_DEFAULT_TIMEOUT, SWIPE_TO_ELEMENT_DEFAULT_PULLING_TIMEOUT);
    }

    /**
     * Swipe to element in specified direction while it will not be present on
     * the screen. If element is on the screen already, swiping will not be
     * performed.
     * <p>
     * Method (waiting for element to be present or not) is affected by implicit
     * wait.
     *
     * @param elementLocator locator of element to which it will be swiped
     * @param direction direction of swiping
     * @param timeout for how long to swipe, ms
     * @param pullingTimeout pulling timeout, ms
     * @see MobileUtils#swipe(Direction)
     */
    public static void swipeToElement(final By elementLocator, final Direction direction, int timeout, int pullingTimeout) {
    	WebDriver driver = DriverPool.getDriver();
        if (driver.findElements(elementLocator).isEmpty()) {

            new FluentWait<By>(elementLocator).withTimeout(timeout, TimeUnit.SECONDS).pollingEvery(pullingTimeout, TimeUnit.MILLISECONDS)
                    .until(new Function<By, Boolean>() {
                        @Override
                        public Boolean apply(By input) {
                            swipe(direction);
                            return !driver.findElements(elementLocator).isEmpty();
                        }
                    });
        }
    }

	/**
	 * @see MobileUtils#swipeToElementInsideContainer(ExtendedWebElement, By,
	 *      Direction, int, int)
	 */
	public static void swipeToElementInsideContainer(final ExtendedWebElement container, final By elementLocator) {
		swipeToElementInsideContainer(container, elementLocator, Direction.UP, SWIPE_TO_ELEMENT_DEFAULT_TIMEOUT,
				SWIPE_TO_ELEMENT_DEFAULT_PULLING_TIMEOUT);
	}
	
	/**
	 * @see MobileUtils#swipeToElementInsideContainer(ExtendedWebElement, By,
	 *      Direction, int, int)
	 */
	public static void swipeToElementInsideContainer(final ExtendedWebElement container, final By elementLocator,
			Direction direction) {
		swipeToElementInsideContainer(container, elementLocator, direction, SWIPE_TO_ELEMENT_DEFAULT_TIMEOUT,
				SWIPE_TO_ELEMENT_DEFAULT_PULLING_TIMEOUT);
	}

    /**
     * Swipe to element inside container in specified direction while element
     * will not be present on the screen. If element is on the screen already,
     * Swiping will not be performed.
     * <p>
     * Method (waiting for element to be present or not) is affected by implicit
     * wait.
     *
     * @param container element, inside which swiping is expected
     * @param elementLocator locator of element to which it will be swiped
     * @param direction direction of swiping
     * @param timeout for how long to swipe, ms
     * @param pullingTimeout pulling timeout, ms
     * @see MobileUtils#swipeInsideContainer(ExtendedWebElement, Direction)
     */
    public static void swipeToElementInsideContainer(final ExtendedWebElement container, final By elementLocator, final Direction direction, int timeout,
            int pullingTimeout) {
    	WebDriver driver = DriverPool.getDriver();
        if (driver.findElements(elementLocator).isEmpty()) {

            new FluentWait<By>(elementLocator).withTimeout(timeout, TimeUnit.SECONDS).pollingEvery(pullingTimeout, TimeUnit.MILLISECONDS)
                    .until(new Function<By, Boolean>() {
                        @Override
                        public Boolean apply(By input) {
                            swipeInsideContainer(container, direction);
                            return !driver.findElements(elementLocator).isEmpty();
                        }
                    });
        }
    }

    /**
     * Waiting for element to be moveless. Element has different position (Y
     * coordinate) on the screen when keyboard shown, not shown or focus is
     * switching from one element to another, and invalid coordinates could be
     * captured if "find element" operation was performed in inappropriate
     * moment, as it could be changed in several moments e.g. when keyboard will
     * be fully hidden
     *
     * @param webElement element to be moveless
     */
    public static void waitForElementToBeMoveless(final WebElement webElement) {
        FluentWait<WebElement> fluentWait = new FluentWait<WebElement>(webElement);
        fluentWait.withTimeout(5, TimeUnit.SECONDS).pollingEvery(500, TimeUnit.MILLISECONDS).until(new Function<WebElement, Object>() {

            int yPosition = 0;

            @Override
            public Object apply(WebElement input) {
                int newYPosition = webElement.getLocation().getY();
                boolean result = (0 == newYPosition - yPosition);
                yPosition = newYPosition;
                return Boolean.valueOf(result);
            }
        });
    }
    
    /**
     * Swip inside container in specified direction preliminary half a screen.
     * <p>
     * Tested at Nexus 6P Android 8.0.0.
     *
     * @param container element, inside which swiping is expected
     * @param direction direction of swiping
     * @see MobileUtils#swipe(Direction)
     */
    public static void swipeInsideContainer(ExtendedWebElement container, Direction direction) {
    	WebDriver driver = DriverPool.getDriver();
        int screenWidth = driver.manage().window().getSize().getWidth();
        //int screenHeight = driver.manage().window().getSize().getHeight();

        int screenCenterX = Double.valueOf(screenWidth / 2).intValue();
        //int screenCenterY = Double.valueOf(screenHeight / 2).intValue();

        //int containerPositionX = container.getLocation().getX();
        int containerPositionY = container.getLocation().getY();

        //int containerWidth = container.getSize().getWidth();
        int containerHeight = container.getSize().getHeight();

        //int containerCenterX = Double.valueOf(containerPositionX + (containerWidth / 2)).intValue();
        int containerCenterY = Double.valueOf(containerPositionY + (containerHeight / 2)).intValue();

        int offsetX = 0;
        int offsetY = 0;

		switch (direction) {
		case UP: {
			throw new RuntimeException("Not implemented yet");
		}
		case DOWN: {
			throw new RuntimeException("Not implemented yet");
		}
		case RIGHT: {
			offsetX = Double.valueOf(screenWidth - screenWidth * 0.05).intValue();
			break;
		}
		case LEFT: {
			offsetX = Double.valueOf(screenWidth * 0.05).intValue();
			break;
		}
		default:
			throw new RuntimeException("Unknown direction!");
		}

        swipe(screenCenterX, containerCenterY, offsetX, offsetY, SWIPE_ACTION_DEFAULT_INTERVAL);
    }
    /**
     * Swipe from specified point to the point specified by offset.
     *
     * @param x X coordinate of start point
     * @param y Y coordinate of start point
     * @param offsetX X offset of swiping
     * @param offsetY Y offset of swiping
     * @param duration swipe action duration, ms
     * @see TouchAction#moveTo(int, int)
     */
    public static void swipe(int x, int y, int offsetX, int offsetY, int duration) {
        new TouchAction((AppiumDriver<?>) DriverPool.getDriver()).press(x, y).waitAction(Duration.ofMillis(duration)).moveTo(offsetX, offsetY).release().perform();
    }

}
