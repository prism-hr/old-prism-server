package com.zuehlke.pgadmissions.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.enums.OpportunityRequestStatus;
import com.zuehlke.pgadmissions.domain.enums.OpportunityRequestType;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name = "OPPORTUNITY_REQUEST")
public class OpportunityRequest {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
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

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OpportunityRequestStatus status = OpportunityRequestStatus.NEW;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(name = "atas_required")
    private Boolean atasRequired;

    @Column(name = "study_options")
    private String studyOptions;

    @Column(name = "advertising_dealine_year")
    private Integer advertisingDeadlineYear;

    @Column(name = "request_type")
    @Enumerated(EnumType.STRING)
    private OpportunityRequestType type;

    @ManyToOne
    @JoinColumn(name = "source_program_id")
    private Program sourceProgram;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "opportunity_request_id", nullable = false)
    @OrderBy("createdTimestamp")
    private List<OpportunityRequestComment> comments = Lists.newArrayList();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_type_id")
    private ProgramType programType;

    @Transient
    private String funding;

    @Transient
    private Boolean acceptingApplications = true;

    @Transient
    private Integer studyDurationNumber;

    @Transient
    private String studyDurationUnit;

    @Transient
    private boolean forceCreatingNewInstitution;

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
        computeStudyDurationNumberAndUnit();
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
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

    public String getStudyOptions() {
        return studyOptions;
    }

    public void setStudyOptions(String studyOptions) {
        this.studyOptions = studyOptions;
    }

    public Integer getAdvertisingDeadlineYear() {
        return advertisingDeadlineYear;
    }

    public void setAdvertisingDeadlineYear(Integer advertisingDeadlineYear) {
        this.advertisingDeadlineYear = advertisingDeadlineYear;
    }

    public OpportunityRequestType getType() {
        return type;
    }

    public void setType(OpportunityRequestType type) {
        this.type = type;
    }

    public Program getSourceProgram() {
        return sourceProgram;
    }

    public void setSourceProgram(Program sourceProgram) {
        this.sourceProgram = sourceProgram;
    }

    public List<OpportunityRequestComment> getComments() {
        return comments;
    }

    public ProgramType getProgramType() {
        return programType;
    }

    public void setProgramType(ProgramType programType) {
        this.programType = programType;
    }

    public String getFunding() {
        return funding;
    }

    public void setFunding(String funding) {
        this.funding = funding;
    }

    public Boolean getAcceptingApplications() {
        return acceptingApplications;
    }

    public void setAcceptingApplications(Boolean acceptingApplications) {
        this.acceptingApplications = acceptingApplications;
    }

    public Integer getStudyDurationNumber() {
        return studyDurationNumber;
    }

    public void setStudyDurationNumber(Integer studyDurationNumber) {
        this.studyDurationNumber = studyDurationNumber;
        computeStudyDuration();
    }

    public String getStudyDurationUnit() {
        return studyDurationUnit;
    }

    public void setStudyDurationUnit(String studyDurationUnit) {
        this.studyDurationUnit = studyDurationUnit;
        computeStudyDuration();
    }

    public boolean isForceCreatingNewInstitution() {
        return forceCreatingNewInstitution;
    }

    public void setForceCreatingNewInstitution(boolean forceCreatingNewInstitution) {
        this.forceCreatingNewInstitution = forceCreatingNewInstitution;
    }

    private void computeStudyDuration() {
        Integer studyDuration = getStudyDurationNumber();
        String studyDurationUnit = getStudyDurationUnit();
        if (studyDuration == null || studyDurationUnit == null) {
            this.studyDuration = null;
        } else if ("MONTHS".equals(studyDurationUnit)) {
            this.studyDuration = studyDuration;
        } else if ("YEARS".equals(studyDurationUnit)) {
            this.studyDuration = studyDuration * 12;
        }
    }

    private void computeStudyDurationNumberAndUnit() {
        Integer duration = getStudyDuration();
        if (duration == null) {
            this.studyDurationNumber = null;
            this.studyDurationUnit = null;
        } else if (duration % 12 == 0) {
            this.studyDurationNumber = duration / 12;
            this.studyDurationUnit = "YEARS";
        } else {
            this.studyDurationNumber = duration;
            this.studyDurationUnit = "MONTHS";
        }
    }

}
