package com.zuehlke.pgadmissions.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.zuehlke.pgadmissions.domain.enums.OpportunityRequestStatus;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name = "OPPORTUNITY_REQUEST")
public class OpportunityRequest {

	@Id
	@GeneratedValue
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "institution_domicile_id")
	private Domicile institutionCountry;

	@Column(name = "institution_code")
	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 10)
	private String institutionCode;

	@Column(name = "other_institution_name")
	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 200)
	private String otherInstitution;

	@Column(name = "title")
	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 200)
	private String programTitle;

	@Column(name = "description")
	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 3000)
	private String programDescription;

	@Column(name = "study_duration")
	private Integer studyDuration;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "author_id")
	private RegisteredUser author;

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private OpportunityRequestStatus status = OpportunityRequestStatus.NEW;

	@Column(name = "created_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Column(name = "atas_required")
	private Boolean atasRequired;

	@Column(name = "start_date")
	@Temporal(value = TemporalType.DATE)
	private Date applicationStartDate;

	@Column(name = "advertisingDuration")
	private Integer advertisingDuration;

	@Transient
	private Integer studyDurationNumber;

	@Transient
	private String studyDurationUnit;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Domicile getInstitutionCountry() {
		return institutionCountry;
	}

	public void setInstitutionCountry(Domicile institutionCountry) {
		this.institutionCountry = institutionCountry;
	}

	public String getInstitutionCode() {
		return institutionCode;
	}

	public void setInstitutionCode(String institutionCode) {
		this.institutionCode = institutionCode;
	}

	public String getOtherInstitution() {
		return otherInstitution;
	}

	public void setOtherInstitution(String otherInstitution) {
		this.otherInstitution = otherInstitution;
	}

	public String getProgramTitle() {
		return programTitle;
	}

	public void setProgramTitle(String programTitle) {
		this.programTitle = programTitle;
	}

	public String getProgramDescription() {
		return programDescription;
	}

	public void setProgramDescription(String programDescription) {
		this.programDescription = programDescription;
	}

	public Integer getStudyDuration() {
		return studyDuration;
	}

	public void setStudyDuration(Integer studyDuration) {
		this.studyDuration = studyDuration;
	}

	public RegisteredUser getAuthor() {
		return author;
	}

	public void setAuthor(RegisteredUser author) {
		this.author = author;
	}

	public OpportunityRequestStatus getStatus() {
		return status;
	}

	public void setStatus(OpportunityRequestStatus status) {
		this.status = status;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Boolean getAtasRequired() {
		return atasRequired;
	}

	public void setAtasRequired(Boolean atasRequired) {
		this.atasRequired = atasRequired;
	}

	public Date getApplicationStartDate() {
		return applicationStartDate;
	}

	public void setApplicationStartDate(Date applicationStartDate) {
		this.applicationStartDate = applicationStartDate;
	}

	public Integer getAdvertisingDuration() {
		return advertisingDuration;
	}

	public void setAdvertisingDuration(Integer advertisingDuration) {
		this.advertisingDuration = advertisingDuration;
	}

	public Integer getStudyDurationNumber() {
		return studyDurationNumber;
	}

	public void setStudyDurationNumber(Integer studyDurationNumber) {
		this.studyDurationNumber = studyDurationNumber;
	}

	public String getStudyDurationUnit() {
		return studyDurationUnit;
	}

	public void setStudyDurationUnit(String studyDurationUnit) {
		this.studyDurationUnit = studyDurationUnit;
	}

	public void computeStudyDuration() {
		int studyDuration = getStudyDurationNumber();
		String studyDurationUnit = getStudyDurationUnit();
		if ("YEARS".equals(studyDurationUnit)) {
			studyDuration = studyDuration * 12;
		}
		this.studyDuration = studyDuration;
	}

	public void computeStudyDurationNumberAndUnit() {
		Integer duration = getStudyDuration();
		if (duration % 12 == 0) {
			setStudyDurationNumber(duration / 12);
			setStudyDurationUnit("YEARS");
		} else {
			setStudyDurationNumber(duration);
			setStudyDurationUnit("MONTHS");
		}
	}

}
