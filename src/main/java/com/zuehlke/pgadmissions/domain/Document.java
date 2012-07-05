package com.zuehlke.pgadmissions.domain;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.Type;

import com.zuehlke.pgadmissions.domain.enums.DocumentType;

@Entity(name = "DOCUMENT")
@Access(AccessType.FIELD)
public class Document extends DomainObject<Integer> {

	private static final long serialVersionUID = -6396463075916267580L;
	@ManyToOne
	@JoinColumn(name = "uploaded_by_id")
	private RegisteredUser uploadedBy;
	

	@OneToOne(mappedBy="proofOfAward")	
	private Qualification qualification;

	

	@Column(name = "uploaded_time_stamp", insertable = false)
	@Generated(GenerationTime.INSERT)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateUploaded;

	@Type(type = "com.zuehlke.pgadmissions.dao.custom.DocumentTypeEnumUserType")
	@Column(name = "document_type")	
	private DocumentType type;

	@Column(name = "content_type")
	private String contentType;
	

	@Column(name = "file_name")
	private String fileName;

	@Column(name = "file_content")
	@Type(type ="binary")
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

	public Date getDateUploaded() {
		return dateUploaded;
	}

	public void setDateUploaded(Date dateUploaded) {
		this.dateUploaded = dateUploaded;
	}

	public RegisteredUser getUploadedBy() {
		return uploadedBy;
	}

	public void setUploadedBy(RegisteredUser uploadedBy) {
		this.uploadedBy = uploadedBy;
	}

	public Qualification getQualification() {
		return qualification;
	}

	public void setQualification(Qualification qualification) {
		this.qualification = qualification;
	}


	
}
