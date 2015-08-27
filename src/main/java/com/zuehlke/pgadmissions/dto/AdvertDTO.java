package com.zuehlke.pgadmissions.dto;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit;

public class AdvertDTO {

    private Integer id;

    private String userFirstName;

    private String userLastName;

    private String userAccountProfileUrl;

    private String userAccountImageUrl;

    private Integer institutionId;

    private String institutionName;

    private Integer institutionLogoImageId;

    private Integer departmentId;

    private String departmentName;

    private Integer programId;

    private String programName;

    private Integer projectId;

    private String projectName;

    private String opportunityType;
    
    private String opportunityCategory;

    private String name;

    private String summary;

    private String description;

    private Integer backgroundImageId;

    private String homepage;

    private String applyHomepage;

    private String telephone;

    private String addressLine1;

    private String addressLine2;

    private String addressTown;

    private String addressRegion;

    private String addressCode;

    private String addressDomicileId;

    private String addressDomicileName;

    private String addressGoogleId;

    private BigDecimal addresCoordinateLatitude;

    private BigDecimal addressCoordinateLongitude;

    private String feeCurrency;

    private PrismDurationUnit feeInterval;

    private BigDecimal feeMonthMinimum;

    private BigDecimal feeMonthMaximum;

    private BigDecimal feeYearMinimum;

    private BigDecimal feeYearMaximum;

    private String payCurrency;

    private PrismDurationUnit payInterval;

    private BigDecimal payMonthMinimum;

    private BigDecimal payMonthMaximum;

    private BigDecimal payYearMinimum;

    private BigDecimal payYearMaximum;

    private LocalDate closingDate;

    private String sequenceIdentifier;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Integer institutionId) {
        this.institutionId = institutionId;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public Integer getInstitutionLogoImageId() {
        return institutionLogoImageId;
    }

    public void setInstitutionLogoImageId(Integer institutionLogoImageId) {
        this.institutionLogoImageId = institutionLogoImageId;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getOpportunityType() {
        return opportunityType;
    }

    public void setOpportunityType(String opportunityType) {
        this.opportunityType = opportunityType;
    }

    public String getOpportunityCategory() {
        return opportunityCategory;
    }

    public void setOpportunityCategory(String opportunityCategory) {
        this.opportunityCategory = opportunityCategory;
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

    public Integer getBackgroundImageId() {
        return backgroundImageId;
    }

    public void setBackgroundImageId(Integer backgroundImageId) {
        this.backgroundImageId = backgroundImageId;
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

    public String getAddressDomicileId() {
        return addressDomicileId;
    }

    public void setAddressDomicileId(String addressDomicileId) {
        this.addressDomicileId = addressDomicileId;
    }

    public String getAddressDomicileName() {
        return addressDomicileName;
    }

    public void setAddressDomicileName(String addressDomicileName) {
        this.addressDomicileName = addressDomicileName;
    }

    public String getAddressGoogleId() {
        return addressGoogleId;
    }

    public void setAddressGoogleId(String addressGoogleId) {
        this.addressGoogleId = addressGoogleId;
    }

    public BigDecimal getAddresCoordinateLatitude() {
        return addresCoordinateLatitude;
    }

    public void setAddresCoordinateLatitude(BigDecimal addressCoordinateLatitude) {
        this.addresCoordinateLatitude = addressCoordinateLatitude;
    }

    public BigDecimal getAddressCoordinateLongitude() {
        return addressCoordinateLongitude;
    }

    public void setAddressCoordinateLongitude(BigDecimal addressCoordinateLongitude) {
        this.addressCoordinateLongitude = addressCoordinateLongitude;
    }

    public String getFeeCurrency() {
        return feeCurrency;
    }

    public void setFeeCurrency(String feeCurrency) {
        this.feeCurrency = feeCurrency;
    }

    public PrismDurationUnit getFeeInterval() {
        return feeInterval;
    }

    public void setFeeInterval(PrismDurationUnit feeInterval) {
        this.feeInterval = feeInterval;
    }

    public BigDecimal getFeeMonthMinimum() {
        return feeMonthMinimum;
    }

    public void setFeeMonthMinimum(BigDecimal feeMonthMinimum) {
        this.feeMonthMinimum = feeMonthMinimum;
    }

    public BigDecimal getFeeMonthMaximum() {
        return feeMonthMaximum;
    }

    public void setFeeMonthMaximum(BigDecimal feeMonthMaximum) {
        this.feeMonthMaximum = feeMonthMaximum;
    }

    public BigDecimal getFeeYearMinimum() {
        return feeYearMinimum;
    }

    public void setFeeYearMinimum(BigDecimal feeYearMinimum) {
        this.feeYearMinimum = feeYearMinimum;
    }

    public BigDecimal getFeeYearMaximum() {
        return feeYearMaximum;
    }

    public void setFeeYearMaximum(BigDecimal feeYearMaximum) {
        this.feeYearMaximum = feeYearMaximum;
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

    public String getSequenceIdentifier() {
        return sequenceIdentifier;
    }

    public void setSequenceIdentifier(String sequenceIdentifier) {
        this.sequenceIdentifier = sequenceIdentifier;
    }

}
