package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.solr.analysis.ASCIIFoldingFilterFactory;
import org.apache.solr.analysis.LowerCaseFilterFactory;
import org.apache.solr.analysis.SnowballPorterFilterFactory;
import org.apache.solr.analysis.StandardTokenizerFactory;
import org.apache.solr.analysis.StopFilterFactory;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;

@AnalyzerDef(name = "institutionNameAnalyzer", tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class), filters = {
        @TokenFilterDef(factory = LowerCaseFilterFactory.class), @TokenFilterDef(factory = StopFilterFactory.class),
        @TokenFilterDef(factory = SnowballPorterFilterFactory.class, params = @Parameter(name = "language", value = "English")),
        @TokenFilterDef(factory = ASCIIFoldingFilterFactory.class) })
@Entity
@Table(name = "INSTITUTION")
@Indexed
public class Institution extends PrismScope {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "system_id", nullable = false)
    private PrismSystem system;

    @ManyToOne
    @JoinColumn(name = "institution_domicile_id", nullable = false)
    private InstitutionDomicile domicile;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "homepage", nullable = false)
    private String homepage;

    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public PrismSystem getSystem() {
        return system;
    }

    public void setSystem(PrismSystem system) {
        this.system = system;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
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

    public Institution withSystem(PrismSystem system) {
        this.system = system;
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

    @Override
    public String getScopeName() {
        return "institution";
    }

    @Override
    public Institution getInstitution() {
        return this;
    }

    @Override
    public Program getProgram() {
        return null;
    }

    @Override
    public Project getProject() {
        return null;
    }

    @Override
    public Application getApplication() {
        return null;
    }
}
