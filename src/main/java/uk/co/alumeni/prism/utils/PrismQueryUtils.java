package uk.co.alumeni.prism.utils;

import static java.math.RoundingMode.HALF_UP;
import static org.apache.commons.lang.StringEscapeUtils.escapeSql;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.BooleanUtils;

import uk.co.alumeni.prism.PrismConstants;

import com.google.common.base.Joiner;

public class PrismQueryUtils {

    public static String prepareRowsForSqlInsert(List<String> rows) {
        return Joiner.on(", ").join(rows);
    }

    public static String prepareColumnsForSqlInsert(List<String> columns) {
        return "(" + prepareRowsForSqlInsert(columns) + ")";
    }

    public static String prepareStringForSqlInsert(String value) {
        return value == null ? PrismConstants.NULL : "'" + escapeSql(PrismStringUtils.cleanString(value)) + "'";
    }

    public static String prepareIntegerForSqlInsert(Integer value) {
        return value == null ? PrismConstants.NULL : escapeSql(value.toString());
    }

    public static String prepareIntegerForSqlInsert(BigInteger value) {
        return value == null ? PrismConstants.NULL : "'" + escapeSql(value.toString()) + "'";
    }

    public static String prepareDecimalForSqlInsert(BigDecimal value) {
        return value == null ? PrismConstants.NULL : "'" + escapeSql(value.setScale(2, HALF_UP).toPlainString()) + "'";
    }

    public static String prepareBooleanForSqlInsert(boolean value) {
        return PrismStringUtils.cleanString(new Integer(BooleanUtils.toInteger(value)).toString());
    }

    public static String generateOnDuplicateUpdateClause(String[] columns) {
        return Stream.of(columns).map(c -> c + " = values(" + c + ")").collect(Collectors.joining(",\n"));
    }

}
