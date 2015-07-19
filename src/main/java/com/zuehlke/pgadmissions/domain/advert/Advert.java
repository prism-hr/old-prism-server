package com.zuehlke.pgadmissions.domain.advert;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.address.AddressAdvert;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.resource.*;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "advert")
public class Advert extends ResourceOpportunityAttribute {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "summary")
    private String summary;

    @Lob
    @Column(name = "description")
    private String description;

    @OneToOne
    @JoinColumn(name = "background_image_id")
    private Document backgroundImage;

    @Column(name = "homepage")
    private String homepage;

    @Column(name = "apply_homepage")
    private String applyHomepage;

    @Column(name = "telephone")
    private String telephone;

    @OneToOne
    @JoinColumn(name = "advert_address_id")
    private AddressAdvert address;

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

    @OneToOne(mappedBy = "advert")
    private Institution institution;

    @OneToOne(mappedBy = "advert")
    private Department department;

    @OneToOne(mappedBy = "advert")
    private Program program;

    @OneToOne(mappedBy = "advert")
    private Project project;

    @Embedded
    private AdvertCategories categories;

    @Embedded
    private AdvertTargets targets;

    @OrderBy(clause = "closing_date desc")
    @OneToMany(mappedBy = "advert")
    private Set<AdvertClosingDate> closingDates = Sets.newHashSet();

    @OrderBy(clause = "sequence_identifier desc")
    @OneToMany(mappedBy = "advert")
    private Set<Application> applications = Sets.newHashSet();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Document getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(Document backgroundImage) {
        this.backgroundImage = backgroundImage;
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

    public AddressAdvert getAddress() {
        return address;
    }

    public void setAddress(AddressAdvert address) {
        this.address = address;
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

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
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

    public AdvertCategories getCategories() {
        return categories;
    }

    public void setCategories(AdvertCategories categories) {
        this.categories = categories;
    }

    public AdvertTargets getTargets() {
        return targets;
    }

    public void setTargets(AdvertTargets targets) {
        this.targets = targets;
    }

    public Set<AdvertClosingDate> getClosingDates() {
        return closingDates;
    }

    public boolean isImported() {
        ResourceOpportunity resource = getResourceOpportunity();
        return resource == null ? false : resource.getImportedCode() != null;
    }

    public Advert withName(String name) {
        this.name = name;
        return this;
    }

    public ResourceParent getResource() {
        return ObjectUtils.firstNonNull(institution, department, getResourceOpportunity());
    }

    public ResourceOpportunity getResourceOpportunity() {
        return ObjectUtils.firstNonNull(program, project);
    }

    public boolean isAdvertOfScope(PrismScope scope) {
        return getResource().getResourceScope().equals(scope);
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

}
