package com.zuehlke.pgadmissions.rest.representation.comment;

import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import com.zuehlke.pgadmissions.domain.definitions.PrismYesNoUnsureResponse;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.FileRepresentation;

public class CommentRepresentation {

    private Integer id;

    private UserRepresentation user;

    private UserRepresentation delegateUser;

    private PrismAction action;

    private Boolean emphasizedAction;

    private Boolean declinedResponse;

    private String content;

    private PrismState transitionState;

    private LocalDate userSpecifiedDueDate;

    private PrismYesNoUnsureResponse applicationEligible;

    private Boolean applicationInterested;

    private String residenceState;

    private Boolean suitableForInstitution;

    private Boolean suitableForOpportunity;

    private Boolean desireToInterview;

    private Boolean desireToRecruit;

    private LocalDateTime interviewDateTime;

    private TimeZone interviewTimeZone;

    private String interviewDurationDisplay;

    private String intervieweeInstructions;

    private String interviewerInstructions;

    private String interviewLocation;

    private String equivalentExperience;

    private String positionTitle;

    private String positionDescription;

    private LocalDate positionProvisionalStartDate;

    private String appointmentConditions;

    private Boolean recruiterAcceptAppointment;

    private Integer applicationRating;

    private Boolean useCustomRefereeQuestions;

    private Boolean useCustomRecruiterQuestions;

    private Integer customQuestionVersionId;

    private String customQuestionResponse;

    private String exportRequest;

    private String exportResponse;

    private String exportError;

    private String exportReference;

    private DateTime createdTimestamp;

    private Set<CommentAssignedUserRepresentation> assignedUsers;

    private Set<AppointmentTimeslotRepresentation> appointmentTimeslots;

    private Set<AppointmentPreferenceRepresentation> appointmentPreferences;

    private Set<CommentPropertyRepresentation> properties;

    private Set<FileRepresentation> documents;

    private List<CommentCustomResponseRepresentation> customResponses;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserRepresentation getUser() {
        return user;
    }

    public void setUser(UserRepresentation user) {
        this.user = user;
    }

    public UserRepresentation getDelegateUser() {
        return delegateUser;
    }

    public void setDelegateUser(UserRepresentation delegateUser) {
        this.delegateUser = delegateUser;
    }

    public PrismAction getAction() {
        return action;
    }

    public void setAction(PrismAction action) {
        this.action = action;
    }

    public Boolean getEmphasizedAction() {
        return emphasizedAction;
    }

    public void setEmphasizedAction(Boolean emphasizedAction) {
        this.emphasizedAction = emphasizedAction;
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

    public final String getInterviewDurationDisplay() {
        return interviewDurationDisplay;
    }

    public final void setInterviewDurationEndDateTimeDisplay(String interviewDurationDisplay) {
        this.interviewDurationDisplay = interviewDurationDisplay;
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

    public Integer getApplicationRating() {
        return applicationRating;
    }

    public void setApplicationRating(Integer applicationRating) {
        this.applicationRating = applicationRating;
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

    public DateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Set<CommentAssignedUserRepresentation> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(Set<CommentAssignedUserRepresentation> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public Set<AppointmentTimeslotRepresentation> getAppointmentTimeslots() {
        return appointmentTimeslots;
    }

    public void setAppointmentTimeslots(Set<AppointmentTimeslotRepresentation> appointmentTimeslots) {
        this.appointmentTimeslots = appointmentTimeslots;
    }

    public Set<AppointmentPreferenceRepresentation> getAppointmentPreferences() {
        return appointmentPreferences;
    }

    public void setAppointmentPreferences(Set<AppointmentPreferenceRepresentation> appointmentPreferences) {
        this.appointmentPreferences = appointmentPreferences;
    }

    public Set<FileRepresentation> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<FileRepresentation> documents) {
        this.documents = documents;
    }

    public Set<CommentPropertyRepresentation> getProperties() {
        return properties;
    }

    public void setProperties(Set<CommentPropertyRepresentation> properties) {
        this.properties = properties;
    }

    public List<CommentCustomResponseRepresentation> getCustomResponses() {
        return customResponses;
    }

    public void setCustomResponses(List<CommentCustomResponseRepresentation> customResponses) {
        this.customResponses = customResponses;
    }

    public CommentRepresentation addId(Integer id) {
        this.id = id;
        return this;
    }

    public CommentRepresentation addUser(UserRepresentation user) {
        this.user = user;
        return this;
    }

    public CommentRepresentation addDelegateUser(UserRepresentation delegateUser) {
        this.delegateUser = delegateUser;
        return this;
    }

    public CommentRepresentation addAction(PrismAction action) {
        this.action = action;
        return this;
    }

    public CommentRepresentation addDeclinedResponse(Boolean declinedResponse) {
        this.declinedResponse = declinedResponse;
        return this;
    }

    public CommentRepresentation addCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
        return this;
    }

    public CommentRepresentation addInterviewTimeZone(TimeZone interviewTimeZone) {
        this.interviewTimeZone = interviewTimeZone;
        return this;
    }

    public CommentRepresentation addInterviewDateTime(LocalDateTime interviewDateTime) {
        this.interviewDateTime = interviewDateTime;
        return this;
    }

    public CommentRepresentation addInterviewDuration(String interviewDuration) {
        this.interviewDurationDisplay = interviewDuration;
        return this;
    }

    public CommentRepresentation addIntervieweeInstructions(String intervieweeInstructions) {
        this.intervieweeInstructions = intervieweeInstructions;
        return this;
    }

    public CommentRepresentation addInterviewLocation(String interviewLocation) {
        this.interviewLocation = interviewLocation;
        return this;
    }

    public void addAppointmentTimeslot(AppointmentTimeslotRepresentation appointmentTimeslot) {
        appointmentTimeslots.add(appointmentTimeslot);
    }

}
