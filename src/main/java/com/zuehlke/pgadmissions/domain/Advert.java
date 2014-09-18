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
        @TokenFilterDef(factory = ASCIIFoldingFilterFactory.class)})
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

    @Column(name = "fee_interval")
    @Enumerated(EnumType.STRING)
    private DurationUnit feeInterval;
    
    @Column(name = "fee_currency")
    private String feeCurrency;

    @Column(name = "fee_currency_at_locale")
    private String feeCurrencyAtLocale;

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

    @Column(name = "pay_currency")
    private String payCurrency;

    @Column(name = "pay_currency_at_locale")
    private String payCurrencyAtLocale;
    
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
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "advert_id", nullable = false)
    private Set<AdvertTarget> targets = Sets.newHashSet();
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "advert_id", nullable = false)
    private Set<AdvertKeyword> keywords = Sets.newHashSet();

    @OneToOne(mappedBy = "advert")
    private Program program;

    @OneToOne(mappedBy = "advert")
    private Project project;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "advert_id", nullable = false)
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

    public String getApplyLink() {
        return applyLink;
    }

    public void setApplyLink(String applyLink) {
        this.applyLink = applyLink;
    }

    public InstitutionAddress getAddress() {
        return address;
    }

    public void setAddress(InstitutionAddress address) {
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

    public DurationUnit getFeeInterval() {
        return feeInterval;
    }

    public void setFeeInterval(DurationUnit feeInterval) {
        this.feeInterval = feeInterval;
    }
    
    public String getFeeCurrency() {
        return feeCurrency;
    }

    public void setFeeCurrency(String feeCurrency) {
        this.feeCurrency = feeCurrency;
    }

    public String getCurrencyAtLocale() {
        return feeCurrencyAtLocale;
    }

    public void setCurrencyAtLocale(String currencyAtLocale) {
        this.feeCurrencyAtLocale = currencyAtLocale;
    }

    public BigDecimal getMonthFeeMinimumSpecified() {
        return monthFeeMinimumSpecified;
    }

    public void setMonthFeeMinimumSpecified(BigDecimal monthFeeMinimumSpecified) {
        this.monthFeeMinimumSpecified = monthFeeMinimumSpecified;
    }

    public BigDecimal getMonthFeeMaximumSpecified() {
        return monthFeeMaximumSpecified;
    }

    public void setMonthFeeMaximumSpecified(BigDecimal monthFeeMaximumSpecified) {
        this.monthFeeMaximumSpecified = monthFeeMaximumSpecified;
    }

    public BigDecimal getYearFeeMinimumSpecified() {
        return yearFeeMinimumSpecified;
    }

    public void setYearFeeMinimumSpecified(BigDecimal yearFeeMinimumSpecified) {
        this.yearFeeMinimumSpecified = yearFeeMinimumSpecified;
    }

    public BigDecimal getYearFeeMaximumSpecified() {
        return yearFeeMaximumSpecified;
    }

    public void setYearFeeMaximumSpecified(BigDecimal yearFeeMaximumSpecified) {
        this.yearFeeMaximumSpecified = yearFeeMaximumSpecified;
    }

    public BigDecimal getMonthFeeMinimumAtLocale() {
        return monthFeeMinimumAtLocale;
    }

    public void setMonthFeeMinimumAtLocale(BigDecimal monthFeeMinimumAtLocale) {
        this.monthFeeMinimumAtLocale = monthFeeMinimumAtLocale;
    }

    public BigDecimal getMonthFeeMaximumAtLocale() {
        return monthFeeMaximumAtLocale;
    }

    public void setMonthFeeMaximumAtLocale(BigDecimal monthFeeMaximumAtLocale) {
        this.monthFeeMaximumAtLocale = monthFeeMaximumAtLocale;
    }

    public BigDecimal getYearFeeMinimumAtLocale() {
        return yearFeeMinimumAtLocale;
    }

    public void setYearFeeMinimumAtLocale(BigDecimal yearFeeMinimumAtLocale) {
        this.yearFeeMinimumAtLocale = yearFeeMinimumAtLocale;
    }

    public BigDecimal getYearFeeMaximumAtLocale() {
        return yearFeeMaximumAtLocale;
    }

    public void setYearFeeMaximumAtLocale(BigDecimal yearFeeMaximumAtLocale) {
        this.yearFeeMaximumAtLocale = yearFeeMaximumAtLocale;
    }

    public final String getPayCurrencyAtLocale() {
        return payCurrencyAtLocale;
    }

    public final void setPayCurrencyAtLocale(String payCurrencyAtLocale) {
        this.payCurrencyAtLocale = payCurrencyAtLocale;
    }

    public DurationUnit getPayInterval() {
        return payInterval;
    }

    public void setPayInterval(DurationUnit payInterval) {
        this.payInterval = payInterval;
    }

    public BigDecimal getMonthPayMinimumSpecified() {
        return monthPayMinimumSpecified;
    }

    public void setMonthPayMinimumSpecified(BigDecimal monthPayMinimumSpecified) {
        this.monthPayMinimumSpecified = monthPayMinimumSpecified;
    }

    public BigDecimal getMonthPayMaximumSpecified() {
        return monthPayMaximumSpecified;
    }

    public void setMonthPayMaximumSpecified(BigDecimal monthPayMaximumSpecified) {
        this.monthPayMaximumSpecified = monthPayMaximumSpecified;
    }

    public BigDecimal getYearPayMinimumSpecified() {
        return yearPayMinimumSpecified;
    }

    public void setYearPayMinimumSpecified(BigDecimal yearPayMinimumSpecified) {
        this.yearPayMinimumSpecified = yearPayMinimumSpecified;
    }

    public BigDecimal getYearPayMaximumSpecified() {
        return yearPayMaximumSpecified;
    }

    public void setYearPayMaximumSpecified(BigDecimal yearPayMaximumSpecified) {
        this.yearPayMaximumSpecified = yearPayMaximumSpecified;
    }

    public BigDecimal getMonthPayMinimumAtLocale() {
        return monthPayMinimumAtLocale;
    }

    public void setMonthPayMinimumAtLocale(BigDecimal monthPayMinimumAtLocale) {
        this.monthPayMinimumAtLocale = monthPayMinimumAtLocale;
    }

    public BigDecimal getMonthPayMaximumAtLocale() {
        return monthPayMaximumAtLocale;
    }

    public void setMonthPayMaximumAtLocale(BigDecimal monthPayMaximumAtLocale) {
        this.monthPayMaximumAtLocale = monthPayMaximumAtLocale;
    }

    public BigDecimal getYearPayMinimumAtLocale() {
        return yearPayMinimumAtLocale;
    }

    public void setYearPayMinimumAtLocale(BigDecimal yearPayMinimumAtLocale) {
        this.yearPayMinimumAtLocale = yearPayMinimumAtLocale;
    }

    public BigDecimal getYearPayMaximumAtLocale() {
        return yearPayMaximumAtLocale;
    }

    public void setYearPayMaximumAtLocale(BigDecimal yearPayMaximumAtLocale) {
        this.yearPayMaximumAtLocale = yearPayMaximumAtLocale;
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

    public final Set<AdvertTarget> getTargets() {
        return targets;
    }

    public final Set<AdvertKeyword> getKeywords() {
        return keywords;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Set<AdvertClosingDate> getClosingDates() {
        return closingDates;
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
