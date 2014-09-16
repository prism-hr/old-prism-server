package com.zuehlke.pgadmissions.domain;

import java.util.Set;
import java.util.TimeZone;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.YesNoUnsureResponse;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;

@Entity
@Table(name = "COMMENT")
public class Comment {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "system_id")
    private System system;

    @ManyToOne
    @JoinColumn(name = "institution_id")
    private Institution institution;

    @ManyToOne
    @JoinColumn(name = "program_id")
    private Program program;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
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

    @Size(max = 50000, message = "A maximum of 50000 characters are allowed.")
    @Lob
    private String content;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private State state;

    @ManyToOne
    @JoinColumn(name = "transition_state_id")
    private State transitionState;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_qualified")
    private YesNoUnsureResponse qualified;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_competent_in_work_language")
    private YesNoUnsureResponse competentInWorkLanguage;

    @ManyToOne
    @JoinColumn(name = "application_residence_state_id")
    private ResidenceState residenceState;

    @Column(name = "application_suitable_for_institution")
    private Boolean suitableForInstitution;

    @Column(name = "application_suitable_for_opportunity")
    private Boolean suitableForOpportunity;

    @Column(name = "application_desire_to_interview")
    private Boolean desireToInterview;

    @Column(name = "application_desire_to_recruit")
    private Boolean desireToRecruit;

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

    @Column(name = "application_rating")
    private Integer applicationRating;

    @Column(name = "application_use_custom_referee_questions")
    private Boolean useCustomRefereeQuestions;

    @Column(name = "application_use_custom_recruiter_questions")
    private Boolean useCustomRecruiterQuestions;

    @Column(name = "comment_custom_question_version_id")
    private Integer customQuestionVersionId;

    @Column(name = "custom_question_response")
    private String customQuestionResponse;

    @Column(name = "application_export_request")
    private String exportRequest;

    @Column(name = "application_export_response")
    private String exportResponse;

    @Column(name = "application_export_error")
    private String exportError;

    @Column(name = "application_export_reference")
    private String exportReference;

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
    private Set<CommentAppointmentTimeslot> appointmentTimeslots = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comment_id", nullable = false)
    private Set<CommentAppointmentPreference> appointmentPreferences = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comment_id", nullable = false)
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

    public boolean isDeclinedResponse() {
        return declinedResponse;
    }

    public void setDeclinedResponse(boolean declinedResponse) {
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

    public final ResidenceState getResidenceState() {
        return residenceState;
    }

    public final void setResidenceState(ResidenceState residenceState) {
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

    public Boolean isDesireToInterview() {
        return desireToInterview;
    }

    public void setDesireToInterview(Boolean desireToInterview) {
        this.desireToInterview = desireToInterview;
    }

    public Boolean isDesireToRecruit() {
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

    public DateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Set<Document> getDocuments() {
        return documents;
    }

    public Set<CommentAppointmentTimeslot> getAppointmentTimeslots() {
        return appointmentTimeslots;
    }

    public Set<CommentAppointmentPreference> getAppointmentPreferences() {
        return appointmentPreferences;
    }

    public Resource getResource() {
        if (system != null) {
            return system;
        } else if (institution != null) {
            return institution;
        } else if (program != null) {
            return program;
        } else if (project != null) {
            return project;
        }
        return application;
    }

    public void setResource(Resource resource) {
        this.system = null;
        this.institution = null;
        this.program = null;
        this.project = null;
        this.application = null;
        try {
            PropertyUtils.setProperty(this, resource.getClass().getSimpleName().toLowerCase(), resource);
        } catch (Exception e) {
            throw new Error(e);
        }
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

    public Comment withRejectionReason(RejectionReason rejectionReason) {
        this.rejectionReason = rejectionReason;
        return this;
    }

    public Comment withExportRequest(String exportRequest) {
        this.exportRequest = exportRequest;
        return this;
    }

    public Comment withExportResponse(String exportResponse) {
        this.exportResponse = exportResponse;
        return this;
    }

    public Comment withExportReference(String exportReference) {
        this.exportReference = exportReference;
        return this;
    }

    public Comment withCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
        return this;
    }

    public Comment addAssignedUser(User user, Role role, PrismRoleTransitionType roleTransitionType) {
        assignedUsers.add(new CommentAssignedUser().withUser(user).withRole(role).withRoleTransitionType(roleTransitionType).withComment(this));
        return this;
    }

    public Comment withQualified(final YesNoUnsureResponse qualified) {
        this.qualified = qualified;
        return this;
    }

    public Comment withCompetentInWorkLanguage(final YesNoUnsureResponse competentInWorkLanguage) {
        this.competentInWorkLanguage = competentInWorkLanguage;
        return this;
    }

    public Comment withResidenceState(final ResidenceState residenceState) {
        this.residenceState = residenceState;
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

    public Comment withDesireToRecruit(final Boolean desireToRecruit) {
        this.desireToRecruit = desireToRecruit;
        return this;
    }

    public Comment withDesireToInterview(final Boolean desireToInterview) {
        this.desireToInterview = desireToInterview;
        return this;
    }

    public Comment withSuitableForOpportunity(final Boolean suitableForOpportunity) {
        this.suitableForOpportunity = suitableForOpportunity;
        return this;
    }

    public Comment withSuitableForInstitution(final Boolean suitableForInstitution) {
        this.suitableForInstitution = suitableForInstitution;
        return this;
    }

    public Comment withPositionDescription(final String positionDescription) {
        this.positionDescription = positionDescription;
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

    public boolean isApplicationCreatorEligibilityUncertain() {
        return getQualified() == YesNoUnsureResponse.UNSURE || getCompetentInWorkLanguage() == YesNoUnsureResponse.UNSURE;
    }

    public User getAuthor() {
        return delegateUser == null ? user : delegateUser;
    }

    public boolean isProgramCreateComment() {
        return action.getCreationScope() == null ? false : action.getCreationScope().getId() == PrismScope.PROGRAM ? true : false;
    }

    public boolean isProgramUpdateComment() {
        return action.getScope().getId() == PrismScope.PROGRAM && action.getActionCategory() == PrismActionCategory.VIEW_EDIT_RESOURCE;
    }

    public boolean isProgramCreateOrUpdateComment() {
        return isProgramCreateComment() || isProgramUpdateComment();
    }

    public boolean isProjectCreateComment() {
        return action.getCreationScope() == null ? false : action.getCreationScope().getId() == PrismScope.PROJECT ? true : false;
    }

    public boolean isProjectUpdateComment() {
        return action.getScope().getId() == PrismScope.PROJECT && action.getActionCategory() == PrismActionCategory.VIEW_EDIT_RESOURCE;
    }

    public boolean isProjectCreateOrUpdateComment() {
        return isProjectCreateComment() || isProjectUpdateComment();
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
        return transitionState.getId() == PrismState.APPLICATION_VALIDATION;
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

    public boolean isRatingComment() {
        return action.isRatingAction();
    }

    public boolean isTransitionComment() {
        StateGroup previousStateGroup = state.getStateGroup();
        StateGroup currentStateGroup = transitionState.getStateGroup();
        return action.isTransitionAction() && (previousStateGroup.getId() != currentStateGroup.getId() || previousStateGroup.isRepeatable());
    }

}
