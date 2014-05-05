package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferState;

/**
 * I represent a single transfer of application form. Applications forms are transfered from PRISM system to UCL-PORTICO system. I represent the whole lifecycle
 * of the transfer - from scheduling up to finishing (successfully or unsuccessfully).
 * <p/>
 * 
 * Remark: the actual data transfer is supposed to happen via a mixture of webservice published by UCL-PORTICO and SFTP transfer. For any given application form
 * several transfers may happen during the history of the system. Business logic is deciding that at some point given application form should be transferred to
 * UCL by creating an ApplicationFormTransfer instance with status set to QUEUED_FOR_WEBSERVICE_CALL.
 */
@Entity
@Table(name = "APPLICATION_TRANSFER")
public class ApplicationTransfer implements Serializable {

    private static final long serialVersionUID = 9133196638104217546L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "created_timestamp", insertable = false)
    @Generated(GenerationTime.INSERT)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTimestamp;

    /** The application form that constitutes my payload (a payload of the transfer I am representing). */
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "transfer")
    private ApplicationForm applicationForm;

    /** Timepoint when I was created (so this is the timepoint of scheduling). */
    @Column(name = "began_timestamp")
    private Date beganTimestamp;

    /** Timepoint when I was successfully finished. In case of failed transfers this value stays null. */
    @Column(name = "ended_timestamp")
    private Date endedTimestamp;

    @Column(name = "application_transfer_state_id")
    @Enumerated(EnumType.STRING)
    private ApplicationTransferState state;

    @Column(name = "external_applicant_reference")
    private String externalApplicantReference;

    @Column(name = "external_transfer_reference")
    private String externalTransferReference;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public ApplicationForm getApplicationForm() {
        return applicationForm;
    }

    public void setApplicationForm(ApplicationForm applicationForm) {
        this.applicationForm = applicationForm;
    }

    public Date getBeganTimestamp() {
        return beganTimestamp;
    }

    public void setBeganTimestamp(Date beganTimestamp) {
        this.beganTimestamp = beganTimestamp;
    }

    public Date getEndedTimestamp() {
        return endedTimestamp;
    }

    public void setEndedTimestamp(Date endedTimestamp) {
        this.endedTimestamp = endedTimestamp;
    }

    public ApplicationTransferState getState() {
        return state;
    }

    public void setState(ApplicationTransferState state) {
        this.state = state;
    }

    public String getExternalTransferReference() {
        return externalTransferReference;
    }

    public void setExternalTransferReference(String externalTransferReference) {
        this.externalTransferReference = externalTransferReference;
    }

    public String getExternalApplicantReference() {
        return externalApplicantReference;
    }

    public void setExternalApplicantReference(String externalApplicantReference) {
        this.externalApplicantReference = externalApplicantReference;
    }

}
