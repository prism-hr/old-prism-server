package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity
@Table(name = "APPLICATION_FORM_ADDITIONAL_INFO")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AdditionalInformation implements Serializable, FormSectionObject {

    private static final long serialVersionUID = -1761742614792933388L;

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "has_convictions")
    private Boolean convictions;

    @Column(name = "convictions_text")
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 400)
    private String convictionsText;

    @OneToOne(mappedBy = "additionalInformation", fetch = FetchType.LAZY)
    private ApplicationForm application;

    @Transient
    private boolean acceptedTerms;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Boolean getConvictions() {
        return convictions;
    }

    public void setConvictions(Boolean convictions) {
        this.convictions = convictions;
    }

    public String getConvictionsText() {
        return convictionsText;
    }

    public void setConvictionsText(String convictionsText) {
        this.convictionsText = convictionsText;
    }

    public ApplicationForm getApplication() {
        return application;
    }

    public void setApplication(ApplicationForm application) {
        this.application = application;
    }

    public boolean isAcceptedTerms() {
        return acceptedTerms;
    }

    public void setAcceptedTerms(boolean acceptedTerms) {
        this.acceptedTerms = acceptedTerms;
    }

}
