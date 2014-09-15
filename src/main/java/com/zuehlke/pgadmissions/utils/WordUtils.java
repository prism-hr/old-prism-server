package com.zuehlke.pgadmissions.utils;

public class WordUtils {

    public static String pluralize(String entityName) {
        if (entityName.endsWith("y")) {
            return entityName.substring(0, entityName.length() - 1) + "ies";
        } else if(entityName.endsWith("s")) {
            return entityName + "es";
        }
        return entityName + "s";
    }

}
