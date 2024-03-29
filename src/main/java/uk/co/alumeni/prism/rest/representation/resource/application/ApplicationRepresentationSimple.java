package uk.co.alumeni.prism.rest.representation.resource.application;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import uk.co.alumeni.prism.rest.representation.profile.*;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationExtended;

import java.util.List;

public class ApplicationRepresentationSimple extends ResourceRepresentationExtended {

    private LocalDate closingDate;

    private DateTime submittedTimestamp;

    private ApplicationProgramDetailRepresentation programDetail;

    private ProfilePersonalDetailRepresentation personalDetail;

    private ProfileAddressRepresentation address;

    private List<ProfileQualificationRepresentation> qualifications;

    private List<ProfileAwardRepresentation> awards;

    private List<ProfileEmploymentPositionRepresentation> employmentPositions;

    private List<ProfileRefereeRepresentation> referees;

    private ProfileDocumentRepresentation document;

    private ProfileAdditionalInformationRepresentation additionalInformation;

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

    public ProfilePersonalDetailRepresentation getPersonalDetail() {
        return personalDetail;
    }

    public void setPersonalDetail(ProfilePersonalDetailRepresentation personalDetail) {
        this.personalDetail = personalDetail;
    }

    public ProfileAddressRepresentation getAddress() {
        return address;
    }

    public void setAddress(ProfileAddressRepresentation address) {
        this.address = address;
    }

    public List<ProfileQualificationRepresentation> getQualifications() {
        return qualifications;
    }

    public void setQualifications(List<ProfileQualificationRepresentation> qualifications) {
        this.qualifications = qualifications;
    }

    public List<ProfileAwardRepresentation> getAwards() {
        return awards;
    }

    public void setAwards(List<ProfileAwardRepresentation> awards) {
        this.awards = awards;
    }

    public List<ProfileEmploymentPositionRepresentation> getEmploymentPositions() {
        return employmentPositions;
    }

    public void setEmploymentPositions(List<ProfileEmploymentPositionRepresentation> employmentPositions) {
        this.employmentPositions = employmentPositions;
    }

    public List<ProfileRefereeRepresentation> getReferees() {
        return referees;
    }

    public void setReferees(List<ProfileRefereeRepresentation> referees) {
        this.referees = referees;
    }

    public ProfileDocumentRepresentation getDocument() {
        return document;
    }

    public void setDocument(ProfileDocumentRepresentation document) {
        this.document = document;
    }

    public ProfileAdditionalInformationRepresentation getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(ProfileAdditionalInformationRepresentation additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

}
