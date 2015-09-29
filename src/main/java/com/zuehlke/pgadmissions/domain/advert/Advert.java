package com.zuehlke.pgadmissions.domain.advert;

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

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.address.Address;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.resource.Department;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.System;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserAssignment;
import com.zuehlke.pgadmissions.domain.workflow.OpportunityType;
import com.zuehlke.pgadmissions.workflow.user.AdvertReassignmentProcessor;

@Entity
@Table(name = "advert", uniqueConstraints = { @UniqueConstraint(columnNames = { "institution_id", "department_id", "program_id", "project_id" }) })
public class Advert implements UniqueEntity, UserAssignment<AdvertReassignmentProcessor> {

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

    @OneToOne
    @JoinColumn(name = "advert_closing_date_id", unique = true)
    private AdvertClosingDate closingDate;

    @Column(name = "last_currency_conversion_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate lastCurrencyConversionDate;

    @Column(name = "globally_visible", nullable = false)
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

    @OrderBy(clause = "closing_date desc")
    @OneToMany(mappedBy = "advert")
    private Set<AdvertClosingDate> closingDates = Sets.newHashSet();

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

    public LocalDate getLastCurrencyConversionDate() {
        return lastCurrencyConversionDate;
    }

    public void setLastCurrencyConversionDate(LocalDate lastCurrencyConversionDate) {
        this.lastCurrencyConversionDate = lastCurrencyConversionDate;
    }

    public AdvertClosingDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(AdvertClosingDate closingDate) {
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

    public Set<AdvertClosingDate> getClosingDates() {
        return closingDates;
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

    public boolean hasConvertedPay() {
        return pay != null && !pay.getCurrencySpecified().equals(pay.getCurrencyAtLocale());
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

}
