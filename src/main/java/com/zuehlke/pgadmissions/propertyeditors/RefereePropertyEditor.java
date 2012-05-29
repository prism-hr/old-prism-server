package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.services.RefereeService;

@Component
public class RefereePropertyEditor extends PropertyEditorSupport {

	private final RefereeService refereeService;
	

	RefereePropertyEditor() {
		this(null);
	}
	
	@Autowired
	public RefereePropertyEditor(RefereeService refereeService) {
		this.refereeService = refereeService;
	}

	@Override
	public void setAsText(String strId) throws IllegalArgumentException {
		if (strId == null) {
			setValue(null);
			return;
		}
		setValue(refereeService.getRefereeById(Integer.parseInt(strId)));

	}

	@Override
	public String getAsText() {
		if (getValue() == null || ((Referee) getValue()).getId() == null) {
			return null;
		}
		return ((Referee) getValue()).getId().toString();
	}
}
