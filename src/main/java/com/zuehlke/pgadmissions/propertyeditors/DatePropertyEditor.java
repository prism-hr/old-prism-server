package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
@Component
public class DatePropertyEditor extends PropertyEditorSupport{
	
	@Override
	public void setAsText(String strDate) throws IllegalArgumentException {
		if(StringUtils.isEmpty(strDate)){
			
			setValue(null);
			return;
		}
		try {
			setValue(new SimpleDateFormat("dd-MMM-yyyy").parse(strDate));
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
		
	}

	@Override
	public String getAsText() {
		if(getValue() == null){
			return null;
		}
		return new SimpleDateFormat("dd-MMM-yyyy").format(getValue());
	}
}
