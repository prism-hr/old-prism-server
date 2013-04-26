package com.zuehlke.pgadmissions.referencedata.adapters;

import java.util.List;

import com.zuehlke.pgadmissions.domain.CodeObject;
import com.zuehlke.pgadmissions.referencedata.v1.jaxb.Disabilities.Disability;

public class DisabilityAdapter implements ImportData {

	private Disability disability;
	
	public String getName() {
		return disability.getName();
	}

	public DisabilityAdapter(Disability disability) {
		this.disability = disability;
	}

	@Override
	public String getStringCode() {
		return disability.getCode();
	}

	@Override
	public com.zuehlke.pgadmissions.domain.Disability createDomainObject(List<? extends CodeObject> currentData, List<? extends CodeObject> changes) {
		com.zuehlke.pgadmissions.domain.Disability result = new com.zuehlke.pgadmissions.domain.Disability();
		result.setCode(Integer.parseInt(disability.getCode()));
		result.setName(disability.getName());
		result.setEnabled(true);
		return result;
	}
}
