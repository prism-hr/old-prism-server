package com.zuehlke.pgadmissions.rest.representation;

import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.representation.resource.InstitutionAddressRepresentation;

public class AdvertRepresentation {

    private Integer id;

    private String title;

    private String summary;

    private String description;

    private String applyLink;

    private InstitutionAddressRepresentation address;

    private Integer studyDurationMinimum;

    private Integer studyDurationMaximum;

    private FinancialDetailsRepresentation fee;

    private FinancialDetailsRepresentation pay;

    private LocalDate closingDate;

    private UserRepresentation user;

    private PrismScope resourceScope;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getApplyLink() {
        return applyLink;
    }

    public void setApplyLink(String applyLink) {
        this.applyLink = applyLink;
    }

    public InstitutionAddressRepresentation getAddress() {
        return address;
    }

    public void setAddress(InstitutionAddressRepresentation address) {
        this.address = address;
    }

    public Integer getStudyDurationMinimum() {
        return studyDurationMinimum;
    }

    public void setStudyDurationMinimum(Integer studyDurationMinimum) {
        this.studyDurationMinimum = studyDurationMinimum;
    }

    public Integer getStudyDurationMaximum() {
        return studyDurationMaximum;
    }

    public void setStudyDurationMaximum(Integer studyDurationMaximum) {
        this.studyDurationMaximum = studyDurationMaximum;
    }

    public FinancialDetailsRepresentation getFee() {
        return fee;
    }

    public void setFee(FinancialDetailsRepresentation fee) {
        this.fee = fee;
    }

    public FinancialDetailsRepresentation getPay() {
        return pay;
    }

    public void setPay(FinancialDetailsRepresentation pay) {
        this.pay = pay;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }

    public UserRepresentation getUser() {
        return user;
    }

    public void setUser(UserRepresentation user) {
        this.user = user;
    }

    public PrismScope getResourceScope() {
        return resourceScope;
    }

    public void setResourceScope(PrismScope resourceScope) {
        this.resourceScope = resourceScope;
    }
}
