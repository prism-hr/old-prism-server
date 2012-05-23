package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.services.DisabilityService;

@Component
public class DisabilityPropertyEditor extends PropertyEditorSupport {

	private final DisabilityService disabilityService;

	@Autowired
	public DisabilityPropertyEditor(DisabilityService disabilityService) {
		this.disabilityService = disabilityService;
	}

	@Override
	public void setAsText(String strId) throws IllegalArgumentException {
		if (strId == null || StringUtils.isBlank(strId)) {
			setValue(null);
			return;
		}
		setValue(disabilityService.getDisabilityById(Integer.parseInt(strId)));
	}

	@Override
	public String getAsText() {
		if (getValue() == null || ((Disability) getValue()).getId() == null) {
			return null;
		}
		return ((Disability) getValue()).getId().toString();
	}
}
