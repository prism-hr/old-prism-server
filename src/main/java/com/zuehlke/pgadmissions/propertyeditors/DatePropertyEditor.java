package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Component;

@Component
public class DatePropertyEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String strDate) throws IllegalArgumentException {
        if (StringUtils.isBlank(strDate)) {
            setValue(null);
            return;
        }
        
        try {
            setValue(DateUtils.parseDate(strDate, new String[] {"dd-MMM-yyyy", "dd MMM yyyy"}));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
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
