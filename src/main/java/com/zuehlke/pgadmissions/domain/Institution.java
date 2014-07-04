package com.zuehlke.pgadmissions.domain;

import java.util.HashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@AnalyzerDef(name = "institutionNameAnalyzer", tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class), filters = {
        @TokenFilterDef(factory = LowerCaseFilterFactory.class), @TokenFilterDef(factory = StopFilterFactory.class),
        @TokenFilterDef(factory = SnowballPorterFilterFactory.class, params = @Parameter(name = "language", value = "English")),
        @TokenFilterDef(factory = ASCIIFoldingFilterFactory.class)})
@Entity
@Table(name = "INSTITUTION", uniqueConstraints = {@UniqueConstraint(columnNames = {"institution_domicile_id", "name"})})
@Indexed
public class Institution extends ResourceDynamic {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "system_id", nullable = false)
    private System system;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "institution_domicile_id", nullable = false)
    private InstitutionDomicile domicile;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "name", nullable = false)
    @Field(analyzer = @Analyzer(definition = "institutionNameAnalyzer"), index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String name;

    @Column(name = "homepage", nullable = false)
    private String homepage;

    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    private State state;
    
    @ManyToOne(fetch = FetchType.LAZY)
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

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public Institution withId(Integer id) {
        this.id = id;
        return this;
    }

    public Institution withCode(String code) {
        this.code = code;
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

    public Institution withName(String name) {
        this.name = name;
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

    public Institution withInitialData(String name) {
        this.name = Preconditions.checkNotNull(name);
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
    public State getState() {
        return state;
    }

    @Override
    public void setState(State state) {
        this.state = state;
    }

    @Override
    public Application getApplication() {
        return null;
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
    public ResourceSignature getResourceSignature() {
        List<HashMap<String, Object>> propertiesWrapper = Lists.newArrayList();
        HashMap<String, Object> properties1 = Maps.newHashMap();
        properties1.put("name", name);
        propertiesWrapper.add(properties1);
        HashMap<String, Object> properties2 = Maps.newHashMap();
        properties2.put("code", code);
        propertiesWrapper.add(properties2);
        return new ResourceSignature(propertiesWrapper);
    }

    @Override
    public State getPreviousState() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setPreviousState(State previousState) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public LocalDate getDueDate() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setDueDate(LocalDate dueDate) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String generateCode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LocalDate getDueDateBaseline() {
        return new LocalDate();
    }

    @Override
    public DateTime getCreatedTimestamp() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setCreatedTimestamp(DateTime createdTimestamp) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public DateTime getUpdatedTimestamp() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setUpdatedTimestamp(DateTime updatedTimestamp) {
        // TODO Auto-generated method stub
        
    }

}
