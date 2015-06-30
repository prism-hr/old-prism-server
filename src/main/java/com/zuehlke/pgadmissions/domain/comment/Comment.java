package com.zuehlke.pgadmissions.domain.comment;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.PrismApplicationReserveStatus;
import com.zuehlke.pgadmissions.domain.definitions.PrismYesNoUnsureResponse;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
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
import com.zuehlke.pgadmissions.utils.PrismReflectionUtils;
import com.zuehlke.pgadmissions.workflow.validation.PrismConstraintRequiredStateTransition;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Set;
import java.util.TimeZone;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_VALIDATION_REQUIRED;
import static com.zuehlke.pgadmissions.domain.definitions.PrismYesNoUnsureResponse.UNSURE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.*;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.CREATE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.VIEW_EDIT_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionType.USER_INVOCATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.*;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup.APPLICATION_REJECTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup.APPLICATION_WITHDRAWN;

@Entity
@Table(name = "comment")
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

    @ManyToOne
    @JoinColumn(name = "institution_partner_id")
    private Institution partner;

    @Column(name = "removed_partner")
    private Boolean removedPartner;

    @Embedded
    private CommentSponsorship sponsorship;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "application_reserve_status")
    private PrismApplicationReserveStatus applicationReserveStatus;

    @ManyToOne
    @JoinColumn(name = "application_rejection_reason_id")
    private RejectionReason rejectionReason;

    @Column(name = "application_rejection_reason_system")
    private String rejectionReasonSystem;

    @Column(name = "application_rating")
    private BigDecimal applicationRating;

    @Lob
    @Column(name = "application_export_request")
    private String exportRequest;

    @Column(name = "application_export_reference")
    private String exportReference;

    @Lob
    @Column(name = "application_export_exception")
    private String exportException;

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
    @PrismConstraintRequiredStateTransition(state = APPLICATION_INTERVIEW, action = APPLICATION_ASSIGN_INTERVIEWERS, transitionState = APPLICATION_INTERVIEW_PENDING_SCHEDULING, error = SYSTEM_VALIDATION_REQUIRED)
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

    public Institution getPartner() {
        return partner;
    }

    public void setPartner(Institution partner) {
        this.partner = partner;
    }

    public Boolean getRemovedPartner() {
        return removedPartner;
    }

    public void setRemovedPartner(Boolean removedPartner) {
        this.removedPartner = removedPartner;
    }

    public CommentSponsorship getSponsorship() {
        return sponsorship;
    }

    public void setSponsorship(CommentSponsorship sponsorship) {
        this.sponsorship = sponsorship;
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

    public PrismApplicationReserveStatus getApplicationReserveStatus() {
        return applicationReserveStatus;
    }

    public void setApplicationReserveStatus(PrismApplicationReserveStatus applicationReserveRating) {
        this.applicationReserveStatus = applicationReserveRating;
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
        PrismReflectionUtils.setProperty(this, resource.getResourceScope().getLowerCamelName(), resource);
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

    public Comment withRemovedPartner(Boolean removedPartner) {
        this.removedPartner = removedPartner;
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

    public Comment withRecruiterAcceptAppointment(Boolean recruiterAcceptAppointment) {
        this.recruiterAcceptAppointment = recruiterAcceptAppointment;
        return this;
    }

    public Comment withApplicationReserveStatus(final PrismApplicationReserveStatus applicationReserveStatus) {
        this.applicationReserveStatus = applicationReserveStatus;
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
        return action.getId().equals(APPLICATION_ASSIGN_INTERVIEWERS) //
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
            } else if (action.getActionType().equals(USER_INVOCATION) && BooleanUtils.isTrue(stateGroup.getRepeatable())
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
        return action.getActionType() == USER_INVOCATION;
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

    public boolean isPartnershipComment() {
        return partner != null;
    }

    public boolean isSponsorshipComment() {
        return sponsorship != null;
    }

    public boolean isProjectPartnerApproveComment() {
        return action.getId().equals(PROJECT_COMPLETE_APPROVAL_PARTNER_STAGE) && transitionState.getId().equals(PROJECT_APPROVAL);
    }

    public boolean isProgramPartnerApproveComment() {
        return action.getId().equals(PROGRAM_COMPLETE_APPROVAL_PARTNER_STAGE) && transitionState.getId().equals(PROGRAM_APPROVAL);
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

}
