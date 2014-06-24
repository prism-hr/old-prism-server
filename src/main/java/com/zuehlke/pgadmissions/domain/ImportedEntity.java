package com.zuehlke.pgadmissions.domain;

import com.google.common.base.Objects;
import org.apache.solr.analysis.*;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Parameter;

import javax.persistence.*;

@AnalyzerDef(name = "importedEntityNameAnalyzer", tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class), filters = {
        @TokenFilterDef(factory = LowerCaseFilterFactory.class), @TokenFilterDef(factory = StopFilterFactory.class),
        @TokenFilterDef(factory = SnowballPorterFilterFactory.class, params = @Parameter(name = "language", value = "English")),
        @TokenFilterDef(factory = ASCIIFoldingFilterFactory.class)})
@Entity
@Table(name = "imported_entity", uniqueConstraints = {@UniqueConstraint(columnNames = {"institution_id", "imported_entity_type", "code"}),
        @UniqueConstraint(columnNames = {"institution_id", "imported_entity_type", "name"})})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "imported_entity_type", discriminatorType = DiscriminatorType.STRING)
@Indexed
public abstract class ImportedEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "name", nullable = false)
    @Field(analyzer = @Analyzer(definition = "importedEntityNameAnalyzer"), index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String name;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

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

    @Override
    public String toString() {
        return Objects.toStringHelper(this.getClass()).add("code", code).add("name", name).toString();
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ImportedEntity other = (ImportedEntity) obj;
        return Objects.equal(this.code, other.code) && Objects.equal(this.name, other.name)
                && Objects.equal(this.enabled, other.enabled);
    }
}
