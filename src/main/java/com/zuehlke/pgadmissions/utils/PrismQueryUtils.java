package com.zuehlke.pgadmissions.utils;

import static java.math.RoundingMode.HALF_UP;
import static org.apache.commons.lang.StringEscapeUtils.escapeSql;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;

import com.google.common.base.Joiner;

public class PrismQueryUtils {

    public static String prepareRowsForSqlInsert(List<String> sequence) {
        return Joiner.on(", ").join(sequence);
    }

    public static String prepareCellsForSqlInsert(List<String> cells) {
        return "(" + prepareRowsForSqlInsert(cells) + ")";
    }

    public static String prepareStringForSqlInsert(String value) {
        return value == null ? "null" : "'" + escapeSql(prepareStringForInsert(value)) + "'";
    }

    public static String prepareIntegerForSqlInsert(Integer value) {
        return value == null ? "null" : "'" + escapeSql(value.toString()) + "'";
    }

    public static String prepareIntegerForSqlInsert(BigInteger value) {
        return value == null ? "null" : "'" + escapeSql(value.toString()) + "'";
    }

    public static String prepareDecimalForSqlInsert(BigDecimal value) {
        return value == null ? "null" : "'" + escapeSql(value.setScale(2, HALF_UP).toPlainString()) + "'";
    }

    public static String prepareBooleanForSqlInsert(boolean value) {
        return prepareStringForInsert(new Integer(BooleanUtils.toInteger(value)).toString());
    }

    public static String prepareStringForInsert(String string) {
        return string.replace("\n", " ").replace("\r", " ").replace("\t", " ").replaceAll(" +", " ").trim();
    }

}
