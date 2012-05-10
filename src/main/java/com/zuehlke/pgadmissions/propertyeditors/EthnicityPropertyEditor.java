package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.Ethnicity;
import com.zuehlke.pgadmissions.services.EthnicityService;

@Component
public class EthnicityPropertyEditor extends PropertyEditorSupport {

	private final EthnicityService ethnicityService;

	EthnicityPropertyEditor(){
		this(null);
	}
	
	@Autowired
	public EthnicityPropertyEditor(EthnicityService ethService) {
		this.ethnicityService = ethService;	
	}
	
	@Override
	public void setAsText(String strId) throws IllegalArgumentException {
		if(strId == null || StringUtils.isBlank(strId)){
			setValue(null);
			return;
		}
		setValue(ethnicityService.getEthnicityById(Integer.parseInt(strId)));
	}

	@Override
	public String getAsText() {
		if(getValue() == null || ((Ethnicity)getValue()).getId() == null){
			return null;
		}
		return ((Ethnicity)getValue()).getId().toString();
	}
}
