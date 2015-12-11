package uk.co.alumeni.prism.domain.advert;

import static uk.co.alumeni.prism.dao.WorkflowDAO.advertScopes;
import static uk.co.alumeni.prism.utils.PrismReflectionUtils.getProperty;

import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.address.Address;
import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.resource.Department;
import uk.co.alumeni.prism.domain.resource.Institution;
import uk.co.alumeni.prism.domain.resource.Program;
import uk.co.alumeni.prism.domain.resource.Project;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.ResourceOpportunity;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.domain.resource.System;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserAssignment;
import uk.co.alumeni.prism.domain.workflow.OpportunityType;
import uk.co.alumeni.prism.workflow.user.AdvertReassignmentProcessor;

@Entity
@Table(name = "advert", uniqueConstraints = { @UniqueConstraint(columnNames = { "institution_id", "department_id", "program_id", "project_id" }) })
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
    @JoinColumn(name = "program_id")
    private Program program;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "opportunity_type_id")
    private OpportunityType opportunityType;

    @Column(name = "opportunity_category")
    private String opportunityCategories;

    @Lob
    @Column(name = "target_opportunity_type")
    private String targetOpportunityTypes;

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
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "advert_address_id")
    private Address address;

    @Embedded
    private AdvertFinancialDetail pay;

    @Column(name = "closing_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate closingDate;

    @Column(name = "globally_visible")
    private Boolean globallyVisible;

    @Column(name = "sequence_identifier", unique = true)
    private String sequenceIdentifier;

    @Embedded
    private AdvertCategories categories;

    @OrderBy(clause = "id")
    @OneToMany(mappedBy = "advert")
    private Set<AdvertTarget> targets = Sets.newHashSet();

    @OrderBy(clause = "id")
    @OneToMany(mappedBy = "advert")
    private Set<AdvertCompetence> competences = Sets.newHashSet();

    @OrderBy(clause = "sequence_identifier desc")
    @OneToMany(mappedBy = "advert")
    private Set<Application> applications = Sets.newHashSet();

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

    public Department getDepartment() {
        return department;
    }

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

    public String getTargetOpportunityTypes() {
        return targetOpportunityTypes;
    }

    public void setTargetOpportunityTypes(String targetOpportunityTypes) {
        this.targetOpportunityTypes = targetOpportunityTypes;
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

    public Set<AdvertCompetence> getCompetences() {
        return competences;
    }

    public Set<Application> getApplications() {
        return applications;
    }

    public Advert withName(String name) {
        this.name = name;
        return this;
    }

    public ResourceParent getResource() {
        return ObjectUtils.firstNonNull(getResourceOpportunity(), department, institution);
    }

    public List<ResourceParent> getParentResources() {
        PrismScope scope = getResource().getResourceScope();
        List<ResourceParent> parentResources = Lists.newArrayList();
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

    public void setResource(Resource resource) {
        this.system = resource.getSystem();
        this.institution = resource.getInstitution();
        this.department = resource.getDepartment();
        this.program = resource.getProgram();
        this.project = resource.getProject();
    }

    public ResourceOpportunity getResourceOpportunity() {
        return (ResourceOpportunity) ObjectUtils.firstNonNull(project, program);
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
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("institution", institution).addProperty("department", department).addProperty("program", program).addProperty("project", project);
    }

    @Override
    public int compareTo(Advert other) {
        return ObjectUtils.compare(name, other.getName());
    }

}
