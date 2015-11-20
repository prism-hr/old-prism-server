package com.zuehlke.pgadmissions.domain.comment;

import static com.zuehlke.pgadmissions.domain.definitions.PrismYesNoUnsureResponse.UNSURE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ASSIGN_REVIEWERS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_OFFER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_REJECTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_REFERENCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_UPLOAD_REFERENCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_WITHDRAW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.DEPARTMENT_CREATE_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_CREATE_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_CREATE_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.CREATE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.VIEW_EDIT_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_FEEDBACK;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_INTERVIEW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REFERENCE;
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
import com.zuehlke.pgadmissions.domain.Activity;
import com.zuehlke.pgadmissions.domain.Competence;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.PrismRejectionReason;
import com.zuehlke.pgadmissions.domain.definitions.PrismYesNoUnsureResponse;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.resource.Department;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.System;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserAssignment;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.domain.workflow.StateGroup;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowResourceExecution;
import com.zuehlke.pgadmissions.workflow.user.CommentReassignmentProcessor;

@Entity
@Table(name = "comment")
public class Comment extends WorkflowResourceExecution implements Activity, UserAssignment<CommentReassignmentProcessor> {

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

    @Column(name = "application_shared")
    private Boolean shared;

    @Column(name = "application_on_course")
    private Boolean onCourse;

    @Column(name = "application_rating")
    private BigDecimal rating;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_eligible")
    private PrismYesNoUnsureResponse eligible;

    @Column(name = "application_interested")
    private Boolean interested;

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

    @Column(name = "application_partner_accept_appointment")
    private Boolean partnerAcceptAppointment;

    @Column(name = "application_applicant_accept_appointment")
    private Boolean applicantAcceptAppointment;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_rejection_reason")
    private PrismRejectionReason rejectionReason;

    @Column(name = "created_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdTimestamp;

    @Column(name = "sequence_identifier", unique = true)
    private String sequenceIdentifier;

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

    @OrderBy(clause = "id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comment_id", nullable = false)
    private Set<CommentCompetence> competences = Sets.newHashSet();

    @OrderBy(clause = "timeslot_datetime")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comment_id", nullable = false)
    private Set<CommentAppointmentTimeslot> appointmentTimeslots = Sets.newHashSet();

    @OrderBy(clause = "preference_datetime")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comment_id", nullable = false)
    private Set<CommentAppointmentPreference> appointmentPreferences = Sets.newHashSet();

    @OrderBy(clause = "id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comment_id")
    private Set<Document> documents = Sets.newHashSet();

    @Transient
    private Set<State> secondaryTransitionStates = Sets.newHashSet();

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public System getSystem() {
        return system;
    }

    @Override
    public void setSystem(System system) {
        this.system = system;
    }

    @Override
    public Institution getInstitution() {
        return institution;
    }

    @Override
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

    @Override
    public Program getProgram() {
        return program;
    }

    @Override
    public void setProgram(Program program) {
        this.program = program;
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public Application getApplication() {
        return application;
    }

    @Override
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

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public Boolean getShared() {
        return shared;
    }

    public void setShared(Boolean shared) {
        this.shared = shared;
    }

    public Boolean getOnCourse() {
        return onCourse;
    }

    public void setOnCourse(Boolean onCourse) {
        this.onCourse = onCourse;
    }

    public PrismYesNoUnsureResponse getEligible() {
        return eligible;
    }

    public Boolean getInterested() {
        return interested;
    }

    public void setInterested(Boolean interested) {
        this.interested = interested;
    }

    public void setEligible(PrismYesNoUnsureResponse eligible) {
        this.eligible = eligible;
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

    public Boolean getPartnerAcceptAppointment() {
        return partnerAcceptAppointment;
    }

    public void setPartnerAcceptAppointment(Boolean partnerAcceptAppointment) {
        this.partnerAcceptAppointment = partnerAcceptAppointment;
    }

    public Boolean getApplicantAcceptAppointment() {
        return applicantAcceptAppointment;
    }

    public void setApplicantAcceptAppointment(Boolean applicantAcceptAppointment) {
        this.applicantAcceptAppointment = applicantAcceptAppointment;
    }

    public PrismRejectionReason getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(PrismRejectionReason rejectionReason) {
        this.rejectionReason = rejectionReason;
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

    public Set<CommentCompetence> getCompetences() {
        return competences;
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

    public DateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public String getSequenceIdentifier() {
        return sequenceIdentifier;
    }

    @Override
    public void setSequenceIdentifier(String sequenceIdentifier) {
        this.sequenceIdentifier = sequenceIdentifier;
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

    public Comment withRating(BigDecimal rating) {
        this.rating = rating;
        return this;
    }

    public Comment withCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
        return this;
    }

    public Comment withEligible(PrismYesNoUnsureResponse eligible) {
        this.eligible = eligible;
        return this;
    }

    public Comment withInterested(Boolean interested) {
        this.interested = interested;
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

    public Comment withPartnerAcceptAppointment(Boolean partnerAcceptAppointment) {
        this.partnerAcceptAppointment = partnerAcceptAppointment;
        return this;
    }

    public Comment withApplicantAcceptAppointment(Boolean applicantAcceptAppointment) {
        this.applicantAcceptAppointment = applicantAcceptAppointment;
        return this;
    }

    public Comment withRejectionReason(PrismRejectionReason rejectionReason) {
        this.rejectionReason = rejectionReason;
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

    public Comment addDocument(Document document) {
        documents.add(document);
        return this;
    }

    public Comment addCompetence(Competence competence, Integer importance, Integer rating, String remark) {
        competences.add(new CommentCompetence().withCompetence(competence).withImportance(importance).withRating(rating).withRemark(remark));
        return this;
    }

    public boolean isApplicationCreatorEligibilityUnsure() {
        return getEligible().equals(UNSURE);
    }

    public User getActionOwner() {
        return delegateUser == null ? user : delegateUser;
    }

    public boolean isApplicationAssignReviewersComment() {
        return action.getId().equals(APPLICATION_ASSIGN_REVIEWERS);
    }

    public boolean isApplicationProvideReferenceComment() {
        return action.getId().equals(APPLICATION_PROVIDE_REFERENCE) || action.getId().equals(APPLICATION_UPLOAD_REFERENCE);
    }

    public boolean isApplicationProvideReferenceDelegateComment() {
        return action.getId().equals(APPLICATION_PROVIDE_REFERENCE) && delegateUser != null;
    }

    public boolean isApplicationConfirmOfferRecommendationComment() {
        return action.getId().equals(APPLICATION_CONFIRM_OFFER);
    }

    public boolean isApplicationCreatedComment() {
        return Arrays.asList(INSTITUTION_CREATE_APPLICATION, DEPARTMENT_CREATE_APPLICATION, PROJECT_CREATE_APPLICATION).contains(action.getId());
    }

    public boolean isApplicationCompleteComment() {
        return action.getId().equals(APPLICATION_COMPLETE);
    }

    public boolean isApplicationProcessingCompletedComment() {
        return Arrays.asList(APPLICATION_CONFIRM_OFFER, APPLICATION_CONFIRM_REJECTION, APPLICATION_WITHDRAW)
                .contains(action.getId()) || isApplicationAutomatedRejectionComment() || isApplicationAutomatedWithdrawalComment();
    }

    public boolean isApplicationAutomatedRejectionComment() {
        return action.getId().equals(APPLICATION_ESCALATE) && state.getStateGroup().getId() != APPLICATION_REJECTED
                && transitionState.getStateGroup().getId().equals(APPLICATION_REJECTED);
    }

    public boolean isApplicationAutomatedWithdrawalComment() {
        return action.getId().equals(APPLICATION_ESCALATE) && state.getStateGroup().getId() != APPLICATION_WITHDRAWN
                && transitionState.getStateGroup().getId().equals(APPLICATION_WITHDRAWN);
    }

    public boolean isApplicationAssignRefereesComment() {
        return (isStateGroupTransitionComment() && transitionState.getId().equals(APPLICATION_REFERENCE)) ||
                (isSecondaryStateGroupTransitionComment() && secondaryTransitionStates.contains(new State().withId(APPLICATION_REFERENCE)));
    }

    public boolean isApplicationUpdateRefereesComment() {
        return isApplicationViewEditComment()
                && (transitionState.getId().equals(APPLICATION_REFERENCE) || secondaryTransitionStates.contains(new State().withId(APPLICATION_REFERENCE)));
    }

    public boolean isInterviewScheduledExpeditedComment() {
        return action.getId().equals(PrismAction.APPLICATION_ASSIGN_INTERVIEWERS) //
                && Arrays.asList(APPLICATION_INTERVIEW_PENDING_INTERVIEW, APPLICATION_INTERVIEW_PENDING_FEEDBACK).contains(transitionState.getId());
    }

    public boolean isApplicationInterviewScheduledConfirmedComment() {
        return action.getId().equals(APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS);
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

    public boolean isPartnershipStateTransitionComment() {
        return action.getPartnershipState() != null;
    }

    public boolean isCreateComment() {
        return action.getActionCategory().equals(CREATE_RESOURCE);
    }

    public boolean isViewEditComment() {
        return action.getActionCategory().equals(VIEW_EDIT_RESOURCE);
    }

    public boolean isApplicationViewEditComment() {
        return action.getId().equals(APPLICATION_VIEW_EDIT);
    }

    public boolean isUserComment() {
        return BooleanUtils.isFalse(action.getSystemInvocationOnly());
    }

    public boolean isSecondaryStateGroupTransitionComment() {
        return !secondaryTransitionStates.isEmpty();
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

    public boolean isRatingComment(PrismScope scope) {
        return action.getId().getScope().equals(scope) && !(rating == null || competences.isEmpty());
    }

    public String getApplicationRatingDisplay() {
        return rating == null ? null : rating.toPlainString();
    }

    public String getUserDisplay() {
        return user == null ? null : user.getFullName();
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
    public Class<CommentReassignmentProcessor> getUserReassignmentProcessor() {
        return CommentReassignmentProcessor.class;
    }

    @Override
    public boolean isResourceUserAssignmentProperty() {
        User resourceUser = getResource().getUser();
        return resourceUser.equals(user) || resourceUser.equals(delegateUser);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        Comment other = (Comment) object;
        return Objects.equal(id, other.getId());
    }

    @Override
    public EntitySignature getEntitySignature() {
        return null;
    }

}
