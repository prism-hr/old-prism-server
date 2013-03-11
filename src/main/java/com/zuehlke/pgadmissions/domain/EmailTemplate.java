package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;

@Entity(name = "EMAIL_TEMPLATE")
public class EmailTemplate implements Serializable {

	private static final long serialVersionUID = -3640707667534813533L;

	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "name")
	@Enumerated(EnumType.STRING)
	private EmailTemplateName name;

	@Lob
	@Column(name = "content")
	private String content;

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
