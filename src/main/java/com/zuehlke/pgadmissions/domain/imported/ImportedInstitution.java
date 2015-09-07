package com.zuehlke.pgadmissions.domain.imported;

import static com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity.IMPORTED_INSTITUTION;

import java.util.Set;

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

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedInstitutionMapping;
import com.zuehlke.pgadmissions.domain.resource.Institution;

import uk.co.alumeni.prism.api.model.imported.ImportedEntityResponseDefinition;
import uk.co.alumeni.prism.api.model.imported.ImportedInstitutionDefinition;

@Entity
@Table(name = "imported_institution", uniqueConstraints = {@UniqueConstraint(columnNames = {"imported_domicile_id", "name"})})
public class ImportedInstitution extends ImportedEntity<Integer, ImportedInstitutionMapping>
        implements ImportedInstitutionDefinition<ImportedEntitySimple>, ImportedEntityResponseDefinition<Integer>, ImportedEntityIndexable {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "imported_domicile_id", nullable = false)
    private ImportedEntitySimple domicile;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "ucas_ids", unique = true)
    private String ucasIds;

    @Column(name = "facebook_id")
    private String facebookId;

    @Column(name = "hesa_id")
    private Integer hesaId;

    @Column(name = "indexed", nullable = false)
    private Boolean indexed;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @OneToOne(mappedBy = "importedInstitution")
    private Institution institution;

    @OneToMany(mappedBy = "importedEntity")
    private Set<ImportedInstitutionMapping> mappings = Sets.newHashSet();

    @OneToMany(mappedBy = "institution")
    private Set<ImportedInstitutionSubjectArea> institutionSubjectAreas = Sets.newHashSet();

    @OneToMany(mappedBy = "institution")
    private Set<ImportedProgram> programs = Sets.newHashSet();

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
        return IMPORTED_INSTITUTION;
    }

    @Override
    public ImportedEntitySimple getDomicile() {
        return domicile;
    }

    @Override
    public void setDomicile(ImportedEntitySimple domicile) {
        this.domicile = domicile;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getUcasIds() {
        return ucasIds;
    }

    public void setUcasIds(String ucasIds) {
        this.ucasIds = ucasIds;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public Integer getHesaId() {
        return hesaId;
    }

    public void setHesaId(Integer hesaId) {
        this.hesaId = hesaId;
    }

    @Override
    public Boolean getIndexed() {
        return indexed;
    }

    @Override
    public void setIndexed(Boolean indexed) {
        this.indexed = indexed;
    }

    @Override
    public Boolean getEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Institution getInstitution() {
        return institution;
    }

    @Override
    public Set<ImportedInstitutionMapping> getMappings() {
        return mappings;
    }

    public Set<ImportedInstitutionSubjectArea> getInstitutionSubjectAreas() {
        return institutionSubjectAreas;
    }

    public Set<ImportedProgram> getPrograms() {
        return programs;
    }

    public ImportedInstitution withId(Integer id) {
        this.id = id;
        return this;
    }

    public ImportedInstitution withDomicile(ImportedEntitySimple domicile) {
        this.domicile = domicile;
        return this;
    }

    public ImportedInstitution withName(String name) {
        this.name = name;
        return this;
    }

    public ImportedInstitution withUcasIds(String ucasIds) {
        this.ucasIds = ucasIds;
        return this;
    }

    public ImportedInstitution withIndexed(Boolean indexed) {
        this.indexed = indexed;
        return this;
    }

    public ImportedInstitution withFacebookId(String facebookId) {
        this.facebookId = facebookId;
        return this;
    }

    public ImportedInstitution withEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public String getDomicileDisplay() {
        return domicile == null ? null : domicile.toString();
    }

    @Override
    public String index() {
        return domicile.getId().toString() + super.index();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(domicile.getId(), name);
    }

    @Override
    public boolean equals(Object object) {
        if (!super.equals(object)) {
            return false;
        }
        ImportedInstitution other = (ImportedInstitution) object;
        return Objects.equal(domicile, other.getDomicile());
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("domicile", getDomicile());
    }

}
