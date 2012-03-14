package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;


public class DocumentBuilder {
	
	private Integer id;
	private String fileName;

	private byte[] content;
	
	private ApplicationForm applicationForm;
	
	private String contentType;
	private DocumentType type;
	
	
	public DocumentBuilder applicationForm(ApplicationForm applicationForm){
		this.applicationForm = applicationForm;
		return this;
	}
	
	public DocumentBuilder contentType(String contentType){
		this.contentType = contentType;
		return this;
	}
	
	public DocumentBuilder type(DocumentType type){
		this.type = type;
		return this;
	}
	
	
	public DocumentBuilder id(Integer id){
		this.id = id;
		return this;
	}
	
	public DocumentBuilder fileName(String fileName){
		this.fileName = fileName;
		return this;
	}
	
	public DocumentBuilder content(byte[] content){
		this.content = content;
		return this;
	}

	public Document toDocument(){
		Document document = new Document();
		document.setId(id);
		document.setFileName(fileName);
		document.setContent(content);
		document.setType(type);
		document.setContentType(contentType);
		document.setApplicationForm(applicationForm);
		return document;
	}
}
