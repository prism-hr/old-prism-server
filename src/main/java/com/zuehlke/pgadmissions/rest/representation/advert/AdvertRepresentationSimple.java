package com.zuehlke.pgadmissions.rest.representation.advert;

import java.util.List;

import com.zuehlke.pgadmissions.rest.representation.address.AddressAdvertRepresentation;

public class AdvertRepresentationSimple {

    private Integer id;

    private String summary;

    private String description;
    
    private Integer backgroundImage;

    private String homepage;

    private String applyHomepage;

    private String telephone;

    private AddressAdvertRepresentation address;

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

    public Integer getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(Integer backgroundImage) {
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

    public AddressAdvertRepresentation getAddress() {
        return address;
    }

    public void setAddress(AddressAdvertRepresentation address) {
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

}
