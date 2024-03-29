package uk.co.alumeni.prism.utils;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.WordUtils;

public class PrismReflectionUtils {

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

    public static Object invokeMethod(Object bean, String methodName, Object... inputs) {
        try {
            return MethodUtils.invokeMethod(bean, methodName, inputs);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public static Object invokeStaticMethod(Class<?> beanClass, String methodName, Object... inputs) {
        try {
            return MethodUtils.invokeStaticMethod(beanClass, methodName, inputs);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public static String getMethodName(Enum<?> definition) {
        return getMethodName(definition, "get");
    }

    public static String getMethodName(Enum<?> definition, String prefix) {
        String[] nameParts = definition.name().split("_");
        String methodName = prefix;
        for (String namePart : nameParts) {
            methodName = methodName + WordUtils.capitalizeFully(namePart);
        }
        return methodName;
    }

    public static <T> void copyProperty(T fromResource, T toResource, String property) {
        if (hasProperty(fromResource, property)) {
            setProperty(toResource, property, getProperty(fromResource, property));
        }
    }

}
