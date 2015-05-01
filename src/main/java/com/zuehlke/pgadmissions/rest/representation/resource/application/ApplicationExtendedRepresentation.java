package com.zuehlke.pgadmissions.rest.representation.resource.application;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.rest.representation.ApplicationSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.AbstractResourceRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ProgramRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ProjectRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.advert.AdvertRepresentation;

public class ApplicationExtendedRepresentation extends AbstractResourceRepresentation {

    private ProgramRepresentation program;

    private ProjectRepresentation project;

    private LocalDate closingDate;

    private DateTime submittedTimestamp;

    private Boolean previousApplication;

    private ApplicationStudyDetailRepresentation studyDetail;

    private ProgramDetailRepresentation programDetail;

    private List<String> possibleThemes;

    private List<String> possibleLocations;

    private List<ApplicationSuggestedSupervisorRepresentation> supervisors;

    private PersonalDetailRepresentation personalDetail;

    private ApplicationAddressRepresentation address;

    private List<QualificationRepresentation> qualifications;

    private List<EmploymentPositionRepresentation> employmentPositions;

    private List<FundingRepresentation> fundings;

    private List<PrizeRepresentation> prizes;

    private List<RefereeRepresentation> referees;

    private ApplicationDocumentRepresentation document;

    private AdditionalInformationRepresentation additionalInformation;

    private List<UserRepresentation> usersInterestedInApplication;

    private List<UserRepresentation> usersPotentiallyInterestedInApplication;

    private InterviewRepresentation interview;

    private OfferRepresentation offerRecommendation;

    private List<ApplicationAssignedSupervisorRepresentation> assignedSupervisors;

    private List<PrismStudyOption> availableStudyOptions;

    private List<String> primaryThemes;

    private List<String> secondaryThemes;

    private BigDecimal applicationRatingAverage;

    private ApplicationSummaryRepresentation resourceSummary;

    private List<AdvertRepresentation> recommendedAdverts;

    public ProgramRepresentation getProgram() {
        return program;
    }

    public void setProgram(ProgramRepresentation program) {
        this.program = program;
    }

    public ProjectRepresentation getProject() {
        return project;
    }

    public void setProject(ProjectRepresentation project) {
        this.project = project;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }

    public DateTime getSubmittedTimestamp() {
        return submittedTimestamp;
    }

    public void setSubmittedTimestamp(DateTime submittedTimestamp) {
        this.submittedTimestamp = submittedTimestamp;
    }

    public final Boolean getPreviousApplication() {
        return previousApplication;
    }

    public final void setPreviousApplication(Boolean previousApplication) {
        this.previousApplication = previousApplication;
    }

    public ApplicationStudyDetailRepresentation getStudyDetail() {
        return studyDetail;
    }

    public void setStudyDetail(ApplicationStudyDetailRepresentation studyDetail) {
        this.studyDetail = studyDetail;
    }

    public ProgramDetailRepresentation getProgramDetail() {
        return programDetail;
    }

    public void setProgramDetail(ProgramDetailRepresentation programDetail) {
        this.programDetail = programDetail;
    }

    public List<String> getPossibleThemes() {
        return possibleThemes;
    }

    public void setPossibleThemes(List<String> possibleThemes) {
        this.possibleThemes = possibleThemes;
    }

    public List<String> getPossibleLocations() {
        return possibleLocations;
    }

    public void setPossibleLocations(List<String> possibleLocations) {
        this.possibleLocations = possibleLocations;
    }

    public List<ApplicationSuggestedSupervisorRepresentation> getSupervisors() {
        return supervisors;
    }

    public void setSupervisors(List<ApplicationSuggestedSupervisorRepresentation> supervisors) {
        this.supervisors = supervisors;
    }

    public PersonalDetailRepresentation getPersonalDetail() {
        return personalDetail;
    }

    public void setPersonalDetail(PersonalDetailRepresentation personalDetail) {
        this.personalDetail = personalDetail;
    }

    public ApplicationAddressRepresentation getAddress() {
        return address;
    }

    public void setAddress(ApplicationAddressRepresentation address) {
        this.address = address;
    }

    public List<QualificationRepresentation> getQualifications() {
        return qualifications;
    }

    public void setQualifications(List<QualificationRepresentation> qualifications) {
        this.qualifications = qualifications;
    }

    public List<EmploymentPositionRepresentation> getEmploymentPositions() {
        return employmentPositions;
    }

    public void setEmploymentPositions(List<EmploymentPositionRepresentation> employmentPositions) {
        this.employmentPositions = employmentPositions;
    }

    public List<FundingRepresentation> getFundings() {
        return fundings;
    }

    public void setFundings(List<FundingRepresentation> fundings) {
        this.fundings = fundings;
    }

    public List<PrizeRepresentation> getPrizes() {
        return prizes;
    }

    public void setPrizes(List<PrizeRepresentation> prizes) {
        this.prizes = prizes;
    }

    public List<RefereeRepresentation> getReferees() {
        return referees;
    }

    public void setReferees(List<RefereeRepresentation> referees) {
        this.referees = referees;
    }

    public ApplicationDocumentRepresentation getDocument() {
        return document;
    }

    public void setDocument(ApplicationDocumentRepresentation document) {
        this.document = document;
    }

    public AdditionalInformationRepresentation getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(AdditionalInformationRepresentation additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public List<UserRepresentation> getUsersInterestedInApplication() {
        return usersInterestedInApplication;
    }

    public void setUsersInterestedInApplication(List<UserRepresentation> usersInterestedInApplication) {
        this.usersInterestedInApplication = usersInterestedInApplication;
    }

    public List<UserRepresentation> getUsersPotentiallyInterestedInApplication() {
        return usersPotentiallyInterestedInApplication;
    }

    public void setUsersPotentiallyInterestedInApplication(List<UserRepresentation> usersPotentiallyInterestedInApplication) {
        this.usersPotentiallyInterestedInApplication = usersPotentiallyInterestedInApplication;
    }

    public InterviewRepresentation getInterview() {
        return interview;
    }

    public void setInterview(InterviewRepresentation interview) {
        this.interview = interview;
    }

    public OfferRepresentation getOfferRecommendation() {
        return offerRecommendation;
    }

    public void setOfferRecommendation(OfferRepresentation offerRecommendation) {
        this.offerRecommendation = offerRecommendation;
    }

    public List<ApplicationAssignedSupervisorRepresentation> getAssignedSupervisors() {
        return assignedSupervisors;
    }

    public void setAssignedSupervisors(List<ApplicationAssignedSupervisorRepresentation> assignedSupervisors) {
        this.assignedSupervisors = assignedSupervisors;
    }

    public List<PrismStudyOption> getAvailableStudyOptions() {
        return availableStudyOptions;
    }

    public void setAvailableStudyOptions(List<PrismStudyOption> availableStudyOptions) {
        this.availableStudyOptions = availableStudyOptions;
    }

    public List<String> getPrimaryThemes() {
        return primaryThemes;
    }

    public void setPrimaryThemes(List<String> primaryThemes) {
        this.primaryThemes = primaryThemes;
    }

    public List<String> getSecondaryThemes() {
        return secondaryThemes;
    }

    public void setSecondaryThemes(List<String> secondaryThemes) {
        this.secondaryThemes = secondaryThemes;
    }

    public BigDecimal getApplicationRatingAverage() {
        return applicationRatingAverage;
    }

    public void setApplicationRatingAverage(BigDecimal applicationRatingAverage) {
        this.applicationRatingAverage = applicationRatingAverage;
    }

    public ApplicationSummaryRepresentation getResourceSummary() {
        return resourceSummary;
    }

    public void setResourceSummary(ApplicationSummaryRepresentation resourceSummary) {
        this.resourceSummary = resourceSummary;
    }

    public final List<AdvertRepresentation> getRecommendedAdverts() {
        return recommendedAdverts;
    }

    public final void setRecommendedAdverts(List<AdvertRepresentation> recommendedAdverts) {
        this.recommendedAdverts = recommendedAdverts;
    }

}
