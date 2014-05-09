package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.ImportedEntityService;

@Component
public class DomicilePropertyEditor extends PropertyEditorSupport {

    @Autowired
	private ImportedEntityService importedEntityService;
	
    @Autowired
	private EncryptionHelper encryptionHelper;

	@Override
	public void setAsText(String strId) throws IllegalArgumentException {
		if(strId == null || StringUtils.isBlank(strId)){
			setValue(null);
			return;
		}
		setValue(importedEntityService.getDomicileById(encryptionHelper.decryptToInteger(strId)));
		
	}

	@Override
	public String getAsText() {
		if(getValue() == null || ((Domicile)getValue()).getId() == null){
			return null;
		}
		return encryptionHelper.encrypt(((Domicile)getValue()).getId());
	}
}
