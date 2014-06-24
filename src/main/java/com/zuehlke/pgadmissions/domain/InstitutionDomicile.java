package com.zuehlke.pgadmissions.domain;

import org.apache.solr.analysis.*;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Parameter;

import javax.persistence.*;

@AnalyzerDef(name = "institutionDomicileNameAnalyzer", tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class), filters = {
        @TokenFilterDef(factory = LowerCaseFilterFactory.class), @TokenFilterDef(factory = StopFilterFactory.class),
        @TokenFilterDef(factory = SnowballPorterFilterFactory.class, params = @Parameter(name = "language", value = "English")),
        @TokenFilterDef(factory = ASCIIFoldingFilterFactory.class)})
@Entity
@Table(name = "INSTITUTION_DOMICILE")
@Indexed
public class InstitutionDomicile {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "name", nullable = false, unique = true)
    @Field(analyzer = @Analyzer(definition = "institutionDomicileNameAnalyzer"), index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String name;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public InstitutionDomicile withId(Integer id) {
        this.id = id;
        return this;
    }

    public InstitutionDomicile withCode(String code) {
        this.code = code;
        return this;
    }

    public InstitutionDomicile withName(String name) {
        this.name = name;
        return this;
    }

    public InstitutionDomicile withEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}
