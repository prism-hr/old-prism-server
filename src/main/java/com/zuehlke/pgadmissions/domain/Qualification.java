package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name="APPLICATION_FORM_QUALIFICATION")
public class Qualification implements Serializable, FormSectionObject {

	private static final long serialVersionUID = -8949535622435302565L;
	
	@Id
    @GeneratedValue
	private Integer id;
	
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
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
	@JoinColumn(name = "institution_id")
	private Institution institution;
    
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
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="application_form_id")
	private ApplicationForm application;

	@Transient
    private boolean acceptedTerms;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Document getProofOfAward() {
        return proofOfAward;
    }

    public void setProofOfAward(Document proofOfAward) {
        this.proofOfAward = proofOfAward;
    }

    public Date getQualificationAwardDate() {
        return qualificationAwardDate;
    }

    public void setQualificationAwardDate(Date qualificationAwardDate) {
        this.qualificationAwardDate = qualificationAwardDate;
    }

    public String getQualificationSubject() {
        return qualificationSubject;
    }

    public void setQualificationSubject(String qualificationSubject) {
        this.qualificationSubject = qualificationSubject;
    }

    public String getQualificationTitle() {
        return qualificationTitle;
    }

    public void setQualificationTitle(String qualificationTitle) {
        this.qualificationTitle = qualificationTitle;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public String getQualificationLanguage() {
        return qualificationLanguage;
    }

    public void setQualificationLanguage(String qualificationLanguage) {
        this.qualificationLanguage = qualificationLanguage;
    }

    public QualificationType getQualificationType() {
        return qualificationType;
    }

    public void setQualificationType(QualificationType qualificationType) {
        this.qualificationType = qualificationType;
    }

    public String getQualificationGrade() {
        return qualificationGrade;
    }

    public void setQualificationGrade(String qualificationGrade) {
        this.qualificationGrade = qualificationGrade;
    }

    public Date getQualificationStartDate() {
        return qualificationStartDate;
    }

    public void setQualificationStartDate(Date qualificationStartDate) {
        this.qualificationStartDate = qualificationStartDate;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Boolean getSendToUCL() {
        return sendToUCL;
    }

    public void setSendToUCL(Boolean sendToUCL) {
        this.sendToUCL = sendToUCL;
    }

    public ApplicationForm getApplication() {
        return application;
    }

    public void setApplication(ApplicationForm application) {
        this.application = application;
    }

    public boolean isAcceptedTerms() {
        return acceptedTerms;
    }

    public void setAcceptedTerms(boolean acceptedTerms) {
        this.acceptedTerms = acceptedTerms;
    }
    
}
