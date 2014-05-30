package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.services.ApplicationService;

@Component
public class ApplicationFormPropertyEditor extends PropertyEditorSupport {

	private final ApplicationService applicationsService;

	public ApplicationFormPropertyEditor() {
		this(null);
	}
	
	@Autowired
	public ApplicationFormPropertyEditor(ApplicationService applicationsService) {
		this.applicationsService = applicationsService;
	}

	@Override
	public void setAsText(String strId) throws IllegalArgumentException {
		if (strId == null) {
			setValue(null);
			return;
		}
		setValue(applicationsService.getByApplicationNumber(strId));
	}

	@Override
	public String getAsText() {
		if (getValue() == null ) {
			return null;
		}
		return ((Application) getValue()).getCode().toString();
	}
}
