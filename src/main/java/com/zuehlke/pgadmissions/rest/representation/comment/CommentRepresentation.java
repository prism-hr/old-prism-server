package com.zuehlke.pgadmissions.rest.representation.comment;

import java.util.Set;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.YesNoUnsureResponse;
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

    private YesNoUnsureResponse eligible;

    private YesNoUnsureResponse competentInWorkLanguage;

    private String residenceState;

    private Boolean suitableForInstitution;

    private Boolean suitableForOpportunity;

    private Boolean desireToInterview;

    private Boolean desireToRecruit;

    private LocalDateTime interviewDateTime;

    private TimeZone interviewTimeZone;

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

    private Set<CommentAssignedUserRepresentation> assignedUsers = Sets.newLinkedHashSet();

    private Set<FileRepresentation> documents = Sets.newLinkedHashSet();

    private Set<AppointmentTimeslotRepresentation> appointmentTimeslots = Sets.newLinkedHashSet();

    private Set<AppointmentPreferenceRepresentation> appointmentPreferences = Sets.newLinkedHashSet();
    
    private Set<CommentPropertyRepresentation> properties = Sets.newLinkedHashSet();

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

    public final Boolean getEmphasizedAction() {
        return emphasizedAction;
    }

    public final void setEmphasizedAction(Boolean emphasizedAction) {
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

    public final YesNoUnsureResponse getEligible() {
        return eligible;
    }

    public final void setEligible(YesNoUnsureResponse eligible) {
        this.eligible = eligible;
    }

    public YesNoUnsureResponse getCompetentInWorkLanguage() {
        return competentInWorkLanguage;
    }

    public void setCompetentInWorkLanguage(YesNoUnsureResponse competentInWorkLanguage) {
        this.competentInWorkLanguage = competentInWorkLanguage;
    }

    public final String getResidenceState() {
        return residenceState;
    }

    public final void setResidenceState(String residenceState) {
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

    public Set<FileRepresentation> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<FileRepresentation> documents) {
        this.documents = documents;
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

    public CommentRepresentation withId(Integer id) {
        this.id = id;
        return this;
    }

    public CommentRepresentation withUser(UserRepresentation user) {
        this.user = user;
        return this;
    }

    public CommentRepresentation withDelegateUser(UserRepresentation delegateUser) {
        this.delegateUser = delegateUser;
        return this;
    }

    public CommentRepresentation withAction(PrismAction action) {
        this.action = action;
        return this;
    }

    public CommentRepresentation withDeclinedResponse(Boolean declinedResponse) {
        this.declinedResponse = declinedResponse;
        return this;
    }

    public CommentRepresentation withCreatedTimestamp(DateTime createdTimestamp) {
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

    public CommentRepresentation addInterviewDuration(Integer interviewDuration) {
        this.interviewDuration = interviewDuration;
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

    public final Set<CommentPropertyRepresentation> getProperties() {
        return properties;
    }

    public final void setProperties(Set<CommentPropertyRepresentation> properties) {
        this.properties = properties;
    }

}
