package com.zuehlke.pgadmissions.domain.comment;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Set;
import java.util.TimeZone;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
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
import javax.persistence.Transient;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.PrismYesNoUnsureResponse;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
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
    private PrismYesNoUnsureResponse applicationEligible;

    @Column(name = "application_interested")
    private Boolean applicationInterested;

    @Embedded
    private CommentApplicationInterviewAppointment interviewAppointment;

    @Embedded
    private CommentApplicationInterviewInstruction interviewInstruction;

    @Embedded
    private CommentApplicationPositionDetail positionDetail;

    @Embedded
    private CommentApplicationOfferDetail offerDetail;

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

    @Column(name = "application_export_request")
    private String exportRequest;
    
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
    private Set<CommentState> commentStates = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comment_id", nullable = false)
    private Set<CommentTransitionState> commentTransitionStates = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comment_id", nullable = false)
    private Set<CommentAppointmentTimeslot> appointmentTimeslots = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comment_id", nullable = false)
    private Set<CommentAppointmentPreference> appointmentPreferences = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comment_id", nullable = false)
    private Set<CommentCustomResponse> customResponses = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comment_id")
    private Set<Document> documents = Sets.newHashSet();

    @Transient
    private Set<State> secondaryTransitionStates = Sets.newHashSet();

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

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public State getTransitionState() {
        return transitionState;
    }

    public void setTransitionState(State transitionState) {
        this.transitionState = transitionState;
    }

    public PrismYesNoUnsureResponse getApplicationEligible() {
        return applicationEligible;
    }

    public Boolean getApplicationInterested() {
        return applicationInterested;
    }

    public void setApplicationInterested(Boolean applicationInterested) {
        this.applicationInterested = applicationInterested;
    }

    public void setApplicationEligible(PrismYesNoUnsureResponse applicationEligible) {
        this.applicationEligible = applicationEligible;
    }

    public CommentApplicationInterviewAppointment getInterviewAppointment() {
        return interviewAppointment;
    }

    public void setInterviewAppointment(CommentApplicationInterviewAppointment interviewAppointment) {
        this.interviewAppointment = interviewAppointment;
    }

    public CommentApplicationInterviewInstruction getInterviewInstruction() {
        return interviewInstruction;
    }

    public void setInterviewInstruction(CommentApplicationInterviewInstruction interviewInstruction) {
        this.interviewInstruction = interviewInstruction;
    }

    public CommentApplicationPositionDetail getPositionDetail() {
        return positionDetail;
    }

    public void setPositionDetail(CommentApplicationPositionDetail positionDetail) {
        this.positionDetail = positionDetail;
    }

    public CommentApplicationOfferDetail getOfferDetail() {
        return offerDetail;
    }

    public void setOfferDetail(CommentApplicationOfferDetail offerDetail) {
        this.offerDetail = offerDetail;
    }

    public Boolean getRecruiterAcceptAppointment() {
        return recruiterAcceptAppointment;
    }

    public void setRecruiterAcceptAppointment(Boolean recruiterAcceptAppointment) {
        this.recruiterAcceptAppointment = recruiterAcceptAppointment;
    }

    public RejectionReason getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(RejectionReason rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getRejectionReasonSystem() {
        return rejectionReasonSystem;
    }

    public void setRejectionReasonSystem(String rejectionReasonSystem) {
        this.rejectionReasonSystem = rejectionReasonSystem;
    }

    public BigDecimal getApplicationRating() {
        return applicationRating;
    }

    public void setApplicationRating(BigDecimal applicationRating) {
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

    public final String getApplicationExportRequest() {
        return exportRequest;
    }

    public final void setApplicationExportRequest(String applicationExportRequest) {
        this.exportRequest = applicationExportRequest;
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

    public State getParentResourceTransitionState() {
        return parentResourceTransitionState;
    }

    public void setParentResourceTransitionState(State parentResourceTransitionState) {
        this.parentResourceTransitionState = parentResourceTransitionState;
    }

    public Set<CommentAssignedUser> getAssignedUsers() {
        return assignedUsers;
    }

    public Set<CommentState> getCommentStates() {
        return commentStates;
    }

    public Set<CommentTransitionState> getCommentTransitionStates() {
        return commentTransitionStates;
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

    public Set<CommentCustomResponse> getCustomResponses() {
        return customResponses;
    }

    public DateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Set<State> getSecondaryTransitionStates() {
        return secondaryTransitionStates;
    }

    public void addSecondaryTransitionState(State state) {
        secondaryTransitionStates.add(state);
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

    public Comment withDelegateUser(User delegateUser) {
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

    public Comment withTransitionState(State transitionState) {
        this.transitionState = transitionState;
        return this;
    }

    public Comment withApplicationRating(BigDecimal applicationRating) {
        this.applicationRating = applicationRating;
        return this;
    }

    public Comment withExportRequest(String exportRequest) {
        this.exportRequest = exportRequest;
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

    public Comment withApplicationEligible(PrismYesNoUnsureResponse eligible) {
        this.applicationEligible = eligible;
        return this;
    }

    public Comment withApplicationInterested(Boolean applicationInterested) {
        this.applicationInterested = applicationInterested;
        return this;
    }

    public Comment withInterviewAppointment(CommentApplicationInterviewAppointment interviewAppointment) {
        this.interviewAppointment = interviewAppointment;
        return this;
    }

    public Comment withInterviewInstruction(CommentApplicationInterviewInstruction interviewInstruction) {
        this.interviewInstruction = interviewInstruction;
        return this;
    }

    public Comment addAssignedUser(User user, Role role, PrismRoleTransitionType roleTransitionType) {
        assignedUsers.add(new CommentAssignedUser().withUser(user).withRole(role).withRoleTransitionType(roleTransitionType));
        return this;
    }

    public Comment addCommentState(State state, Boolean primaryState) {
        commentStates.add(new CommentState().withState(state).withPrimaryState(primaryState));
        return this;
    }

    public Comment addCommentTransitionState(State transitionState, Boolean primaryState) {
        commentTransitionStates.add(new CommentTransitionState().withState(transitionState).withPrimaryState(primaryState));
        return this;
    }

    public boolean isApplicationCreatorEligibilityUnsure() {
        return getApplicationEligible() == PrismYesNoUnsureResponse.UNSURE;
    }

    public User getAuthor() {
        return delegateUser == null ? user : delegateUser;
    }

    public boolean isProgramApproveOrDeactivateComment() {
        return Arrays.asList(PrismState.PROGRAM_APPROVED, PrismState.PROGRAM_DEACTIVATED).contains(transitionState.getId());
    }

    public boolean isProgramRestoreComment() {
        return action.getId() == PrismAction.INSTITUTION_IMPORT_PROGRAM && state.getId() == PrismState.PROGRAM_DISABLED_PENDING_REACTIVATION
                && transitionState.getId() == PrismState.PROGRAM_APPROVED;
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
        return action.getId().getCreationScope() == PrismScope.APPLICATION;
    }

    public boolean isApplicationSubmittedComment() {
        return action.getId() == PrismAction.APPLICATION_COMPLETE;
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
        return action.getId() == PrismAction.APPLICATION_ESCALATE && transitionState.getStateGroup().getId() == PrismStateGroup.APPLICATION_REJECTED
                && rejectionReason == null;
    }

    public boolean isApplicationInterviewPendingInterviewComment() {
        return transitionState.getId() == PrismState.APPLICATION_INTERVIEW_PENDING_INTERVIEW;
    }

    public boolean isInterviewScheduledExpeditedComment() {
        return action.getId() == PrismAction.APPLICATION_ASSIGN_INTERVIEWERS
                && Arrays.asList(PrismState.APPLICATION_INTERVIEW_PENDING_INTERVIEW, PrismState.APPLICATION_INTERVIEW_PENDING_FEEDBACK).contains(
                        transitionState.getId());
    }

    public boolean isUserCreationComment() {
        for (CommentAssignedUser assignee : assignedUsers) {
            if (assignee.getRoleTransitionType() == CREATE && assignee.getUser().getPassword() == null) {
                return true;
            }
        }
        return false;
    }

    public boolean isStateTransitionComment() {
        return !state.equals(transitionState) || action.getCreationScope() != null;
    }

    public boolean isStateGroupTransitionComment() {
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

    public boolean isViewEditComment() {
        return action.getActionCategory() == PrismActionCategory.VIEW_EDIT_RESOURCE;
    }

    public boolean isUserComment() {
        return action.getActionType() == PrismActionType.USER_INVOCATION;
    }

    public boolean isSecondaryTransitionComment() {
        return !secondaryTransitionStates.isEmpty();
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
        if (interviewAppointment == null) {
            return null;
        }
        LocalDateTime interviewDateTime = interviewAppointment.getInterviewDateTime();
        return interviewDateTime == null ? null : interviewDateTime.toString(dateTimeFormat, LocaleUtils.toLocale(getResource().getLocale().toString()));
    }

    public String getPositionProvisionalStartDateDisplay(String dateFormat) {
        LocalDate positionProvisionalStartDate = offerDetail == null ? null : offerDetail.getPositionProvisionalStartDate();
        return positionProvisionalStartDate == null ? null : positionProvisionalStartDate.toString(dateFormat,
                LocaleUtils.toLocale(getResource().getLocale().toString()));
    }

    public String getInterviewTimeZoneDisplay() {
        if (interviewAppointment == null) {
            return null;
        }
        TimeZone interviewTimezone = interviewAppointment.getInterviewTimeZone();
        return interviewTimezone == null ? null : interviewTimezone.getDisplayName(LocaleUtils.toLocale(getResource().getLocale().toString()));
    }

}
