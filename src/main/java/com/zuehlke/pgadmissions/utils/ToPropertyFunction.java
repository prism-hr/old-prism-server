package com.zuehlke.pgadmissions.utils;

import org.apache.commons.beanutils.PropertyUtils;

import com.google.common.base.Function;

public class ToPropertyFunction<T, P> implements Function<T, P> {

    private String propertyName;

    public ToPropertyFunction(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    @SuppressWarnings("unchecked")
    public P apply(T input) {
        try {
            return (P) PropertyUtils.getSimpleProperty(input, propertyName);
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}
