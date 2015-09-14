package com.zuehlke.pgadmissions.rest.representation.resource.application;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationExtended;

public class ApplicationRepresentationSimple extends ResourceRepresentationExtended {

    private LocalDate closingDate;

    private DateTime submittedTimestamp;

    private ApplicationProgramDetailRepresentation programDetail;

    private ApplicationPersonalDetailRepresentation personalDetail;

    private ApplicationAddressRepresentation address;

    private List<ApplicationQualificationRepresentation> qualifications;

    private List<ApplicationEmploymentPositionRepresentation> employmentPositions;

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

    public ApplicationProgramDetailRepresentation getProgramDetail() {
        return programDetail;
    }

    public void setProgramDetail(ApplicationProgramDetailRepresentation programDetail) {
        this.programDetail = programDetail;
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
