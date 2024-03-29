package uk.co.alumeni.prism.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrismWordUtils {

    private static Logger log = LoggerFactory.getLogger(PrismWordUtils.class);

    public static String pluralize(String entityName) {
        if (entityName.endsWith("y")) {
            return entityName.substring(0, entityName.length() - 1) + "ies";
        } else if (entityName.endsWith("s")) {
            return entityName + "es";
        }
        return entityName + "s";
    }

    public static String singularize(String entityName) {
        if (entityName.endsWith("ies")) {
            return entityName.substring(0, entityName.length() - 3) + "y";
        } else if (entityName.endsWith("es")) {
            return entityName.substring(0, entityName.length() - 2);
        } else if (entityName.endsWith("s")) {
            return entityName.substring(0, entityName.length() - 1);
        }
        log.warn("Unrecognized plural form: " + entityName);
        return entityName;
    }


}
