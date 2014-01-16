package com.zuehlke.pgadmissions.referencedata.adapters;

import java.util.List;

import com.zuehlke.pgadmissions.domain.CodeObject;
import com.zuehlke.pgadmissions.referencedata.v2.jaxb.Ethnicities.Ethnicity;

public class EthnicityAdapter implements ImportData {

	private Ethnicity ethnicity;
	
	public String getName() {
		return ethnicity.getName();
	}

	public EthnicityAdapter(Ethnicity ethnicity) {
		this.ethnicity = ethnicity;
	}

	@Override
	public String getStringCode() {
		return ethnicity.getCode();
	}

	@Override
	public com.zuehlke.pgadmissions.domain.Ethnicity createDomainObject(List<? extends CodeObject> currentData, List<? extends CodeObject> changes) {
		com.zuehlke.pgadmissions.domain.Ethnicity result = new com.zuehlke.pgadmissions.domain.Ethnicity();
		result.setCode(Integer.parseInt(ethnicity.getCode()));
		result.setName(ethnicity.getName());
		result.setEnabled(true);
		return result;
	}
}
