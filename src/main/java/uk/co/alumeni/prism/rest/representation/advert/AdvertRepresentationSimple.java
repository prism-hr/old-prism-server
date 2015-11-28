package uk.co.alumeni.prism.rest.representation.advert;

import java.util.List;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCondition;
import uk.co.alumeni.prism.rest.representation.DocumentRepresentation;
import uk.co.alumeni.prism.rest.representation.address.AddressRepresentation;

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

    private AddressRepresentation parentAddress;

    private AdvertFinancialDetailRepresentation financialDetails;

    private AdvertClosingDateRepresentation closingDate;

    private List<AdvertClosingDateRepresentation> closingDates;

    private List<AdvertThemeRepresentation> themes;

    private AdvertCategoriesRepresentation categories;

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

    public AddressRepresentation getParentAddress() {
        return parentAddress;
    }

    public void setParentAddress(AddressRepresentation parentAddress) {
        this.parentAddress = parentAddress;
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

    public List<AdvertThemeRepresentation> getThemes() {
        return themes;
    }

    public void setThemes(List<AdvertThemeRepresentation> themes) {
        this.themes = themes;
    }

    public AdvertCategoriesRepresentation getCategories() {
        return categories;
    }

    public void setCategories(AdvertCategoriesRepresentation categories) {
        this.categories = categories;
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

    public AdvertRepresentationSimple withId(Integer id) {
        this.id = id;
        return this;
    }

    public AdvertRepresentationSimple withSummary(String summary) {
        this.summary = summary;
        return this;
    }

}
