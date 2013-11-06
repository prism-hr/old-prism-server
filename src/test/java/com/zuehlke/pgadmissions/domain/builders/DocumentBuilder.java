package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;

public class DocumentBuilder {
	
	private Integer id;
	private String fileName;

	private byte[] content;
	
	private Qualification qualification;
	private String contentType;
	private DocumentType type;
	private Date dateUploaded;
	private RegisteredUser uploadedBy;
	
	public DocumentBuilder qualification(Qualification qualification){
		this.qualification = qualification;
		return this;
	}
	
	public DocumentBuilder uploadedBy(RegisteredUser uploadedBy){
		this.uploadedBy = uploadedBy;
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
	
	public DocumentBuilder dateUploaded(java.util.Date date){
		this.dateUploaded = date;
		return this;
	}
	
	public DocumentBuilder content(byte[] content){
		this.content = content;
		return this;
	}

	public Document build() {
		Document document = new Document();
		document.setId(id);
		document.setFileName(fileName);
		document.setContent(content);
		document.setType(type);
		document.setContentType(contentType);
		document.setDateUploaded(dateUploaded);
		document.setUploadedBy(uploadedBy);
		document.setQualification(qualification);
		return document;
	}
}
