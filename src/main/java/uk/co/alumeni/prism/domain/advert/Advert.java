package uk.co.alumeni.prism.domain.advert;

import com.google.common.base.Objects;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.address.Address;
import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.resource.*;
import uk.co.alumeni.prism.domain.resource.System;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserAssignment;
import uk.co.alumeni.prism.domain.workflow.OpportunityType;
import uk.co.alumeni.prism.domain.workflow.Scope;
import uk.co.alumeni.prism.workflow.user.AdvertReassignmentProcessor;

import javax.persistence.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static uk.co.alumeni.prism.PrismConstants.HYPHEN;
import static uk.co.alumeni.prism.PrismConstants.SPACE;
import static uk.co.alumeni.prism.dao.WorkflowDAO.advertScopes;
import static uk.co.alumeni.prism.utils.PrismReflectionUtils.getProperty;
import static uk.co.alumeni.prism.utils.PrismReflectionUtils.setProperty;

@Entity
@Table(name = "advert", uniqueConstraints = {@UniqueConstraint(columnNames = {"institution_id", "department_id", "program_id", "project_id"})})
public class Advert implements UniqueEntity, UserAssignment<AdvertReassignmentProcessor>, Comparable<Advert> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "system_id")
    private System system;

    @ManyToOne
    @JoinColumn(name = "institution_id")
    private Institution institution;

    @ManyToOne
    @JoinColumn(name = "institution_advert_id")
    private Advert institutionAdvert;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne
    @JoinColumn(name = "department_advert_id")
    private Advert departmentAdvert;

    @ManyToOne
    @JoinColumn(name = "program_id")
    private Program program;

    @ManyToOne
    @JoinColumn(name = "program_advert_id")
    private Advert programAdvert;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "project_advert_id")
    private Advert projectAdvert;

    @ManyToOne
    @JoinColumn(name = "scope_id")
    private Scope scope;

    @ManyToOne
    @JoinColumn(name = "opportunity_type_id")
    private OpportunityType opportunityType;

    @Column(name = "opportunity_category")
    private String opportunityCategories;

    @Lob
    @Column(name = "study_option")
    private String studyOptions;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "summary")
    private String summary;

    @Lob
    @Column(name = "description")
    private String description;

    @OneToOne
    @JoinColumn(name = "background_image_id")
    private Document backgroundImage;

    @Column(name = "homepage")
    private String homepage;

    @Column(name = "apply_homepage")
    private String applyHomepage;

    @Column(name = "telephone")
    private String telephone;

    @OneToOne
    @JoinColumn(name = "address_id")
    private Address address;

    @Column(name = "duration_minimum")
    private Integer durationMinimum;

    @Column(name = "duration_maximum")
    private Integer durationMaximum;

    @Embedded
    private AdvertFinancialDetail pay;

    @Column(name = "closing_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate closingDate;

    @Column(name = "globally_visible")
    private Boolean globallyVisible;

    @Column(name = "submitted", nullable = false)
    private Boolean submitted;

    @Column(name = "published", nullable = false)
    private Boolean published;

    @Column(name = "sequence_identifier", unique = true)
    private String sequenceIdentifier;

    @Embedded
    private AdvertCategories categories;

    @OrderBy(clause = "id")
    @OneToMany(mappedBy = "advert")
    private Set<AdvertTarget> targets = newHashSet();

    @OrderBy(clause = "id")
    @OneToMany(mappedBy = "targetAdvert")
    private Set<AdvertTarget> targetsIndirect = newHashSet();

    @OrderBy(clause = "id")
    @OneToMany(mappedBy = "advert")
    private Set<AdvertCompetence> competences = newHashSet();

    @OrderBy(clause = "sequence_identifier desc")
    @OneToMany(mappedBy = "advert")
    private Set<Application> applications = newHashSet();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public Advert getInstitutionAdvert() {
        return institutionAdvert;
    }

    public void setInstitutionAdvert(Advert institutionAdvert) {
        this.institutionAdvert = institutionAdvert;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Advert getDepartmentAdvert() {
        return departmentAdvert;
    }

    public void setDepartmentAdvert(Advert departmentAdvert) {
        this.departmentAdvert = departmentAdvert;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public Advert getProgramAdvert() {
        return programAdvert;
    }

    public void setProgramAdvert(Advert programAdvert) {
        this.programAdvert = programAdvert;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Advert getProjectAdvert() {
        return projectAdvert;
    }

    public void setProjectAdvert(Advert projectAdvert) {
        this.projectAdvert = projectAdvert;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public OpportunityType getOpportunityType() {
        return opportunityType;
    }

    public void setOpportunityType(OpportunityType opportunityType) {
        this.opportunityType = opportunityType;
    }

    public String getOpportunityCategories() {
        return opportunityCategories;
    }

    public void setOpportunityCategories(String opportunityCategories) {
        this.opportunityCategories = opportunityCategories;
    }

    public String getStudyOptions() {
        return studyOptions;
    }

    public void setStudyOptions(String studyOptions) {
        this.studyOptions = studyOptions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Document getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(Document backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getApplyHomepage() {
        return applyHomepage;
    }

    public void setApplyHomepage(String applyHomepage) {
        this.applyHomepage = applyHomepage;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Integer getDurationMinimum() {
        return durationMinimum;
    }

    public void setDurationMinimum(Integer durationMinimum) {
        this.durationMinimum = durationMinimum;
    }

    public Integer getDurationMaximum() {
        return durationMaximum;
    }

    public void setDurationMaximum(Integer durationMaximum) {
        this.durationMaximum = durationMaximum;
    }

    public AdvertFinancialDetail getPay() {
        return pay;
    }

    public void setPay(AdvertFinancialDetail pay) {
        this.pay = pay;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }

    public Boolean getGloballyVisible() {
        return globallyVisible;
    }

    public void setGloballyVisible(Boolean globallyVisible) {
        this.globallyVisible = globallyVisible;
    }

    public Boolean getSubmitted() {
        return submitted;
    }

    public void setSubmitted(Boolean submitted) {
        this.submitted = submitted;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public String getSequenceIdentifier() {
        return sequenceIdentifier;
    }

    public void setSequenceIdentifier(String sequenceIdentifier) {
        this.sequenceIdentifier = sequenceIdentifier;
    }

    public AdvertCategories getCategories() {
        return categories;
    }

    public void setCategories(AdvertCategories categories) {
        this.categories = categories;
    }

    public Set<AdvertTarget> getTargets() {
        return targets;
    }

    public Set<AdvertTarget> getTargetsIndirect() {
        return targetsIndirect;
    }

    public Set<AdvertCompetence> getCompetences() {
        return competences;
    }

    public Set<Application> getApplications() {
        return applications;
    }

    public ResourceParent getResource() {
        return firstNonNull(getResourceOpportunity(), getResourceParent());
    }

    public void setResource(Resource resource) {
        Map<PrismScope, Resource> enclosingResources = resource.getEnclosingResources();
        enclosingResources.keySet().stream().forEach(enclosingScope -> {
            String enclosingReference = enclosingScope.getLowerCamelName();
            setProperty(this, enclosingReference, enclosingResources.get(enclosingScope));
        });
    }

    public ResourceParent getResourceParent() {
        return firstNonNull(department, institution);
    }

    public ResourceOpportunity getResourceOpportunity() {
        return firstNonNull(project, program);
    }

    public List<ResourceParent> getParentResources() {
        PrismScope scope = getResource().getResourceScope();
        List<ResourceParent> parentResources = newArrayList();
        for (PrismScope advertScope : advertScopes) {
            if (advertScope.ordinal() < scope.ordinal()) {
                ResourceParent parentResource = (ResourceParent) getProperty(this, advertScope.getLowerCamelName());
                if (parentResource != null) {
                    parentResources.add(parentResource);
                }
            }
        }
        return parentResources;
    }

    public List<ResourceParent> getEnclosingResources() {
        List<ResourceParent> enclosingResources = newArrayList();
        for (PrismScope advertScope : advertScopes) {
            ResourceParent enclosingResource = (ResourceParent) getProperty(this, advertScope.getLowerCamelName());
            if (enclosingResource != null) {
                enclosingResources.add(enclosingResource);
            }
        }
        return enclosingResources;
    }

    public boolean isAdvertOfScope(PrismScope scope) {
        return getResource().getResourceScope().equals(scope);
    }

    public boolean sameAs(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final Advert other = (Advert) object;
        Integer id = getId();
        Integer otherId = other.getId();
        return id != null && otherId != null && id.equals(otherId);
    }

    @Override
    public Class<AdvertReassignmentProcessor> getUserReassignmentProcessor() {
        return AdvertReassignmentProcessor.class;
    }

    @Override
    public boolean isResourceUserAssignmentProperty() {
        return true;
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
        Advert other = (Advert) object;
        return equal(id, other.getId());
    }

    @Override
    public int compareTo(Advert other) {
        return ObjectUtils.compare(name, other.getName());
    }

    @Override
    public String toString() {
        return stream(new ResourceParent[]{institution, department, program, project}).filter(resource -> resource != null)
                .map(resource -> resource.getAdvert().getName()).collect(joining(SPACE + HYPHEN + SPACE));
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("institution", institution).addProperty("department", department).addProperty("program", program)
                .addProperty("project", project);
    }

}
