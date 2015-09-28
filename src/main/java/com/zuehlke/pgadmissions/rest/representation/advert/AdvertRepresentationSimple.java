package com.zuehlke.pgadmissions.rest.representation.advert;

import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.address.AddressRepresentation;

import java.util.List;

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

    private AdvertFinancialDetailRepresentation pay;

    private AdvertClosingDateRepresentation closingDate;

    private List<AdvertClosingDateRepresentation> closingDates;

    private AdvertCategoriesRepresentation categories;

    private List<AdvertTargetRepresentation> connections;

    private List<AdvertCompetenceRepresentation> competences;

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

    public AdvertFinancialDetailRepresentation getPay() {
        return pay;
    }

    public void setPay(AdvertFinancialDetailRepresentation pay) {
        this.pay = pay;
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

    public List<AdvertTargetRepresentation> getConnections() {
        return connections;
    }

    public void setConnections(List<AdvertTargetRepresentation> connections) {
        this.connections = connections;
    }

    public List<AdvertCompetenceRepresentation> getCompetences() {
        return competences;
    }

    public void setCompetences(List<AdvertCompetenceRepresentation> competences) {
        this.competences = competences;
    }

    public String getSequenceIdentifier() {
        return sequenceIdentifier;
    }

    public void setSequenceIdentifier(String sequenceIdentifier) {
        this.sequenceIdentifier = sequenceIdentifier;
    }

}
