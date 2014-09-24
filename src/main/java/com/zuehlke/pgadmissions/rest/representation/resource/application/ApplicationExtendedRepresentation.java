package com.zuehlke.pgadmissions.rest.representation.resource.application;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.rest.representation.AbstractResourceRepresentation;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.AppointmentTimeslotRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.InstitutionRepresentation;

public class ApplicationExtendedRepresentation extends AbstractResourceRepresentation {

    private InstitutionRepresentation institution;

    private String programTitle;

    private String projectTitle;

    private LocalDate closingDate;

    private DateTime submittedTimestamp;

    private PersonalDetailRepresentation personalDetail;

    private List<ApplicationSuggestedSupervisorRepresentation> supervisors;

    private ProgramDetailRepresentation programDetail;

    private ApplicationAddressRepresentation address;

    private List<QualificationRepresentation> qualifications;

    private List<EmploymentPositionRepresentation> employmentPositions;

    private List<FundingRepresentation> fundings;

    private List<RefereeRepresentation> referees;

    private ApplicationDocumentRepresentation document;

    private AdditionalInformationRepresentation additionalInformation;

    private List<UserRepresentation> usersInterestedInApplication;

    private List<UserRepresentation> usersPotentiallyInterestedInApplication;

    private List<AppointmentTimeslotRepresentation> appointmentTimeslots;

    private List<UserAppointmentPreferencesRepresentation> appointmentPreferences;

    private OfferRepresentation offerRecommendation;

    private List<ApplicationAssignedSupervisorRepresentation> assignedSupervisors;

    private List<PrismStudyOption> availableStudyOptions;

    public InstitutionRepresentation getInstitution() {
        return institution;
    }

    public void setInstitution(InstitutionRepresentation institution) {
        this.institution = institution;
    }

    public String getProgramTitle() {
        return programTitle;
    }

    public void setProgramTitle(String programTitle) {
        this.programTitle = programTitle;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
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

    public PersonalDetailRepresentation getPersonalDetail() {
        return personalDetail;
    }

    public void setPersonalDetail(PersonalDetailRepresentation personalDetail) {
        this.personalDetail = personalDetail;
    }

    public ProgramDetailRepresentation getProgramDetail() {
        return programDetail;
    }

    public void setProgramDetail(ProgramDetailRepresentation programDetail) {
        this.programDetail = programDetail;
    }

    public List<ApplicationSuggestedSupervisorRepresentation> getSupervisors() {
        return supervisors;
    }

    public void setSupervisors(List<ApplicationSuggestedSupervisorRepresentation> supervisors) {
        this.supervisors = supervisors;
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

    public final List<AppointmentTimeslotRepresentation> getAppointmentTimeslots() {
        return appointmentTimeslots;
    }

    public final void setAppointmentTimeslots(List<AppointmentTimeslotRepresentation> appointmentTimeslots) {
        this.appointmentTimeslots = appointmentTimeslots;
    }

    public final List<UserAppointmentPreferencesRepresentation> getAppointmentPreferences() {
        return appointmentPreferences;
    }

    public final void setAppointmentPreferences(List<UserAppointmentPreferencesRepresentation> appointmentPreferences) {
        this.appointmentPreferences = appointmentPreferences;
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
}
