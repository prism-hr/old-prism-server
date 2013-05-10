package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.LanguageService;
@Component
public class LanguagePropertyEditor extends PropertyEditorSupport {

	private final LanguageService languageService;
	private final EncryptionHelper encryptionHelper;

	public LanguagePropertyEditor(){
		this(null, null);
	}
	
	@Autowired
	public LanguagePropertyEditor(LanguageService languageService, EncryptionHelper encryptionHelper) {
		this.languageService = languageService;
		this.encryptionHelper = encryptionHelper;
	 
	}
	@Override
	public void setAsText(String strId) throws IllegalArgumentException {
		if(strId == null || StringUtils.isBlank(strId)){
			setValue(null);
			return;
		}
		setValue(languageService.getLanguageById(encryptionHelper.decryptToInteger(strId)));
		
	}

	@Override
	public String getAsText() {
		if(getValue() == null || ((Language)getValue()).getId() == null){
			return null;
		}
		return encryptionHelper.encrypt(((Language)getValue()).getId());
	}
}
