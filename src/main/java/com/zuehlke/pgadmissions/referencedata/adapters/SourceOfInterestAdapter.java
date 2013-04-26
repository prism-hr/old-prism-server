package com.zuehlke.pgadmissions.referencedata.adapters;

import java.util.List;

import com.zuehlke.pgadmissions.domain.CodeObject;
import com.zuehlke.pgadmissions.referencedata.v1.jaxb.SourcesOfInterest.SourceOfInterest;

public class SourceOfInterestAdapter implements ImportData {

	private SourceOfInterest sourceOfInterest;
	
	public String getName() {
		return sourceOfInterest.getName();
	}

	public SourceOfInterestAdapter(SourceOfInterest sourceOfInterest) {
		this.sourceOfInterest = sourceOfInterest;
	}

	@Override
	public String getStringCode() {
		return sourceOfInterest.getCode();
	}

	@Override
	public com.zuehlke.pgadmissions.domain.SourcesOfInterest createDomainObject(List<? extends CodeObject> currentData, List<? extends CodeObject> changes) {
		com.zuehlke.pgadmissions.domain.SourcesOfInterest result = new com.zuehlke.pgadmissions.domain.SourcesOfInterest();
		result.setCode(sourceOfInterest.getCode());
		result.setName(sourceOfInterest.getName());
		result.setEnabled(true);
		return result;
	}
}
