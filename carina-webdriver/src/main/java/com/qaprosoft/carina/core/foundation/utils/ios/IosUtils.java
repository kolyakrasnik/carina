package com.qaprosoft.carina.core.foundation.utils.ios;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;

import com.qaprosoft.carina.core.foundation.utils.mobile.MobileUtils;
import com.qaprosoft.carina.core.foundation.webdriver.DriverPool;

import io.appium.java_client.ios.IOSDriver;

/**
 * Useful iOS utilities. For usage: import
 * com.qaprosoft.carina.core.foundation.utils.ios.IosUtils;
 *
 */
public class IosUtils extends MobileUtils {

    private static final Logger LOGGER = Logger.getLogger(IosUtils.class);

    /**
     * Tap several times using JS
     * 
     * @param startx int
     * @param starty int
     * @param times int
     */
    @SuppressWarnings("serial")
	public static void tap(int startx, int starty, int times) {
        WebDriver driver = DriverPool.getDriver();

        for (int i = 0; i < times; i++) {
            LOGGER.info(String.format("Tap #: %d. X: %d. Y:%d", (i + 1), startx, starty));
            ((IOSDriver<?>) driver).executeScript("mobile: tap", new HashMap<String, Double>() {
                {
                    put("duration", 0.1);
                    put("x", (double) startx);
                    put("y", (double) starty);
                }
            });
        }
    }

}
