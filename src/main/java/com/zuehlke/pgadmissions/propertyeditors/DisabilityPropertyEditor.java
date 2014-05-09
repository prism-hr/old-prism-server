package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.ImportedEntityService;

@Component
public class DisabilityPropertyEditor extends PropertyEditorSupport {

	private final ImportedEntityService disabilityService;
	private final EncryptionHelper encryptionHelper;

	@Autowired
	public DisabilityPropertyEditor(ImportedEntityService disabilityService, EncryptionHelper encryptionHelper) {
		this.disabilityService = disabilityService;
		this.encryptionHelper = encryptionHelper;
	}

	@Override
	public void setAsText(String strId) throws IllegalArgumentException {
		if (strId == null || StringUtils.isBlank(strId)) {
			setValue(null);
			return;
		}
		setValue(disabilityService.getDisabilityById(encryptionHelper.decryptToInteger(strId)));
	}

	@Override
	public String getAsText() {
		if (getValue() == null || ((Disability) getValue()).getId() == null) {
			return null;
		}
		return encryptionHelper.encrypt(((Disability) getValue()).getId());
	}
}
