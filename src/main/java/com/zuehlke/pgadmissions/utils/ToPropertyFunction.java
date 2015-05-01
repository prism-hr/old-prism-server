package com.zuehlke.pgadmissions.utils;

import com.google.common.base.Function;
import org.apache.commons.beanutils.PropertyUtils;

public class ToPropertyFunction<T, P> implements Function<T, P> {

    private String propertyName;

    public ToPropertyFunction(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public P apply(T input) {
        try {
            return (P) PropertyUtils.getSimpleProperty(input, propertyName);
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}
