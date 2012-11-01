package org.pushingpixels.substance.api;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

class AASupport {
    public static final String DESKTOPFONTHINTS;
    
    static {
        String hintHolder = null;
        
        try {
            Class<?> toolkit = Class.forName("sun.awt.SunToolkit");
            Field hints = toolkit.getField("DESKTOPFONTHINTS");
            hintHolder = (String) hints.get(null);
        } catch (RuntimeException e) {
            throw new ExceptionInInitializerError(e);
        } catch (Exception e) {
            hintHolder = "awt.font.desktophints"; // currrent value for reference property
        }
        
        DESKTOPFONTHINTS = hintHolder;
    }
    
    public static final Object AA_TEXT_PROPERTY_KEY;
    
    static {
        Object propertyHolder = null;
        
        try {
            Class<?> toolkit = Class.forName("sun.swing.SwingUtilities2");
            Field utilities = toolkit.getField("AA_TEXT_PROPERTY_KEY");
            propertyHolder = utilities.get(null);
        } catch (RuntimeException e) {
            throw new ExceptionInInitializerError(e);
        } catch (Exception e) {
            propertyHolder = new Object(); // fake value
        }
        
        AA_TEXT_PROPERTY_KEY = propertyHolder;
    }
    
    private static final Method getAATextInfo;
    
    static {
        Method method = null;
        
        try {
            Class<?> aaTextInfo = Class.forName("sun.swing.SwingUtilities2$AATextInfo");
            method = aaTextInfo.getDeclaredMethod("getAATextInfo", boolean.class);
        } catch (RuntimeException e) {
            throw new ExceptionInInitializerError(e);
        } catch (Exception ignore) { }
        
        getAATextInfo = method;
    }
    
    public static Object getAATextInfo(boolean lafCondition) {
        if (getAATextInfo != null) {
            try {
                return getAATextInfo.invoke(null, lafCondition);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception ignore) { }
        }
        
        return null;
    }
}
