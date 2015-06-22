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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedInstitutionMapping;

@Entity
@Table(name = "IMPORTED_INSTITUTION", uniqueConstraints = { @UniqueConstraint(columnNames = { "imported_domicile_id", "name" }) })
public class ImportedInstitution extends ImportedEntity<ImportedInstitutionMapping> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "imported_domicile_id", nullable = false)
    private ImportedDomicile domicile;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "ucas_id", unique = true)
    private String ucasId;

    @Column(name = "facebook_id", unique = true)
    private String facebookId;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @OneToMany(mappedBy = "importedInstitution")
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

    public ImportedDomicile getDomicile() {
        return domicile;
    }

    public void setDomicile(ImportedDomicile domicile) {
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

    public String getUcasId() {
        return ucasId;
    }

    public void setUcasId(String ucasId) {
        this.ucasId = ucasId;
    }

    public String getFacebookId() {
        return facebookId;
    }

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

    public ImportedInstitution withDomicile(ImportedDomicile domicile) {
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
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("domicile", getDomicile());
    }

}
