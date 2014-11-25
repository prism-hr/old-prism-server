package com.zuehlke.pgadmissions.rest.dto;

import com.zuehlke.pgadmissions.domain.definitions.PrismYesNoUnsureResponse;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import java.util.TimeZone;

public class CommentDTO {

    private Integer user;

    private Integer delegateUser;

    private PrismAction action;

    @Size(max = 50000)
    private String content;

    private PrismState transitionState;

    private PrismYesNoUnsureResponse applicationEligible;

    private Boolean applicationInterested;

    private BigDecimal applicationRating;

    private LocalDateTime interviewDateTime;

    private TimeZone interviewTimeZone;

    private Integer interviewDuration;

    @Size(max = 1000)
    private String intervieweeInstructions;

    @Size(max = 1000)
    private String interviewerInstructions;

    @Size(max = 100)
    private String interviewLocation;

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

    @Valid
    private List<CommentAssignedUserDTO> assignedUsers;

    @Valid
    private List<PrismState> secondaryTransitionStates;

    private List<LocalDateTime> appointmentTimeslots;

    private List<Integer> appointmentPreferences;

    @Valid
    private List<CommentCustomResponseDTO> customResponses;

    @Valid
    private List<FileDTO> documents;

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

    public PrismYesNoUnsureResponse getApplicationEligible() {
        return applicationEligible;
    }

    public void setApplicationEligible(PrismYesNoUnsureResponse applicationEligible) {
        this.applicationEligible = applicationEligible;
    }

    public Boolean getApplicationInterested() {
        return applicationInterested;
    }

    public void setApplicationInterested(Boolean applicationInterested) {
        this.applicationInterested = applicationInterested;
    }

    public BigDecimal getApplicationRating() {
        return applicationRating;
    }

    public void setApplicationRating(BigDecimal applicationRating) {
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

    public List<CommentAssignedUserDTO> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(List<CommentAssignedUserDTO> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public List<PrismState> getSecondaryTransitionStates() {
        return secondaryTransitionStates;
    }

    public void setSecondaryTransitionStates(List<PrismState> secondaryTransitionStates) {
        this.secondaryTransitionStates = secondaryTransitionStates;
    }

    public List<LocalDateTime> getAppointmentTimeslots() {
        return appointmentTimeslots;
    }

    public void setAppointmentTimeslots(List<LocalDateTime> appointmentTimeslots) {
        this.appointmentTimeslots = appointmentTimeslots;
    }

    public List<Integer> getAppointmentPreferences() {
        return appointmentPreferences;
    }

    public void setAppointmentPreferences(List<Integer> appointmentPreferences) {
        this.appointmentPreferences = appointmentPreferences;
    }

    public List<CommentCustomResponseDTO> getCustomResponses() {
        return customResponses;
    }

    public void setCustomResponses(List<CommentCustomResponseDTO> customResponses) {
        this.customResponses = customResponses;
    }

    public List<FileDTO> getDocuments() {
        return documents;
    }

    public void setDocuments(List<FileDTO> documents) {
        this.documents = documents;
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
