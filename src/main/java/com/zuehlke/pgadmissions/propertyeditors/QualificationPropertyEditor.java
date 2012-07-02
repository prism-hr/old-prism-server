package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.dao.QualificationDAO;
import com.zuehlke.pgadmissions.domain.Qualification;

@Component
public class QualificationPropertyEditor extends PropertyEditorSupport {

	private final QualificationDAO qualificationDAO;

	QualificationPropertyEditor() {
		this(null);
	}

	@Autowired
	public QualificationPropertyEditor(QualificationDAO qualificationDAO) {
		this.qualificationDAO = qualificationDAO;

	}

	@Override
	public void setAsText(String strId) throws IllegalArgumentException {
		if (strId == null || StringUtils.isBlank(strId)) {
			setValue(null);
			return;
		}
		setValue(qualificationDAO.getQualificationById(Integer.parseInt(strId)));

	}

	@Override
	public String getAsText() {
		if (getValue() == null || ((Qualification) getValue()).getId() == null) {
			return null;
		}
		return ((Qualification) getValue()).getId().toString();
	}

}
