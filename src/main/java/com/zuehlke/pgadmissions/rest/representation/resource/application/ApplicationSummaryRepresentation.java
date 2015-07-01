package com.zuehlke.pgadmissions.rest.representation.resource.application;

import java.math.BigDecimal;
import java.util.List;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;

public class ApplicationSummaryRepresentation {

    private String createdDate;

    private String submittedDate;

    private String closingDate;

    private String phone;

    private String skype;

    private QualificationSummaryRepresentation latestQualification;

    private EmploymentPositionSummaryRepresentation latestEmploymentPosition;

    private String primaryThemes;

    private String secondaryThemes;

    private List<DocumentSummaryRepresentation> documents = Lists.newLinkedList();

    private Integer referenceProvidedCount;

    private Integer referenceDeclinedCount;

    private List<OtherApplicationSummaryRepresentation> otherLiveApplications;

    private String studyOption;

    private String referralSource;

    private String referrer;

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(String submittedDate) {
        this.submittedDate = submittedDate;
    }

    public String getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(String closingDate) {
        this.closingDate = closingDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String Phone) {
        this.phone = Phone;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

    public QualificationSummaryRepresentation getLatestQualification() {
        return latestQualification;
    }

    public void setLatestQualification(QualificationSummaryRepresentation latestQualification) {
        this.latestQualification = latestQualification;
    }

    public EmploymentPositionSummaryRepresentation getLatestEmploymentPosition() {
        return latestEmploymentPosition;
    }

    public void setLatestEmploymentPosition(EmploymentPositionSummaryRepresentation latestEmploymentPosition) {
        this.latestEmploymentPosition = latestEmploymentPosition;
    }

    public String getPrimaryThemes() {
        return primaryThemes;
    }

    public void setPrimaryThemes(String primaryThemes) {
        this.primaryThemes = primaryThemes;
    }

    public String getSecondaryThemes() {
        return secondaryThemes;
    }

    public void setSecondaryThemes(String secondaryThemes) {
        this.secondaryThemes = secondaryThemes;
    }

    public List<DocumentSummaryRepresentation> getDocuments() {
        return documents;
    }

    public void addDocument(DocumentSummaryRepresentation document) {
        this.documents.add(document);
    }

    public Integer getReferenceProvidedCount() {
        return referenceProvidedCount;
    }

    public void setReferenceProvidedCount(Integer referenceProvidedCount) {
        this.referenceProvidedCount = referenceProvidedCount;
    }

    public Integer getReferenceDeclinedCount() {
        return referenceDeclinedCount;
    }

    public void setReferenceDeclinedCount(Integer referenceDeclinedCount) {
        this.referenceDeclinedCount = referenceDeclinedCount;
    }

    public List<OtherApplicationSummaryRepresentation> getOtherLiveApplications() {
        return otherLiveApplications;
    }

    public void setOtherLiveApplications(List<OtherApplicationSummaryRepresentation> otherLiveApplications) {
        this.otherLiveApplications = otherLiveApplications;
    }

    public void setDocuments(List<DocumentSummaryRepresentation> documents) {
        this.documents = documents;
    }

    public String getStudyOption() {
        return studyOption;
    }

    public void setStudyOption(String studyOption) {
        this.studyOption = studyOption;
    }

    public String getReferralSource() {
        return referralSource;
    }

    public void setReferralSource(String referralSource) {
        this.referralSource = referralSource;
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public ApplicationSummaryRepresentation withCreatedDate(String createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public ApplicationSummaryRepresentation withSubmittedDate(String submittedDate) {
        this.submittedDate = submittedDate;
        return this;
    }

    public ApplicationSummaryRepresentation withClosingDate(String closingDate) {
        this.closingDate = closingDate;
        return this;
    }

    public ApplicationSummaryRepresentation withPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public ApplicationSummaryRepresentation withSkype(String skype) {
        this.skype = skype;
        return this;
    }

    public ApplicationSummaryRepresentation withPrimaryThemes(String primaryThemes) {
        this.primaryThemes = primaryThemes;
        return this;
    }

    public ApplicationSummaryRepresentation withSecondaryThemes(String secondaryThemes) {
        this.secondaryThemes = secondaryThemes;
        return this;
    }

    public ApplicationSummaryRepresentation withStudyOption(String studyOption) {
        this.studyOption = studyOption;
        return this;
    }

    public ApplicationSummaryRepresentation withReferralSource(String referralSource) {
        this.referralSource = referralSource;
        return this;
    }

    public ApplicationSummaryRepresentation withReferrer(String referrer) {
        this.referrer = referrer;
        return this;
    }

    public static class QualificationSummaryRepresentation {

        private String title;

        private String subject;

        private String grade;

        private String institution;

        private String startDate;

        private String endDate;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }

        public String getInstitution() {
            return institution;
        }

        public void setInstitution(String institution) {
            this.institution = institution;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public QualificationSummaryRepresentation withTitle(String title) {
            this.title = title;
            return this;
        }

        public QualificationSummaryRepresentation withSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public QualificationSummaryRepresentation withGrade(String grade) {
            this.grade = grade;
            return this;
        }

        public QualificationSummaryRepresentation withInstitution(String institution) {
            this.institution = institution;
            return this;
        }

        public QualificationSummaryRepresentation withStartDate(String startDate) {
            this.startDate = startDate;
            return this;
        }

        public QualificationSummaryRepresentation withEndDate(String endDate) {
            this.endDate = endDate;
            return this;
        }

    }

    public static class EmploymentPositionSummaryRepresentation {

        private String position;

        private String employer;

        private String startDate;

        private String endDate;

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getEmployer() {
            return employer;
        }

        public void setEmployer(String employer) {
            this.employer = employer;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public EmploymentPositionSummaryRepresentation withPosition(String position) {
            this.position = position;
            return this;
        }

        public EmploymentPositionSummaryRepresentation withEmployer(String employer) {
            this.employer = employer;
            return this;
        }

        public EmploymentPositionSummaryRepresentation withStartDate(String startDate) {
            this.startDate = startDate;
            return this;
        }

        public EmploymentPositionSummaryRepresentation withEndDate(String endDate) {
            this.endDate = endDate;
            return this;
        }

    }

    public static class DocumentSummaryRepresentation {

        private Integer id;

        private String label;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public DocumentSummaryRepresentation withId(Integer id) {
            this.id = id;
            return this;
        }

        public DocumentSummaryRepresentation withLabel(String label) {
            this.label = label;
            return this;
        }

    }

    public static class ApplicationProcessingRepresentation {

        private PrismStateGroup stateGroup;

        private Integer instanceTotal;

        private BigDecimal instanceDurationAverage;

        public PrismStateGroup getStateGroup() {
            return stateGroup;
        }

        public void setStateGroup(PrismStateGroup stateGroup) {
            this.stateGroup = stateGroup;
        }

        public Integer getInstanceTotal() {
            return instanceTotal;
        }

        public void setInstanceTotal(Integer instanceTotal) {
            this.instanceTotal = instanceTotal;
        }

        public BigDecimal getInstanceDurationAverage() {
            return instanceDurationAverage;
        }

        public void setInstanceDurationAverage(BigDecimal instanceDurationAverage) {
            this.instanceDurationAverage = instanceDurationAverage;
        }

        public ApplicationProcessingRepresentation withStateGroup(PrismStateGroup stateGroup) {
            this.stateGroup = stateGroup;
            return this;
        }

        public ApplicationProcessingRepresentation withInstanceTotal(Integer instanceTotal) {
            this.instanceTotal = instanceTotal;
            return this;
        }

        public ApplicationProcessingRepresentation withInstanceDurationAverage(BigDecimal instanceDurationAverage) {
            this.instanceDurationAverage = instanceDurationAverage;
            return this;
        }

    }

    public static class OtherApplicationSummaryRepresentation {

        private Integer id;

        private String code;

        private String program;

        private String project;

        private Integer ratingCount;

        private BigDecimal ratingAverage;

        private PrismStateGroup stateGroup;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getProgram() {
            return program;
        }

        public void setProgram(String program) {
            this.program = program;
        }

        public String getProject() {
            return project;
        }

        public void setProject(String project) {
            this.project = project;
        }

        public Integer getRatingCount() {
            return ratingCount;
        }

        public void setRatingCount(Integer ratingCount) {
            this.ratingCount = ratingCount;
        }

        public BigDecimal getRatingAverage() {
            return ratingAverage;
        }

        public void setRatingAverage(BigDecimal ratingAverage) {
            this.ratingAverage = ratingAverage;
        }

        public PrismStateGroup getStateGroup() {
            return stateGroup;
        }

        public void setStateGroup(PrismStateGroup stateGroup) {
            this.stateGroup = stateGroup;
        }

    }

}
