package uk.co.alumeni.prism.dto;

import org.joda.time.LocalDate;
import uk.co.alumeni.prism.domain.definitions.PrismDomicile;
import uk.co.alumeni.prism.domain.definitions.PrismDurationUnit;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityType;
import uk.co.alumeni.prism.domain.definitions.PrismPaymentOption;

import java.math.BigDecimal;

public class AdvertDTO extends ResourceFlatToNestedDTO {

    private Integer advertId;

    private String userFirstName;

    private String userLastName;

    private String userAccountProfileUrl;

    private String userAccountImageUrl;

    private PrismOpportunityType opportunityType;

    private String opportunityCategories;

    private String studyOptions;

    private Boolean recommended;

    private String name;

    private String summary;

    private String description;

    private Boolean globallyVisible;

    private Boolean published;

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

    private Integer durationMinimum;

    private Integer durationMaximum;

    private PrismPaymentOption payOption;

    private PrismDurationUnit payInterval;

    private Integer payHoursWeekMinimum;

    private Integer payHoursWeekMaximum;

    private String payCurrency;

    private BigDecimal payMinimum;

    private BigDecimal payMaximum;

    private String payBenefit;

    private String payBenefitDescription;

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

    public PrismOpportunityType getOpportunityType() {
        return opportunityType;
    }

    public void setOpportunityType(PrismOpportunityType opportunityType) {
        this.opportunityType = opportunityType;
    }

    public String getOpportunityCategories() {
        return opportunityCategories;
    }

    public void setOpportunityCategories(String opportunityCategories) {
        this.opportunityCategories = opportunityCategories;
    }

    public String getStudyOptions() {
        return studyOptions;
    }

    public void setStudyOptions(String studyOptions) {
        this.studyOptions = studyOptions;
    }

    public Boolean getRecommended() {
        return recommended;
    }

    public void setRecommended(Boolean recommended) {
        this.recommended = recommended;
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

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
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

    public Integer getDurationMinimum() {
        return durationMinimum;
    }

    public void setDurationMinimum(Integer durationMinimum) {
        this.durationMinimum = durationMinimum;
    }

    public Integer getDurationMaximum() {
        return durationMaximum;
    }

    public void setDurationMaximum(Integer durationMaximum) {
        this.durationMaximum = durationMaximum;
    }

    public PrismPaymentOption getPayOption() {
        return payOption;
    }

    public void setPayOption(PrismPaymentOption option) {
        this.payOption = option;
    }

    public PrismDurationUnit getPayInterval() {
        return payInterval;
    }

    public void setPayInterval(PrismDurationUnit payInterval) {
        this.payInterval = payInterval;
    }

    public Integer getPayHoursWeekMinimum() {
        return payHoursWeekMinimum;
    }

    public void setPayHoursWeekMinimum(Integer payHoursWeekMinimum) {
        this.payHoursWeekMinimum = payHoursWeekMinimum;
    }

    public Integer getPayHoursWeekMaximum() {
        return payHoursWeekMaximum;
    }

    public void setPayHoursWeekMaximum(Integer payHoursWeekMaximum) {
        this.payHoursWeekMaximum = payHoursWeekMaximum;
    }

    public String getPayCurrency() {
        return payCurrency;
    }

    public void setPayCurrency(String payCurrency) {
        this.payCurrency = payCurrency;
    }

    public BigDecimal getPayMinimum() {
        return payMinimum;
    }

    public void setPayMinimum(BigDecimal payMinimum) {
        this.payMinimum = payMinimum;
    }

    public BigDecimal getPayMaximum() {
        return payMaximum;
    }

    public void setPayMaximum(BigDecimal payMaximum) {
        this.payMaximum = payMaximum;
    }

    public String getPayBenefit() {
        return payBenefit;
    }

    public void setPayBenefit(String payBenefit) {
        this.payBenefit = payBenefit;
    }

    public String getPayBenefitDescription() {
        return payBenefitDescription;
    }

    public void setPayBenefitDescription(String payBenefitDescription) {
        this.payBenefitDescription = payBenefitDescription;
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
