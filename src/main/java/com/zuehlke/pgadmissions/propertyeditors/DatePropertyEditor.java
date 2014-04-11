package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DatePropertyEditor extends PropertyEditorSupport {
    
    private static final Logger log = LoggerFactory.getLogger(DatePropertyEditor.class);

    @Override
    public void setAsText(String strDate) throws IllegalArgumentException {
        if (StringUtils.isBlank(strDate)) {
            setValue(null);
            return;
        }
        
        try {
            setValue(DateUtils.parseDate(strDate, new String[] {"dd-MMM-yyyy", "dd MMM yyyy"}));
        } catch (ParseException e) {
            log.error("Error parsing date: " + strDate, e);
            setValue(null);
        }
    }

    @Override
    public String getAsText() {
        if (getValue() == null) {
            return null;
        }
        return new SimpleDateFormat("dd-MMM-yyyy").format(getValue());
    }
}
