package com.zuehlke.pgadmissions.rest.representation.application;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.rest.representation.AbstractResourceRepresentation;
import com.zuehlke.pgadmissions.rest.representation.InstitutionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;

public class ApplicationRepresentation extends AbstractResourceRepresentation {

    private UserRepresentation user;

    private InstitutionRepresentation institution;

    private ProgramRepresentation program;

    private ProjectRepresentation project;

    private LocalDate closingDate;

    private DateTime submittedTimestamp;

    private LocalDate dueDate;

    private DateTime createdTimestamp;

    private DateTime updatedTimestamp;

    private PersonalDetailsRepresentation personalDetails;

    private ProgramDetailsRepresentation programDetails;

    private ApplicationAddressRepresentation address;

    private List<QualificationRepresentation> qualifications;

    private List<EmploymentPositionRepresentation> employmentPositions;

    private List<FundingRepresentation> fundings;

    private List<RefereeRepresentation> referees;

    private ApplicationDocumentRepresentation document;

    private AdditionalInformationRepresentation additionalInformation;

    private List<UserRepresentation> usersInterestedInApplication;

    private List<UserRepresentation> usersPotentiallyInterestedInApplication;

    private List<DateTime> interviewTimeslots;

    private List<AppointmentPreferenceAnswersRepresentation> preferenceAnswers;

    public InstitutionRepresentation getInstitution() {
        return institution;
    }

    public void setInstitution(InstitutionRepresentation institution) {
        this.institution = institution;
    }

    public ProgramRepresentation getProgram() {
        return program;
    }

    public void setProgram(ProgramRepresentation program) {
        this.program = program;
    }

    public UserRepresentation getUser() {
        return user;
    }

    public void setUser(UserRepresentation user) {
        this.user = user;
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

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public DateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public DateTime getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(DateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public PersonalDetailsRepresentation getPersonalDetails() {
        return personalDetails;
    }

    public void setPersonalDetails(PersonalDetailsRepresentation personalDetails) {
        this.personalDetails = personalDetails;
    }

    public ProgramDetailsRepresentation getProgramDetails() {
        return programDetails;
    }

    public void setProgramDetails(ProgramDetailsRepresentation programDetails) {
        this.programDetails = programDetails;
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

    public List<DateTime> getInterviewTimeslots() {
        return interviewTimeslots;
    }

    public void setInterviewTimeslots(List<DateTime> interviewTimeslots) {
        this.interviewTimeslots = interviewTimeslots;
    }

    public List<AppointmentPreferenceAnswersRepresentation> getPreferenceAnswers() {
        return preferenceAnswers;
    }

    public void setPreferenceAnswers(List<AppointmentPreferenceAnswersRepresentation> preferenceAnswers) {
        this.preferenceAnswers = preferenceAnswers;
    }
}
