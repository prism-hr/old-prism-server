package com.zuehlke.pgadmissions.domain.application;

import static com.zuehlke.pgadmissions.PrismConstants.SPACE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_APPROVED_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REJECTED_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_WITHDRAWN_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED;

import java.math.BigDecimal;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismOfferType;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.profile.ProfileEntity;
import com.zuehlke.pgadmissions.domain.resource.Department;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceCondition;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.ResourcePreviousState;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.resource.System;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.domain.workflow.State;

@Entity
@Table(name = "application")
public class Application extends Resource implements
        ProfileEntity<ApplicationPersonalDetail, ApplicationAddress, ApplicationQualification, ApplicationEmploymentPosition, ApplicationReferee, ApplicationDocument, ApplicationAdditionalInformation> {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "code_legacy")
    private String codeLegacy;

    @ManyToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "system_id", nullable = false)
    private System system;

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

    @Column(name = "confirmed_start_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate confirmedStartDate;

    @Column(name = "confirmed_offer_type")
    @Enumerated(EnumType.STRING)
    private PrismOfferType confirmedOfferType;

    @Column(name = "shared")
    private Boolean shared;

    @Column(name = "submitted_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime submittedTimestamp;

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

    public final String getCodeLegacy() {
        return codeLegacy;
    }

    public final void setCodeLegacy(String codeLegacy) {
        this.codeLegacy = codeLegacy;
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

    public final Advert getAdvert() {
        return advert;
    }

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

    public ApplicationAddress getAddress() {
        return address;
    }

    public void setAddress(ApplicationAddress address) {
        this.address = address;
    }

    public ApplicationDocument getDocument() {
        return document;
    }

    public void setDocument(ApplicationDocument document) {
        this.document = document;
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

    public ApplicationPersonalDetail getPersonalDetail() {
        return personalDetail;
    }

    public void setPersonalDetail(ApplicationPersonalDetail personalDetail) {
        this.personalDetail = personalDetail;
    }

    public ApplicationProgramDetail getProgramDetail() {
        return programDetail;
    }

    public void setProgramDetail(ApplicationProgramDetail programDetail) {
        this.programDetail = programDetail;
    }

    public ApplicationAdditionalInformation getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(ApplicationAdditionalInformation additionalInformation) {
        this.additionalInformation = additionalInformation;
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

    public LocalDate getConfirmedStartDate() {
        return confirmedStartDate;
    }

    public void setConfirmedStartDate(LocalDate confirmedStartDate) {
        this.confirmedStartDate = confirmedStartDate;
    }

    public PrismOfferType getConfirmedOfferType() {
        return confirmedOfferType;
    }

    public void setConfirmedOfferType(PrismOfferType confirmedOfferType) {
        this.confirmedOfferType = confirmedOfferType;
    }

    public Boolean getShared() {
        return shared;
    }

    public void setShared(Boolean shared) {
        this.shared = shared;
    }

    public Set<ApplicationQualification> getQualifications() {
        return qualifications;
    }

    public Set<ApplicationEmploymentPosition> getEmploymentPositions() {
        return employmentPositions;
    }

    public Set<ApplicationReferee> getReferees() {
        return referees;
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

    public String getCreatedTimestampDisplay(String dateFormat) {
        return createdTimestamp == null ? null : createdTimestamp.toString(dateFormat);
    }

    public String getSubmittedTimestampDisplay(String dateFormat) {
        return submittedTimestamp == null ? null : submittedTimestamp.toString(dateFormat);
    }

    public String getClosingDateDisplay(String dateFormat) {
        return closingDate == null ? null : closingDate.toString(dateFormat);
    }

    public boolean isSubmitted() {
        return submittedTimestamp != null;
    }

    public String getParentResourceTitleDisplay(String at, String in) {
        ResourceParent parent = (ResourceParent) getParentResource();
        if (parent instanceof Institution) {
            return parent.getName();
        } else if (parent instanceof Department || parent instanceof Program) {
            return parent.getName() + SPACE + at + SPACE + institution.getName();
        } else if (parent instanceof Project) {
            Resource grandParent = parent.getParentResource();
            if (grandParent instanceof Program) {
                return parent.getName() + SPACE + in + SPACE + program.getName();
            }
            return parent.getName() + SPACE + at + SPACE + institution.getName();
        }
        return null;
    }

    public String getParentResourceCodeDisplay() {
        return getParentResource().getCode();
    }

    public PrismOpportunityType getOpportunityType() {
        Resource resourceParent = getParentResource();
        if (ResourceOpportunity.class.isAssignableFrom(resourceParent.getClass())) {
            return ((ResourceOpportunity) resourceParent).getOpportunityType().getId();
        }
        return null;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature()
                .addProperty("user", user)
                .addProperty("project", project)
                .addProperty("program", program)
                .addProperty("department", department)
                .addProperty("institution", institution)
                .addExclusion("state.id", APPLICATION_APPROVED_COMPLETED)
                .addExclusion("state.id", APPLICATION_REJECTED_COMPLETED)
                .addExclusion("state.id", APPLICATION_WITHDRAWN_COMPLETED)
                .addExclusion("state.id", APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED);
    }

}
