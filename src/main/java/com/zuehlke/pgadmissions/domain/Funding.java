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
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import com.zuehlke.pgadmissions.domain.enums.FundingType;

@Entity(name="APPLICATION_FORM_FUNDING")
@Access(AccessType.FIELD) 
public class Funding extends DomainObject<Integer> implements FormSectionObject{

	private static final long serialVersionUID = -3074034984017639671L;
	
	@Transient
	private boolean acceptedTerms;
	
	@Column(name="award_type")
	@Type(type = "com.zuehlke.pgadmissions.dao.custom.FundingTypeEnumUserType")
	private FundingType type;
	
	@OneToOne(orphanRemoval=true, cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "document_id")
	private Document document;
	
	private String description;
	
	@Column(name="award_value")
	private String value;
	
	@Temporal(TemporalType.DATE)
	@Column(name="award_date")
	private Date awardDate;
	
	@ManyToOne
	@JoinColumn(name="application_form_id")
	private ApplicationForm application;
	
	
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
	
	public FundingType getType() {
		return type;
	}
	
	public void setType(FundingType type) {
		this.type = type;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public Date getAwardDate() {
		return awardDate;
	}
	
	public void setAwardDate(Date awardDate) {
		this.awardDate = awardDate;
	}
	
	public ApplicationForm getApplication() {
		return application;
	}
	
	public void setApplication(ApplicationForm application) {
		this.application = application;
	}
	
	public Document getDocument() {
		return document;
	}
	
	public void setDocument(Document document) {
		this.document = document;
	}

	public boolean isAcceptedTerms() {
		return acceptedTerms;
	}

	public void setAcceptedTerms(boolean acceptedTerms) {
		this.acceptedTerms = acceptedTerms;
	}
}
