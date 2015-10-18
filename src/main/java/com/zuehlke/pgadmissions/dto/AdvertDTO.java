package com.zuehlke.pgadmissions.dto;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.definitions.PrismDomicile;
import com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;

public class AdvertDTO extends ResourceActivityDTO {

    private Integer advertId;

    private String userFirstName;

    private String userLastName;

    private String userAccountProfileUrl;

    private String userAccountImageUrl;

    private LocalDate programAvailableDate;

    private Integer programDurationMinimum;

    private Integer programDurationMaximum;

    private LocalDate projectAvailableDate;

    private Integer projectDurationMinimum;

    private Integer projectDurationMaximum;

    private PrismOpportunityType opportunityType;

    private String targetOpportunityTypes;

    private String name;

    private String summary;

    private String description;

    private Boolean globallyVisible;

    private String homepage;

    private String applyHomepage;

    private String telephone;

    private String addressLine1;

    private String addressLine2;

    private String addressTown;

    private String addressRegion;

    private String addressCode;

    private PrismDomicile addressDomicileId;

    private String addressGoogleId;

    private BigDecimal addressCoordinateLatitude;

    private BigDecimal addressCoordinateLongitude;

    private String payCurrency;

    private PrismDurationUnit payInterval;

    private BigDecimal payMonthMinimum;

    private BigDecimal payMonthMaximum;

    private BigDecimal payYearMinimum;

    private BigDecimal payYearMaximum;

    private LocalDate closingDate;

    private Long applicationCount;

    private Long applicationRatingCount;

    private Double applicationRatingAverage;

    private String sequenceIdentifier;

    public Integer getAdvertId() {
        return advertId;
    }

    public void setAdvertId(Integer advertId) {
        this.advertId = advertId;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserAccountProfileUrl() {
        return userAccountProfileUrl;
    }

    public void setUserAccountProfileUrl(String userAccountProfileUrl) {
        this.userAccountProfileUrl = userAccountProfileUrl;
    }

    public String getUserAccountImageUrl() {
        return userAccountImageUrl;
    }

    public void setUserAccountImageUrl(String userAccountImageUrl) {
        this.userAccountImageUrl = userAccountImageUrl;
    }

    public LocalDate getProgramAvailableDate() {
        return programAvailableDate;
    }

    public void setProgramAvailableDate(LocalDate programAvailableDate) {
        this.programAvailableDate = programAvailableDate;
    }

    public Integer getProgramDurationMinimum() {
        return programDurationMinimum;
    }

    public void setProgramDurationMinimum(Integer programDurationMinimum) {
        this.programDurationMinimum = programDurationMinimum;
    }

    public Integer getProgramDurationMaximum() {
        return programDurationMaximum;
    }

    public void setProgramDurationMaximum(Integer programDurationMaximum) {
        this.programDurationMaximum = programDurationMaximum;
    }

    public LocalDate getProjectAvailableDate() {
        return projectAvailableDate;
    }

    public void setProjectAvailableDate(LocalDate projectAvailableDate) {
        this.projectAvailableDate = projectAvailableDate;
    }

    public Integer getProjectDurationMinimum() {
        return projectDurationMinimum;
    }

    public void setProjectDurationMinimum(Integer projectDurationMinimum) {
        this.projectDurationMinimum = projectDurationMinimum;
    }

    public Integer getProjectDurationMaximum() {
        return projectDurationMaximum;
    }

    public void setProjectDurationMaximum(Integer projectDurationMaximum) {
        this.projectDurationMaximum = projectDurationMaximum;
    }

    public PrismOpportunityType getOpportunityType() {
        return opportunityType;
    }

    public void setOpportunityType(PrismOpportunityType opportunityType) {
        this.opportunityType = opportunityType;
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

    public Boolean getGloballyVisible() {
        return globallyVisible;
    }

    public void setGloballyVisible(Boolean globallyVisible) {
        this.globallyVisible = globallyVisible;
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

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressTown() {
        return addressTown;
    }

    public void setAddressTown(String addressTown) {
        this.addressTown = addressTown;
    }

    public String getAddressRegion() {
        return addressRegion;
    }

    public void setAddressRegion(String addressRegion) {
        this.addressRegion = addressRegion;
    }

    public String getAddressCode() {
        return addressCode;
    }

    public void setAddressCode(String addressCode) {
        this.addressCode = addressCode;
    }

    public PrismDomicile getAddressDomicileId() {
        return addressDomicileId;
    }

    public void setAddressDomicileId(PrismDomicile addressDomicileId) {
        this.addressDomicileId = addressDomicileId;
    }

    public String getAddressGoogleId() {
        return addressGoogleId;
    }

    public void setAddressGoogleId(String addressGoogleId) {
        this.addressGoogleId = addressGoogleId;
    }

    public BigDecimal getAddressCoordinateLatitude() {
        return addressCoordinateLatitude;
    }

    public void setAddressCoordinateLatitude(BigDecimal addressCoordinateLatitude) {
        this.addressCoordinateLatitude = addressCoordinateLatitude;
    }

    public BigDecimal getAddressCoordinateLongitude() {
        return addressCoordinateLongitude;
    }

    public void setAddressCoordinateLongitude(BigDecimal addressCoordinateLongitude) {
        this.addressCoordinateLongitude = addressCoordinateLongitude;
    }

    public String getPayCurrency() {
        return payCurrency;
    }

    public void setPayCurrency(String payCurrency) {
        this.payCurrency = payCurrency;
    }

    public PrismDurationUnit getPayInterval() {
        return payInterval;
    }

    public void setPayInterval(PrismDurationUnit payInterval) {
        this.payInterval = payInterval;
    }

    public BigDecimal getPayMonthMinimum() {
        return payMonthMinimum;
    }

    public void setPayMonthMinimum(BigDecimal payMonthMinimum) {
        this.payMonthMinimum = payMonthMinimum;
    }

    public BigDecimal getPayMonthMaximum() {
        return payMonthMaximum;
    }

    public void setPayMonthMaximum(BigDecimal payMonthMaximum) {
        this.payMonthMaximum = payMonthMaximum;
    }

    public BigDecimal getPayYearMinimum() {
        return payYearMinimum;
    }

    public void setPayYearMinimum(BigDecimal payYearMinimum) {
        this.payYearMinimum = payYearMinimum;
    }

    public BigDecimal getPayYearMaximum() {
        return payYearMaximum;
    }

    public void setPayYearMaximum(BigDecimal payYearMaximum) {
        this.payYearMaximum = payYearMaximum;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }

    public Long getApplicationCount() {
        return applicationCount;
    }

    public void setApplicationCount(Long applicationCount) {
        this.applicationCount = applicationCount;
    }

    public Long getApplicationRatingCount() {
        return applicationRatingCount;
    }

    public void setApplicationRatingCount(Long applicationRatingCount) {
        this.applicationRatingCount = applicationRatingCount;
    }

    public Double getApplicationRatingAverage() {
        return applicationRatingAverage;
    }

    public void setApplicationRatingAverage(Double applicationRatingAverage) {
        this.applicationRatingAverage = applicationRatingAverage;
    }

    public String getSequenceIdentifier() {
        return sequenceIdentifier;
    }

    public void setSequenceIdentifier(String sequenceIdentifier) {
        this.sequenceIdentifier = sequenceIdentifier;
    }

}
