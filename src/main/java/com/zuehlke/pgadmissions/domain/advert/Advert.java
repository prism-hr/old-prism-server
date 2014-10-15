package com.zuehlke.pgadmissions.domain.advert;

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
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertDomain;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertFunction;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertIndustry;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.institution.InstitutionAddress;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;

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

    @Column(name = "sequence_identifier")
    private String sequenceIdentifier;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "advert_id", nullable = false)
    private Set<AdvertDomain> domains = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "advert_id", nullable = false)
    private Set<AdvertFunction> functions = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "advert_id", nullable = false)
    private Set<AdvertIndustry> industries = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "advert_id", nullable = false)
    private Set<AdvertCompetency> competencies = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "advert_id", nullable = false)
    private Set<AdvertTargetInstitution> targetInstitutions = Sets.newHashSet();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "advert_id", nullable = false)
    private Set<AdvertTargetProgramType> targetProgramTypes = Sets.newHashSet();

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

    public final Set<AdvertTargetInstitution> getTargetInstitutions() {
        return targetInstitutions;
    }

    public final Set<AdvertTargetProgramType> getTargetProgramTypes() {
        return targetProgramTypes;
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

    public ResourceParent getParentResource() {
        return project == null ? program : project;
    }

    public boolean isProgramAdvert() {
        return program != null;
    }

    public boolean isProjectAdvert() {
        return project != null;
    }

    public Institution getInstitution() {
        return isProjectAdvert() ? project.getInstitution() : program.getInstitution();
    }

    public boolean hasConvertedFee() {
        return fee != null && !fee.getCurrencySpecified().equals(fee.getCurrencyAtLocale());
    }

    public boolean hasConvertedPay() {
        return pay != null && !pay.getCurrencySpecified().equals(pay.getCurrencyAtLocale());
    }

    public void addCategory(Object advertFilter) {
        Class<?> advertFilterType = advertFilter.getClass();
        if (advertFilterType == PrismAdvertDomain.class) {
            addDomain((PrismAdvertDomain) advertFilter);
        } else if (advertFilterType == PrismAdvertFunction.class) {
            addFunction((PrismAdvertFunction) advertFilter);
        } else if (advertFilterType == PrismAdvertIndustry.class) {
            addIndustry((PrismAdvertIndustry) advertFilter);
        } else if (advertFilterType == String.class) {
            addCompetency((String) advertFilter);
        } else if (advertFilterType == Institution.class) {
            addTargetInstitution((Institution) advertFilter);
        } else if (advertFilterType == PrismProgramType.class) {
            addTargetProgramType((PrismProgramType) advertFilter);
        }
    }

    private void addDomain(PrismAdvertDomain domainId) {
        AdvertDomain domain = new AdvertDomain().withAdvert(this);
        domain.setDomain(domainId);
        domains.add(domain);
    }

    private void addFunction(PrismAdvertFunction functionId) {
        AdvertFunction function = new AdvertFunction().withAdvert(this);
        function.setFunction(functionId);
        functions.add(function);
    }

    private void addIndustry(PrismAdvertIndustry industryId) {
        AdvertIndustry industry = new AdvertIndustry().withAdvert(this);
        industry.setIndustry(industryId);
        industries.add(industry);
    }

    private void addCompetency(String competencyId) {
        AdvertCompetency competency = new AdvertCompetency().withAdvert(this);
        competency.setCompetency(competencyId);
        competencies.add(competency);
    }

    private void addTargetInstitution(Institution institution) {
        AdvertTargetInstitution targetInstitution = new AdvertTargetInstitution().withAdvert(this);
        targetInstitution.setInstitution(institution);
        targetInstitutions.add(targetInstitution);
    }

    private void addTargetProgramType(PrismProgramType programTypeId) {
        AdvertTargetProgramType targetProgramType = new AdvertTargetProgramType().withAdvert(this);
        targetProgramType.setProgramType(programTypeId);
        targetProgramTypes.add(targetProgramType);
    }

}
