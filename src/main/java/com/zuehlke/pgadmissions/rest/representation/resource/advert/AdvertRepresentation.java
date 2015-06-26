package com.zuehlke.pgadmissions.rest.representation.resource.advert;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.InstitutionAddressRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimpleWithImages;

public class AdvertRepresentation {

    private Integer id;

    private UserRepresentation user;

    private ResourceRepresentationSimpleWithImages resource;

    private ResourceRepresentationSimpleWithImages institution;

    private ResourceRepresentationSimpleWithImages partner;

    private ResourceRepresentationSimpleWithImages department;

    private PrismOpportunityType opportunityType;

    private String title;

    private String summary;

    private String description;

    private String homepage;

    private String applyHomepage;

    private String telephone;

    private InstitutionAddressRepresentation address;

    private AdvertFinancialDetailsRepresentation financialDetails;

    private AdvertClosingDateRepresentation closingDate;

    private List<AdvertClosingDateRepresentation> closingDates;

    private AdvertCategoriesRepresentation categories;

    private AdvertTargetsRepresentation targets;

    private String sequenceIdentifier;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserRepresentation getUser() {
        return user;
    }

    public void setUser(UserRepresentation user) {
        this.user = user;
    }

    public ResourceRepresentationSimpleWithImages getResource() {
        return resource;
    }

    public void setResource(ResourceRepresentationSimpleWithImages resource) {
        this.resource = resource;
    }

    public ResourceRepresentationSimpleWithImages getInstitution() {
        return institution;
    }

    public void setInstitution(ResourceRepresentationSimpleWithImages institution) {
        this.institution = institution;
    }

    public ResourceRepresentationSimpleWithImages getPartner() {
        return partner;
    }

    public void setPartner(ResourceRepresentationSimpleWithImages partner) {
        this.partner = partner;
    }

    public ResourceRepresentationSimpleWithImages getDepartment() {
        return department;
    }

    public void setDepartment(ResourceRepresentationSimpleWithImages department) {
        this.department = department;
    }

    public PrismOpportunityType getOpportunityType() {
        return opportunityType;
    }

    public void setOpportunityType(PrismOpportunityType opportunityType) {
        this.opportunityType = opportunityType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public InstitutionAddressRepresentation getAddress() {
        return address;
    }

    public void setAddress(InstitutionAddressRepresentation address) {
        this.address = address;
    }

    public AdvertFinancialDetailsRepresentation getFinancialDetails() {
        return financialDetails;
    }

    public void setFinancialDetails(AdvertFinancialDetailsRepresentation financialDetails) {
        this.financialDetails = financialDetails;
    }

    public AdvertClosingDateRepresentation getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(AdvertClosingDateRepresentation closingDate) {
        this.closingDate = closingDate;
    }

    public List<AdvertClosingDateRepresentation> getClosingDates() {
        return closingDates;
    }

    public void setClosingDates(List<AdvertClosingDateRepresentation> closingDates) {
        this.closingDates = closingDates;
    }

    public AdvertCategoriesRepresentation getCategories() {
        return categories;
    }

    public void setCategories(AdvertCategoriesRepresentation categories) {
        this.categories = categories;
    }

    public AdvertTargetsRepresentation getTargets() {
        return targets;
    }

    public void setTargets(AdvertTargetsRepresentation targets) {
        this.targets = targets;
    }

    public String getSequenceIdentifier() {
        return sequenceIdentifier;
    }

    public void setSequenceIdentifier(String sequenceIdentifier) {
        this.sequenceIdentifier = sequenceIdentifier;
    }

    public AdvertRepresentation withId(Integer id) {
        this.id = id;
        return this;
    }

    public AdvertRepresentation withUser(UserRepresentation user) {
        this.user = user;
        return this;
    }

    public AdvertRepresentation withResource(ResourceRepresentationSimpleWithImages resource) {
        this.resource = resource;
        return this;
    }

    public AdvertRepresentation withInstitution(ResourceRepresentationSimpleWithImages institution) {
        this.resource = institution;
        return this;
    }

    public AdvertRepresentation withPartner(ResourceRepresentationSimpleWithImages partner) {
        this.partner = partner;
        return this;
    }

    public AdvertRepresentation withDepartment(ResourceRepresentationSimpleWithImages department) {
        this.department = department;
        return this;
    }

    public AdvertRepresentation withOpportunityType(PrismOpportunityType opportunityType) {
        this.opportunityType = opportunityType;
        return this;
    }

    public AdvertRepresentation withTitle(String title) {
        this.title = title;
        return this;
    }

    public AdvertRepresentation withSummary(String summary) {
        this.summary = summary;
        return this;
    }

    public AdvertRepresentation withDescription(String description) {
        this.description = description;
        return this;
    }

    public AdvertRepresentation withHomepage(String homepage) {
        this.homepage = homepage;
        return this;
    }

    public AdvertRepresentation withApplyHomepage(String applyHomepage) {
        this.applyHomepage = applyHomepage;
        return this;
    }

    public AdvertRepresentation withTelephone(String telephone) {
        this.telephone = telephone;
        return this;
    }

    public AdvertRepresentation withAddress(InstitutionAddressRepresentation address) {
        this.address = address;
        return this;
    }

    public AdvertRepresentation withFinancialDetails(AdvertFinancialDetailsRepresentation financialDetails) {
        this.financialDetails = financialDetails;
        return this;
    }

    public AdvertRepresentation withClosingDate(AdvertClosingDateRepresentation closingDate) {
        this.closingDate = closingDate;
        return this;
    }

    public AdvertRepresentation withClosingDates(List<AdvertClosingDateRepresentation> closingDates) {
        this.closingDates = closingDates;
        return this;
    }

    public AdvertRepresentation withCategories(AdvertCategoriesRepresentation categories) {
        this.categories = categories;
        return this;
    }

    public AdvertRepresentation withTargets(AdvertTargetsRepresentation targets) {
        this.targets = targets;
        return this;
    }

    public AdvertRepresentation withSequenceIdentifier(String sequenceIdentifier) {
        this.sequenceIdentifier = sequenceIdentifier;
        return this;
    }

}
