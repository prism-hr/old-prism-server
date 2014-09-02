package com.zuehlke.pgadmissions.domain;

import java.util.HashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
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

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@AnalyzerDef(name = "importedInstitutionNameAnalyzer", tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class), filters = {
        @TokenFilterDef(factory = LowerCaseFilterFactory.class), @TokenFilterDef(factory = StopFilterFactory.class),
        @TokenFilterDef(factory = SnowballPorterFilterFactory.class, params = @Parameter(name = "language", value = "English")),
        @TokenFilterDef(factory = ASCIIFoldingFilterFactory.class) })
@Entity
@Table(name = "IMPORTED_INSTITUTION", uniqueConstraints = { @UniqueConstraint(columnNames = { "institution_id", "domicile_id", "code" }),
        @UniqueConstraint(columnNames = { "institution_id", "domicile_id", "name" }) })
@Indexed
public class ImportedInstitution extends ImportedEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @ManyToOne
    @JoinColumn(name = "domicile_id", nullable = false)
    private Domicile domicile;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "name", nullable = false)
    @Field(analyzer = @Analyzer(definition = "importedInstitutionNameAnalyzer"), index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String name;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public Domicile getDomicile() {
        return domicile;
    }

    public void setDomicile(Domicile domicile) {
        this.domicile = domicile;
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

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public ImportedInstitution withId(Integer id) {
        this.id = id;
        return this;
    }

    public ImportedInstitution withInstitution(Institution institution) {
        this.institution = institution;
        return this;
    }

    public ImportedInstitution withDomicile(Domicile domicile) {
        this.domicile = domicile;
        return this;
    }

    public ImportedInstitution withName(String name) {
        this.name = name;
        return this;
    }

    public ImportedInstitution withEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public ImportedInstitution withCode(String code) {
        this.code = code;
        return this;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(institution, domicile, code, name);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final ImportedInstitution other = (ImportedInstitution) object;
        return Objects.equal(institution, other.getInstitution()) && Objects.equal(domicile, other.getDomicile())
                && Objects.equal(code, other.getCode()) && Objects.equal(name, other.getName());
    }
    
    @Override
    public ResourceSignature getResourceSignature() {
        List<HashMap<String, Object>> propertiesWrapper = Lists.newArrayList();
        HashMap<String, Object> properties1 = Maps.newHashMap();
        properties1.put("institution", institution);
        properties1.put("domicile", domicile);
        properties1.put("code", code);
        propertiesWrapper.add(properties1);
        HashMap<String, Object> properties2 = Maps.newHashMap();
        properties2.put("institution", institution);
        properties2.put("domicile", domicile);
        properties2.put("name", name);
        propertiesWrapper.add(properties2);
        return new ResourceSignature(propertiesWrapper);
    }
    
}
