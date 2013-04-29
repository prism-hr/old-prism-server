package com.zuehlke.pgadmissions.referencedata.adapters;

import java.util.List;

import com.zuehlke.pgadmissions.domain.CodeObject;
import com.zuehlke.pgadmissions.referencedata.v1.jaxb.Domiciles.Domicile;

public class CountryOfDomicileAdapter implements ImportData {

	private Domicile domicile;
	
	public String getName() {
		return domicile.getName();
	}

	public CountryOfDomicileAdapter(Domicile domicile) {
		this.domicile = domicile;
	}

	@Override
	public String getStringCode() {
		return domicile.getCode();
	}

	@Override
	public com.zuehlke.pgadmissions.domain.Domicile createDomainObject(List<? extends CodeObject> currentData, List<? extends CodeObject> changes) {
		com.zuehlke.pgadmissions.domain.Domicile result = new com.zuehlke.pgadmissions.domain.Domicile();
		result.setCode(domicile.getCode());
		result.setName(domicile.getName());
		result.setEnabled(true);
		return result;
	}
}
