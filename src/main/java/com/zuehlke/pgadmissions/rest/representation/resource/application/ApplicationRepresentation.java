package com.zuehlke.pgadmissions.rest.representation.resource.application;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationExtended;

public class ApplicationRepresentation extends ResourceRepresentationExtended {
    
    private LocalDate closingDate;

    private DateTime submittedTimestamp;

    private Boolean previousApplication;

    private ApplicationProgramDetailRepresentation programDetail;
    
    private ApplicationStudyDetailRepresentation studyDetail;
    
    private List<String> primaryThemes;

    private List<String> secondaryThemes;

    private List<ApplicationSupervisorRepresentation> supervisors;

    private ApplicationPersonalDetailRepresentation personalDetail;

    private ApplicationAddressRepresentation address;

    private List<ApplicationQualificationRepresentation> qualifications;

    private List<ApplicationEmploymentPositionRepresentation> employmentPositions;

    private List<ApplicationFundingRepresentation> fundings;

    private List<ApplicationPrizeRepresentation> prizes;

    private List<ApplicationRefereeRepresentation> referees;

    private ApplicationDocumentRepresentation document;

    private ApplicationAdditionalInformationRepresentation additionalInformation;

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

    public Boolean getPreviousApplication() {
        return previousApplication;
    }

    public void setPreviousApplication(Boolean previousApplication) {
        this.previousApplication = previousApplication;
    }

    public ApplicationProgramDetailRepresentation getProgramDetail() {
        return programDetail;
    }

    public void setProgramDetail(ApplicationProgramDetailRepresentation programDetail) {
        this.programDetail = programDetail;
    }

    public ApplicationStudyDetailRepresentation getStudyDetail() {
        return studyDetail;
    }

    public void setStudyDetail(ApplicationStudyDetailRepresentation studyDetail) {
        this.studyDetail = studyDetail;
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

    public List<ApplicationSupervisorRepresentation> getSupervisors() {
        return supervisors;
    }

    public void setSupervisors(List<ApplicationSupervisorRepresentation> supervisors) {
        this.supervisors = supervisors;
    }

    public ApplicationPersonalDetailRepresentation getPersonalDetail() {
        return personalDetail;
    }

    public void setPersonalDetail(ApplicationPersonalDetailRepresentation personalDetail) {
        this.personalDetail = personalDetail;
    }

    public ApplicationAddressRepresentation getAddress() {
        return address;
    }

    public void setAddress(ApplicationAddressRepresentation address) {
        this.address = address;
    }

    public List<ApplicationQualificationRepresentation> getQualifications() {
        return qualifications;
    }

    public void setQualifications(List<ApplicationQualificationRepresentation> qualifications) {
        this.qualifications = qualifications;
    }

    public List<ApplicationEmploymentPositionRepresentation> getEmploymentPositions() {
        return employmentPositions;
    }

    public void setEmploymentPositions(List<ApplicationEmploymentPositionRepresentation> employmentPositions) {
        this.employmentPositions = employmentPositions;
    }

    public List<ApplicationFundingRepresentation> getFundings() {
        return fundings;
    }

    public void setFundings(List<ApplicationFundingRepresentation> fundings) {
        this.fundings = fundings;
    }

    public List<ApplicationPrizeRepresentation> getPrizes() {
        return prizes;
    }

    public void setPrizes(List<ApplicationPrizeRepresentation> prizes) {
        this.prizes = prizes;
    }

    public List<ApplicationRefereeRepresentation> getReferees() {
        return referees;
    }

    public void setReferees(List<ApplicationRefereeRepresentation> referees) {
        this.referees = referees;
    }

    public ApplicationDocumentRepresentation getDocument() {
        return document;
    }

    public void setDocument(ApplicationDocumentRepresentation document) {
        this.document = document;
    }

    public ApplicationAdditionalInformationRepresentation getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(ApplicationAdditionalInformationRepresentation additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

}
