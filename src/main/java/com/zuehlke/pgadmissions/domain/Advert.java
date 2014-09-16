package com.zuehlke.pgadmissions.domain;

import java.math.BigDecimal;
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
    private BigDecimal monthFeeMinimumSpecified;

    @Column(name = "month_fee_maximum_specified")
    private BigDecimal monthFeeMaximumSpecified;

    @Column(name = "year_fee_minimum_specified")
    private BigDecimal yearFeeMinimumSpecified;

    @Column(name = "year_fee_maximum_specified")
    private BigDecimal yearFeeMaximumSpecified;

    @Column(name = "month_fee_minimum_at_locale")
    private BigDecimal monthFeeMinimumAtLocale;

    @Column(name = "month_fee_maximum_at_locale")
    private BigDecimal monthFeeMaximumAtLocale;

    @Column(name = "year_fee_minimum_at_locale")
    private BigDecimal yearFeeMinimumAtLocale;

    @Column(name = "year_fee_maximum_at_locale")
    private BigDecimal yearFeeMaximumAtLocale;

    @Column(name = "pay_interval")
    @Enumerated(EnumType.STRING)
    private DurationUnit payInterval;

    @Column(name = "month_pay_minimum_specified")
    private BigDecimal monthPayMinimumSpecified;

    @Column(name = "month_pay_maximum_specified")
    private BigDecimal monthPayMaximumSpecified;

    @Column(name = "year_pay_minimum_specified")
    private BigDecimal yearPayMinimumSpecified;

    @Column(name = "year_pay_maximum_specified")
    private BigDecimal yearPayMaximumSpecified;

    @Column(name = "month_pay_minimum_at_locale")
    private BigDecimal monthPayMinimumAtLocale;

    @Column(name = "month_pay_maximum_at_locale")
    private BigDecimal monthPayMaximumAtLocale;

    @Column(name = "year_pay_minimum_at_locale")
    private BigDecimal yearPayMinimumAtLocale;

    @Column(name = "year_pay_maximum_at_locale")
    private BigDecimal yearPayMaximumAtLocale;

    @OneToOne
    @JoinColumn(name = "advert_closing_date_id", unique = true)
    private AdvertClosingDate closingDate;

    @Column(name = "sequence_identifier")
    private String sequenceIdentifier;

    @OneToOne(mappedBy = "advert")
    private Program program;

    @OneToOne(mappedBy = "advert")
    private Project project;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "advert_id", nullable = false)
    private Set<AdvertClosingDate> closingDates = Sets.newHashSet();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "ADVERT_CATEGORY", joinColumns = @JoinColumn(name = "advert_id", nullable = false), inverseJoinColumns = @JoinColumn(name = "advert_opportunity_category_id", nullable = false))
    private Set<AdvertCategory> categories = Sets.newHashSet();

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

    public final BigDecimal getMonthFeeMinimumSpecified() {
        return monthFeeMinimumSpecified;
    }

    public final void setMonthFeeMinimumSpecified(BigDecimal monthFeeMinimumSpecified) {
        this.monthFeeMinimumSpecified = monthFeeMinimumSpecified;
    }

    public final BigDecimal getMonthFeeMaximumSpecified() {
        return monthFeeMaximumSpecified;
    }

    public final void setMonthFeeMaximumSpecified(BigDecimal monthFeeMaximumSpecified) {
        this.monthFeeMaximumSpecified = monthFeeMaximumSpecified;
    }

    public final BigDecimal getYearFeeMinimumSpecified() {
        return yearFeeMinimumSpecified;
    }

    public final void setYearFeeMinimumSpecified(BigDecimal yearFeeMinimumSpecified) {
        this.yearFeeMinimumSpecified = yearFeeMinimumSpecified;
    }

    public final BigDecimal getYearFeeMaximumSpecified() {
        return yearFeeMaximumSpecified;
    }

    public final void setYearFeeMaximumSpecified(BigDecimal yearFeeMaximumSpecified) {
        this.yearFeeMaximumSpecified = yearFeeMaximumSpecified;
    }

    public final BigDecimal getMonthFeeMinimumAtLocale() {
        return monthFeeMinimumAtLocale;
    }

    public final void setMonthFeeMinimumAtLocale(BigDecimal monthFeeMinimumAtLocale) {
        this.monthFeeMinimumAtLocale = monthFeeMinimumAtLocale;
    }

    public final BigDecimal getMonthFeeMaximumAtLocale() {
        return monthFeeMaximumAtLocale;
    }

    public final void setMonthFeeMaximumAtLocale(BigDecimal monthFeeMaximumAtLocale) {
        this.monthFeeMaximumAtLocale = monthFeeMaximumAtLocale;
    }

    public final BigDecimal getYearFeeMinimumAtLocale() {
        return yearFeeMinimumAtLocale;
    }

    public final void setYearFeeMinimumAtLocale(BigDecimal yearFeeMinimumAtLocale) {
        this.yearFeeMinimumAtLocale = yearFeeMinimumAtLocale;
    }

    public final BigDecimal getYearFeeMaximumAtLocale() {
        return yearFeeMaximumAtLocale;
    }

    public final void setYearFeeMaximumAtLocale(BigDecimal yearFeeMaximumAtLocale) {
        this.yearFeeMaximumAtLocale = yearFeeMaximumAtLocale;
    }

    public final DurationUnit getPayInterval() {
        return payInterval;
    }

    public final void setPayInterval(DurationUnit payInterval) {
        this.payInterval = payInterval;
    }

    public final BigDecimal getMonthPayMinimumSpecified() {
        return monthPayMinimumSpecified;
    }

    public final void setMonthPayMinimumSpecified(BigDecimal monthPayMinimumSpecified) {
        this.monthPayMinimumSpecified = monthPayMinimumSpecified;
    }

    public final BigDecimal getMonthPayMaximumSpecified() {
        return monthPayMaximumSpecified;
    }

    public final void setMonthPayMaximumSpecified(BigDecimal monthPayMaximumSpecified) {
        this.monthPayMaximumSpecified = monthPayMaximumSpecified;
    }

    public final BigDecimal getYearPayMinimumSpecified() {
        return yearPayMinimumSpecified;
    }

    public final void setYearPayMinimumSpecified(BigDecimal yearPayMinimumSpecified) {
        this.yearPayMinimumSpecified = yearPayMinimumSpecified;
    }

    public final BigDecimal getYearPayMaximumSpecified() {
        return yearPayMaximumSpecified;
    }

    public final void setYearPayMaximumSpecified(BigDecimal yearPayMaximumSpecified) {
        this.yearPayMaximumSpecified = yearPayMaximumSpecified;
    }

    public final BigDecimal getMonthPayMinimumAtLocale() {
        return monthPayMinimumAtLocale;
    }

    public final void setMonthPayMinimumAtLocale(BigDecimal monthPayMinimumAtLocale) {
        this.monthPayMinimumAtLocale = monthPayMinimumAtLocale;
    }

    public final BigDecimal getMonthPayMaximumAtLocale() {
        return monthPayMaximumAtLocale;
    }

    public final void setMonthPayMaximumAtLocale(BigDecimal monthPayMaximumAtLocale) {
        this.monthPayMaximumAtLocale = monthPayMaximumAtLocale;
    }

    public final BigDecimal getYearPayMinimumAtLocale() {
        return yearPayMinimumAtLocale;
    }

    public final void setYearPayMinimumAtLocale(BigDecimal yearPayMinimumAtLocale) {
        this.yearPayMinimumAtLocale = yearPayMinimumAtLocale;
    }

    public final BigDecimal getYearPayMaximumAtLocale() {
        return yearPayMaximumAtLocale;
    }

    public final void setYearPayMaximumAtLocale(BigDecimal yearPayMaximumAtLocale) {
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

    public Set<AdvertClosingDate> getClosingDates() {
        return closingDates;
    }

    public Set<AdvertCategory> getCategories() {
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
