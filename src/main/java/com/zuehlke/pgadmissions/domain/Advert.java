package com.zuehlke.pgadmissions.domain;

import java.math.BigInteger;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.solr.analysis.ASCIIFoldingFilterFactory;
import org.apache.solr.analysis.LowerCaseFilterFactory;
import org.apache.solr.analysis.SnowballPorterFilterFactory;
import org.apache.solr.analysis.StandardTokenizerFactory;
import org.apache.solr.analysis.StopFilterFactory;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.DurationUnit;

@AnalyzerDef(name = "advertAnalyzer", tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class), filters = {
        @TokenFilterDef(factory = LowerCaseFilterFactory.class), @TokenFilterDef(factory = StopFilterFactory.class),
        @TokenFilterDef(factory = SnowballPorterFilterFactory.class, params = @Parameter(name = "language", value = "English")),
        @TokenFilterDef(factory = ASCIIFoldingFilterFactory.class) })
@Entity
@Table(name = "ADVERT")
@Indexed
public class Advert {

    @Id
    @GeneratedValue
    private Integer id;
    
    @Column(name = "title", nullable = false)
    @Field(analyzer = @Analyzer(definition = "advertAnalyzer"), index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String title;
    
    @Column(name = "summary")
    @Field(analyzer = @Analyzer(definition = "advertAnalyzer"), index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String summary;

    @Column(name = "description")
    @Field(analyzer = @Analyzer(definition = "advertAnalyzer"), index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String description;

    @Column(name = "apply_link")
    private String applyLink;

    @ManyToOne
    @JoinColumn(name = "institution_address_id")
    private InstitutionAddress address;
    
    @Column(name = "month_study_duration_minimum")
    private Integer studyDurationMinimum;
    
    @Column(name = "month_study_duration_maximum")
    private Integer studyDurationMaximum;
    
    @Column(name = "currency")
    private String currency;
    
    @Column(name = "currency_at_locale")
    private String currencyAtLocale;
    
    @Column(name = "fee_interval")
    @Enumerated(EnumType.STRING)
    private DurationUnit feeInterval;
    
    @Column(name = "month_fee_minimum_specified")
    private BigInteger monthFeeMinimumSpecified;
    
    @Column(name = "month_fee_maximum_specified")
    private BigInteger monthFeeMaximumSpecified;
    
    @Column(name = "year_fee_minimum_specified")
    private BigInteger yearFeeMinimumSpecified;
    
    @Column(name = "year_fee_maximum_specified")
    private BigInteger yearFeeMaximumSpecified;
    
    @Column(name = "month_fee_minimum_at_locale")
    private BigInteger monthFeeMinimumAtLocale;
    
    @Column(name = "month_fee_maximum_at_locale")
    private BigInteger monthFeeMaximumAtLocale;
    
    @Column(name = "year_fee_minimum_at_locale")
    private BigInteger yearFeeMinimumAtLocale;
    
    @Column(name = "year_fee_maximum_at_locale")
    private BigInteger yearFeeMaximumAtLocale;
    
    @Column(name = "pay_interval")
    @Enumerated(EnumType.STRING)
    private DurationUnit payInterval;
    
    @Column(name = "month_pay_minimum_specified")
    private BigInteger monthPayMinimumSpecified;
    
    @Column(name = "month_pay_maximum_specified")
    private BigInteger monthPayMaximumSpecified;
    
    @Column(name = "year_pay_minimum_specified")
    private BigInteger yearPayMinimumSpecified;
    
    @Column(name = "year_pay_maximum_specified")
    private BigInteger yearPayMaximumSpecified;
    
    @Column(name = "month_pay_minimum_at_locale")
    private BigInteger monthPayMinimumAtLocale;
    
    @Column(name = "month_pay_maximum_at_locale")
    private BigInteger monthPayMaximumAtLocale;
    
    @Column(name = "year_pay_minimum_at_locale")
    private BigInteger yearPayMinimumAtLocale;
    
    @Column(name = "year_pay_maximum_at_locale")
    private BigInteger yearPayMaximumAtLocale;
    
    @OneToOne
    @JoinColumn(name = "advert_closing_date_id", unique = true)
    private AdvertClosingDate closingDate;
    
    @Column(name = "sequence_identifier")
    private String sequenceIdentifier;

    @OneToOne(mappedBy = "advert")
    private Program program;
    
    @OneToOne(mappedBy = "advert")
    private Project project;
    
    @OneToMany(mappedBy = "advert")
    private Set<Application> applications = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "advert_id", nullable = false)
    private Set<AdvertClosingDate> closingDates = Sets.newHashSet();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "ADVERT_CATEGORY", joinColumns = @JoinColumn(name = "advert_id", nullable = false), inverseJoinColumns = @JoinColumn(name = "advert_opportunity_category_id", nullable = false))
    private Set<OpportunityCategory> categories = Sets.newHashSet();

    @OneToMany(mappedBy = "advert")
    private Set<AdvertRecruitmentPreference> preferences = Sets.newHashSet();

    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    public final String getTitle() {
        return title;
    }

    public final void setTitle(String title) {
        this.title = title;
    }
    
    public final String getSummary() {
        return summary;
    }

    public final void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public final String getApplyLink() {
        return applyLink;
    }

    public final void setApplyLink(String applyLink) {
        this.applyLink = applyLink;
    }

    public InstitutionAddress getAddress() {
        return address;
    }

    public void setAddress(InstitutionAddress address) {
        this.address = address;
    }

    public final Integer getStudyDurationMinimum() {
        return studyDurationMinimum;
    }

    public final void setStudyDurationMinimum(Integer studyDurationMinimum) {
        this.studyDurationMinimum = studyDurationMinimum;
    }

    public final Integer getStudyDurationMaximum() {
        return studyDurationMaximum;
    }

    public final void setStudyDurationMaximum(Integer studyDurationMaximum) {
        this.studyDurationMaximum = studyDurationMaximum;
    }

    public final String getCurrency() {
        return currency;
    }

    public final void setCurrency(String currency) {
        this.currency = currency;
    }

    public final String getCurrencyAtLocale() {
        return currencyAtLocale;
    }

    public final void setCurrencyAtLocale(String currencyAtLocale) {
        this.currencyAtLocale = currencyAtLocale;
    }

    public final DurationUnit getFeeInterval() {
        return feeInterval;
    }

    public final void setFeeInterval(DurationUnit feeInterval) {
        this.feeInterval = feeInterval;
    }

    public final BigInteger getMonthFeeMinimumSpecified() {
        return monthFeeMinimumSpecified;
    }

    public final void setMonthFeeMinimumSpecified(BigInteger monthFeeMinimumSpecified) {
        this.monthFeeMinimumSpecified = monthFeeMinimumSpecified;
    }

    public final BigInteger getMonthFeeMaximumSpecified() {
        return monthFeeMaximumSpecified;
    }

    public final void setMonthFeeMaximumSpecified(BigInteger monthFeeMaximumSpecified) {
        this.monthFeeMaximumSpecified = monthFeeMaximumSpecified;
    }

    public final BigInteger getYearFeeMinimumSpecified() {
        return yearFeeMinimumSpecified;
    }

    public final void setYearFeeMinimumSpecified(BigInteger yearFeeMinimumSpecified) {
        this.yearFeeMinimumSpecified = yearFeeMinimumSpecified;
    }

    public final BigInteger getYearFeeMaximumSpecified() {
        return yearFeeMaximumSpecified;
    }

    public final void setYearFeeMaximumSpecified(BigInteger yearFeeMaximumSpecified) {
        this.yearFeeMaximumSpecified = yearFeeMaximumSpecified;
    }

    public final BigInteger getMonthFeeMinimumAtLocale() {
        return monthFeeMinimumAtLocale;
    }

    public final void setMonthFeeMinimumAtLocale(BigInteger monthFeeMinimumAtLocale) {
        this.monthFeeMinimumAtLocale = monthFeeMinimumAtLocale;
    }

    public final BigInteger getMonthFeeMaximumAtLocale() {
        return monthFeeMaximumAtLocale;
    }

    public final void setMonthFeeMaximumAtLocale(BigInteger monthFeeMaximumAtLocale) {
        this.monthFeeMaximumAtLocale = monthFeeMaximumAtLocale;
    }

    public final BigInteger getYearFeeMinimumAtLocale() {
        return yearFeeMinimumAtLocale;
    }

    public final void setYearFeeMinimumAtLocale(BigInteger yearFeeMinimumAtLocale) {
        this.yearFeeMinimumAtLocale = yearFeeMinimumAtLocale;
    }

    public final BigInteger getYearFeeMaximumAtLocale() {
        return yearFeeMaximumAtLocale;
    }

    public final void setYearFeeMaximumAtLocale(BigInteger yearFeeMaximumAtLocale) {
        this.yearFeeMaximumAtLocale = yearFeeMaximumAtLocale;
    }

    public final DurationUnit getPayInterval() {
        return payInterval;
    }

    public final void setPayInterval(DurationUnit payInterval) {
        this.payInterval = payInterval;
    }

    public final BigInteger getMonthPayMinimumSpecified() {
        return monthPayMinimumSpecified;
    }

    public final void setMonthPayMinimumSpecified(BigInteger monthPayMinimumSpecified) {
        this.monthPayMinimumSpecified = monthPayMinimumSpecified;
    }

    public final BigInteger getMonthPayMaximumSpecified() {
        return monthPayMaximumSpecified;
    }

    public final void setMonthPayMaximumSpecified(BigInteger monthPayMaximumSpecified) {
        this.monthPayMaximumSpecified = monthPayMaximumSpecified;
    }

    public final BigInteger getYearPayMinimumSpecified() {
        return yearPayMinimumSpecified;
    }

    public final void setYearPayMinimumSpecified(BigInteger yearPayMinimumSpecified) {
        this.yearPayMinimumSpecified = yearPayMinimumSpecified;
    }

    public final BigInteger getYearPayMaximumSpecified() {
        return yearPayMaximumSpecified;
    }

    public final void setYearPayMaximumSpecified(BigInteger yearPayMaximumSpecified) {
        this.yearPayMaximumSpecified = yearPayMaximumSpecified;
    }

    public final BigInteger getMonthPayMinimumAtLocale() {
        return monthPayMinimumAtLocale;
    }

    public final void setMonthPayMinimumAtLocale(BigInteger monthPayMinimumAtLocale) {
        this.monthPayMinimumAtLocale = monthPayMinimumAtLocale;
    }

    public final BigInteger getMonthPayMaximumAtLocale() {
        return monthPayMaximumAtLocale;
    }

    public final void setMonthPayMaximumAtLocale(BigInteger monthPayMaximumAtLocale) {
        this.monthPayMaximumAtLocale = monthPayMaximumAtLocale;
    }

    public final BigInteger getYearPayMinimumAtLocale() {
        return yearPayMinimumAtLocale;
    }

    public final void setYearPayMinimumAtLocale(BigInteger yearPayMinimumAtLocale) {
        this.yearPayMinimumAtLocale = yearPayMinimumAtLocale;
    }

    public final BigInteger getYearPayMaximumAtLocale() {
        return yearPayMaximumAtLocale;
    }

    public final void setYearPayMaximumAtLocale(BigInteger yearPayMaximumAtLocale) {
        this.yearPayMaximumAtLocale = yearPayMaximumAtLocale;
    }

    public AdvertClosingDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(AdvertClosingDate closingDate) {
        this.closingDate = closingDate;
    }
    
    public final String getSequenceIdentifier() {
        return sequenceIdentifier;
    }

    public final void setSequenceIdentifier(String sequenceIdentifier) {
        this.sequenceIdentifier = sequenceIdentifier;
    }
    
    public final Program getProgram() {
        return program;
    }

    public final Project getProject() {
        return project;
    }
    
    public final Set<Application> getApplications() {
        return applications;
    }

    public Set<AdvertClosingDate> getClosingDates() {
        return closingDates;
    }

    public Set<OpportunityCategory> getCategories() {
        return categories;
    }

    public Set<AdvertRecruitmentPreference> getPreferences() {
        return preferences;
    }
    
    public Advert withTitle(String title) {
        this.title = title;
        return this;
    }
    
    public ParentResource getParentResource() {
        return project == null ? program : project;
    }
    
    public boolean isProgramAdvert() {
        return program != null;
    }

    public boolean isProjectAdvert() {
        return project != null;
    }
}
