package com.zuehlke.pgadmissions.domain;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertDomain;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertFunction;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertIndustry;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import org.apache.solr.analysis.*;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Parameter;
import org.joda.time.LocalDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

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

    @Embedded
    @AttributeOverrides({@AttributeOverride(name = "interval", column = @Column(name = "fee_interval")),
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
            @AttributeOverride(name = "converted", column = @Column(name = "fee_converted"))})
    private FinancialDetails fee;

    @Embedded
    @AttributeOverrides({@AttributeOverride(name = "interval", column = @Column(name = "pay_interval")),
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
            @AttributeOverride(name = "converted", column = @Column(name = "pay_converted"))})
    private FinancialDetails pay;

    @OneToOne
    @JoinColumn(name = "advert_closing_date_id", unique = true)
    private AdvertClosingDate closingDate;

    @Column(name = "last_currency_conversion_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate lastCurrencyConversionDate;

    @Column(name = "sequence_identifier")
    private String sequenceIdentifier;

    @ManyToMany(cascade = CascadeType.ALL, targetEntity = AdvertDomain.class)
    @JoinColumn(name = "advert_id", nullable = false)
    private Set<PrismAdvertDomain> domains = Sets.newHashSet();

    @ManyToMany(cascade = CascadeType.ALL, targetEntity = AdvertIndustry.class)
    @JoinColumn(name = "advert_id", nullable = false)
    private Set<PrismAdvertIndustry> industries = Sets.newHashSet();

    @ManyToMany(cascade = CascadeType.ALL, targetEntity = AdvertFunction.class)
    @JoinColumn(name = "advert_id", nullable = false)
    private Set<PrismAdvertFunction> functions = Sets.newHashSet();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "ADVERT_TARGET_INSTITUTION", joinColumns = {@JoinColumn(name = "advert_id", nullable = false)}, inverseJoinColumns = {@JoinColumn(name = "institution_id", nullable = false)}, //
            uniqueConstraints = {@UniqueConstraint(columnNames = {"institution_id", "advert_id"})})
    private Set<Institution> targetInstitutions = Sets.newHashSet();

    @ManyToMany(cascade = CascadeType.ALL, targetEntity = AdvertTargetProgramType.class)
    @JoinColumn(name = "advert_id", nullable = false)
    private Set<PrismProgramType> targetProgramTypes = Sets.newHashSet();

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

    public FinancialDetails getFee() {
        return fee;
    }

    public void setFee(FinancialDetails fee) {
        this.fee = fee;
    }

    public FinancialDetails getPay() {
        return pay;
    }

    public void setPay(FinancialDetails pay) {
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

    public final Set<PrismAdvertDomain> getDomains() {
        return domains;
    }

    public final Set<PrismAdvertIndustry> getIndustries() {
        return industries;
    }

    public final Set<PrismAdvertFunction> getFunctions() {
        return functions;
    }

    public final Set<Institution> getTargetInstitutions() {
        return targetInstitutions;
    }

    public final Set<PrismProgramType> getTargetProgramTypes() {
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

    public ParentResource getParentResource() {
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

    @Entity
    @Table(name = "ADVERT_DOMAIN", uniqueConstraints = {@UniqueConstraint(columnNames = {"domain", "advert_id"})})
    private static class AdvertDomain {

        @EmbeddedId
        private AdvertDomainId id;

        @ManyToOne
        @JoinColumn(name = "advertId", insertable = false, updatable = false)
        private Advert advert;

        @Column(name = "domain", insertable = false, updatable = false)
        @Enumerated(EnumType.STRING)
        private PrismAdvertDomain domain;

        @Embeddable
        private static class AdvertDomainId extends AdvertTagId implements Serializable {

            private static final long serialVersionUID = -381523189635209210L;

            @Column(name = "advert_id", nullable = false)
            private Integer advertId;

            @Column(name = "domain", nullable = false)
            @Enumerated(EnumType.STRING)
            private PrismAdvertDomain domain;

            @Override
            protected Integer getAdvertId() {
                return advertId;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected <T extends Enum> T getAdvertTag() {
                return (T) domain;
            }

        }

    }

    @Entity
    @Table(name = "ADVERT_INDUSTRY", uniqueConstraints = {@UniqueConstraint(columnNames = {"industry", "advert_id"})})
    private static class AdvertIndustry {

        @EmbeddedId
        private AdvertIndustryId id;

        @ManyToOne
        @JoinColumn(name = "advertId", insertable = false, updatable = false)
        private Advert advert;

        @Column(name = "industry", insertable = false, updatable = false)
        @Enumerated(EnumType.STRING)
        private PrismAdvertIndustry industry;

        @Embeddable
        private static class AdvertIndustryId extends AdvertTagId implements Serializable {

            private static final long serialVersionUID = -381523189635209210L;

            @Column(name = "advert_id", nullable = false)
            private Integer advertId;

            @Column(name = "industry", nullable = false)
            @Enumerated(EnumType.STRING)
            private PrismAdvertIndustry industry;

            @Override
            protected Integer getAdvertId() {
                return advertId;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected <T extends Enum> T getAdvertTag() {
                return (T) industry;
            }

        }

    }

    @Entity
    @Table(name = "ADVERT_FUNCTION", uniqueConstraints = {@UniqueConstraint(columnNames = {"function", "advert_id"})})
    private static class AdvertFunction {

        @EmbeddedId
        private AdvertFunctionId id;

        @ManyToOne
        @JoinColumn(name = "advertId", insertable = false, updatable = false)
        private Advert advert;

        @Column(name = "function", insertable = false, updatable = false)
        @Enumerated(EnumType.STRING)
        private PrismAdvertFunction function;

        @Embeddable
        private static class AdvertFunctionId extends AdvertTagId implements Serializable {

            private static final long serialVersionUID = -381523189635209210L;

            @Column(name = "advert_id", nullable = false)
            private Integer advertId;

            @Column(name = "function", nullable = false)
            @Enumerated(EnumType.STRING)
            private PrismAdvertFunction function;

            @Override
            protected Integer getAdvertId() {
                return advertId;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected <T extends Enum> T getAdvertTag() {
                return (T) function;
            }

        }

    }

    @Entity
    @Table(name = "ADVERT_TARGET_PROGRAM_TYPE", uniqueConstraints = {@UniqueConstraint(columnNames = {"program_type", "advert_id"})})
    private static class AdvertTargetProgramType {

        @EmbeddedId
        private AdvertTargetProgramTypeId id;

        @ManyToOne
        @JoinColumn(name = "advertId", insertable = false, updatable = false)
        private Advert advert;

        @Column(name = "program_type", insertable = false, updatable = false)
        @Enumerated(EnumType.STRING)
        private PrismProgramType programType;

        @Embeddable
        private static class AdvertTargetProgramTypeId extends AdvertTagId implements Serializable {

            private static final long serialVersionUID = -381523189635209210L;

            @Column(name = "advert_id", nullable = false)
            private Integer advertId;

            @Column(name = "program_type", nullable = false)
            @Enumerated(EnumType.STRING)
            private PrismProgramType programType;

            @Override
            protected Integer getAdvertId() {
                return advertId;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected <T extends Enum> T getAdvertTag() {
                return (T) programType;
            }

        }

    }

    private static abstract class AdvertTagId {

        protected abstract Integer getAdvertId();

        protected abstract <T extends Enum> T getAdvertTag();

        @Override
        public int hashCode() {
            return Objects.hashCode(getAdvertId(), getAdvertTag());
        }

        @Override
        public boolean equals(Object object) {
            if (object == null) {
                return false;
            }
            if (getClass() != object.getClass()) {
                return false;
            }
            final AdvertTagId other = (AdvertTagId) object;
            return Objects.equal(getAdvertId(), other.getAdvertId()) && Objects.equal(getAdvertTag(), other.getAdvertTag());
        }

    }

}
