package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.services.SupervisorService;

@Component
public class SupervisorPropertyEditor extends PropertyEditorSupport {

	private final SupervisorService supervisorService;

	SupervisorPropertyEditor() {
		this(null);
	}

	@Autowired
	public SupervisorPropertyEditor(SupervisorService supervisorService) {
		this.supervisorService = supervisorService;
	}

	@Override
	public void setAsText(String strId) throws IllegalArgumentException {
		if (strId == null) {
			setValue(null);
			return;
		}
		setValue(supervisorService.getSupervisorWithId(Integer.parseInt(strId)));

	}

	@Override
	public String getAsText() {
		if (getValue() == null || ((Supervisor) getValue()).getId() == null) {
			return null;
		}
		return ((Supervisor) getValue()).getId().toString();
	}
}
