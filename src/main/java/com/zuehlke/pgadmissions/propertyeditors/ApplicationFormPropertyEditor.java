package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.services.ApplicationsService;

@Component
public class ApplicationFormPropertyEditor extends PropertyEditorSupport {

	private final ApplicationsService applicationsService;

	public ApplicationFormPropertyEditor() {
		this(null);
	}
	
	@Autowired
	public ApplicationFormPropertyEditor(ApplicationsService applicationsService) {
		this.applicationsService = applicationsService;
	}

	@Override
	public void setAsText(String strId) throws IllegalArgumentException {
		if (strId == null) {
			setValue(null);
			return;
		}
		setValue(applicationsService.getApplicationByApplicationNumber(strId));
	}

	@Override
	public String getAsText() {
		if (getValue() == null ) {
			return null;
		}
		return ((ApplicationForm) getValue()).getApplicationNumber().toString();
	}
}
