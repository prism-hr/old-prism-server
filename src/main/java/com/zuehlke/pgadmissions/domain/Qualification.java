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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name="APPLICATION_FORM_QUALIFICATION")
@Access(AccessType.FIELD) 
public class Qualification extends DomainObject<Integer> implements FormSectionObject {

	private static final long serialVersionUID = -8949535622435302565L;
	
	@Transient
	private boolean acceptedTerms;
	
	@ManyToOne
	@JoinColumn(name = "proof_of_award_id")
	private Document proofOfAward;
	
	@Temporal(TemporalType.DATE)
	@Column(name="award_date")
	private Date qualificationAwardDate;
	
	@Column(name="subject")
	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 70)
	private String qualificationSubject;
	
	@ManyToOne
	@JoinColumn(name = "institution_domicile_id")	
	private Domicile institutionCountry;
	
	@Column(name="institution")
	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 100)
	private String qualificationInstitution;
	
	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 70)
	@Column(name="qualification_language")
	private String qualificationLanguage;
	
	@ManyToOne
	@JoinColumn(name="qualification_type_id")
	private QualificationType qualificationType;
	
	@Column(name="grade")
	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 70)
	private String qualificationGrade;
	
	@Column(name="start_date")
	@Temporal(TemporalType.DATE)
	private Date qualificationStartDate;
	
	@Type(type = "com.zuehlke.pgadmissions.dao.custom.CheckedStatusEnumUserType")
	@Column(name="completed")
	private CheckedStatus completed;
	
	@Column(name="send_to_ucl")
	private Boolean sendToUCL;
	
	public String getQualificationSubject() {
		return qualificationSubject;
	}

	public void setQualificationSubject(String q_name_of_programme) {
		this.qualificationSubject = q_name_of_programme;
	}

	public String getQualificationInstitution() {
		return qualificationInstitution;
	}

	public void setQualificationInstitution(String q_institution) {
		this.qualificationInstitution = q_institution;
	}

	public String getQualificationLanguage() {
		return qualificationLanguage;
	}

	public void setQualificationLanguage(String q_language_of_study) {
		this.qualificationLanguage = q_language_of_study;
	}
	
	public String getQualificationGrade() {
		return qualificationGrade;
	}

	public void setQualificationGrade(String q_grade) {
		this.qualificationGrade = q_grade;
	}

	public Date getQualificationStartDate() {
		return qualificationStartDate;
	}

	public void setQualificationStartDate(Date q_start_date) {
		this.qualificationStartDate = q_start_date;
	}

	public Date getQualificationAwardDate() {
		return qualificationAwardDate;
	}

	public void setQualificationAwardDate(Date q_award_date) {
		this.qualificationAwardDate = q_award_date;
	}

	@ManyToOne
	@JoinColumn(name="application_form_id")
	private ApplicationForm application = null;
	

	public ApplicationForm getApplication() {
		return application;
	}

	public void setApplication(ApplicationForm application) {
		this.application = application;
	}

	
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

	public CheckedStatus getCompleted() {
		return completed;
	}
	
	public boolean isQualificationCompleted(){
		if(completed == null){
			return false;
		}
		return completed.equals(CheckedStatus.YES);
	}

	public void setCompleted(CheckedStatus completed) {
		this.completed = completed;
	}

	public Domicile getInstitutionCountry() {
		return institutionCountry;
	}

	public void setInstitutionCountry(Domicile institutionCountry) {
		this.institutionCountry = institutionCountry;
	}

	public Document getProofOfAward() {
		return proofOfAward;
	}

	public void setProofOfAward(Document proofOfAward) {
		this.proofOfAward = proofOfAward;
	}

	public boolean isAcceptedTerms() {
		return acceptedTerms;
	}

	public void setAcceptedTerms(boolean acceptedTerms) {
		this.acceptedTerms = acceptedTerms;
	}

    public QualificationType getQualificationType() {
        return qualificationType;
    }

    public void setQualificationType(QualificationType qualificationType) {
        this.qualificationType = qualificationType;
    }
    
    public Boolean getSendToUCL() {
		return sendToUCL;
	}
    
    public void setSendToUCL(Boolean sendToUCL) {
		this.sendToUCL = sendToUCL;
	}
}
