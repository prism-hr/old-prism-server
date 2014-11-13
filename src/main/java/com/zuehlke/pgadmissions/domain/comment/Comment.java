package com.zuehlke.pgadmissions.domain.comment;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Set;
import java.util.TimeZone;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.YesNoUnsureResponse;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.imported.RejectionReason;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.system.System;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.domain.workflow.StateGroup;
import com.zuehlke.pgadmissions.utils.ReflectionUtils;

@Entity
@Table(name = "COMMENT")
public class Comment {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "system_id")
    private System system;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id")
    private Institution institution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id")
    private Program program;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private Application application;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "role_id", nullable = false)
    private String role;

    @ManyToOne
    @JoinColumn(name = "delegate_user_id")
    private User delegateUser;

    @Column(name = "delegate_role_id")
    private String delegateRole;

    @ManyToOne
    @JoinColumn(name = "action_id", nullable = false)
    private Action action;

    @Column(name = "declined_response", nullable = false)
    private Boolean declinedResponse;

    @Lob
    private String content;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private State state;

    @ManyToOne
    @JoinColumn(name = "transition_state_id")
    private State transitionState;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_eligible")
    private YesNoUnsureResponse applicationEligible;

    @Column(name = "application_interested")
    private Boolean applicationInterested;

    @Column(name = "application_interview_datetime")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime interviewDateTime;

    @Column(name = "application_interview_timezone")
    private TimeZone interviewTimeZone;

    @Column(name = "application_interview_duration")
    private Integer interviewDuration;

    @Column(name = "application_interviewee_instructions")
    private String intervieweeInstructions;

    @Column(name = "application_interviewer_instructions")
    private String interviewerInstructions;

    @Column(name = "application_interview_location")
    private String interviewLocation;

    @Column(name = "application_position_title")
    private String positionTitle;

    @Column(name = "application_position_description")
    private String positionDescription;

    @Column(name = "application_position_provisional_start_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate positionProvisionalStartDate;

    @Column(name = "application_appointment_conditions")
    private String appointmentConditions;

    @Column(name = "application_recruiter_accept_appointment")
    private Boolean recruiterAcceptAppointment;

    @ManyToOne
    @JoinColumn(name = "application_rejection_reason_id")
    private RejectionReason rejectionReason;

    @Column(name = "application_rejection_reason_system")
    private String rejectionReasonSystem;

    @Column(name = "application_rating")
    private BigDecimal applicationRating;

    @Column(name = "application_use_custom_referee_questions")
    private Boolean useCustomRefereeQuestions;

    @Column(name = "application_use_custom_recruiter_questions")
    private Boolean useCustomRecruiterQuestions;
    
    @Column(name = "action_custom_question_version")
    private Integer actionCustomQuestionVersion;

    @Column(name = "application_export_reference")
    private String exportReference;

    @Column(name = "application_export_exception")
    private String exportException;

    @ManyToOne
    @JoinColumn(name = "parent_resource_transition_state_id")
    private State parentResourceTransitionState;

    @Column(name = "creator_ip_address")
    private String creatorIpAddress;

    @Column(name = "created_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdTimestamp;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comment_id", nullable = false)
    private Set<CommentAssignedUser> assignedUsers = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comment_id", nullable = false)
    private Set<CommentTransitionState> transitionStates = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comment_id", nullable = false)
    private Set<CommentAppointmentTimeslot> appointmentTimeslots = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comment_id", nullable = false)
    private Set<CommentAppointmentPreference> appointmentPreferences = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comment_id", nullable = false)
    private Set<CommentCustomResponse> propertyAnswers = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comment_id")
    private Set<Document> documents = Sets.newHashSet();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public System getSystem() {
        return system;
    }

    public void setSystem(System system) {
        this.system = system;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public User getDelegateUser() {
        return delegateUser;
    }

    public void setDelegateUser(User delegateUser) {
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

    public final State getState() {
        return state;
    }

    public final void setState(State state) {
        this.state = state;
    }

    public State getTransitionState() {
        return transitionState;
    }

    public void setTransitionState(State transitionState) {
        this.transitionState = transitionState;
    }

    public YesNoUnsureResponse getApplicationEligible() {
        return applicationEligible;
    }

    public final Boolean getApplicationInterested() {
        return applicationInterested;
    }

    public final void setApplicationInterested(Boolean applicationInterested) {
        this.applicationInterested = applicationInterested;
    }

    public void setApplicationEligible(YesNoUnsureResponse applicationEligible) {
        this.applicationEligible = applicationEligible;
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

    public final RejectionReason getRejectionReason() {
        return rejectionReason;
    }

    public final void setRejectionReason(RejectionReason rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public final String getRejectionReasonSystem() {
        return rejectionReasonSystem;
    }

    public final void setRejectionReasonSystem(String rejectionReasonSystem) {
        this.rejectionReasonSystem = rejectionReasonSystem;
    }

    public final BigDecimal getApplicationRating() {
        return applicationRating;
    }

    public final void setApplicationRating(BigDecimal applicationRating) {
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

    public final Integer getActionCustomQuestionVersion() {
        return actionCustomQuestionVersion;
    }

    public final void setActionCustomQuestionVersion(Integer actionCustomQuestionVersion) {
        this.actionCustomQuestionVersion = actionCustomQuestionVersion;
    }

    public String getExportReference() {
        return exportReference;
    }

    public void setExportReference(String exportReference) {
        this.exportReference = exportReference;
    }

    public String getExportException() {
        return exportException;
    }

    public void setExportException(String exportException) {
        this.exportException = exportException;
    }

    public String getCreatorIpAddress() {
        return creatorIpAddress;
    }

    public void setCreatorIpAddress(String creatorIpAddress) {
        this.creatorIpAddress = creatorIpAddress;
    }

    public final State getParentResourceTransitionState() {
        return parentResourceTransitionState;
    }

    public final void setParentResourceTransitionState(State parentResourceTransitionState) {
        this.parentResourceTransitionState = parentResourceTransitionState;
    }

    public Set<CommentAssignedUser> getAssignedUsers() {
        return assignedUsers;
    }

    public final Set<CommentTransitionState> getTransitionStates() {
        return transitionStates;
    }

    public Set<CommentAppointmentTimeslot> getAppointmentTimeslots() {
        return appointmentTimeslots;
    }

    public Set<CommentAppointmentPreference> getAppointmentPreferences() {
        return appointmentPreferences;
    }

    public Set<Document> getDocuments() {
        return documents;
    }

    public final Set<CommentCustomResponse> getPropertyAnswers() {
        return propertyAnswers;
    }

    public DateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Resource getResource() {
        return ObjectUtils.firstNonNull(system, institution, program, project, application);
    }

    public void setResource(Resource resource) {
        ReflectionUtils.setProperty(this, resource.getResourceScope().getLowerCaseName(), resource);
    }

    public Comment withId(Integer id) {
        this.id = id;
        return this;
    }

    public Comment withSystem(System system) {
        this.system = system;
        return this;
    }

    public Comment withInstitution(Institution institution) {
        this.institution = institution;
        return this;
    }

    public Comment withProgram(Program program) {
        this.program = program;
        return this;
    }

    public Comment withProject(Project project) {
        this.project = project;
        return this;
    }

    public Comment withApplication(Application application) {
        this.application = application;
        return this;
    }

    public Comment withResource(Resource resource) {
        setResource(resource);
        return this;
    }

    public Comment withUser(User user) {
        this.user = user;
        return this;
    }

    public Comment withRole(String role) {
        this.role = role;
        return this;
    }

    public Comment withDelegateUser(final User delegateUser) {
        this.delegateUser = delegateUser;
        return this;
    }

    public Comment withDelegateRole(String delegateRole) {
        this.delegateRole = delegateRole;
        return this;
    }

    public Comment withAction(Action action) {
        this.action = action;
        return this;
    }

    public Comment withDeclinedResponse(Boolean declinedResponse) {
        this.declinedResponse = declinedResponse;
        return this;
    }

    public Comment withContent(String content) {
        this.content = content;
        return this;
    }

    public Comment withState(State state) {
        this.state = state;
        return this;
    }

    public Comment withTransitionState(final State transitionState) {
        this.transitionState = transitionState;
        return this;
    }

    public Comment withPositionTitle(String positionTitle) {
        this.positionTitle = positionTitle;
        return this;
    }

    public Comment withPositionDescription(String positionDescription) {
        this.positionDescription = positionDescription;
        return this;
    }

    public Comment withApplicationRating(BigDecimal applicationRating) {
        this.applicationRating = applicationRating;
        return this;
    }

    public Comment withExportReference(String exportReference) {
        this.exportReference = exportReference;
        return this;
    }

    public Comment withExportException(String exportException) {
        this.exportException = exportException;
        return this;
    }

    public Comment withCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
        return this;
    }

    public Comment withApplicationEligible(final YesNoUnsureResponse eligible) {
        this.applicationEligible = eligible;
        return this;
    }

    public Comment withApplicationInterested(Boolean applicationInterested) {
        this.applicationInterested = applicationInterested;
        return this;
    }

    public Comment withInterviewDateTime(final LocalDateTime interviewDateTime) {
        this.interviewDateTime = interviewDateTime;
        return this;
    }

    public Comment withInterviewTimeZone(final TimeZone interviewTimeZone) {
        this.interviewTimeZone = interviewTimeZone;
        return this;
    }

    public Comment withInterviewDuration(final Integer interviewDuration) {
        this.interviewDuration = interviewDuration;
        return this;
    }

    public Comment withIntervieweeInstructions(final String intervieweeInstructions) {
        this.intervieweeInstructions = intervieweeInstructions;
        return this;
    }

    public Comment withInterviewerInstructions(final String interviewerInstructions) {
        this.interviewerInstructions = interviewerInstructions;
        return this;
    }

    public Comment withInterviewLocation(final String interviewLocation) {
        this.interviewLocation = interviewLocation;
        return this;
    }

    public Comment withAppointmentConditions(final String appointmentConditions) {
        this.appointmentConditions = appointmentConditions;
        return this;
    }

    public Comment withPositionProvisionalStartDate(final LocalDate positionProvisionalStartDate) {
        this.positionProvisionalStartDate = positionProvisionalStartDate;
        return this;
    }

    public Comment addAssignedUser(User user, Role role, PrismRoleTransitionType roleTransitionType) {
        assignedUsers.add(new CommentAssignedUser().withUser(user).withRole(role).withRoleTransitionType(roleTransitionType));
        return this;
    }

    public Comment addTransitionState(State transitionState, Boolean primaryState) {
        transitionStates.add(new CommentTransitionState().withTransitionState(transitionState).withPrimaryState(primaryState));
        return this;
    }

    public boolean isApplicationCreatorEligibilityUnsure() {
        return getApplicationEligible() == YesNoUnsureResponse.UNSURE;
    }

    public User getAuthor() {
        return delegateUser == null ? user : delegateUser;
    }

    public boolean isProgramApproveOrDeactivateComment() {
        return action.getScope().getId() == PrismScope.PROGRAM
                && Arrays.asList(PrismState.PROGRAM_APPROVED, PrismState.PROGRAM_DEACTIVATED).contains(transitionState.getId());
    }

    public boolean isProjectApproveOrDeactivateComment() {
        return action.getScope().getId() == PrismScope.PROJECT
                && Arrays.asList(PrismState.PROJECT_APPROVED, PrismState.PROJECT_DEACTIVATED).contains(transitionState.getId());
    }

    public boolean isProjectCreateApplicationComment() {
        return action.getId() == PrismAction.PROGRAM_CREATE_APPLICATION;
    }

    public boolean isApplicationAssignReviewersComment() {
        return action.getId() == PrismAction.APPLICATION_ASSIGN_REVIEWERS;
    }

    public boolean isApplicationProvideReferenceComment() {
        return action.getId() == PrismAction.APPLICATION_PROVIDE_REFERENCE;
    }

    public boolean isApplicationConfirmOfferRecommendationComment() {
        return action.getId() == PrismAction.APPLICATION_CONFIRM_OFFER_RECOMMENDATION;
    }

    public boolean isApplicationCreatedComment() {
        return transitionState.getId() == PrismState.APPLICATION_UNSUBMITTED;
    }

    public boolean isApplicationSubmittedComment() {
        return state.getStateGroup().getId() == PrismStateGroup.APPLICATION_UNSUBMITTED
                && transitionState.getStateGroup().getId() == PrismStateGroup.APPLICATION_VALIDATION;
    }

    public boolean isApplicationSubmittedToClosingDateComment() {
        return state.getStateGroup().getId() == PrismStateGroup.APPLICATION_UNSUBMITTED && transitionState.getId() == PrismState.APPLICATION_VALIDATION;
    }

    public boolean isApplicationApprovedComment() {
        return transitionState.getStateGroup().getId() == PrismStateGroup.APPLICATION_APPROVED && transitionState.getId() != PrismState.APPLICATION_APPROVED;
    }

    public boolean isApplicationRejectedComment() {
        return transitionState.getStateGroup().getId() == PrismStateGroup.APPLICATION_REJECTED && transitionState.getId() != PrismState.APPLICATION_REJECTED;
    }

    public boolean isApplicationWithdrawnComment() {
        return this.transitionState.getStateGroup().getId() == PrismStateGroup.APPLICATION_WITHDRAWN;
    }

    public boolean isApplicationPurgeComment() {
        return action.getId() == PrismAction.APPLICATION_PURGE;
    }

    public boolean isApplicationRatingComment() {
        return action.getRatingAction() && !declinedResponse;
    }

    public boolean isApplicationCompletionComment() {
        return Arrays.asList(PrismAction.APPLICATION_CONFIRM_OFFER_RECOMMENDATION, PrismAction.APPLICATION_CONFIRM_REJECTION, PrismAction.APPLICATION_WITHDRAW)
                .contains(action.getId());
    }

    public boolean isApplicationReferenceComment() {
        return action.getId() == PrismAction.APPLICATION_PROVIDE_REFERENCE;
    }

    public boolean isApplicationAutomatedRejectionComment() {
        return Arrays.asList(PrismAction.APPLICATION_ESCALATE, PrismAction.APPLICATION_TERMINATE).contains(action.getId())
                && transitionState.getStateGroup().getId() == PrismStateGroup.APPLICATION_REJECTED && rejectionReason == null;
    }

    public boolean isApplicationInterviewPendingInterviewComment() {
        return transitionState.getId() == PrismState.APPLICATION_INTERVIEW_PENDING_INTERVIEW;
    }

    public boolean isInterviewScheduledExpeditedComment() {
        return action.getId() == PrismAction.APPLICATION_ASSIGN_INTERVIEWERS
                && Arrays.asList(PrismState.APPLICATION_INTERVIEW_PENDING_INTERVIEW, PrismState.APPLICATION_INTERVIEW_PENDING_FEEDBACK).contains(
                        transitionState.getId());
    }

    public boolean isStateGroupTransitionComment() {
        return !state.getStateGroup().getId().equals(transitionState.getStateGroup().getId());
    }

    public boolean isUserCreationComment() {
        for (CommentAssignedUser assignee : assignedUsers) {
            if (assignee.getRoleTransitionType() == CREATE && assignee.getUser().getPassword() == null) {
                return true;
            }
        }
        return false;
    }

    public boolean isTransitionComment() {
        StateGroup stateGroup = state == null ? null : state.getStateGroup();
        StateGroup transitionStateGroup = transitionState == null ? null : transitionState.getStateGroup();
        if (action.getTransitionAction()) {
            if (action.getActionType() == PrismActionType.USER_INVOCATION) {
                return true;
            } else if (stateGroup == null) {
                return false;
            } else if (stateGroup.getRepeatable()) {
                return true;
            } else if (transitionStateGroup == null) {
                return false;
            } else if (!stateGroup.getId().equals(transitionStateGroup.getId())) {
                return true;
            } else if (action.getCreationScope() != null) {
                return true;
            }
        }
        return false;
    }

    public String getApplicationRatingDisplay() {
        return applicationRating == null ? null : applicationRating.toPlainString();
    }

    public String getUserDisplay() {
        return user == null ? null : user.getFullName();
    }

    public String getRejectionReasonDisplay() {
        return rejectionReason == null ? rejectionReasonSystem : rejectionReason.getName();
    }

    public String getCreatedTimestampDisplay(String dateFormat) {
        return createdTimestamp.toString(dateFormat, LocaleUtils.toLocale(getResource().getLocale().toString()));
    }

    public String getInterviewDateTimeDisplay(String dateTimeFormat) {
        return interviewDateTime == null ? null : interviewDateTime.toString(dateTimeFormat, LocaleUtils.toLocale(getResource().getLocale().toString()));
    }

    public String getPositionProvisionalStartDateDisplay(String dateFormat) {
        return positionProvisionalStartDate == null ? null : positionProvisionalStartDate.toString(dateFormat,
                LocaleUtils.toLocale(getResource().getLocale().toString()));
    }

    public String getInterviewTimeZoneDisplay() {
        return interviewTimeZone == null ? null : interviewTimeZone.getDisplayName(LocaleUtils.toLocale(getResource().getLocale().toString()));
    }

}
