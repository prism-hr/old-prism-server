package com.zuehlke.pgadmissions.rest.domain;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.*;
import com.zuehlke.pgadmissions.domain.definitions.ApplicationResidenceStatus;
import com.zuehlke.pgadmissions.domain.definitions.YesNoUnsureResponse;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.rest.domain.application.DocumentRepresentation;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.persistence.*;
import javax.validation.constraints.Size;

import java.util.Set;
import java.util.TimeZone;

public class CommentRepresentation {

    private UserRepresentation user;

    private String role;

    private UserRepresentation delegateUser;

    private String delegateRole;

    private Action action;

    private Boolean declinedResponse;

    private String content;

    private PrismState transitionState;

    private LocalDate userSpecifiedDueDate;

    private YesNoUnsureResponse qualified;

    private YesNoUnsureResponse competentInWorkLanguage;

    private ApplicationResidenceStatus residenceStatus;

    private Boolean suitableForInstitution;

    private Boolean suitableForOpportunity;

    private Boolean desireToInterview;

    private Boolean desireToRecruit;

    private DateTime interviewDateTime;

    private TimeZone interviewTimeZone = TimeZone.getTimeZone("GMT");

    private Integer interviewDuration;

    private String intervieweeInstructions;

    private String interviewerInstructions;

    private String interviewLocation;

    private String equivalentExperience;

    private String positionTitle;

    private String positionDescription;

    private LocalDate positionProvisionalStartDate;

    private String appointmentConditions;

    private Boolean recruiterAcceptAppointment;

    private Integer rating;

    private Boolean useCustomRefereeQuestions;

    private Boolean useCustomRecruiterQuestions;

    private Integer customQuestionVersionId;

    private String customQuestionResponse;

    private String exportRequest;

    private String exportResponse;

    private String exportError;

    private String exportReference;

    private Action actionOnParentResource;

    private DateTime createdTimestamp;

    private Set<CommentAssignedUserRepresentation> commentAssignedUsers = Sets.newHashSet();

    private Set<DocumentRepresentation> documents = Sets.newHashSet();

    private Set<CommentAppointmentTimeslot> appointmentTimeslots = Sets.newHashSet();

    private Set<CommentAppointmentPreference> appointmentPreferences = Sets.newHashSet();

    public UserRepresentation getUser() {
        return user;
    }

    public void setUser(UserRepresentation user) {
        this.user = user;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public UserRepresentation getDelegateUser() {
        return delegateUser;
    }

    public void setDelegateUser(UserRepresentation delegateUser) {
        this.delegateUser = delegateUser;
    }

    public String getDelegateRole() {
        return delegateRole;
    }

    public void setDelegateRole(String delegateRole) {
        this.delegateRole = delegateRole;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Boolean getDeclinedResponse() {
        return declinedResponse;
    }

    public void setDeclinedResponse(Boolean declinedResponse) {
        this.declinedResponse = declinedResponse;
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

    public LocalDate getUserSpecifiedDueDate() {
        return userSpecifiedDueDate;
    }

    public void setUserSpecifiedDueDate(LocalDate userSpecifiedDueDate) {
        this.userSpecifiedDueDate = userSpecifiedDueDate;
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

    public ApplicationResidenceStatus getResidenceStatus() {
        return residenceStatus;
    }

    public void setResidenceStatus(ApplicationResidenceStatus residenceStatus) {
        this.residenceStatus = residenceStatus;
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

    public String getEquivalentExperience() {
        return equivalentExperience;
    }

    public void setEquivalentExperience(String equivalentExperience) {
        this.equivalentExperience = equivalentExperience;
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

    public Boolean getRecruiterAcceptAppointment() {
        return recruiterAcceptAppointment;
    }

    public void setRecruiterAcceptAppointment(Boolean recruiterAcceptAppointment) {
        this.recruiterAcceptAppointment = recruiterAcceptAppointment;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Boolean getUseCustomRefereeQuestions() {
        return useCustomRefereeQuestions;
    }

    public void setUseCustomRefereeQuestions(Boolean useCustomRefereeQuestions) {
        this.useCustomRefereeQuestions = useCustomRefereeQuestions;
    }

    public Boolean getUseCustomRecruiterQuestions() {
        return useCustomRecruiterQuestions;
    }

    public void setUseCustomRecruiterQuestions(Boolean useCustomRecruiterQuestions) {
        this.useCustomRecruiterQuestions = useCustomRecruiterQuestions;
    }

    public Integer getCustomQuestionVersionId() {
        return customQuestionVersionId;
    }

    public void setCustomQuestionVersionId(Integer customQuestionVersionId) {
        this.customQuestionVersionId = customQuestionVersionId;
    }

    public String getCustomQuestionResponse() {
        return customQuestionResponse;
    }

    public void setCustomQuestionResponse(String customQuestionResponse) {
        this.customQuestionResponse = customQuestionResponse;
    }

    public String getExportRequest() {
        return exportRequest;
    }

    public void setExportRequest(String exportRequest) {
        this.exportRequest = exportRequest;
    }

    public String getExportResponse() {
        return exportResponse;
    }

    public void setExportResponse(String exportResponse) {
        this.exportResponse = exportResponse;
    }

    public String getExportError() {
        return exportError;
    }

    public void setExportError(String exportError) {
        this.exportError = exportError;
    }

    public String getExportReference() {
        return exportReference;
    }

    public void setExportReference(String exportReference) {
        this.exportReference = exportReference;
    }

    public Action getActionOnParentResource() {
        return actionOnParentResource;
    }

    public void setActionOnParentResource(Action actionOnParentResource) {
        this.actionOnParentResource = actionOnParentResource;
    }

    public DateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Set<CommentAssignedUserRepresentation> getCommentAssignedUsers() {
        return commentAssignedUsers;
    }

    public void setCommentAssignedUsers(Set<CommentAssignedUserRepresentation> commentAssignedUsers) {
        this.commentAssignedUsers = commentAssignedUsers;
    }

    public Set<DocumentRepresentation> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<DocumentRepresentation> documents) {
        this.documents = documents;
    }

    public Set<CommentAppointmentTimeslot> getAppointmentTimeslots() {
        return appointmentTimeslots;
    }

    public void setAppointmentTimeslots(Set<CommentAppointmentTimeslot> appointmentTimeslots) {
        this.appointmentTimeslots = appointmentTimeslots;
    }

    public Set<CommentAppointmentPreference> getAppointmentPreferences() {
        return appointmentPreferences;
    }

    public void setAppointmentPreferences(Set<CommentAppointmentPreference> appointmentPreferences) {
        this.appointmentPreferences = appointmentPreferences;
    }
}
