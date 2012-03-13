package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Document;


public class DocumentBuilder {
	
	private Integer id;
	private String fileName;

	private byte[] content;
	
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
		return document;
	}
}
