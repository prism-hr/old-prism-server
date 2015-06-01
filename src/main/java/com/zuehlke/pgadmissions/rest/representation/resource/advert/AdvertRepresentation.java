package com.zuehlke.pgadmissions.rest.representation.resource.advert;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertDomain;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertFunction;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertIndustry;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.DepartmentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.InstitutionAddressRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.InstitutionAdvertRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSponsorRepresentation;

public class AdvertRepresentation {

    private Integer id;

    private String title;

    private String summary;

    private String description;

    private String homepage;

    private String applyHomepage;

    private String telephone;

    private InstitutionAddressRepresentation address;

    private String sponsorshipPurpose;

    private BigDecimal sponsorshipTarget;

    private BigDecimal sponsorshipSecured;

    private Integer sponsorCount;

    private List<ResourceSponsorRepresentation> topTenSponsors;

    private FinancialDetailsRepresentation fee;

    private FinancialDetailsRepresentation pay;

    private AdvertClosingDateRepresentation closingDate;

    private List<AdvertClosingDateRepresentation> closingDates;

    private UserRepresentation user;

    private PrismScope resourceScope;

    private Integer resourceId;

    private Set<PrismAdvertDomain> domains;

    private Set<PrismAdvertIndustry> industries;

    private Set<PrismAdvertFunction> functions;

    private List<String> competencies;

    private List<String> themes;

    private PrismOpportunityType opportunityType;

    private List<PrismStudyOption> studyOptions;

    private List<PrismAction> partnerActions;

    private List<String> locations;

    private Integer backgroundImage;

    private InstitutionAdvertRepresentation institution;

    private InstitutionAdvertRepresentation partner;

    private DepartmentRepresentation department;

    private String sequenceIdentifier;

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

    public String getSponsorshipPurpose() {
        return sponsorshipPurpose;
    }

    public void setSponsorshipPurpose(String sponsorshipPurpose) {
        this.sponsorshipPurpose = sponsorshipPurpose;
    }

    public BigDecimal getSponsorshipTarget() {
        return sponsorshipTarget;
    }

    public void setSponsorshipTarget(BigDecimal sponsorshipTarget) {
        this.sponsorshipTarget = sponsorshipTarget;
    }

    public BigDecimal getSponsorshipSecured() {
        return sponsorshipSecured;
    }

    public void setSponsorshipSecured(BigDecimal sponsorshipSecured) {
        this.sponsorshipSecured = sponsorshipSecured;
    }

    public Integer getSponsorCount() {
        return sponsorCount;
    }

    public void setSponsorCount(Integer sponsorCount) {
        this.sponsorCount = sponsorCount;
    }

    public List<ResourceSponsorRepresentation> getTopTenSponsors() {
        return topTenSponsors;
    }

    public void setTopTenSponsors(List<ResourceSponsorRepresentation> topTenSponsors) {
        this.topTenSponsors = topTenSponsors;
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

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public Set<PrismAdvertDomain> getDomains() {
        return domains;
    }

    public void setDomains(Set<PrismAdvertDomain> domains) {
        this.domains = domains;
    }

    public Set<PrismAdvertIndustry> getIndustries() {
        return industries;
    }

    public void setIndustries(Set<PrismAdvertIndustry> industries) {
        this.industries = industries;
    }

    public Set<PrismAdvertFunction> getFunctions() {
        return functions;
    }

    public void setFunctions(Set<PrismAdvertFunction> functions) {
        this.functions = functions;
    }

    public List<String> getCompetencies() {
        return competencies;
    }

    public void setCompetencies(List<String> competencies) {
        this.competencies = competencies;
    }

    public List<String> getThemes() {
        return themes;
    }

    public void setThemes(List<String> themes) {
        this.themes = themes;
    }

    public PrismOpportunityType getOpportunityType() {
        return opportunityType;
    }

    public void setOpportunityType(PrismOpportunityType opportunityType) {
        this.opportunityType = opportunityType;
    }

    public List<PrismStudyOption> getStudyOptions() {
        return studyOptions;
    }

    public void setStudyOptions(List<PrismStudyOption> studyOptions) {
        this.studyOptions = studyOptions;
    }

    public List<PrismAction> getPartnerActions() {
        return partnerActions;
    }

    public void setPartnerActions(List<PrismAction> partnerActions) {
        this.partnerActions = partnerActions;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public InstitutionAdvertRepresentation getInstitution() {
        return institution;
    }

    public void setInstitution(InstitutionAdvertRepresentation institution) {
        this.institution = institution;
    }

    public InstitutionAdvertRepresentation getPartner() {
        return partner;
    }

    public void setPartner(InstitutionAdvertRepresentation partner) {
        this.partner = partner;
    }

    public Integer getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(Integer backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public DepartmentRepresentation getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentRepresentation department) {
        this.department = department;
    }

    public String getSequenceIdentifier() {
        return sequenceIdentifier;
    }

    public void setSequenceIdentifier(String sequenceIdentifier) {
        this.sequenceIdentifier = sequenceIdentifier;
    }

}
