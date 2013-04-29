package com.zuehlke.pgadmissions.referencedata.adapters;

import java.util.List;

import com.zuehlke.pgadmissions.domain.CodeObject;
import com.zuehlke.pgadmissions.referencedata.v1.jaxb.Nationalities.Nationality;

public class NationalityAdapter implements ImportData {

	private Nationality nationality;
	
	public String getName() {
		return nationality.getName();
	}

	public NationalityAdapter(Nationality nationality) {
		this.nationality = nationality;
	}

	@Override
	public String getStringCode() {
		return nationality.getCode();
	}

	@Override
	public com.zuehlke.pgadmissions.domain.Language createDomainObject(List<? extends CodeObject> currentData, List<? extends CodeObject> changes) {
		com.zuehlke.pgadmissions.domain.Language result = new com.zuehlke.pgadmissions.domain.Language();
		result.setCode(nationality.getCode());
		result.setName(nationality.getName());
		result.setEnabled(true);
		return result;
	}
}
