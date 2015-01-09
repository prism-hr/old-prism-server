package com.zuehlke.pgadmissions.domain.imported;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.institution.Institution;

@Entity
@Table(name = "IMPORTED_INSTITUTION", uniqueConstraints = { @UniqueConstraint(columnNames = { "institution_id", "domicile_id", "code" }) })
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

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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
        return Objects.equal(institution, other.getInstitution()) && Objects.equal(domicile, other.getDomicile()) && Objects.equal(code, other.getCode())
                && Objects.equal(name, other.getName());
    }

    public String getDomicileDisplay() {
        return domicile == null ? null : domicile.toString();
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("institution", getInstitution()).addProperty("code", getCode());
    }

}
