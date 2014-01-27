package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.BooleanUtils;

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name="APPLICATION_FORM_QUALIFICATION")
public class Qualification implements FormSectionObject, Serializable {

	private static final long serialVersionUID = -8949535622435302565L;
	
	@Id
    @GeneratedValue
	private Integer id;
	
	@Transient
	private boolean acceptedTerms;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "proof_of_award_id")
	private Document proofOfAward;
	
	@Temporal(TemporalType.DATE)
	@Column(name="award_date")
	private Date qualificationAwardDate;
	
	@Column(name="subject")
	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 70)
	private String qualificationSubject;
	
	@Column(name="title")
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 70)
    private String qualificationTitle;
    
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "institution_domicile_id")	
	private Domicile institutionCountry;
	
	@Column(name="institution_name")
	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 200)
	private String qualificationInstitution;
	
	@Column(name="other_institution_name")
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 200)
    private String otherQualificationInstitution;
    
	@Column(name="institution_code")
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 10)
    private String qualificationInstitutionCode;
    
	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 70)
	@Column(name="qualification_language")
	private String qualificationLanguage;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="qualification_type_id")
	private QualificationType qualificationType;
	
	@Column(name="grade")
	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 70)
	private String qualificationGrade;
	
	@Column(name="start_date")
	@Temporal(TemporalType.DATE)
	private Date qualificationStartDate;
	
	@Column(name="completed")
	private Boolean completed;
	
	@Column(name="send_to_ucl")
	private Boolean sendToUCL;
	
	@ManyToOne
	@JoinColumn(name="application_form_id")
	private ApplicationForm application = null;

	
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

	public ApplicationForm getApplication() {
		return application;
	}

	public void setApplication(ApplicationForm application) {
		this.application = application;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public Boolean getCompleted() {
		return completed;
	}
	
	public boolean isQualificationCompleted(){
		return BooleanUtils.isTrue(completed);
	}

	public void setCompleted(Boolean completed) {
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
	    proofOfAward.setIsReferenced(true);
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

    public String getOtherQualificationInstitution() {
        return otherQualificationInstitution;
    }

    public void setOtherQualificationInstitution(String otherQualificationInstitution) {
        this.otherQualificationInstitution = otherQualificationInstitution;
    }

    public String getQualificationInstitutionCode() {
        return qualificationInstitutionCode;
    }

    public void setQualificationInstitutionCode(String qualificationInstitutionCode) {
        this.qualificationInstitutionCode = qualificationInstitutionCode;
    }

    public String getQualificationTitle() {
        return qualificationTitle;
    }

    public void setQualificationTitle(String qualificationTitle) {
        this.qualificationTitle = qualificationTitle;
    }
}
