package com.zuehlke.pgadmissions.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

import com.zuehlke.pgadmissions.domain.enums.DocumentType;

@Entity(name = "DOCUMENT")
@Access(AccessType.FIELD)
public class Document extends DomainObject<Integer> {


	private static final long serialVersionUID = -6396463075916267580L;
	
	@Type(type = "com.zuehlke.pgadmissions.dao.custom.DocumentTypeEnumUserType")
	@Column(name = "document_type")	
	private DocumentType type;
	
	
	@Column(name = "content_type")
	private String contentType;	

	@Column(name = "file_name")
	private String fileName;

	@Column(name = "file_content")
	private byte[] content;
	
	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	@Id
	@GeneratedValue
	@Access(AccessType.PROPERTY)
	public Integer getId() {
		return id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}
	
	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public DocumentType getType() {
		return type;
	}

	public void setType(DocumentType type) {
		this.type = type;
	}
}
