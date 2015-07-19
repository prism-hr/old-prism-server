package com.zuehlke.pgadmissions.domain.imported;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedInstitutionMapping;
import uk.co.alumeni.prism.api.model.imported.ImportedEntityResponseDefinition;
import uk.co.alumeni.prism.api.model.imported.ImportedInstitutionDefinition;

import javax.persistence.*;
import java.util.Set;

import static com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity.IMPORTED_INSTITUTION;

@Entity
@Table(name = "imported_institution", uniqueConstraints = { @UniqueConstraint(columnNames = { "imported_domicile_id", "name" }) })
@Table(name = "IMPORTED_INSTITUTION", uniqueConstraints = {@UniqueConstraint(columnNames = {"imported_domicile_id", "name"})})
public class ImportedInstitution extends ImportedEntity<Integer, ImportedInstitutionMapping>
        implements ImportedInstitutionDefinition<ImportedEntitySimple>, ImportedEntityResponseDefinition<Integer> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "imported_domicile_id", nullable = false)
    private ImportedEntitySimple domicile;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "ucas_id", unique = true)
    private String ucasId;

    @Column(name = "facebook_id", unique = true)
    private String facebookId;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @OneToMany(mappedBy = "importedEntity")
    private Set<ImportedInstitutionMapping> mappings = Sets.newHashSet();

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

    @Override
    public String getUcasId() {
        return ucasId;
    }

    @Override
    public void setUcasId(String ucasId) {
        this.ucasId = ucasId;
    }

    @Override
    public String getFacebookId() {
        return facebookId;
    }

    @Override
    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
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
    public Set<ImportedInstitutionMapping> getMappings() {
        return mappings;
    }

    public ImportedInstitution withDomicile(ImportedEntitySimple domicile) {
        this.domicile = domicile;
        return this;
    }

    public ImportedInstitution withName(String name) {
        this.name = name;
        return this;
    }

    public ImportedInstitution withUcasId(String ucasId) {
        this.ucasId = ucasId;
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
    public int index() {
        return Objects.hashCode(domicile.getId(), name);
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("domicile", getDomicile());
    }

}
