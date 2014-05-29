package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
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
import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.enums.ResidenceStatus;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;

@Entity
@Table(name = "COMMENT")
public class Comment implements Serializable {

    private static final long serialVersionUID = 2861325991249900547L;

    @Id
    @GeneratedValue
    private Integer id;

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

    @Column(name = "role_id")
    private String role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delegate_user_id")
    private User delegateUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delegate_role_id")
    private Role delegateRole;

    @ManyToOne
    @JoinColumn(name = "action_id")
    private Action action;

    @Column(name = "declined_response")
    private Boolean declinedResponse;

    @Size(max = 50000, message = "A maximum of 50000 characters are allowed.")
    @Lob
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transition_state_id")
    private State transitionState;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_qualified")
    private ValidationQuestionOptions qualified;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_competent_in_work_language")
    private ValidationQuestionOptions competentInWorkLanguage;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_residence_status")
    private ResidenceStatus residenceStatus;

    @Column(name = "application_suitable_for_institution")
    private Boolean suitableForInstitution;

    @Column(name = "application_suitable_for_opportunity")
    private Boolean suitableForOpportunity;

    @Column(name = "application_desire_to_interview")
    private Boolean desireToInterview;

    @Column(name = "application_desire_to_recruit")
    private Boolean desireToRecruit;

    @Column(name = "application_interview_datetime")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime interviewDateTime;

    @Column(name = "application_interview_timezone")
    private TimeZone interviewTimeZone = TimeZone.getTimeZone("GMT");

    @Column(name = "application_interview_duration")
    private Integer interviewDuration;

    @Column(name = "application_interviewee_instructions")
    private String intervieweeInstructions;

    @Column(name = "application_interviewer_instructions")
    private String interviewerInstructions;

    @Column(name = "application_interview_location")
    private String interviewLocation;

    @Column(name = "application_equivalent_experience")
    private String equivalentExperience;

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

    @Column(name = "application_rating")
    private Integer rating;

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

    @Column(name = "creator_ip_address")
    private String creatorIpAddress;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "comment")
    private Set<CommentAssignedUser> commentAssignedUser = Sets.newHashSet();

    @Column(name = "created_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdTimestamp;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "comment")
    private Set<CommentAssignedUser> commentAssignedUsers = Sets.newHashSet();

    @OneToMany
    @JoinColumn(name = "comment_id")
    private Set<Document> documents = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comment_id")
    private Set<AppointmentTimeslot> availableAppointmentTimeslots = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comment_id")
    private Set<AppointmentPreference> availableAppointmentPreferences = Sets.newHashSet();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Role getDelegateRole() {
        return delegateRole;
    }

    public void setDelegateRole(Role delegateRole) {
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

    public State getTransitionState() {
        return transitionState;
    }

    public void setTransitionState(State transitionState) {
        this.transitionState = transitionState;
    }

    public ValidationQuestionOptions getQualified() {
        return qualified;
    }

    public void setQualified(ValidationQuestionOptions qualified) {
        this.qualified = qualified;
    }

    public ValidationQuestionOptions getCompetentInWorkLanguage() {
        return competentInWorkLanguage;
    }

    public void setCompetentInWorkLanguage(ValidationQuestionOptions competentInWorkLanguage) {
        this.competentInWorkLanguage = competentInWorkLanguage;
    }

    public ResidenceStatus getResidenceStatus() {
        return residenceStatus;
    }

    public void setResidenceStatus(ResidenceStatus residenceStatus) {
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

    public String getCreatorIpAddress() {
        return creatorIpAddress;
    }

    public void setCreatorIpAddress(String creatorIpAddress) {
        this.creatorIpAddress = creatorIpAddress;
    }

    public Set<CommentAssignedUser> getCommentAssignedUsers() {
        return commentAssignedUser;
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

    public Set<AppointmentTimeslot> getAvailableAppointmentTimeslots() {
        return availableAppointmentTimeslots;
    }

    public void addDocument(Document document) {
        document.setIsReferenced(true);
        this.documents.add(document);
    }

    public void setDocument(Document document) {
        this.documents.clear();
        if (document != null) {
            addDocument(document);
        }
    }

    public Comment withId(Integer id) {
        this.id = id;
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

    public Comment withUser(User user) {
        this.user = user;
        return this;
    }

    public Comment withRole(String role) {
        this.role = role;
        return this;
    }

    public Comment withDelegateUser(User user) {
        this.user = user;
        return this;
    }

    public Comment withDelegateRole(Role delegateRole) {
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

    public Comment withCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
        return this;
    }

    public boolean isAtLeastOneAnswerUnsure() {
        return getResidenceStatus() == ResidenceStatus.UNSURE || getQualified() == ValidationQuestionOptions.UNSURE
                || getCompetentInWorkLanguage() == ValidationQuestionOptions.UNSURE;
    }

    public String getTooltipMessage(final String role) {
        return String.format("%s %s (%s) as: %s", user.getFirstName(), user.getLastName(), user.getEmail(), StringUtils.capitalize(role));
    }

}
