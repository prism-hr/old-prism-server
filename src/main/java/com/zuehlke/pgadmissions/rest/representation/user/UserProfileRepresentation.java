package com.zuehlke.pgadmissions.rest.representation.user;

import java.util.List;

import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.rest.representation.profile.ProfileAdditionalInformationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.profile.ProfileAddressRepresentation;
import com.zuehlke.pgadmissions.rest.representation.profile.ProfileDocumentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.profile.ProfileEmploymentPositionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.profile.ProfilePersonalDetailRepresentation;
import com.zuehlke.pgadmissions.rest.representation.profile.ProfileQualificationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.profile.ProfileRefereeRepresentation;

public class UserProfileRepresentation {

    private ProfilePersonalDetailRepresentation personalDetail;
    
    private ProfileAddressRepresentation address;
    
    private List<ProfileQualificationRepresentation> qualifications;
    
    private List<ProfileEmploymentPositionRepresentation> employmentPositions;
    
    private List<ProfileRefereeRepresentation> referees;
    
    private ProfileDocumentRepresentation document;
    
    private ProfileAdditionalInformationRepresentation additionalInformation;
    
    private DateTime updatedTimestamp;

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

    public DateTime getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(DateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }
    
    public UserProfileRepresentation withPersonalDetail(ProfilePersonalDetailRepresentation personalDetail) {
        this.personalDetail = personalDetail;
        return this;
    }

    public UserProfileRepresentation withAddress(ProfileAddressRepresentation address) {
        this.address = address;
        return this;
    }

    public UserProfileRepresentation withQualifications(List<ProfileQualificationRepresentation> qualifications) {
        this.qualifications = qualifications;
        return this;
    }

    public UserProfileRepresentation withEmploymentPositions(List<ProfileEmploymentPositionRepresentation> employmentPositions) {
        this.employmentPositions = employmentPositions;
        return this;
    }
    
    public UserProfileRepresentation withReferees(List<ProfileRefereeRepresentation> referees) {
        this.referees = referees;
        return this;
    }

    public UserProfileRepresentation withDocument(ProfileDocumentRepresentation document) {
        this.document = document;
        return this;
    }

    public UserProfileRepresentation withAdditionalInformation(ProfileAdditionalInformationRepresentation additionalInformation) {
        this.additionalInformation = additionalInformation;
        return this;
    }

    public UserProfileRepresentation withUpdatedTimestamp(DateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
        return this;
    }
    
    
}