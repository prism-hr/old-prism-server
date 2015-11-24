package uk.co.alumeni.prism.jaxb;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * {@link http://java.net/jira/browse/JAXB-643?page=com.atlassian.jira.plugin.system.issuetabpanels%3Aworklog-tabpanel}
 * Sun's DatatypeFactory#newXMLGregorianCalendar(String) and XMLGregorianCalendar
 * which was buldled in jdk/jre6 lost backward compatibility in xsd:gMonth.
 */
public final class GMonthAdapter {

    private static final String GMONTH_FORMAT = "--MM";

    private GMonthAdapter() {
    }

    public static DateTime parse(String s) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(GMONTH_FORMAT);
        return fmt.parseDateTime(s);
    }

    public static String print(DateTime dt) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(GMONTH_FORMAT);
        return fmt.print(dt);
    }
}
