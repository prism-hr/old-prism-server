package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LocalDatePropertyEditor extends PropertyEditorSupport {

    private static final Logger log = LoggerFactory.getLogger(LocalDatePropertyEditor.class);

    private DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MMM-yyyy");

    @Override
    public void setAsText(String strDate) throws IllegalArgumentException {
        if (StringUtils.isBlank(strDate)) {
            setValue(null);
            return;
        }

        try {
            setValue(formatter.parseLocalDate(strDate));
        } catch (IllegalArgumentException e) {
            log.error("Error parsing date: " + strDate, e);
            setValue(null);
        }

    }

    @Override
    public String getAsText() {
        if (getValue() == null) {
            return null;
        }
        return formatter.print((LocalDate) getValue());
    }
}
