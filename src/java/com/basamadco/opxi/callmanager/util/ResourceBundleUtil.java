package com.basamadco.opxi.callmanager.util;

import java.util.*;
import java.util.logging.Logger;
import java.text.MessageFormat;

/**
 * @author Jrad
 *         Date: Dec 14, 2006
 *         Time: 10:57:12 AM
 */
public class ResourceBundleUtil {

    private static final Logger logger = Logger.getLogger(ResourceBundleUtil.class.getName());


    private static ResourceBundle resourceBundle;

    private static Locale locale = new Locale("fa");

    static {
        try {
            resourceBundle = PropertyResourceBundle.getBundle("i18n.messages", locale);
        } catch (Exception e) {
            logger.severe("Unable to load resource bundle: " + e);
        }
    }

    public static String getMessage(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (MissingResourceException e) {
            return "---";
        }
    }

    public static String getMessage(String key, String arg0) {
        try {
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(locale);
            formatter.applyPattern(resourceBundle.getString(key));
            return formatter.format(new Object[]{arg0});
        } catch (MissingResourceException e) {
            return "---";
        }
    }

    public static String getMessage(String key, String arg0, String arg1) {
        try {
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(locale);
            formatter.applyPattern(resourceBundle.getString(key));


            return formatter.format(new Object[]{arg0, arg1});
        } catch (MissingResourceException e) {
            return "---";
        }
    }

    public static String getMessage(String key, String arg0, Date date) {
        try {
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(locale);
            formatter.applyPattern(resourceBundle.getString(key));
            return formatter.format(new Object[]{arg0, date});
        } catch (MissingResourceException e) {
            return "---";
        }
    }

}
