package com.zuehlke.pgadmissions.utils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class GsonExclusionStrategies {

    public static ExclusionStrategy excludeClass(final Class<?> excludedClass){
        return new ExclusionStrategy() {
            
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return false;
            }
            
            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return  clazz.equals(excludedClass);
            }
        };
    }
    
}
