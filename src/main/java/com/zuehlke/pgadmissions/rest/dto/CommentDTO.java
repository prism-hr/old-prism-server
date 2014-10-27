package com.zuehlke.pgadmissions.rest.dto;

import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import com.zuehlke.pgadmissions.domain.definitions.YesNoUnsureResponse;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;

public class CommentDTO {

    private Integer user;

    private Integer delegateUser;

    private PrismAction action;

    @Size(max = 50000)
    private String content;

    private PrismState transitionState;

    @Valid
    private Set<CommentAssignedUserDTO> assignedUsers;

    @Valid
    private List<FileDTO> documents;

    private YesNoUnsureResponse eligible;

    private Boolean interested;

    private Integer applicationRating;

    private LocalDateTime interviewDateTime;

    private TimeZone interviewTimeZone;

    private Integer interviewDuration;

    @Size(max = 1000)
    private String intervieweeInstructions;

    @Size(max = 1000)
    private String interviewerInstructions;

    @Size(max = 100)
    private String interviewLocation;

    private Set<LocalDateTime> appointmentTimeslots;

    private Set<Integer> appointmentPreferences;

    private Boolean recruiterAcceptAppointment;

    @Size(max = 255)
    private String positionTitle;

    @Size(max = 2000)
    private String positionDescription;

    private LocalDate positionProvisionalStartDate;

    private String appointmentConditions;

    private Boolean declinedResponse;

    private Integer rejectionReason;

    private InstitutionDTO institution;

    private ProgramDTO program;

    private ProjectDTO project;

    public Integer getUser() {
        return user;
    }

    public void setUser(Integer user) {
        this.user = user;
    }

    public Integer getDelegateUser() {
        return delegateUser;
    }

    public void setDelegateUser(Integer delegateUser) {
        this.delegateUser = delegateUser;
    }

    public PrismAction getAction() {
        return action;
    }

    public void setAction(PrismAction action) {
        this.action = action;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public PrismState getTransitionState() {
        return transitionState;
    }

    public void setTransitionState(PrismState transitionState) {
        this.transitionState = transitionState;
    }

    public Set<CommentAssignedUserDTO> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(Set<CommentAssignedUserDTO> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public List<FileDTO> getDocuments() {
        return documents;
    }

    public void setDocuments(List<FileDTO> documents) {
        this.documents = documents;
    }

    public YesNoUnsureResponse getEligible() {
        return eligible;
    }

    public void setEligible(YesNoUnsureResponse eligible) {
        this.eligible = eligible;
    }

    public final Boolean getInterested() {
        return interested;
    }

    public final void setInterested(Boolean interested) {
        this.interested = interested;
    }

    public Integer getApplicationRating() {
        return applicationRating;
    }

    public void setApplicationRating(Integer applicationRating) {
        this.applicationRating = applicationRating;
    }

    public LocalDateTime getInterviewDateTime() {
        return interviewDateTime;
    }

    public void setInterviewDateTime(LocalDateTime interviewDateTime) {
        this.interviewDateTime = interviewDateTime;
    }

    public TimeZone getInterviewTimeZone() {
        return interviewTimeZone;
    }

    public void setInterviewTimeZone(TimeZone interviewTimeZone) {
        this.interviewTimeZone = interviewTimeZone;
    }

    public Integer getInterviewDuration() {
        return interviewDuration;
    }

    public void setInterviewDuration(Integer interviewDuration) {
        this.interviewDuration = interviewDuration;
    }

    public String getIntervieweeInstructions() {
        return intervieweeInstructions;
    }

    public void setIntervieweeInstructions(String intervieweeInstructions) {
        this.intervieweeInstructions = intervieweeInstructions;
    }

    public String getInterviewerInstructions() {
        return interviewerInstructions;
    }

    public void setInterviewerInstructions(String interviewerInstructions) {
        this.interviewerInstructions = interviewerInstructions;
    }

    public String getInterviewLocation() {
        return interviewLocation;
    }

    public void setInterviewLocation(String interviewLocation) {
        this.interviewLocation = interviewLocation;
    }

    public Set<LocalDateTime> getAppointmentTimeslots() {
        return appointmentTimeslots;
    }

    public void setAppointmentTimeslots(Set<LocalDateTime> appointmentTimeslots) {
        this.appointmentTimeslots = appointmentTimeslots;
    }

    public Set<Integer> getAppointmentPreferences() {
        return appointmentPreferences;
    }

    public void setAppointmentPreferences(Set<Integer> appointmentPreferences) {
        this.appointmentPreferences = appointmentPreferences;
    }

    public Boolean getRecruiterAcceptAppointment() {
        return recruiterAcceptAppointment;
    }

    public void setRecruiterAcceptAppointment(Boolean recruiterAcceptAppointment) {
        this.recruiterAcceptAppointment = recruiterAcceptAppointment;
    }

    public String getPositionTitle() {
        return positionTitle;
    }

    public void setPositionTitle(String positionTitle) {
        this.positionTitle = positionTitle;
    }

    public String getPositionDescription() {
        return positionDescription;
    }

    public void setPositionDescription(String positionDescription) {
        this.positionDescription = positionDescription;
    }

    public LocalDate getPositionProvisionalStartDate() {
        return positionProvisionalStartDate;
    }

    public void setPositionProvisionalStartDate(LocalDate positionProvisionalStartDate) {
        this.positionProvisionalStartDate = positionProvisionalStartDate;
    }

    public String getAppointmentConditions() {
        return appointmentConditions;
    }

    public void setAppointmentConditions(String appointmentConditions) {
        this.appointmentConditions = appointmentConditions;
    }

    public Boolean getDeclinedResponse() {
        return declinedResponse;
    }

    public void setDeclinedResponse(Boolean declinedResponse) {
        this.declinedResponse = declinedResponse;
    }

    public Integer getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(Integer rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public InstitutionDTO getInstitution() {
        return institution;
    }

    public void setInstitution(InstitutionDTO institution) {
        this.institution = institution;
    }

    public ProgramDTO getProgram() {
        return program;
    }

    public void setProgram(ProgramDTO program) {
        this.program = program;
    }

    public ProjectDTO getProject() {
        return project;
    }

    public void setProject(ProjectDTO project) {
        this.project = project;
    }

    public Object fetchResouceDTO() {
        switch (action.getScope()) {
        case INSTITUTION:
            return institution;
        case PROGRAM:
            return program;
        case PROJECT:
            return project;
        default:
            throw new Error();
        }
    }

}
