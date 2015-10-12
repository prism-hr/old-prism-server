package com.zuehlke.pgadmissions.rest.representation.advert;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;
import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.address.AddressRepresentation;

public class AdvertRepresentationSimple {

    private Integer id;

    private String summary;

    private String description;

    private Boolean globallyVisible;

    private DocumentRepresentation backgroundImage;

    private String homepage;

    private String applyHomepage;

    private String telephone;

    private AddressRepresentation address;

    private AdvertFinancialDetailRepresentation financialDetails;

    private AdvertClosingDateRepresentation closingDate;

    private List<AdvertClosingDateRepresentation> closingDates;

    private AdvertCategoriesRepresentation categories;

    private List<AdvertTargetRepresentation> targets;
    
    private List<AdvertCompetenceRepresentation> competences;

    private List<PrismActionCondition> externalConditions;

    private String sequenceIdentifier;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Boolean getGloballyVisible() {
        return globallyVisible;
    }

    public void setGloballyVisible(Boolean globallyVisible) {
        this.globallyVisible = globallyVisible;
    }

    public DocumentRepresentation getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(DocumentRepresentation backgroundImage) {
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

    public AddressRepresentation getAddress() {
        return address;
    }

    public void setAddress(AddressRepresentation address) {
        this.address = address;
    }

    public AdvertFinancialDetailRepresentation getFinancialDetails() {
        return financialDetails;
    }

    public void setFinancialDetails(AdvertFinancialDetailRepresentation financialDetails) {
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

    public List<AdvertTargetRepresentation> getTargets() {
        return targets;
    }

    public void setTargets(List<AdvertTargetRepresentation> targets) {
        this.targets = targets;
    }

    public List<AdvertCompetenceRepresentation> getCompetences() {
        return competences;
    }

    public void setCompetences(List<AdvertCompetenceRepresentation> competences) {
        this.competences = competences;
    }

    public List<PrismActionCondition> getExternalConditions() {
        return externalConditions;
    }

    public void setExternalConditions(List<PrismActionCondition> externalConditions) {
        this.externalConditions = externalConditions;
    }

    public String getSequenceIdentifier() {
        return sequenceIdentifier;
    }

    public void setSequenceIdentifier(String sequenceIdentifier) {
        this.sequenceIdentifier = sequenceIdentifier;
    }

}
