package uk.co.alumeni.prism.rest.representation.profile;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.DateTime;

public class ProfileRepresentationUser extends ProfileRepresentationMessage {
    
    private BigDecimal completeScore;
    
    private ProfilePersonalDetailRepresentation personalDetail;

    private ProfileAddressRepresentation address;

    private List<ProfileQualificationRepresentation> qualifications;

    private List<ProfileAwardRepresentation> awards;

    private List<ProfileEmploymentPositionRepresentation> employmentPositions;

    private List<ProfileRefereeRepresentation> referees;

    private ProfileDocumentRepresentation document;

    private ProfileAdditionalInformationRepresentation additionalInformation;

    private Boolean shared;

    private DateTime updatedTimestamp;

    public BigDecimal getCompleteScore() {
        return completeScore;
    }

    public void setCompleteScore(BigDecimal completeScore) {
        this.completeScore = completeScore;
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

    public Boolean getShared() {
        return shared;
    }

    public void setShared(Boolean shared) {
        this.shared = shared;
    }

    public DateTime getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(DateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public ProfileRepresentationUser withCompleteScore(BigDecimal completeScore) {
        this.completeScore = completeScore;
        return this;
    }
    
    public ProfileRepresentationUser withPersonalDetail(ProfilePersonalDetailRepresentation personalDetail) {
        this.personalDetail = personalDetail;
        return this;
    }

    public ProfileRepresentationUser withAddress(ProfileAddressRepresentation address) {
        this.address = address;
        return this;
    }

    public ProfileRepresentationUser withQualifications(List<ProfileQualificationRepresentation> qualifications) {
        this.qualifications = qualifications;
        return this;
    }

    public ProfileRepresentationUser withAwards(List<ProfileAwardRepresentation> awards) {
        this.awards = awards;
        return this;
    }

    public ProfileRepresentationUser withEmploymentPositions(List<ProfileEmploymentPositionRepresentation> employmentPositions) {
        this.employmentPositions = employmentPositions;
        return this;
    }

    public ProfileRepresentationUser withReferees(List<ProfileRefereeRepresentation> referees) {
        this.referees = referees;
        return this;
    }

    public ProfileRepresentationUser withDocument(ProfileDocumentRepresentation document) {
        this.document = document;
        return this;
    }

    public ProfileRepresentationUser withAdditionalInformation(ProfileAdditionalInformationRepresentation additionalInformation) {
        this.additionalInformation = additionalInformation;
        return this;
    }

    public ProfileRepresentationUser withShared(final Boolean shared) {
        this.shared = shared;
        return this;
    }

    public ProfileRepresentationUser withUpdatedTimestamp(DateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
        return this;
    }

}
