package com.zuehlke.pgadmissions.utils;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.WordUtils;

public class ReflectionUtils {

    public static Object getStaticProperty(Class<?> clazz, String property) {
        try {
            return clazz.getField(property).get(null);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public static Object getProperty(Object bean, String property) {
        try {
            return PropertyUtils.getSimpleProperty(bean, property);
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

    public static boolean hasProperty(Object bean, String property) {
        return PropertyUtils.isReadable(bean, property);
    }

    public static Object getNestedProperty(Object bean, String property, boolean suppressIncompletePath) {
        try {
            return PropertyUtils.getNestedProperty(bean, property);
        } catch (NestedNullException e) {
            if (!suppressIncompletePath) {
                throw new Error(e);
            }
            return null;
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

    public static String getMethodName(Enum<?> definition) {
        String[] nameParts = definition.name().split("_");
        String methodName = "get";
        for (String namePart : nameParts) {
            methodName = methodName + WordUtils.capitalizeFully(namePart);
        }
        return methodName;
    }

}
