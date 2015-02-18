package com.zuehlke.pgadmissions.services.builders;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class ApplicationExportBuilderHelper {

    private DatatypeFactory datatypeFactory;

    public ApplicationExportBuilderHelper() throws DatatypeConfigurationException {
        datatypeFactory = DatatypeFactory.newInstance();
    }

    public String cleanString(String text) {
        return text == null ? null : text.replaceAll("[^\\x20-\\x7F|\\x80-\\xFD|\\n|\\r]", "");
    }

    public XMLGregorianCalendar buildXmlDate(LocalDate date) {
        return date == null ? null : buildXmlDate(date.toDateTimeAtStartOfDay());
    }

    public XMLGregorianCalendar buildXmlDate(DateTime dateTime) {
        return dateTime == null ? null : datatypeFactory.newXMLGregorianCalendar(dateTime.toGregorianCalendar());
    }

    public XMLGregorianCalendar buildXmlDateYearOnly(String date) {
        if (date != null) {;
            XMLGregorianCalendar xmlCalendar = datatypeFactory.newXMLGregorianCalendar();
            xmlCalendar.setYear(Integer.valueOf(date));
            return xmlCalendar;
        }
        return null;
    }

}
