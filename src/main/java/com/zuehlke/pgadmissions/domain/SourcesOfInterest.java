package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "SOURCES_OF_INTEREST")
public class SourcesOfInterest implements ImportedObject, Serializable {

    private static final long serialVersionUID = -3309557608853073374L;

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

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

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isFreeText() {
        return code.equalsIgnoreCase("OTHER") || code.equalsIgnoreCase("NEWS_AD") || code.equalsIgnoreCase("OTH_ACAD ") || code.equalsIgnoreCase("OTH_WEB");
    }

    @Override
    public Date getDisabledDate() {
        return null;
    }

    @Override
    public void setDisabledDate(Date disabledDate) {
        // ignore
    }

    @Override
    public String getStringCode() {
        return code;
    }
}
