package com.zuehlke.pgadmissions.domain;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

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
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@AnalyzerDef(name = "institutionNameAnalyzer", tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class), filters = {
        @TokenFilterDef(factory = LowerCaseFilterFactory.class), @TokenFilterDef(factory = StopFilterFactory.class),
        @TokenFilterDef(factory = SnowballPorterFilterFactory.class, params = @Parameter(name = "language", value = "English")),
        @TokenFilterDef(factory = ASCIIFoldingFilterFactory.class)})
@Entity
@Table(name = "INSTITUTION", uniqueConstraints = {@UniqueConstraint(columnNames = {"institution_domicile_id", "title"})})
@Indexed
public class Institution extends ParentResource {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "system_id", nullable = false)
    private System system;
    
    @Column(name = "referrer")
    private String referrer;
    
    @Column(name = "code")
    private String code;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "institution_domicile_id", nullable = false)
    private InstitutionDomicile domicile;

    @Column(name = "title", nullable = false, unique = true)
    @Field(analyzer = @Analyzer(definition = "institutionNameAnalyzer"), index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String title;

    @Column(name = "homepage", nullable = false)
    private String homepage;
    
    @OneToOne
    @JoinColumn(name = "logo_document_id")
    private Document logoDocument;

    @JoinColumn(name = "institution_address_id")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private InstitutionAddress address;
    
    @Column(name = "is_ucl_institution", nullable = false)
    private Boolean uclInstitution = false;
    
    @Column(name = "application_rating_count_percentile_05")
    private Integer applicationRatingCount05;
    
    @Column(name = "application_rating_count_percentile_20")
    private Integer applicationRatingCount20;

    @Column(name = "application_rating_count_percentile_35")
    private Integer applicationRatingCount35;
    
    @Column(name = "application_rating_count_percentile_50")
    private Integer applicationRatingCount50;
    
    @Column(name = "application_rating_count_percentile_65")
    private Integer applicationRatingCount65;
    
    @Column(name = "application_rating_count_percentile_80")
    private Integer applicationRatingCount80;
    
    @Column(name = "application_rating_count_percentile_95")
    private Integer applicationRatingCount95;
    
    @Column(name = "application_rating_average_percentile_05")
    private BigDecimal applicationRatingAverage05;
    
    @Column(name = "application_rating_average_percentile_20")
    private BigDecimal applicationRatingAverage20;

    @Column(name = "application_rating_average_percentile_35")
    private BigDecimal applicationRatingAverage35;
    
    @Column(name = "application_rating_average_percentile_50")
    private BigDecimal applicationRatingAverage50;
    
    @Column(name = "application_rating_average_percentile_65")
    private BigDecimal applicationRatingAverage65;
    
    @Column(name = "application_rating_average_percentile_80")
    private BigDecimal applicationRatingAverage80;
    
    @Column(name = "application_rating_average_percentile_95")
    private BigDecimal applicationRatingAverage95;
    
    @ManyToOne
    @JoinColumn(name = "state_id")
    private State state;

    @ManyToOne
    @JoinColumn(name = "previous_state_id")
    private State previousState;

    @Column(name = "due_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate dueDate;

    @Column(name = "created_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdTimestamp;

    @Column(name = "updated_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime updatedTimestamp;
    
    @Column(name = "sequence_identifier", unique = true)
    private String sequenceIdentifier;
    
    @OneToMany(mappedBy = "institution")
    private Set<ImportedEntityFeed> importedEntityFeeds = Sets.newHashSet();

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }
    
    @Override
    public String getCode() {
        return code;
    }
    
    @Override
    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public InstitutionDomicile getDomicile() {
        return domicile;
    }

    public void setDomicile(InstitutionDomicile domicile) {
        this.domicile = domicile;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public final Document getLogoDocument() {
        return logoDocument;
    }

    public final void setLogoDocument(Document logoDocument) {
        this.logoDocument = logoDocument;
    }

    public InstitutionAddress getAddress() {
        return address;
    }

    public void setAddress(InstitutionAddress address) {
        this.address = address;
    }

    public boolean isUclInstitution() {
        return uclInstitution;
    }

    public void setUclInstitution(boolean uclInstitution) {
        this.uclInstitution = uclInstitution;
    }

    @Override
    public final Integer getApplicationRatingCount05() {
        return applicationRatingCount05;
    }

    @Override
    public final void setApplicationRatingCount05(Integer applicationRatingCount05) {
        this.applicationRatingCount05 = applicationRatingCount05;
    }

    @Override
    public final Integer getApplicationRatingCount20() {
        return applicationRatingCount20;
    }

    @Override
    public final void setApplicationRatingCount20(Integer applicationRatingCount20) {
        this.applicationRatingCount20 = applicationRatingCount20;
    }

    @Override
    public final Integer getApplicationRatingCount35() {
        return applicationRatingCount35;
    }

    @Override
    public final void setApplicationRatingCount35(Integer applicationRatingCount35) {
        this.applicationRatingCount35 = applicationRatingCount35;
    }

    @Override
    public final Integer getApplicationRatingCount50() {
        return applicationRatingCount50;
    }

    @Override
    public final void setApplicationRatingCount50(Integer applicationRatingCount50) {
        this.applicationRatingCount50 = applicationRatingCount50;
    }

    @Override
    public final Integer getApplicationRatingCount65() {
        return applicationRatingCount65;
    }

    @Override
    public final void setApplicationRatingCount65(Integer applicationRatingCount65) {
        this.applicationRatingCount65 = applicationRatingCount65;
    }

    @Override
    public final Integer getApplicationRatingCount80() {
        return applicationRatingCount80;
    }

    @Override
    public final void setApplicationRatingCount80(Integer applicationRatingCount80) {
        this.applicationRatingCount80 = applicationRatingCount80;
    }

    @Override
    public final Integer getApplicationRatingCount95() {
        return applicationRatingCount95;
    }

    @Override
    public final void setApplicationRatingCount95(Integer applicationRatingCount95) {
        this.applicationRatingCount95 = applicationRatingCount95;
    }
    
    @Override
    public final BigDecimal getApplicationRatingAverage05() {
        return applicationRatingAverage05;
    }

    @Override
    public final void setApplicationRatingAverage05(BigDecimal applicationRatingAverage05) {
        this.applicationRatingAverage05 = applicationRatingAverage05;
    }

    @Override
    public final BigDecimal getApplicationRatingAverage20() {
        return applicationRatingAverage20;
    }

    @Override
    public final void setApplicationRatingAverage20(BigDecimal applicationRatingAverage20) {
        this.applicationRatingAverage20 = applicationRatingAverage20;
    }

    @Override
    public final BigDecimal getApplicationRatingAverage35() {
        return applicationRatingAverage35;
    }

    @Override
    public final void setApplicationRatingAverage35(BigDecimal applicationRatingAverage35) {
        this.applicationRatingAverage35 = applicationRatingAverage35;
    }

    @Override
    public final BigDecimal getApplicationRatingAverage50() {
        return applicationRatingAverage50;
    }

    @Override
    public final void setApplicationRatingAverage50(BigDecimal applicationRatingAverage50) {
        this.applicationRatingAverage50 = applicationRatingAverage50;
    }

    @Override
    public final BigDecimal getApplicationRatingAverage65() {
        return applicationRatingAverage65;
    }

    @Override
    public final void setApplicationRatingAverage65(BigDecimal applicationRatingAverage65) {
        this.applicationRatingAverage65 = applicationRatingAverage65;
    }

    @Override
    public final BigDecimal getApplicationRatingAverage80() {
        return applicationRatingAverage80;
    }

    @Override
    public final void setApplicationRatingAverage80(BigDecimal applicationRatingAverage80) {
        this.applicationRatingAverage80 = applicationRatingAverage80;
    }

    @Override
    public final BigDecimal getApplicationRatingAverage95() {
        return applicationRatingAverage95;
    }

    @Override
    public final void setApplicationRatingAverage95(BigDecimal applicationRatingAverage95) {
        this.applicationRatingAverage95 = applicationRatingAverage95;
    }

    public final Set<ImportedEntityFeed> getImportedEntityFeeds() {
        return importedEntityFeeds;
    }

    public Institution withId(Integer id) {
        this.id = id;
        return this;
    }

    public Institution withSystem(System system) {
        this.system = system;
        return this;
    }

    public Institution withUser(User user) {
        this.user = user;
        return this;
    }
    
    public Institution withDomicile(InstitutionDomicile domicile) {
        this.domicile = domicile;
        return this;
    }

    public Institution withTitle(final String title) {
        this.title = title;
        return this;
    }

    public Institution withState(State state) {
        this.state = state;
        return this;
    }

    public Institution withHomepage(String homepage) {
        this.homepage = homepage;
        return this;
    }
    
    public Institution withLogoDocument(Document logoDocument) {
        this.logoDocument = logoDocument;
        return this;
    }

    public Institution withAddress(InstitutionAddress address) {
        this.address = address;
        return this;
    }
    
    public Institution withUclInstitution(boolean uclInstitution) {
        this.uclInstitution = uclInstitution;
        return this;
    }

    public Institution withCreatedTimestamp(final DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
        return this;
    }

    public Institution withUpdatedTimestamp(final DateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
        return this;
    }

    @Override
    public System getSystem() {
        return system;
    }

    @Override
    public void setSystem(System system) {
        this.system = system;
    }

    @Override
    public Institution getInstitution() {
        return this;
    }

    @Override
    public void setInstitution(Institution institution) {
    }

    @Override
    public Program getProgram() {
        return null;
    }

    @Override
    public void setProgram(Program program) {
    }

    @Override
    public Project getProject() {
        return null;
    }

    @Override
    public void setProject(Project project) {
    }

    @Override
    public Application getApplication() {
        return null;
    }
    
    @Override
    public String getReferrer() {
        return referrer;
    }
    
    @Override
    public void setReferrer (String referrer) {
        this.referrer = referrer;
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void setState(State state) {
        this.state = state;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public State getPreviousState() {
        return previousState;
    }

    @Override
    public void setPreviousState(State previousState) {
        this.previousState = previousState;
    }

    @Override
    public LocalDate getDueDate() {
        return dueDate;
    }

    @Override
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    @Override
    public DateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    @Override
    public void setCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;

    }

    @Override
    public DateTime getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    @Override
    public void setUpdatedTimestamp(DateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public String getSequenceIdentifier() {
        return sequenceIdentifier;
    }

    @Override
    public void setSequenceIdentifier(String sequenceIdentifier) {
        this.sequenceIdentifier = sequenceIdentifier;
    }
    
    @Override
    public ResourceSignature getResourceSignature() {
        List<HashMap<String, Object>> propertiesWrapper = Lists.newArrayList();
        HashMap<String, Object> properties = Maps.newHashMap();
        properties.put("domicile", domicile);
        properties.put("title", title);
        propertiesWrapper.add(properties);
        return new ResourceSignature(propertiesWrapper);
    }

}
