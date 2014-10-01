package com.zuehlke.pgadmissions.utils;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;

import com.google.common.collect.Lists;

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
    
    public static List<Object> getPropertyValues(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        List<Object> properties = Lists.newArrayList();
        for (java.lang.reflect.Field field : fields) {
            try {
                properties.add(field.get(object));
            } catch (Exception e) {
                throw new Error(e);
            }
        }
        return properties;
    }
    
}
