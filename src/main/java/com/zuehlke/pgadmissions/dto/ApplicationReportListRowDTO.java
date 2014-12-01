package com.zuehlke.pgadmissions.dto;

import java.math.BigDecimal;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.definitions.PrismOfferType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;

public class ApplicationReportListRowDTO {

    private Integer id;

    private String name;

    private String email;

    private String nationality;

    private String residence;

    private String countryOfBirth;

    private LocalDate dateOfBirth;

    private String gender;

    private String institution;

    private String program;

    private String project;

    private String studyOption;

    private String referralSource;

    private String referrer;

    private DateTime createdDate;

    private DateTime closingDate;

    private DateTime submittedDate;

    private DateTime updatedDate;

    private Integer ratingCount;

    private BigDecimal ratingAverage;

    private PrismStateGroup state;

    private Integer providedReferences;

    private Integer declinedReferences;

    private Integer verificationInstanceCount;

    private BigDecimal verificationInstanceDurationAverage;

    private Integer referenceInstanceCount;

    private BigDecimal referenceInstanceDurationAverage;

    private Integer reviewInstanceCount;

    private BigDecimal reviewInstanceDurationAverage;

    private Integer interviewInstanceCount;

    private BigDecimal interviewInstanceDurationAverage;

    private Integer approvalInstanceCount;

    private BigDecimal approvalInstanceDurationAverage;

    private LocalDate confirmedStartDate;

    private PrismOfferType confirmedOfferType;

    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final String getEmail() {
        return email;
    }

    public final void setEmail(String email) {
        this.email = email;
    }

    public final String getNationality() {
        return nationality;
    }

    public final void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public final String getResidence() {
        return residence;
    }

    public final void setResidence(String residence) {
        this.residence = residence;
    }

    public final String getCountryOfBirth() {
        return countryOfBirth;
    }

    public final void setCountryOfBirth(String countryOfBirth) {
        this.countryOfBirth = countryOfBirth;
    }

    public final LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public final void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public final String getGender() {
        return gender;
    }

    public final void setGender(String gender) {
        this.gender = gender;
    }

    public final String getInstitution() {
        return institution;
    }

    public final void setInstitution(String institution) {
        this.institution = institution;
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

    public final DateTime getCreatedDate() {
        return createdDate;
    }

    public final void setCreatedDate(DateTime createdDate) {
        this.createdDate = createdDate;
    }

    public final DateTime getClosingDate() {
        return closingDate;
    }

    public final void setClosingDate(DateTime closingDate) {
        this.closingDate = closingDate;
    }

    public final DateTime getSubmittedDate() {
        return submittedDate;
    }

    public final void setSubmittedDate(DateTime submittedDate) {
        this.submittedDate = submittedDate;
    }

    public final DateTime getUpdatedDate() {
        return updatedDate;
    }

    public final void setUpdatedDate(DateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public final Integer getRatingCount() {
        return ratingCount;
    }

    public final void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public final BigDecimal getRatingAverage() {
        return ratingAverage;
    }

    public final void setRatingAverage(BigDecimal ratingAverage) {
        this.ratingAverage = ratingAverage;
    }

    public final PrismStateGroup getState() {
        return state;
    }

    public final void setState(PrismStateGroup state) {
        this.state = state;
    }

    public final Integer getProvidedReferences() {
        return providedReferences;
    }

    public final void setProvidedReferences(Integer providedReferences) {
        this.providedReferences = providedReferences;
    }

    public final Integer getDeclinedReferences() {
        return declinedReferences;
    }

    public final void setDeclinedReferences(Integer declinedReferences) {
        this.declinedReferences = declinedReferences;
    }

    public final Integer getApplicationVerificationInstanceCount() {
        return verificationInstanceCount;
    }

    public final void setApplicationVerificationInstanceCount(Integer applicationVerificationInstanceCount) {
        this.verificationInstanceCount = applicationVerificationInstanceCount;
    }

    public final BigDecimal getVerificationInstanceDurationAverage() {
        return verificationInstanceDurationAverage;
    }

    public final void setVerificationInstanceDurationAverage(BigDecimal verificationInstanceDurationAverage) {
        this.verificationInstanceDurationAverage = verificationInstanceDurationAverage;
    }

    public final Integer getReferenceInstanceCount() {
        return referenceInstanceCount;
    }

    public final void setReferenceInstanceCount(Integer referenceInstanceCount) {
        this.referenceInstanceCount = referenceInstanceCount;
    }

    public final BigDecimal getReferenceInstanceDurationAverage() {
        return referenceInstanceDurationAverage;
    }

    public final void setReferenceInstanceDurationAverage(BigDecimal referenceInstanceDurationAverage) {
        this.referenceInstanceDurationAverage = referenceInstanceDurationAverage;
    }

    public final Integer getReviewInstanceCount() {
        return reviewInstanceCount;
    }

    public final void setReviewInstanceCount(Integer reviewInstanceCount) {
        this.reviewInstanceCount = reviewInstanceCount;
    }

    public final BigDecimal getReviewInstanceDurationAverage() {
        return reviewInstanceDurationAverage;
    }

    public final void setReviewInstanceDurationAverage(BigDecimal reviewInstanceDurationAverage) {
        this.reviewInstanceDurationAverage = reviewInstanceDurationAverage;
    }

    public final Integer getInterviewInstanceCount() {
        return interviewInstanceCount;
    }

    public final void setInterviewInstanceCount(Integer interviewInstanceCount) {
        this.interviewInstanceCount = interviewInstanceCount;
    }

    public final BigDecimal getInterviewInstanceDurationAverage() {
        return interviewInstanceDurationAverage;
    }

    public final void setInterviewInstanceDurationAverage(BigDecimal interviewInstanceDurationAverage) {
        this.interviewInstanceDurationAverage = interviewInstanceDurationAverage;
    }

    public final Integer getApprovalInstanceCount() {
        return approvalInstanceCount;
    }

    public final void setApprovalInstanceCount(Integer approvalInstanceCount) {
        this.approvalInstanceCount = approvalInstanceCount;
    }

    public final BigDecimal getApprovalInstanceDurationAverage() {
        return approvalInstanceDurationAverage;
    }

    public final void setApprovalInstanceDurationAverage(BigDecimal approvalInstanceDurationAverage) {
        this.approvalInstanceDurationAverage = approvalInstanceDurationAverage;
    }

    public final LocalDate getConfirmedStartDate() {
        return confirmedStartDate;
    }

    public final void setConfirmedStartDate(LocalDate confirmedStartDate) {
        this.confirmedStartDate = confirmedStartDate;
    }

    public final PrismOfferType getConfirmedOfferType() {
        return confirmedOfferType;
    }

    public final void setConfirmedOfferType(PrismOfferType confirmedOfferType) {
        this.confirmedOfferType = confirmedOfferType;
    }

    public String getDateOfBirthDisplay(String dateFormat) {
        return dateOfBirth == null ? null : dateOfBirth.toString(dateFormat);
    }

    public String getCreatedDateDisplay(String dateFormat) {
        return createdDate == null ? null : createdDate.toString(dateFormat);
    }

    public String getClosingDateDisplay(String dateFormat) {
        return closingDate == null ? null : closingDate.toString(dateFormat);
    }

    public String getSubmittedDateDisplay(String dateFormat) {
        return submittedDate == null ? null : submittedDate.toString(dateFormat);
    }

    public String getUpdatedDateDisplay(String dateFormat) {
        return updatedDate == null ? null : updatedDate.toString(dateFormat);
    }

    public String getAcademicYear() {
        DateTime baseline = submittedDate == null ? createdDate : submittedDate;
        Integer baselineMonth = baseline.getMonthOfYear();
        return baselineMonth < 10 ? Integer.toString(baseline.getYear()) : Integer.toString(baseline.plusYears(1).getYear());
    }

    public String getRatingCountDisplay() {
        return ratingCount == null ? null : ratingCount.toString();
    }

    public String getRatingAverageDisplay() {
        return ratingAverage == null ? null : ratingAverage.toPlainString();
    }

    public String getProvidedReferencesDisplay() {
        return providedReferences == null ? null : providedReferences.toString();
    }

    public String getDeclinedReferencesDisplay() {
        return declinedReferences == null ? null : declinedReferences.toString();
    }

    public String getConfirmedStartDateDisplay(String dateFormat) {
        return confirmedStartDate == null ? null : confirmedStartDate.toString(dateFormat);
    }

    public String getVerificationInstanceCountDisplay() {
        return verificationInstanceCount == null ? null : verificationInstanceCount.toString();
    }

    public String getVerificationInstanceDurationAverageDisplay() {
        return verificationInstanceDurationAverage == null ? null : verificationInstanceDurationAverage.toPlainString();
    }

    public String getReferenceInstanceCountDisplay() {
        return referenceInstanceCount == null ? null : referenceInstanceCount.toString();
    }

    public String getReferenceInstanceDurationAverageDisplay() {
        return referenceInstanceDurationAverage == null ? null : referenceInstanceDurationAverage.toPlainString();
    }

    public String getReviewInstanceCountDisplay() {
        return reviewInstanceCount == null ? null : reviewInstanceCount.toString();
    }

    public String getReviewInstanceDurationAverageDisplay() {
        return reviewInstanceDurationAverage == null ? null : reviewInstanceDurationAverage.toPlainString();
    }

    public String getInterviewInstanceCountDisplay() {
        return interviewInstanceCount == null ? null : interviewInstanceCount.toString();
    }

    public String getInterviewInstanceDurationAverageDisplay() {
        return interviewInstanceDurationAverage == null ? null : interviewInstanceDurationAverage.toPlainString();
    }

    public String getApprovalInstanceCountDisplay() {
        return approvalInstanceCount == null ? null : approvalInstanceCount.toString();
    }

    public String getApprovalInstanceDurationAverageDisplay() {
        return approvalInstanceDurationAverage == null ? null : approvalInstanceDurationAverage.toPlainString();
    }

    public PrismStudyOption getStudyOptionDisplay() {
        return studyOption == null ? null : PrismStudyOption.valueOf(studyOption);
    }

}