package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.CountryService;

@Component
public class CountryPropertyEditor extends PropertyEditorSupport {

	private final CountryService countryService;
	private final EncryptionHelper encryptionHelper;

	public CountryPropertyEditor(){
		this(null, null);
	}
	
	@Autowired
	public CountryPropertyEditor(CountryService countryService, EncryptionHelper encryptionHelper) {
		this.countryService = countryService;
		this.encryptionHelper = encryptionHelper;	
	}
	
	@Override
	public void setAsText(String strId) throws IllegalArgumentException {
		if(strId == null || StringUtils.isBlank(strId)){
			setValue(null);
			return;
		}
		setValue(countryService.getCountryById(encryptionHelper.decryptToInteger(strId)));
		
	}

	@Override
	public String getAsText() {
		if(getValue() == null || ((Country)getValue()).getId() == null){
			return null;
		}
		return encryptionHelper.encrypt(((Country)getValue()).getId());
	}
}
