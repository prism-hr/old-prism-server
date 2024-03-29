package uk.co.alumeni.prism.rest.representation.advert;

import org.joda.time.LocalDate;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCondition;
import uk.co.alumeni.prism.rest.representation.DocumentRepresentation;
import uk.co.alumeni.prism.rest.representation.address.AddressRepresentation;

import java.util.List;

public class AdvertRepresentationSimple {

    private Integer id;

    private String summary;

    private String description;

    private Boolean globallyVisible;

    private Boolean published;

    private DocumentRepresentation backgroundImage;

    private String homepage;

    private String applyHomepage;

    private String telephone;

    private AddressRepresentation address;

    private AddressRepresentation parentAddress;

    private AdvertFinancialDetailRepresentation financialDetail;

    private LocalDate closingDate;

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

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
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

    public AdvertFinancialDetailRepresentation getFinancialDetail() {
        return financialDetail;
    }

    public void setFinancialDetail(AdvertFinancialDetailRepresentation financialDetail) {
        this.financialDetail = financialDetail;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
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
