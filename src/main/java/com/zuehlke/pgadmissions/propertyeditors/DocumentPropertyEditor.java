package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.services.DocumentService;

@Component
public class DocumentPropertyEditor extends PropertyEditorSupport{

	private final DocumentService documentService;
	
	DocumentPropertyEditor(){
		this(null);
	}
	
	@Autowired
	public DocumentPropertyEditor(DocumentService documentService) {
		this.documentService = documentService;	
	}
	
	@Override
	public void setAsText(String strId) throws IllegalArgumentException {
		if(strId == null || StringUtils.isBlank(strId)){
			setValue(null);
			return;
		}
		setValue(documentService.getDocumentById(Integer.parseInt(strId)));
		
	}

	@Override
	public String getAsText() {
		if(getValue() == null || ((Document)getValue()).getId() == null){
			return null;
		}
		return ((Document)getValue()).getId().toString();
	}
}
