package com.zuehlke.pgadmissions.services.exporters;

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
        if (text != null) {
            return text.replaceAll("[^\\x20-\\x7F|\\x80-\\xFD|\\n|\\r]", "");
        }
        return null;
    }

    public String cleanPhoneNumber(String number) {
        return number.replaceAll("[^0-9()+ ]", "");
    }

    public XMLGregorianCalendar buildXmlDate(LocalDate localDate) {
        return buildXmlDate(localDate.toDateTimeAtStartOfDay());
    }

    public XMLGregorianCalendar buildXmlDate(DateTime dateTime) {
        if (dateTime != null) {
            return datatypeFactory.newXMLGregorianCalendar(dateTime.toGregorianCalendar());
        }
        return null;
    }

    public XMLGregorianCalendar buildXmlDateYearOnly(String date) {
        if (date != null) {
            XMLGregorianCalendar xmlCalendar = datatypeFactory.newXMLGregorianCalendar();
            xmlCalendar.setYear(Integer.valueOf(date));
            return xmlCalendar;
        }
        return null;
    }
    
}
