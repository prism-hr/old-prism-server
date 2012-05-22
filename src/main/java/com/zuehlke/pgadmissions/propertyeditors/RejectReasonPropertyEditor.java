package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.RejectReason;
import com.zuehlke.pgadmissions.services.RejectService;

@Component
public class RejectReasonPropertyEditor extends PropertyEditorSupport {

	private final RejectService rejectService;

	RejectReasonPropertyEditor(){
		this(null);
	}
	
	@Autowired
	public RejectReasonPropertyEditor(RejectService rejectService) {
		this.rejectService = rejectService;
	}
	@Override
	public void setAsText(String strId) throws IllegalArgumentException {
		if(strId == null || StringUtils.isBlank(strId)){
			setValue(null);
			return;
		}
		setValue(rejectService.getRejectReasonById(Integer.parseInt(strId)));
		
	}

	@Override
	public String getAsText() {
		if(getValue() == null || ((RejectReason)getValue()).getId() == null){
			return null;
		}
		return ((RejectReason)getValue()).getId().toString();
	}
}
