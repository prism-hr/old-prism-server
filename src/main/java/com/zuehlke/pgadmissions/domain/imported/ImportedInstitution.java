package com.zuehlke.pgadmissions.domain.imported;

import static com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity.INSTITUTION;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.institution.Institution;

@Entity
@Table(name = "IMPORTED_INSTITUTION", uniqueConstraints = { @UniqueConstraint(columnNames = { "institution_id", "code" }) })
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

    @Column(name = "code")
    private String code;

    @Lob
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @Column(name = "custom", nullable = false)
    private Boolean custom;

    @Column(name = "ucas_id")
    private String ucasId;

    @Column(name = "facebook_id")
    private String facebookId;

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
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Institution getInstitution() {
        return institution;
    }

    @Override
    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    @Override
    public PrismImportedEntity getType() {
        return INSTITUTION;
    }

    public Domicile getDomicile() {
        return domicile;
    }

    public void setDomicile(Domicile domicile) {
        this.domicile = domicile;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
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
    public Boolean getEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public final Boolean getCustom() {
        return custom;
    }

    public final void setCustom(Boolean custom) {
        this.custom = custom;
    }

    public ImportedInstitution withFacebookId(String facebookId) {
        this.setFacebookId(facebookId);
        return this;
    }

    public ImportedInstitution withUcasId(String ucasId){
        this.setUcasId(ucasId);
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

    public ImportedInstitution withCode(String code) {
        this.code = code;
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

    public ImportedInstitution withCustom(Boolean custom) {
        this.custom = custom;
        return this;
    }

    public String getDomicileDisplay() {
        return domicile == null ? null : domicile.toString();
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("institution", getInstitution()).addProperty("code", getCode());
    }

}
