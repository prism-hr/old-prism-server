package com.zuehlke.pgadmissions.domain.imported;

import static com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity.IMPORTED_SUBJECT_AREA;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import jersey.repackaged.com.google.common.base.Objects;
import uk.co.alumeni.prism.api.model.imported.ImportedEntityResponseDefinition;
import uk.co.alumeni.prism.api.model.imported.ImportedSubjectAreaDefinition;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.TargetEntity;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedSubjectAreaMapping;

@Entity
@Table(name = "imported_subject_area")
public class ImportedSubjectArea extends ImportedEntity<Integer, ImportedSubjectAreaMapping> implements TargetEntity,
        ImportedSubjectAreaDefinition, ImportedEntityResponseDefinition<Integer> {

    @Id
    private Integer id;

    @Column(name = "jacs_code", nullable = false, unique = true)
    private String jacsCode;

    @Column(name = "jacs_code_old")
    private String jacsCodeOld;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "ucas_subject", nullable = false)
    private Integer ucasSubject;

    @ManyToOne
    @JoinColumn(name = "parent_imported_subject_area_id")
    private ImportedSubjectArea parent;

    @Column(name = "index_score")
    private BigDecimal indexScore;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @OneToMany(mappedBy = "importedEntity")
    private Set<ImportedSubjectAreaMapping> mappings = Sets.newHashSet();

    @OneToMany(mappedBy = "parent")
    private Set<ImportedSubjectArea> children = Sets.newHashSet();

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public PrismImportedEntity getType() {
        return IMPORTED_SUBJECT_AREA;
    }

    @Override
    public String getJacsCode() {
        return jacsCode;
    }

    @Override
    public void setJacsCode(String jacsCode) {
        this.jacsCode = jacsCode;
    }

    public String getJacsCodeOld() {
        return jacsCodeOld;
    }

    public void setJacsCodeOld(String jacsCodeOld) {
        this.jacsCodeOld = jacsCodeOld;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getUcasSubject() {
        return ucasSubject;
    }

    public void setUcasSubject(Integer ucasSubject) {
        this.ucasSubject = ucasSubject;
    }

    public ImportedSubjectArea getParent() {
        return parent;
    }

    public void setParent(ImportedSubjectArea parent) {
        this.parent = parent;
    }

    public BigDecimal getIndexScore() {
        return indexScore;
    }

    public void setIndexScore(BigDecimal indexScore) {
        this.indexScore = indexScore;
    }

    @Override
    public Boolean getEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Set<ImportedSubjectAreaMapping> getMappings() {
        return mappings;
    }

    public Set<ImportedSubjectArea> getChildren() {
        return children;
    }

    public List<ImportedSubjectArea> getAncestorsPath() {
        List<ImportedSubjectArea> ancestors = getParent() == null ? new LinkedList<>() : getParent().getAncestorsPath();
        ancestors.add(this);
        return ancestors;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        ImportedProgramSubjectArea other = (ImportedProgramSubjectArea) object;
        return Objects.equal(id, other.getId());
    }

    @Override
    public String index() {
        return id.toString();
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addExclusion("id", id);
    }

}