package com.zuehlke.pgadmissions.domain.comment;

import static com.zuehlke.pgadmissions.domain.definitions.PrismYesNoUnsureResponse.UNSURE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ASSIGN_REVIEWERS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_OFFER_RECOMMENDATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_REJECTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_REFERENCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_UPLOAD_REFERENCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_WITHDRAW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_IMPORT_PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_CREATE_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_CREATE_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.CREATE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.VIEW_EDIT_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_FEEDBACK;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_INTERVIEW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REFERENCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_APPROVED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_APPROVED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_DISABLED_PENDING_REACTIVATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup.APPLICATION_REJECTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup.APPLICATION_WITHDRAWN;

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

import org.apache.commons.lang.BooleanUtils;
import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.PrismApplicationReserveStatus;
import com.zuehlke.pgadmissions.domain.definitions.PrismYesNoUnsureResponse;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.resource.Department;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.System;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.domain.workflow.StateGroup;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowResourceExecution;

@Entity
@Table(name = "comment")
public class Comment extends WorkflowResourceExecution {

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
    @JoinColumn(name = "department_id")
    private Department department;

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

    @ManyToOne
    @JoinColumn(name = "delegate_user_id")
    private User delegateUser;

    @ManyToOne
    @JoinColumn(name = "action_id", nullable = false)
    private Action action;

    @Column(name = "declined_response", nullable = false)
    private Boolean declinedResponse;

    @Lob
    @Column(name = "content")
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
    private CommentInterviewAppointment interviewAppointment;

    @Embedded
    private CommentInterviewInstruction interviewInstruction;

    @Embedded
    private CommentPositionDetail positionDetail;

    @Embedded
    private CommentOfferDetail offerDetail;

    @Column(name = "application_recruiter_accept_appointment")
    private Boolean recruiterAcceptAppointment;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_reserve_status")
    private PrismApplicationReserveStatus applicationReserveStatus;

    @ManyToOne
    @JoinColumn(name = "application_imported_rejection_reason_id")
    private ImportedEntitySimple rejectionReason;

    @Column(name = "application_rejection_reason_system")
    private String rejectionReasonSystem;

    @Column(name = "application_rating")
    private BigDecimal applicationRating;

    @Embedded
    private CommentExport export;

    @Column(name = "created_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdTimestamp;

    @OrderBy(clause = "role_id, id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comment_id", nullable = false)
    private Set<CommentAssignedUser> assignedUsers = Sets.newHashSet();

    @OrderBy(clause = "primary_state desc, state_id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comment_id", nullable = false)
    private Set<CommentState> commentStates = Sets.newHashSet();

    @OrderBy(clause = "primary_state desc, state_id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comment_id", nullable = false)
    private Set<CommentTransitionState> commentTransitionStates = Sets.newHashSet();

    @OrderBy(clause = "timeslot_datetime")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comment_id", nullable = false)
    private Set<CommentAppointmentTimeslot> appointmentTimeslots = Sets.newHashSet();

    @OrderBy(clause = "preference_datetime")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comment_id", nullable = false)
    private Set<CommentAppointmentPreference> appointmentPreferences = Sets.newHashSet();

    @OrderBy(clause = "action_custom_question_configuration_id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comment_id", nullable = false)
    private Set<CommentCustomResponse> customResponses = Sets.newHashSet();

    @OrderBy(clause = "id")
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

    @Override
    public Department getDepartment() {
        return department;
    }

    @Override
    public void setDepartment(Department department) {
        this.department = department;
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

    public User getDelegateUser() {
        return delegateUser;
    }

    public void setDelegateUser(User delegateUser) {
        this.delegateUser = delegateUser;
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

    public CommentInterviewAppointment getInterviewAppointment() {
        return interviewAppointment;
    }

    public void setInterviewAppointment(CommentInterviewAppointment interviewAppointment) {
        this.interviewAppointment = interviewAppointment;
    }

    public CommentInterviewInstruction getInterviewInstruction() {
        return interviewInstruction;
    }

    public void setInterviewInstruction(CommentInterviewInstruction interviewInstruction) {
        this.interviewInstruction = interviewInstruction;
    }

    public CommentPositionDetail getPositionDetail() {
        return positionDetail;
    }

    public void setPositionDetail(CommentPositionDetail positionDetail) {
        this.positionDetail = positionDetail;
    }

    public CommentOfferDetail getOfferDetail() {
        return offerDetail;
    }

    public void setOfferDetail(CommentOfferDetail offerDetail) {
        this.offerDetail = offerDetail;
    }

    public Boolean getRecruiterAcceptAppointment() {
        return recruiterAcceptAppointment;
    }

    public void setRecruiterAcceptAppointment(Boolean recruiterAcceptAppointment) {
        this.recruiterAcceptAppointment = recruiterAcceptAppointment;
    }

    public PrismApplicationReserveStatus getApplicationReserveStatus() {
        return applicationReserveStatus;
    }

    public void setApplicationReserveStatus(PrismApplicationReserveStatus applicationReserveRating) {
        this.applicationReserveStatus = applicationReserveRating;
    }

    public ImportedEntitySimple getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(ImportedEntitySimple rejectionReason) {
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

    public CommentExport getExport() {
        return export;
    }

    public void setExport(CommentExport export) {
        this.export = export;
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

    public Comment withDelegateUser(User delegateUser) {
        this.delegateUser = delegateUser;
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

    public Comment withInterviewAppointment(CommentInterviewAppointment interviewAppointment) {
        this.interviewAppointment = interviewAppointment;
        return this;
    }

    public Comment withInterviewInstruction(CommentInterviewInstruction interviewInstruction) {
        this.interviewInstruction = interviewInstruction;
        return this;
    }

    public Comment withRecruiterAcceptAppointment(Boolean recruiterAcceptAppointment) {
        this.recruiterAcceptAppointment = recruiterAcceptAppointment;
        return this;
    }

    public Comment withApplicationReserveStatus(final PrismApplicationReserveStatus applicationReserveStatus) {
        this.applicationReserveStatus = applicationReserveStatus;
        return this;
    }

    public Comment withApplicationExport(CommentExport export) {
        this.export = export;
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
        return getApplicationEligible() == UNSURE;
    }

    public User getActionOwner() {
        return delegateUser == null ? user : delegateUser;
    }

    public boolean isInstitutionApproveComment() {
        return !INSTITUTION_APPROVED.equals(state.getId()) && INSTITUTION_APPROVED.equals(transitionState.getId());
    }

    public boolean isProgramApproveComment() {
        return !PROGRAM_APPROVED.equals(state.getId()) && PROGRAM_APPROVED.equals(transitionState.getId());
    }

    public boolean isProgramViewEditComment() {
        return action.getId() == PROGRAM_VIEW_EDIT;
    }

    public boolean isProgramRestoreComment() {
        return action.getId() == INSTITUTION_IMPORT_PROGRAM && state.getId() == PROGRAM_DISABLED_PENDING_REACTIVATION
                && transitionState.getId() == PROGRAM_APPROVED;
    }

    public boolean isProjectCreateApplicationComment() {
        return action.getId() == PROJECT_CREATE_APPLICATION;
    }

    public boolean isProjectViewEditComment() {
        return action.getId() == PROJECT_VIEW_EDIT;
    }

    public boolean isApplicationAssignReviewersComment() {
        return action.getId() == APPLICATION_ASSIGN_REVIEWERS;
    }

    public boolean isApplicationProvideReferenceComment() {
        return action.getId() == APPLICATION_PROVIDE_REFERENCE || action.getId() == APPLICATION_UPLOAD_REFERENCE;
    }

    public boolean isApplicationConfirmOfferRecommendationComment() {
        return action.getId() == APPLICATION_CONFIRM_OFFER_RECOMMENDATION;
    }

    public boolean isApplicationCreatedComment() {
        return Arrays.asList(PROGRAM_CREATE_APPLICATION, PROJECT_CREATE_APPLICATION).contains(action.getId());
    }

    public boolean isApplicationSubmittedComment() {
        return action.getId() == PrismAction.APPLICATION_COMPLETE;
    }

    public boolean isApplicationRatingComment() {
        return action.getRatingAction() && applicationRating != null;
    }

    public boolean isApplicationCompletionComment() {
        return Arrays.asList(APPLICATION_CONFIRM_OFFER_RECOMMENDATION, APPLICATION_CONFIRM_REJECTION, APPLICATION_WITHDRAW)
                .contains(action.getId()) || isApplicationAutomatedRejectionComment() || isApplicationAutomatedWithdrawalComment();
    }

    public boolean isApplicationReferenceComment() {
        return action.getId() == APPLICATION_PROVIDE_REFERENCE;
    }

    public boolean isApplicationAutomatedRejectionComment() {
        return action.getId() == APPLICATION_ESCALATE && state.getStateGroup().getId() != APPLICATION_REJECTED
                && transitionState.getStateGroup().getId() == APPLICATION_REJECTED;
    }

    public boolean isApplicationAutomatedWithdrawalComment() {
        return action.getId() == APPLICATION_ESCALATE && state.getStateGroup().getId() != APPLICATION_WITHDRAWN
                && transitionState.getStateGroup().getId() == APPLICATION_WITHDRAWN;
    }

    public boolean isApplicationAssignRefereesComment() {
        return (isStateGroupTransitionComment() && transitionState.getId() == APPLICATION_REFERENCE) ||
                (isSecondaryStateGroupTransitionComment() && secondaryTransitionStates.contains(new State().withId(APPLICATION_REFERENCE)));
    }

    public boolean isApplicationUpdateRefereesComment() {
        return isApplicationViewEditComment()
                && (transitionState.getId() == APPLICATION_REFERENCE || secondaryTransitionStates.contains(new State().withId(APPLICATION_REFERENCE)));
    }

    public boolean isInterviewScheduledExpeditedComment() {
        return action.getId().equals(PrismAction.APPLICATION_ASSIGN_INTERVIEWERS) //
                && Arrays.asList(APPLICATION_INTERVIEW_PENDING_INTERVIEW, APPLICATION_INTERVIEW_PENDING_FEEDBACK).contains(transitionState.getId());
    }

    public boolean isApplicationInterviewScheduledConfirmedComment() {
        return action.getId().equals(APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS);
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
        if (BooleanUtils.isTrue(action.getTransitionAction())) {
            if (!Objects.equal(stateGroup, transitionStateGroup)) {
                return true;
            } else if (isUserComment() && BooleanUtils.isTrue(stateGroup.getRepeatable())
                    && !Objects.equal(state, transitionState)) {
                return true;
            }
        }
        return false;
    }

    public boolean isCreateComment() {
        return action.getActionCategory() == CREATE_RESOURCE;
    }

    public boolean isViewEditComment() {
        return action.getActionCategory() == VIEW_EDIT_RESOURCE;
    }

    public boolean isApplicationViewEditComment() {
        return action.getId() == APPLICATION_VIEW_EDIT;
    }

    public boolean isUserComment() {
        return BooleanUtils.isFalse(action.getSystemInvocationOnly());
    }

    public boolean isSecondaryStateGroupTransitionComment() {
        return !secondaryTransitionStates.isEmpty();
    }

    public boolean isDelegateComment() {
        return delegateUser != null;
    }

    public boolean isApplicationProvideReferenceDelegateComment() {
        return isDelegateComment() && action.getId() == APPLICATION_PROVIDE_REFERENCE;
    }

    public boolean isApplicationDelegateAdministrationComment() {
        CommentAssignedUser firstAssignee = assignedUsers.isEmpty() ? null : assignedUsers.iterator().next();
        return firstAssignee != null && firstAssignee.getRole().getId() == APPLICATION_ADMINISTRATOR && firstAssignee.getRoleTransitionType() == CREATE;
    }

    public boolean isApplicationInterviewScheduledComment(DateTime baseline) {
        LocalDateTime interviewDateTime = interviewAppointment == null ? null : interviewAppointment.getInterviewDateTime();
        return interviewDateTime == null ? false : interviewAppointment.getInterviewDateTime()
                .toDateTime(DateTimeZone.forTimeZone(interviewAppointment.getInterviewTimeZone())).isAfter(baseline);
    }

    public boolean isApplicationInterviewRecordedComment(DateTime baseline) {
        LocalDateTime interviewDateTime = interviewAppointment == null ? null : interviewAppointment.getInterviewDateTime();
        return interviewDateTime == null ? false : interviewAppointment.getInterviewDateTime()
                .toDateTime(DateTimeZone.forTimeZone(interviewAppointment.getInterviewTimeZone())).isBefore(baseline);
    }

    public boolean isApplicationReserveStatusComment() {
        return applicationReserveStatus != null;
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
        return createdTimestamp.toString(dateFormat);
    }

    public String getInterviewDateTimeDisplay(String dateTimeFormat) {
        LocalDateTime interviewDateTime = interviewAppointment == null ? null : interviewAppointment.getInterviewDateTime();
        return interviewDateTime == null ? null : interviewDateTime.toString(dateTimeFormat);
    }

    public String getPositionProvisionalStartDateDisplay(String dateFormat) {
        LocalDate positionProvisionalStartDate = offerDetail == null ? null : offerDetail.getPositionProvisionalStartDate();
        return positionProvisionalStartDate == null ? null : positionProvisionalStartDate.toString(dateFormat);
    }

    public String getInterviewTimeZoneDisplay() {
        TimeZone interviewTimezone = interviewAppointment == null ? null : interviewAppointment.getInterviewTimeZone();
        return interviewTimezone == null ? null : interviewTimezone.getDisplayName();
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return null;
    }

}
