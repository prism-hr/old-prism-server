package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity
@Table(name = "APPLICATION_ADDITIONAL_INFORMATION")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplicationAdditionalInformation {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "has_convictions", nullable = false)
    private Boolean hasConvictions;

    @Column(name = "convictions_text")
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 400)
    private String convictionsText;

    @OneToOne(mappedBy = "additionalInformation")
    private Application application;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Boolean getHasConvictions() {
        return hasConvictions;
    }

    public void setHasConvictions(Boolean hasConvictions) {
        this.hasConvictions = hasConvictions;
    }

    public String getConvictionsText() {
        return convictionsText;
    }

    public void setConvictionsText(String convictionsText) {
        this.convictionsText = convictionsText;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public ApplicationAdditionalInformation withId(Integer id) {
        this.id = id;
        return this;
    }

    public ApplicationAdditionalInformation withHasConvictions(Boolean hasConvictions) {
        this.hasConvictions = hasConvictions;
        return this;
    }

    public ApplicationAdditionalInformation withConvictionsText(String convictionsText) {
        this.convictionsText = convictionsText;
        return this;
    }

    public ApplicationAdditionalInformation withApplication(Application application) {
        this.application = application;
        return this;
    }
}
