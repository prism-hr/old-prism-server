package com.zuehlke.pgadmissions.rest.representation;

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

    private List<ApplicationProcessingRepresentation> processings;

    private String studyOption;

    private String referralSource;

    private String referrer;

    public final String getCreatedDate() {
        return createdDate;
    }

    public final void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public final String getSubmittedDate() {
        return submittedDate;
    }

    public final void setSubmittedDate(String submittedDate) {
        this.submittedDate = submittedDate;
    }

    public final String getClosingDate() {
        return closingDate;
    }

    public final void setClosingDate(String closingDate) {
        this.closingDate = closingDate;
    }

    public final String getPhone() {
        return phone;
    }

    public final void setPhone(String Phone) {
        this.phone = Phone;
    }

    public final String getSkype() {
        return skype;
    }

    public final void setSkype(String skype) {
        this.skype = skype;
    }

    public final QualificationSummaryRepresentation getLatestQualification() {
        return latestQualification;
    }

    public final void setLatestQualification(QualificationSummaryRepresentation latestQualification) {
        this.latestQualification = latestQualification;
    }

    public final EmploymentPositionSummaryRepresentation getLatestEmploymentPosition() {
        return latestEmploymentPosition;
    }

    public final void setLatestEmploymentPosition(EmploymentPositionSummaryRepresentation latestEmploymentPosition) {
        this.latestEmploymentPosition = latestEmploymentPosition;
    }

    public final String getPrimaryThemes() {
        return primaryThemes;
    }

    public final void setPrimaryThemes(String primaryThemes) {
        this.primaryThemes = primaryThemes;
    }

    public final String getSecondaryThemes() {
        return secondaryThemes;
    }

    public final void setSecondaryThemes(String secondaryThemes) {
        this.secondaryThemes = secondaryThemes;
    }

    public final List<DocumentSummaryRepresentation> getDocuments() {
        return documents;
    }

    public final void addDocument(DocumentSummaryRepresentation document) {
        this.documents.add(document);
    }

    public final Integer getReferenceProvidedCount() {
        return referenceProvidedCount;
    }

    public final void setReferenceProvidedCount(Integer referenceProvidedCount) {
        this.referenceProvidedCount = referenceProvidedCount;
    }

    public final Integer getReferenceDeclinedCount() {
        return referenceDeclinedCount;
    }

    public final void setReferenceDeclinedCount(Integer referenceDeclinedCount) {
        this.referenceDeclinedCount = referenceDeclinedCount;
    }

    public final List<OtherApplicationSummaryRepresentation> getOtherLiveApplications() {
        return otherLiveApplications;
    }

    public final void setOtherLiveApplications(List<OtherApplicationSummaryRepresentation> otherLiveApplications) {
        this.otherLiveApplications = otherLiveApplications;
    }

    public final void setDocuments(List<DocumentSummaryRepresentation> documents) {
        this.documents = documents;
    }

    public final List<ApplicationProcessingRepresentation> getProcessings() {
        return processings;
    }

    public final void setProcessings(List<ApplicationProcessingRepresentation> processings) {
        this.processings = processings;
    }

    public final String getStudyOption() {
        return studyOption;
    }

    public final void setStudyOption(String studyOption) {
        this.studyOption = studyOption;
    }

    public final String getReferralSource() {
        return referralSource;
    }

    public final void setReferralSource(String referralSource) {
        this.referralSource = referralSource;
    }

    public final String getReferrer() {
        return referrer;
    }

    public final void setReferrer(String referrer) {
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

        public final String getTitle() {
            return title;
        }

        public final void setTitle(String title) {
            this.title = title;
        }

        public final String getSubject() {
            return subject;
        }

        public final void setSubject(String subject) {
            this.subject = subject;
        }

        public final String getGrade() {
            return grade;
        }

        public final void setGrade(String grade) {
            this.grade = grade;
        }

        public final String getInstitution() {
            return institution;
        }

        public final void setInstitution(String institution) {
            this.institution = institution;
        }

        public final String getStartDate() {
            return startDate;
        }

        public final void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public final String getEndDate() {
            return endDate;
        }

        public final void setEndDate(String endDate) {
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

        public final String getPosition() {
            return position;
        }

        public final void setPosition(String position) {
            this.position = position;
        }

        public final String getEmployer() {
            return employer;
        }

        public final void setEmployer(String employer) {
            this.employer = employer;
        }

        public final String getStartDate() {
            return startDate;
        }

        public final void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public final String getEndDate() {
            return endDate;
        }

        public final void setEndDate(String endDate) {
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

        public final Integer getId() {
            return id;
        }

        public final void setId(Integer id) {
            this.id = id;
        }

        public final String getLabel() {
            return label;
        }

        public final void setLabel(String label) {
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

        public final PrismStateGroup getStateGroup() {
            return stateGroup;
        }

        public final void setStateGroup(PrismStateGroup stateGroup) {
            this.stateGroup = stateGroup;
        }

        public final Integer getInstanceTotal() {
            return instanceTotal;
        }

        public final void setInstanceTotal(Integer instanceTotal) {
            this.instanceTotal = instanceTotal;
        }

        public final BigDecimal getInstanceDurationAverage() {
            return instanceDurationAverage;
        }

        public final void setInstanceDurationAverage(BigDecimal instanceDurationAverage) {
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

        private String program;

        private String project;

        private BigDecimal ratingCount;

        private BigDecimal ratingAverage;

        private PrismStateGroup stateGroup;

        public final Integer getId() {
            return id;
        }

        public final void setId(Integer id) {
            this.id = id;
        }

        public final String getProgram() {
            return program;
        }

        public final void setProgram(String program) {
            this.program = program;
        }

        public final String getProject() {
            return project;
        }

        public final void setProject(String project) {
            this.project = project;
        }

        public final BigDecimal getRatingCount() {
            return ratingCount;
        }

        public final void setRatingCount(BigDecimal ratingCount) {
            this.ratingCount = ratingCount;
        }

        public final BigDecimal getRatingAverage() {
            return ratingAverage;
        }

        public final void setRatingAverage(BigDecimal ratingAverage) {
            this.ratingAverage = ratingAverage;
        }

        public final PrismStateGroup getStateGroup() {
            return stateGroup;
        }

        public final void setStateGroup(PrismStateGroup stateGroup) {
            this.stateGroup = stateGroup;
        }

    }

}
