package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class BooleanPropertyEditor extends PropertyEditorSupport {

	@Override
	public void setAsText(String boolText) throws IllegalArgumentException {
		if (boolText == null || !StringUtils.hasText(boolText) || "null".equals(boolText)) {
			setValue(null);
			return;
		}
		setValue(Boolean.valueOf(boolText));
	}
}
