package com.zuehlke.pgadmissions.domain;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.solr.analysis.*;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Parameter;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;

@AnalyzerDef(name = "institutionNameAnalyzer", tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class), filters = {
        @TokenFilterDef(factory = LowerCaseFilterFactory.class), @TokenFilterDef(factory = StopFilterFactory.class),
        @TokenFilterDef(factory = SnowballPorterFilterFactory.class, params = @Parameter(name = "language", value = "English")),
        @TokenFilterDef(factory = ASCIIFoldingFilterFactory.class)})
@Entity
@Table(name = "INSTITUTION", uniqueConstraints = {@UniqueConstraint(columnNames = {"institution_domicile_id", "name"})})
@Indexed
public class Institution extends Resource {

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

}
