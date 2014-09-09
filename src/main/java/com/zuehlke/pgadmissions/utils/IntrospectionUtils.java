package com.zuehlke.pgadmissions.utils;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;

public class IntrospectionUtils {

    public static Object getProperty(Object bean, String property) {
        try {
            return  PropertyUtils.getSimpleProperty(bean, property);
        } catch (Exception e) {
            throw new Error(e);
        }
    }
    
    public static void setProperty(Object bean, String property, Object value) {
        try {
            PropertyUtils.setSimpleProperty(bean, property, value);
        } catch (Exception e) {
            throw new Error(e);
        }
    }
    
    public static Object invokeMethod(Object bean, String methodName, Object... inputs) {
        try {
            return MethodUtils.invokeMethod(bean, methodName, inputs);
        } catch (Exception e) {
            throw new Error(e);
        }
    }
    
}
