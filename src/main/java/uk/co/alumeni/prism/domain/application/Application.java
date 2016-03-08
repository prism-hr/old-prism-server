package uk.co.alumeni.prism.domain.application;

import java.math.BigDecimal;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityType;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.profile.ProfileEntity;
import uk.co.alumeni.prism.domain.resource.Department;
import uk.co.alumeni.prism.domain.resource.Institution;
import uk.co.alumeni.prism.domain.resource.Program;
import uk.co.alumeni.prism.domain.resource.Project;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.ResourceCondition;
import uk.co.alumeni.prism.domain.resource.ResourceOpportunity;
import uk.co.alumeni.prism.domain.resource.ResourcePreviousState;
import uk.co.alumeni.prism.domain.resource.ResourceState;
import uk.co.alumeni.prism.domain.resource.System;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserRole;
import uk.co.alumeni.prism.domain.workflow.State;
import uk.co.alumeni.prism.domain.workflow.StateActionPending;

import com.google.common.collect.Sets;

@Entity
@Table(name = "application")
public class Application extends Resource
        implements
        ProfileEntity<ApplicationPersonalDetail, ApplicationAddress, ApplicationQualification, ApplicationAward, ApplicationEmploymentPosition, ApplicationReferee, ApplicationDocument, ApplicationAdditionalInformation> {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "code", unique = true)
    private String code;

    @ManyToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "system_id", nullable = false)
    private uk.co.alumeni.prism.domain.resource.System system;

    @ManyToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "institution_id")
    private Institution institution;

    @ManyToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "program_id")
    private Program program;

    @ManyToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "advert_id")
    private Advert advert;

    @Column(name = "opportunity_category", nullable = false)
    private String opportunityCategories;

    @Column(name = "closing_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate closingDate;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_program_detail_id", unique = true)
    private ApplicationProgramDetail programDetail;

    @OrderBy(clause = "id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_id", nullable = false)
    private Set<ApplicationTheme> themes = Sets.newHashSet();

    @OrderBy(clause = "id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_id", nullable = false)
    private Set<ApplicationLocation> locations = Sets.newHashSet();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_personal_detail_id", unique = true)
    private ApplicationPersonalDetail personalDetail;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_address_id", unique = true)
    private ApplicationAddress address;

    @OrderBy(clause = "id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_id", nullable = false)
    private Set<ApplicationQualification> qualifications = Sets.newHashSet();

    @OrderBy(clause = "id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_id", nullable = false)
    private Set<ApplicationAward> awards = Sets.newHashSet();

    @OrderBy(clause = "id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_id", nullable = false)
    private Set<ApplicationEmploymentPosition> employmentPositions = Sets.newHashSet();

    @OrderBy(clause = "id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_id", nullable = false)
    private Set<ApplicationReferee> referees = Sets.newHashSet();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_document_id", unique = true)
    private ApplicationDocument document;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_additional_information_id", unique = true)
    private ApplicationAdditionalInformation additionalInformation;

    @Column(name = "application_rating_count")
    private Integer applicationRatingCount;

    @Column(name = "application_rating_average")
    private BigDecimal applicationRatingAverage;

    @Column(name = "completion_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate completionDate;

    @Column(name = "offered_position_name")
    private String offeredPositionName;

    @Lob
    @Column(name = "offered_position_description")
    private String offeredPositionDescription;

    @Column(name = "offered_start_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate offeredStartDate;

    @Lob
    @Column(name = "offered_appointment_conditions")
    private String offeredAppointmentConditions;

    @Column(name = "shared", nullable = false)
    private Boolean shared;

    @Column(name = "on_course", nullable = false)
    private Boolean onCourse;

    @Column(name = "application_year")
    private String applicationYear;

    @Column(name = "application_month")
    private Integer applicationMonth;

    @Column(name = "application_month_sequence")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate applicationMonthSequence;

    @Column(name = "application_week")
    private Integer applicationWeek;

    @Column(name = "application_week_sequence")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate applicationWeekSequence;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private State state;

    @ManyToOne
    @JoinColumn(name = "previous_state_id")
    private State previousState;

    @Column(name = "due_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate dueDate;

    @Column(name = "created_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdTimestamp;
    
    @Column(name = "submitted_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime submittedTimestamp;

    @Column(name = "updated_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime updatedTimestamp;

    @Column(name = "sequence_identifier", unique = true)
    private String sequenceIdentifier;

    @OneToMany(mappedBy = "application")
    private Set<ResourceState> resourceStates = Sets.newHashSet();

    @OneToMany(mappedBy = "application")
    private Set<ResourcePreviousState> resourcePreviousStates = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_id")
    private Set<ResourceCondition> resourceConditions = Sets.newHashSet();

    @OneToMany(mappedBy = "application")
    private Set<Comment> comments = Sets.newHashSet();

    @OneToMany(mappedBy = "application")
    private Set<UserRole> userRoles = Sets.newHashSet();

    @OneToMany(mappedBy = "application")
    private Set<StateActionPending> stateActionPendings = Sets.newHashSet();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_id", unique = true)
    private Set<ApplicationHiringManager> hiringManagers = Sets.newHashSet();

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
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
    public final Advert getAdvert() {
        return advert;
    }

    @Override
    public final void setAdvert(Advert advert) {
        this.advert = advert;
    }

    @Override
    public String getOpportunityCategories() {
        return opportunityCategories;
    }

    @Override
    public void setOpportunityCategories(String opportunityCategories) {
        this.opportunityCategories = opportunityCategories;
    }

    @Override
    public Application getApplication() {
        return this;
    }

    public ApplicationProgramDetail getProgramDetail() {
        return programDetail;
    }

    public void setProgramDetail(ApplicationProgramDetail programDetail) {
        this.programDetail = programDetail;
    }

    public Set<ApplicationTheme> getThemes() {
        return themes;
    }

    public Set<ApplicationLocation> getLocations() {
        return locations;
    }

    @Override
    public ApplicationPersonalDetail getPersonalDetail() {
        return personalDetail;
    }

    @Override
    public void setPersonalDetail(ApplicationPersonalDetail personalDetail) {
        this.personalDetail = personalDetail;
    }

    @Override
    public ApplicationAddress getAddress() {
        return address;
    }

    @Override
    public void setAddress(ApplicationAddress address) {
        this.address = address;
    }

    @Override
    public Set<ApplicationQualification> getQualifications() {
        return qualifications;
    }

    @Override
    public Set<ApplicationAward> getAwards() {
        return awards;
    }

    @Override
    public Set<ApplicationEmploymentPosition> getEmploymentPositions() {
        return employmentPositions;
    }

    @Override
    public Set<ApplicationReferee> getReferees() {
        return referees;
    }

    @Override
    public ApplicationDocument getDocument() {
        return document;
    }

    @Override
    public void setDocument(ApplicationDocument document) {
        this.document = document;
    }

    @Override
    public ApplicationAdditionalInformation getAdditionalInformation() {
        return additionalInformation;
    }

    @Override
    public void setAdditionalInformation(ApplicationAdditionalInformation additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    @Override
    public DateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    @Override
    public void setCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public DateTime getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    @Override
    public void setUpdatedTimestamp(DateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public DateTime getSubmittedTimestamp() {
        return submittedTimestamp;
    }

    public void setSubmittedTimestamp(DateTime submittedTimestamp) {
        this.submittedTimestamp = submittedTimestamp;
    }

    public String getApplicationYear() {
        return applicationYear;
    }

    public void setApplicationYear(String applicationYear) {
        this.applicationYear = applicationYear;
    }

    public Integer getApplicationMonth() {
        return applicationMonth;
    }

    public void setApplicationMonth(Integer applicationMonth) {
        this.applicationMonth = applicationMonth;
    }

    public LocalDate getApplicationMonthSequence() {
        return applicationMonthSequence;
    }

    public void setApplicationMonthSequence(LocalDate applicationMonthSequence) {
        this.applicationMonthSequence = applicationMonthSequence;
    }

    public Integer getApplicationWeek() {
        return applicationWeek;
    }

    public void setApplicationWeek(Integer applicationWeek) {
        this.applicationWeek = applicationWeek;
    }

    public LocalDate getApplicationWeekSequence() {
        return applicationWeekSequence;
    }

    public void setApplicationWeekSequence(LocalDate applicationWeekSequence) {
        this.applicationWeekSequence = applicationWeekSequence;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }

    public Integer getApplicationRatingCount() {
        return applicationRatingCount;
    }

    public void setApplicationRatingCount(Integer applicationRatingCount) {
        this.applicationRatingCount = applicationRatingCount;
    }

    public BigDecimal getApplicationRatingAverage() {
        return applicationRatingAverage;
    }

    public void setApplicationRatingAverage(BigDecimal applicationRatingAverage) {
        this.applicationRatingAverage = applicationRatingAverage;
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }

    public String getOfferedPositionName() {
        return offeredPositionName;
    }

    public void setOfferedPositionName(String offeredPositionName) {
        this.offeredPositionName = offeredPositionName;
    }

    public String getOfferedPositionDescription() {
        return offeredPositionDescription;
    }

    public void setOfferedPositionDescription(String offeredPositionDescription) {
        this.offeredPositionDescription = offeredPositionDescription;
    }

    public LocalDate getOfferedStartDate() {
        return offeredStartDate;
    }

    public void setOfferedStartDate(LocalDate offeredStartDate) {
        this.offeredStartDate = offeredStartDate;
    }

    public String getOfferedAppointmentConditions() {
        return offeredAppointmentConditions;
    }

    public void setOfferedAppointmentConditions(String offeredAppointmentConditions) {
        this.offeredAppointmentConditions = offeredAppointmentConditions;
    }

    @Override
    public Boolean getShared() {
        return shared;
    }

    @Override
    public void setShared(Boolean shared) {
        this.shared = shared;
    }

    public Boolean getOnCourse() {
        return onCourse;
    }

    public void setOnCourse(Boolean onCourse) {
        this.onCourse = onCourse;
    }

    @Override
    public Set<ResourceState> getResourceStates() {
        return resourceStates;
    }

    @Override
    public Set<ResourcePreviousState> getResourcePreviousStates() {
        return resourcePreviousStates;
    }

    @Override
    public Set<ResourceCondition> getResourceConditions() {
        return resourceConditions;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    @Override
    public Set<UserRole> getUserRoles() {
        return userRoles;
    }

    @Override
    public Set<StateActionPending> getStateActionPendings() {
        return stateActionPendings;
    }

    public Set<ApplicationHiringManager> getHiringManagers() {
        return hiringManagers;
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void setState(State state) {
        this.state = state;
    }

    @Override
    public State getPreviousState() {
        return previousState;
    }

    @Override
    public void setPreviousState(State previousState) {
        this.previousState = previousState;
    }

    @Override
    public LocalDate getDueDate() {
        return dueDate;
    }

    @Override
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public String getSequenceIdentifier() {
        return sequenceIdentifier;
    }

    @Override
    public void setSequenceIdentifier(String sequenceIdentifier) {
        this.sequenceIdentifier = sequenceIdentifier;
    }

    public Application withUser(User user) {
        this.user = user;
        return this;
    }

    public Application withParentResource(Resource parentResource) {
        setParentResource(parentResource);
        return this;
    }

    public Application withAdvert(Advert advert) {
        this.advert = advert;
        return this;
    }

    public Application withOpportunityCategories(String opportunityCategories) {
        this.opportunityCategories = opportunityCategories;
        return this;
    }

    public Application withOnCourse(Boolean onCourse) {
        this.onCourse = onCourse;
        return this;
    }

    public Application addHiringManager(User user) {
        this.hiringManagers.add(new ApplicationHiringManager().withUser(user));
        return this;
    }

    public String getCreatedTimestampDisplay(String dateFormat) {
        return createdTimestamp == null ? null : createdTimestamp.toString(dateFormat);
    }

    public String getSubmittedTimestampDisplay(String dateFormat) {
        return submittedTimestamp == null ? null : submittedTimestamp.toString(dateFormat);
    }

    public String getClosingDateDisplay(String dateFormat) {
        return closingDate == null ? null : closingDate.toString(dateFormat);
    }

    public String getOfferedStartDateDisplay(String dateFormat) {
        return offeredStartDate == null ? null : offeredStartDate.toString(dateFormat);
    }

    public boolean isSubmitted() {
        return submittedTimestamp != null;
    }

    public PrismOpportunityType getOpportunityType() {
        Resource resourceParent = getParentResource();
        if (ResourceOpportunity.class.isAssignableFrom(resourceParent.getClass())) {
            return ((ResourceOpportunity) resourceParent).getOpportunityType().getId();
        }
        return null;
    }

    @Override
    public UniqueEntity.EntitySignature getEntitySignature() {
        return new UniqueEntity.EntitySignature()
                .addProperty("user", user)
                .addProperty("project", project)
                .addProperty("program", program)
                .addProperty("department", department)
                .addProperty("institution", institution)
                .addExclusion("state.id", PrismState.APPLICATION_APPROVED_COMPLETED)
                .addExclusion("state.id", PrismState.APPLICATION_REJECTED_COMPLETED)
                .addExclusion("state.id", PrismState.APPLICATION_WITHDRAWN_COMPLETED)
                .addExclusion("state.id", PrismState.APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED);
    }

}
