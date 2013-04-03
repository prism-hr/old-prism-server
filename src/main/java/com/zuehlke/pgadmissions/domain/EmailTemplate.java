package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;

@Entity(name = "EMAIL_TEMPLATE")
public class EmailTemplate implements Serializable {

	private static final long serialVersionUID = -3640707667534813533L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	@Enumerated(EnumType.STRING)
	private EmailTemplateName name;

	@Lob
	@Column(name = "content")
	private String content;
	
	@Column(name = "version")
	@Temporal(TemporalType.TIMESTAMP)
	private Date version;
	
	@Column(name = "active")
	private Boolean active;
	
	public void setActive(Boolean active) {
		this.active = active;
	}
	
	public Boolean getActive() {
		return active;
	}
	
	public Date getVersion() {
		return version;
	}
	
	public void setVersion(Date version) {
		this.version = version;
	}

	public EmailTemplateName getName() {
		return name;
	}

	public void setName(EmailTemplateName name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setId(Long id) {
		this.id=id;
	}
	
	public Long getId() {
		return id;
	}
}
