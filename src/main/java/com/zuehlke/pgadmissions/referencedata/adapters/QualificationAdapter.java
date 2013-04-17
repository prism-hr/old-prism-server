package com.zuehlke.pgadmissions.referencedata.adapters;

import java.util.List;

import com.zuehlke.pgadmissions.domain.CodeObject;
import com.zuehlke.pgadmissions.referencedata.jaxb.Qualifications.Qualification;

public class QualificationAdapter implements ImportData {

	private Qualification qualification;
	
	public String getName() {
		return qualification.getName();
	}

	public QualificationAdapter(Qualification qualification) {
		this.qualification = qualification;
	}

	@Override
	public String getStringCode() {
		return qualification.getCode();
	}

	@Override
	public com.zuehlke.pgadmissions.domain.QualificationType createDomainObject(List<? extends CodeObject> currentData, List<? extends CodeObject> changes) {
		com.zuehlke.pgadmissions.domain.QualificationType result = new com.zuehlke.pgadmissions.domain.QualificationType();
		result.setCode(qualification.getCode());
		result.setName(qualification.getName());
		result.setEnabled(true);
		return result;
	}
}
