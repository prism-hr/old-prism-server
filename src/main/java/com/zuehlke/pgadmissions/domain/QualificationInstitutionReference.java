package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "INSTITUTION_REFERENCE")
public class QualificationInstitutionReference implements SelfReferringImportedObject, Serializable {

    private static final long serialVersionUID = 2746228908173552617L;

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "domicile_code")
    private String domicileCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "enabled_object_id")
    private QualificationInstitutionReference enabledObject;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        if (!enabled && enabledObject != null) {
            return enabledObject.getName();
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getCode() {
        return code;
    }

    public String getStringCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public QualificationInstitutionReference getEnabledObject() {
        return enabledObject;
    }

    @Override
    public void setEnabledObject(SelfReferringImportedObject enabledObject) {
        this.enabledObject = (QualificationInstitutionReference) enabledObject;
    }

    @Override
    public Date getDisabledDate() {
        return null;
    }

    @Override
    public void setDisabledDate(Date disabledDate) {
        // ignore
    }

    public String getDomicileCode() {
        return domicileCode;
    }

    public void setDomicileCode(String domicileCode) {
        this.domicileCode = domicileCode;
    }

}
