package com.zuehlke.pgadmissions.utils;

import static com.zuehlke.pgadmissions.utils.PrismConversionUtils.floatToBigDecimal;
import static org.apache.commons.lang.StringEscapeUtils.escapeSql;

import java.util.List;

import com.google.common.base.Joiner;

public class PrismQueryUtils {

    public static String prepareRowsForSqlInsert(List<String> sequence) {
        return Joiner.on(", ").join(sequence);
    }

    public static String prepareCellsForSqlInsert(List<String> cells) {
        return "(" + prepareRowsForSqlInsert(cells) + ")";
    }

    public static String prepareStringForSqlInsert(String string) {
        return string == null ? "null" : "'" + escapeSql(prepareStringForInsert(string)) + "'";
    }

    public static String prepareDecimalForSqlInsert(Float decimal) {
        return decimal == null ? "null" : "'" + escapeSql(floatToBigDecimal(decimal, 2).toPlainString()) + "'";
    }

    public static String prepareStringForInsert(String string) {
        return string.replace("\n", "").replace("\r", "").replace("\t", "").replaceAll(" +", " ");
    }
    
}
