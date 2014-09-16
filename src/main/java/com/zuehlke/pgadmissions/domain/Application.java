package com.zuehlke.pgadmissions.domain;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.PrismOfferType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;

@Entity
@Table(name = "APPLICATION")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Application extends Resource {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "code")
    private String code;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "system_id", nullable = false)
    private System system;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @ManyToOne
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "referrer")
    private String referrer;

    @Column(name = "closing_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate closingDate;

    @Column(name = "previous_closing_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate previousClosingDate;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_program_detail_id", unique = true)
    private ApplicationProgramDetail programDetail;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_id", nullable = false)
    private Set<ApplicationSupervisor> supervisors = Sets.newHashSet();
    
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_personal_detail_id", unique = true)
    private ApplicationPersonalDetail personalDetail;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_address_id", unique = true)
    private ApplicationAddress address;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_id", nullable = false)
    private Set<ApplicationQualification> qualifications = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_id", nullable = false)
    private Set<ApplicationEmploymentPosition> employmentPositions = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application_id", nullable = false)
    private Set<ApplicationFunding> fundings = Sets.newHashSet();

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

    @Column(name = "confirmed_start_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate confirmedStartDate;

    @ManyToOne
    @JoinColumn(name = "confirmed_primary_supervisor_id")
    private User confirmedPrimarySupervisor;
    
    @ManyToOne
    @JoinColumn(name = "confirmed_secondary_supervisor_id")
    private User confirmedSecondarySupervisor;

    @Column(name = "confirmed_offer_type")
    @Enumerated(EnumType.STRING)
    private PrismOfferType confirmedOfferType;

    @Column(name = "retain", nullable = false)
    private Boolean retain;

    @Column(name = "submitted_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime submittedTimestamp;

    @Column(name = "sequence_identifier", unique = true)
    private String sequenceIdentifier;

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

    @OneToMany(mappedBy = "application")
    private Set<Comment> comments = Sets.newHashSet();
    
    @OneToMany(mappedBy = "application")
    private Set<UserRole> userRoles = Sets.newHashSet();

    @Transient
    private Boolean acceptedTerms;

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
        return this;
    }

    @Override
    public String getReferrer() {
        return referrer;
    }

    @Override
    public void setReferrer(String referrer) {
        this.referrer = referrer;
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

    public Boolean getAcceptedTerms() {
        return acceptedTerms;
    }

    public void setAcceptedTerms(Boolean acceptedTerms) {
        this.acceptedTerms = acceptedTerms;
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

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }

    public final LocalDate getPreviousClosingDate() {
        return previousClosingDate;
    }

    public final void setPreviousClosingDate(LocalDate previousClosingDate) {
        this.previousClosingDate = previousClosingDate;
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

    public final Set<ApplicationSupervisor> getSupervisors() {
        return supervisors;
    }

    public ApplicationAdditionalInformation getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(ApplicationAdditionalInformation additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public final Integer getApplicationRatingCount() {
        return applicationRatingCount;
    }

    public final void setApplicationRatingCount(Integer applicationRatingCount) {
        this.applicationRatingCount = applicationRatingCount;
    }

    public final BigDecimal getApplicationRatingAverage() {
        return applicationRatingAverage;
    }

    public final void setApplicationRatingAverage(BigDecimal applicationRatingAverage) {
        this.applicationRatingAverage = applicationRatingAverage;
    }

    public final LocalDate getConfirmedStartDate() {
        return confirmedStartDate;
    }

    public final void setConfirmedStartDate(LocalDate confirmedStartDate) {
        this.confirmedStartDate = confirmedStartDate;
    }

    public final User getConfirmedPrimarySupervisor() {
        return confirmedPrimarySupervisor;
    }

    public final void setConfirmedPrimarySupervisor(User confirmedPrimarySupervisor) {
        this.confirmedPrimarySupervisor = confirmedPrimarySupervisor;
    }

    public final User getConfirmedSecondarySupervisor() {
        return confirmedSecondarySupervisor;
    }

    public final void setConfirmedSecondarySupervisor(User confirmedSecondarySupervisor) {
        this.confirmedSecondarySupervisor = confirmedSecondarySupervisor;
    }

    public final PrismOfferType getConfirmedOfferType() {
        return confirmedOfferType;
    }

    public final void setConfirmedOfferType(PrismOfferType confirmedOfferType) {
        this.confirmedOfferType = confirmedOfferType;
    }

    public final Boolean getRetain() {
        return retain;
    }

    public final void setRetain(Boolean retain) {
        this.retain = retain;
    }

    public Set<ApplicationQualification> getQualifications() {
        return qualifications;
    }

    public Set<ApplicationFunding> getFundings() {
        return fundings;
    }

    public Set<ApplicationEmploymentPosition> getEmploymentPositions() {
        return employmentPositions;
    }

    public Set<ApplicationReferee> getReferees() {
        return referees;
    }

    public final Set<Comment> getComments() {
        return comments;
    }
    
    public final Set<UserRole> getUserRoles() {
        return userRoles;
    }

    public Application withId(Integer id) {
        this.id = id;
        return this;
    }

    public Application withSystem(System system) {
        this.system = system;
        return this;
    }

    public Application withInstitution(Institution institution) {
        this.institution = institution;
        return this;
    }

    public Application withProgram(Program program) {
        this.program = program;
        return this;
    }

    public Application withProject(final Project project) {
        this.project = project;
        return this;
    }

    public Application withUser(User user) {
        this.user = user;
        return this;
    }

    public Application withState(State state) {
        this.state = state;
        return this;
    }

    public Application withDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public Application withCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
        return this;
    }

    public Application withSubmittedTimestamp(DateTime submittedTimestamp) {
        this.submittedTimestamp = submittedTimestamp;
        return this;
    }

    public Application withPersonalDetail(ApplicationPersonalDetail personalDetail) {
        this.personalDetail = personalDetail;
        return this;
    }

    public Application withProgramDetail(ApplicationProgramDetail programDetail) {
        this.programDetail = programDetail;
        return this;
    }

    public Application withAddress(ApplicationAddress applicationAddress) {
        this.address = applicationAddress;
        return this;
    }

    public Application withDocument(ApplicationDocument applicationDocument) {
        this.document = applicationDocument;
        return this;
    }

    public Application withAdditionalInformation(ApplicationAdditionalInformation additionalInformation) {
        this.additionalInformation = additionalInformation;
        return this;
    }

    public Application withDoRetain(Boolean doRetain) {
        this.retain = doRetain;
        return this;
    }

    public Application withAcceptedTerms(Boolean acceptedTerms) {
        this.acceptedTerms = acceptedTerms;
        return this;
    }

    public Application withReferees(ApplicationReferee... referees) {
        this.referees.addAll(Arrays.asList(referees));
        return this;
    }

    public Application withQualifications(ApplicationQualification... qualifications) {
        this.qualifications.addAll(Arrays.asList(qualifications));
        return this;
    }

    public Application withParentResource(Resource parentResource) {
        setParentResource(parentResource);
        return this;
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

    @Override
    public void addComment(Comment comment) {
        comments.add(comment);
    }
    
    public LocalDate getRecommendedStartDate() {
        return project == null ? program.getRecommendedStartDate() : project.getRecommendedStartDate();
    }
    
    public Set<ParentResource> getParentResources() {
        Set<ParentResource> parentResources = Sets.newLinkedHashSet();
        if (project != null) {
            parentResources.add(project);
        }
        parentResources.add(program);
        parentResources.add(institution);
        return parentResources;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        List<HashMap<String, Object>> propertiesWrapper = Lists.newArrayList();
        HashMap<String, Object> properties = Maps.newHashMap();
        properties.put("user", user);
        properties.put("program", program);
        if (project != null) {
            properties.put("project", project);
        }
        propertiesWrapper.add(properties);
        HashMultimap<String, Object> exclusions = HashMultimap.create();
        exclusions.put("state.id", PrismState.APPLICATION_APPROVED_COMPLETED);
        exclusions.put("state.id", PrismState.APPLICATION_REJECTED_COMPLETED);
        exclusions.put("state.id", PrismState.APPLICATION_WITHDRAWN_COMPLETED);
        return new ResourceSignature(propertiesWrapper, exclusions);
    }

}
