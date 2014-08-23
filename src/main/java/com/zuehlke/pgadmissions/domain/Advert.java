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
import org.hibernate.annotations.Type;
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
import org.joda.time.LocalDate;

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
    private String title;

    @Column(name = "description")
    @Field(analyzer = @Analyzer(definition = "advertAnalyzer"), index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String description;

    @Column(name = "apply_link")
    private String applyLink;

    @Column(name = "publish_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate publishDate;

    @Column(name = "immediate_start", nullable = false)
    private Boolean immediateStart;

    @ManyToOne
    @JoinColumn(name = "institution_address_id")
    private InstitutionAddress address;

    @Column(name = "month_study_duration")
    private Integer studyDuration;

    @Column(name = "fee_interval")
    @Enumerated(EnumType.STRING)
    private DurationUnit feeInterval;

    @Column(name = "fee_value")
    private BigDecimal feeValue;

    @Column(name = "fee_annualised")
    private BigDecimal feeAnnualised;

    @Column(name = "pay_interval")
    @Enumerated(EnumType.STRING)
    private DurationUnit payInterval;

    @Column(name = "pay_value")
    private BigDecimal payValue;

    @Column(name = "pay_annualised")
    private BigDecimal payAnnualised;
    
    @Column(name = "sequence_identifier", nullable = false)
    private String sequenceIdentifier;

    @OneToOne(mappedBy = "advert")
    private Program program;
    
    @OneToOne(mappedBy = "advert")
    private Project project;
    
    @OneToMany(mappedBy = "advert")
    private Set<Application> applications = Sets.newHashSet();

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

    @OneToOne
    @JoinColumn(name = "advert_closing_date_id", unique = true)
    private AdvertClosingDate closingDate;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "advert_id", nullable = false)
    private Set<AdvertClosingDate> closingDates = Sets.newHashSet();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "ADVERT_CATEGORY", joinColumns = @JoinColumn(name = "advert_id", nullable = false), inverseJoinColumns = @JoinColumn(name = "advert_opportunity_category_id", nullable = false))
    private Set<OpportunityCategory> categories = Sets.newHashSet();

    @OneToMany(mappedBy = "advert")
    private Set<AdvertRecruitmentPreference> recruitmentPreferences = Sets.newHashSet();

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

    public final LocalDate getPublishDate() {
        return publishDate;
    }

    public final void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    public boolean isImmediateStart() {
        return immediateStart;
    }

    public void setImmediateStart(boolean immediateStart) {
        this.immediateStart = immediateStart;
    }

    public InstitutionAddress getAddress() {
        return address;
    }

    public void setAddress(InstitutionAddress address) {
        this.address = address;
    }

    public Integer getStudyDuration() {
        return studyDuration;
    }

    public DurationUnit getFeeInterval() {
        return feeInterval;
    }

    public void setFeeInterval(DurationUnit feeInterval) {
        this.feeInterval = feeInterval;
    }

    public BigDecimal getFeeValue() {
        return feeValue;
    }

    public void setFeeValue(BigDecimal feeValue) {
        this.feeValue = feeValue;
    }

    public BigDecimal getFeeAnnualised() {
        return feeAnnualised;
    }

    public void setFeeAnnualised(BigDecimal feeAnnualised) {
        this.feeAnnualised = feeAnnualised;
    }

    public DurationUnit getPayInterval() {
        return payInterval;
    }

    public void setPayInterval(DurationUnit payInterval) {
        this.payInterval = payInterval;
    }

    public BigDecimal getPayValue() {
        return payValue;
    }

    public void setPayValue(BigDecimal payValue) {
        this.payValue = payValue;
    }

    public BigDecimal getPayAnnualised() {
        return payAnnualised;
    }

    public void setPayAnnualised(BigDecimal payAnnualised) {
        this.payAnnualised = payAnnualised;
    }

    public final String getSequenceIdentifier() {
        return sequenceIdentifier;
    }

    public final void setSequenceIdentifier(String sequenceIdentifier) {
        this.sequenceIdentifier = sequenceIdentifier;
    }

    public void setStudyDuration(Integer studyDuration) {
        this.studyDuration = studyDuration;
    }

    public AdvertClosingDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(AdvertClosingDate closingDate) {
        this.closingDate = closingDate;
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

    public Set<AdvertRecruitmentPreference> getRecruitmentPreferences() {
        return recruitmentPreferences;
    }
    
    public Advert withTitle(String title) {
        this.title = title;
        return this;
    }
    
    public Advert withPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
        return this;
    }
    
    public Advert withImmediateStart(Boolean immediateStart) {
        this.immediateStart = immediateStart;
        return this;
    }
    
    public ParentResource getParentResource() {
        return project == null ? program : project;
    }

}
