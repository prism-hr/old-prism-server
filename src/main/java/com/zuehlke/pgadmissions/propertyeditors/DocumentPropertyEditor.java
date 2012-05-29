package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.DocumentService;

@Component
public class DocumentPropertyEditor extends PropertyEditorSupport{

	private final DocumentService documentService;
	private final EncryptionHelper encryptionHelper;
	
	DocumentPropertyEditor(){
		this(null, null);
	}
	
	@Autowired
	public DocumentPropertyEditor(DocumentService documentService, EncryptionHelper encryptionHelper) {
		this.documentService = documentService;
		this.encryptionHelper = encryptionHelper;	
	}
	
	@Override
	public void setAsText(String strId) throws IllegalArgumentException {
		if(strId == null || StringUtils.isBlank(strId)){
			setValue(null);
			return;
		}
		setValue(documentService.getDocumentById(encryptionHelper.decryptToInteger(strId)));
		
	}

	@Override
	public String getAsText() {
		if(getValue() == null || ((Document)getValue()).getId() == null){
			return null;
		}
		return encryptionHelper.encrypt(((Document)getValue()).getId());
	}
}
