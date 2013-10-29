package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.services.ProgramsService;

@Component
public class ProgramPropertyEditor extends PropertyEditorSupport {

	private final ProgramsService programsService;

	public ProgramPropertyEditor() {
		this(null);
	}

	@Autowired
	public ProgramPropertyEditor(ProgramsService programsService) {
		this.programsService = programsService;
	}

	@Override
	public void setAsText(String strId) throws IllegalArgumentException {
		if (strId == null || StringUtils.isBlank(strId)) {
			setValue(null);
			return;
		}
		setValue(programsService.getProgramByCode(strId));
	}

	@Override
	public String getAsText() {
		if (getValue() == null || ((Program) getValue()).getId() == null) {
			return null;
		}
		return ((Program) getValue()).getCode();
	}
}
