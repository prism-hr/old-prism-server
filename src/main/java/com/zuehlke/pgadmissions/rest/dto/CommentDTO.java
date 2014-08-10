package com.zuehlke.pgadmissions.rest.dto;

import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.definitions.YesNoUnsureResponse;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;

public class CommentDTO {

    private PrismAction action;

    private String content;

    private PrismState transitionState;

    private Set<CommentAssignedUserDTO> assignedUsers;

    private List<Integer> documents;

    private YesNoUnsureResponse qualified;

    private YesNoUnsureResponse competentInWorkLanguage;

    private String residenceState;

    private Boolean suitableForInstitution;

    private Boolean suitableForOpportunity;

    private Boolean desireToInterview;

    private Boolean desireToRecruit;

    private Integer rating;

    private DateTime interviewDateTime;

    private TimeZone interviewTimeZone;

    private Integer interviewDuration;

    private String intervieweeInstructions;

    private String interviewerInstructions;

    private String interviewLocation;

    private Set<DateTime> appointmentTimeslots;

    private Set<Integer> appointmentPreferences;

    private Boolean recruiterAcceptAppointment;
    
    private String rejectionReason;

    private String positionTitle;

    private String positionDescription;

    private DateTime positionProvisionalStartDate;

    private String appointmentConditions;

    private Boolean declinedResponse;

    private Integer rejectionReason;

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

    public List<Integer> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Integer> documents) {
        this.documents = documents;
    }

    public YesNoUnsureResponse getQualified() {
        return qualified;
    }

    public void setQualified(YesNoUnsureResponse qualified) {
        this.qualified = qualified;
    }

    public YesNoUnsureResponse getCompetentInWorkLanguage() {
        return competentInWorkLanguage;
    }

    public void setCompetentInWorkLanguage(YesNoUnsureResponse competentInWorkLanguage) {
        this.competentInWorkLanguage = competentInWorkLanguage;
    }

    public String getResidenceState() {
        return residenceState;
    }

    public void setResidenceState(String residenceState) {
        this.residenceState = residenceState;
    }

    public Boolean getSuitableForInstitution() {
        return suitableForInstitution;
    }

    public void setSuitableForInstitution(Boolean suitableForInstitution) {
        this.suitableForInstitution = suitableForInstitution;
    }

    public Boolean getSuitableForOpportunity() {
        return suitableForOpportunity;
    }

    public void setSuitableForOpportunity(Boolean suitableForOpportunity) {
        this.suitableForOpportunity = suitableForOpportunity;
    }

    public Boolean getDesireToInterview() {
        return desireToInterview;
    }

    public void setDesireToInterview(Boolean desireToInterview) {
        this.desireToInterview = desireToInterview;
    }

    public Boolean getDesireToRecruit() {
        return desireToRecruit;
    }

    public void setDesireToRecruit(Boolean desireToRecruit) {
        this.desireToRecruit = desireToRecruit;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public DateTime getInterviewDateTime() {
        return interviewDateTime;
    }

    public void setInterviewDateTime(DateTime interviewDateTime) {
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

    public Set<DateTime> getAppointmentTimeslots() {
        return appointmentTimeslots;
    }

    public void setAppointmentTimeslots(Set<DateTime> appointmentTimeslots) {
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

    public final String getRejectionReason() {
        return rejectionReason;
    }

    public final void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
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

    public DateTime getPositionProvisionalStartDate() {
        return positionProvisionalStartDate;
    }

    public void setPositionProvisionalStartDate(DateTime positionProvisionalStartDate) {
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
}
