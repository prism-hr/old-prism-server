package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.services.QualificationService;

@Component
public class QualificationPropertyEditor extends PropertyEditorSupport {

	private final QualificationService qualificationService;

	public QualificationPropertyEditor() {
		this(null);
	}

	@Autowired
	public QualificationPropertyEditor(QualificationService qualificationDAO) {
		this.qualificationService = qualificationDAO;
	}

	@Override
	public void setAsText(String strId) throws IllegalArgumentException {
		if (strId == null || StringUtils.isBlank(strId)) {
			setValue(null);
			return;
		}
		setValue(qualificationService.getQualificationById(Integer.parseInt(strId)));
	}

	@Override
	public String getAsText() {
		if (getValue() == null || ((Qualification) getValue()).getId() == null) {
			return null;
		}
		return ((Qualification) getValue()).getId().toString();
	}
}
