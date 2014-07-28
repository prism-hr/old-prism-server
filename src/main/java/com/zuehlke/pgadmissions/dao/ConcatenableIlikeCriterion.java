package com.zuehlke.pgadmissions.dao;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.PostgreSQL82Dialect;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.engine.spi.TypedValue;

public class ConcatenableIlikeCriterion implements Criterion {

    private static final long serialVersionUID = 1L;

    private String value;

    private String[] columns;

    /**
     * 
     * @param value - the search query to use in the ilike operation
     * @param match - the type of matching needed. @see Hibernate MatchMode
     * @param columns - all the columns that you wish to concatenate
     */
    public ConcatenableIlikeCriterion(String value, MatchMode match, String... columns) {
        if (columns == null || columns.length == 0) {
            throw new RuntimeException("At least one column must be specified");
        }

        // implementation of MatchMode
        match = match != null ? match : MatchMode.EXACT;
        if (MatchMode.ANYWHERE.equals(match) || MatchMode.START.equals(match)) {
            value = value + "%";
        }
        if (MatchMode.ANYWHERE.equals(match) || MatchMode.END.equals(match)) {
            value = "%" + value;
        }

        this.value = value;
        this.columns = columns;

    }

    /**
     * Gets the input type (the search query) as a typed Hibernate value
     */
    @Override
    public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        return new TypedValue[] { criteriaQuery.getTypedValue(criteria, columns[0], value.toString().toLowerCase()) };

    }

    /**
     * This methods generate the SQL String from the criteria. This methods gets the hibernate properties, and translate them into database columns. Afterwards,
     * a native sql-code is generated for each database.
     * 
     * @TODO: Get the concatenate function from the JDBC driver or dialect, if possible.
     */
    @Override
    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        Dialect dialect = criteriaQuery.getFactory().getDialect();
        String[] realColumns = new String[columns.length];

        // get the real names of the columns, as they must be used in the native
        // sql code
        for (int i = 0; i < columns.length; i++) {
            realColumns[i] = criteriaQuery.getColumnsUsingProjection(criteria, columns[i])[0];
        }

        // make query for HSQL
        if (dialect instanceof HSQLDialect || dialect instanceof MySQLDialect) {
            String query = "";
            for (int i = 0; i < columns.length; i++) {
                query += realColumns[i] + (i < columns.length - 1 ? ", ' '," : "");
            }

            query = "upper(concat(" + query + ")) like upper(?)";
            return query;

            // Make query for postgres
        } else if (dialect instanceof PostgreSQL82Dialect) {
            String query = "";
            for (int i = 0; i < columns.length; i++) {
                query += realColumns[i] + (i < columns.length - 1 ? " || ' ' || " : "");
            }

            query = "upper(" + query + ") like upper(?)";
            return query;

            // Make query for SQL Server
        } else if (dialect instanceof SQLServerDialect) {
            String query = "";
            for (int i = 0; i < columns.length; i++) {
                query += realColumns[i] + (i < columns.length - 1 ? "+ ' ' +" : "");
            }

            query = "upper(" + query + ") like upper(?)";
            return query;
        }

        return null;
    }

    public static ConcatenableIlikeCriterion ilike(String value, MatchMode match, String... columns) {
        return new ConcatenableIlikeCriterion(value, match, columns);
    }
}