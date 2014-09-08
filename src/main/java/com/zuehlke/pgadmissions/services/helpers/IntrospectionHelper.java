package com.zuehlke.pgadmissions.services.helpers;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.beanutils.PropertyUtils;

import com.google.common.collect.Maps;

public class IntrospectionHelper {

    public static Object getProperty(Object bean, String property) {
        try {
            return  PropertyUtils.getSimpleProperty(bean, property);
        } catch (Exception e) {
            throw new Error(e);
        }
    }
    
    public static HashMap<String, Object> getBeanPropertiesMap(Object bean, String... exclusions) {
        HashMap<String, Object> filters = Maps.newHashMap();
        for (Field field : bean.getClass().getDeclaredFields()) {
            String fieldName = field.getName();
            if (Arrays.asList(exclusions).contains(fieldName)) {
                try {
                    filters.put(fieldName, field.get(bean));
                } catch (Exception e) {
                    throw new Error(e);
                }
            }
        }
        return filters;
    }
    
}
