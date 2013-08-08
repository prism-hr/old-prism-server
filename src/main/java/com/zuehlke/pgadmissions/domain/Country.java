package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name = "COUNTRIES")
public class Country implements ImportedObject, Serializable {

    private static final long serialVersionUID = 2746228908173552617L;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "code")
    private String code;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 100)
    private String name;

    @Id
    @GeneratedValue
    private Integer id;

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
    public Date getDisabledDate() {
        return null;
    }

    @Override
    public void setDisabledDate(Date disabledDate) {
        // ignore
    }

}
