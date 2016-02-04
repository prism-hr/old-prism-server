package com.zuehlke.pgadmissions.domain.advert;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;

import java.math.BigDecimal;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertDomain;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertFunction;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertIndustry;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.department.Department;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.institution.InstitutionAddress;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.ResourceParentAttribute;

@Entity
@Table(name = "ADVERT")
public class Advert extends ResourceParentAttribute {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "summary")
    private String summary;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "homepage")
    private String homepage;

    @Column(name = "apply_homepage")
    private String applyHomepage;
    
    @Column(name = "telephone")
    private String telephone;

    @OneToOne
    @JoinColumn(name = "institution_address_id")
    private InstitutionAddress address;

    @Column(name = "sponsorship_purpose")
    private String sponsorshipPurpose;
    
    @Column(name = "sponsorship_target")
    private BigDecimal sponsorshipTarget;

    @Column(name = "sponsorship_secured")
    private BigDecimal sponsorshipSecured;

    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "interval", column = @Column(name = "fee_interval")),
            @AttributeOverride(name = "currencySpecified", column = @Column(name = "fee_currency_specified")),
            @AttributeOverride(name = "currencyAtLocale", column = @Column(name = "fee_currency_at_locale")),
            @AttributeOverride(name = "monthMinimumSpecified", column = @Column(name = "month_fee_minimum_specified")),
            @AttributeOverride(name = "monthMaximumSpecified", column = @Column(name = "month_fee_maximum_specified")),
            @AttributeOverride(name = "yearMinimumSpecified", column = @Column(name = "year_fee_minimum_specified")),
            @AttributeOverride(name = "yearMaximumSpecified", column = @Column(name = "year_fee_maximum_specified")),
            @AttributeOverride(name = "monthMinimumAtLocale", column = @Column(name = "month_fee_minimum_at_locale")),
            @AttributeOverride(name = "monthMaximumAtLocale", column = @Column(name = "month_fee_maximum_at_locale")),
            @AttributeOverride(name = "yearMinimumAtLocale", column = @Column(name = "year_fee_minimum_at_locale")),
            @AttributeOverride(name = "yearMaximumAtLocale", column = @Column(name = "year_fee_maximum_at_locale")),
            @AttributeOverride(name = "converted", column = @Column(name = "fee_converted")) })
    private AdvertFinancialDetail fee;

    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "interval", column = @Column(name = "pay_interval")),
            @AttributeOverride(name = "currencySpecified", column = @Column(name = "pay_currency_specified")),
            @AttributeOverride(name = "currencyAtLocale", column = @Column(name = "pay_currency_at_locale")),
            @AttributeOverride(name = "monthMinimumSpecified", column = @Column(name = "month_pay_minimum_specified")),
            @AttributeOverride(name = "monthMaximumSpecified", column = @Column(name = "month_pay_maximum_specified")),
            @AttributeOverride(name = "yearMinimumSpecified", column = @Column(name = "year_pay_minimum_specified")),
            @AttributeOverride(name = "yearMaximumSpecified", column = @Column(name = "year_pay_maximum_specified")),
            @AttributeOverride(name = "monthMinimumAtLocale", column = @Column(name = "month_pay_minimum_at_locale")),
            @AttributeOverride(name = "monthMaximumAtLocale", column = @Column(name = "month_pay_maximum_at_locale")),
            @AttributeOverride(name = "yearMinimumAtLocale", column = @Column(name = "year_pay_minimum_at_locale")),
            @AttributeOverride(name = "yearMaximumAtLocale", column = @Column(name = "year_pay_maximum_at_locale")),
            @AttributeOverride(name = "converted", column = @Column(name = "pay_converted")) })
    private AdvertFinancialDetail pay;

    @OneToOne
    @JoinColumn(name = "advert_closing_date_id", unique = true)
    private AdvertClosingDate closingDate;

    @Column(name = "last_currency_conversion_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate lastCurrencyConversionDate;

    @Column(name = "sequence_identifier", unique = true)
    private String sequenceIdentifier;

    @OrderBy(clause = "domain")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "advert_id", nullable = false)
    private Set<AdvertDomain> domains = Sets.newHashSet();

    @OrderBy(clause = "function")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "advert_id", nullable = false)
    private Set<AdvertFunction> functions = Sets.newHashSet();

    @OrderBy(clause = "industry")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "advert_id", nullable = false)
    private Set<AdvertIndustry> industries = Sets.newHashSet();

    @OrderBy(clause = "competency")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "advert_id", nullable = false)
    private Set<AdvertCompetency> competencies = Sets.newHashSet();

    @OrderBy(clause = "theme")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "advert_id", nullable = false)
    private Set<AdvertTheme> themes = Sets.newHashSet();

    @OneToOne(mappedBy = "advert")
    private Institution institution;

    @OneToOne(mappedBy = "advert")
    private Program program;

    @OneToOne(mappedBy = "advert")
    private Project project;

    @OrderBy(clause = "sequence_identifier desc")
    @OneToMany(mappedBy = "advert")
    private Set<Application> applications = Sets.newHashSet();

    @OrderBy(clause = "closing_date desc")
    @OneToMany(mappedBy = "advert")
    private Set<AdvertClosingDate> closingDates = Sets.newHashSet();

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

    public final String getHomepage() {
        return homepage;
    }

    public final void setHomepage(String homepage) {
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

    public InstitutionAddress getAddress() {
        return address;
    }

    public void setAddress(InstitutionAddress address) {
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

    public AdvertFinancialDetail getFee() {
        return fee;
    }

    public void setFee(AdvertFinancialDetail fee) {
        this.fee = fee;
    }

    public AdvertFinancialDetail getPay() {
        return pay;
    }

    public void setPay(AdvertFinancialDetail pay) {
        this.pay = pay;
    }

    public final LocalDate getLastCurrencyConversionDate() {
        return lastCurrencyConversionDate;
    }

    public final void setLastCurrencyConversionDate(LocalDate lastCurrencyConversionDate) {
        this.lastCurrencyConversionDate = lastCurrencyConversionDate;
    }

    public AdvertClosingDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(AdvertClosingDate closingDate) {
        this.closingDate = closingDate;
    }

    public String getSequenceIdentifier() {
        return sequenceIdentifier;
    }

    public void setSequenceIdentifier(String sequenceIdentifier) {
        this.sequenceIdentifier = sequenceIdentifier;
    }

    public final Set<AdvertDomain> getDomains() {
        return domains;
    }

    public final Set<AdvertIndustry> getIndustries() {
        return industries;
    }

    public final Set<AdvertFunction> getFunctions() {
        return functions;
    }

    public final Set<AdvertCompetency> getCompetencies() {
        return competencies;
    }

    public final Set<AdvertTheme> getThemes() {
        return themes;
    }

    @Override
    public Institution getInstitution() {
        return institution;
    }

    @Override
    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    @Override
    public Program getProgram() {
        return program;
    }

    @Override
    public void setProgram(Program program) {
        this.program = program;
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public void setProject(Project project) {
        this.project = project;
    }

    public Set<AdvertClosingDate> getClosingDates() {
        return closingDates;
    }

    public PrismOpportunityType getOpportunityType() {
        ResourceParent resource = getResource();
        PrismScope resourceScope = resource.getResourceScope();
        if (resourceScope.equals(INSTITUTION)) {
            return null;
        }
        ResourceOpportunity opportunity = (ResourceOpportunity) resource;
        return opportunity.getOpportunityType().getPrismOpportunityType();
    }

    public Boolean getImported() {
        return program != null && program.getImported();
    }

    public Advert withTitle(String title) {
        this.title = title;
        return this;
    }

    public ResourceParent getResource() {
        return ObjectUtils.firstNonNull(project, program, institution);
    }

    public boolean isAdvertOfScope(PrismScope scope) {
        return getResource().getResourceScope().equals(scope);
    }

    public Department getDepartment() {
        return getResource().getDepartment();
    }

    public boolean hasConvertedFee() {
        return fee != null && !fee.getCurrencySpecified().equals(fee.getCurrencyAtLocale());
    }

    public boolean hasConvertedPay() {
        return pay != null && !pay.getCurrencySpecified().equals(pay.getCurrencyAtLocale());
    }

    public void addClosingDate(AdvertClosingDate closingDate) {
        closingDates.add(closingDate);
    }

    public void addDomain(PrismAdvertDomain domainId) {
        AdvertDomain domain = new AdvertDomain().withAdvert(this);
        domain.setDomain(domainId);
        domains.add(domain);
    }

    public void addFunction(PrismAdvertFunction functionId) {
        AdvertFunction function = new AdvertFunction().withAdvert(this);
        function.setFunction(functionId);
        functions.add(function);
    }

    public void addIndustry(PrismAdvertIndustry industryId) {
        AdvertIndustry industry = new AdvertIndustry().withAdvert(this);
        industry.setIndustry(industryId);
        industries.add(industry);
    }

    public void addCompetency(String competencyId) {
        AdvertCompetency competency = new AdvertCompetency().withAdvert(this);
        competency.setCompetency(competencyId);
        competencies.add(competency);
    }

    public void addTheme(String themeId) {
        AdvertTheme theme = new AdvertTheme().withAdvert(this);
        theme.setTheme(themeId);
        themes.add(theme);
    }

}
