package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "INSTITUTION_REFERENCE")
public class QualificationInstitutionReference implements ImportedObject, Serializable {

    private static final long serialVersionUID = 2746228908173552617L;

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "domicile_code")
    private String domicileCode;

    @Column(name = "name")
    private String name;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "code")
    private String code;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getDomicileCode() {
        return domicileCode;
    }

    public void setDomicileCode(String domicileCode) {
        this.domicileCode = domicileCode;
    }

    @Override
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public Date getDisabledDate() {
        return null;
    }

    @Override
    public void setDisabledDate(Date disabledDate) {
        // ignore
    }

}
