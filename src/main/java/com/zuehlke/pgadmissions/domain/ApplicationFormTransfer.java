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
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferStatus;

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
@Table(name = "APPLICATION_FORM_TRANSFER")
public class ApplicationFormTransfer implements Serializable {

    private static final long serialVersionUID = 9133196638104217546L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "created_timestamp", insertable = false)
    @Generated(GenerationTime.INSERT)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTimestamp;

    /** The application form that constitutes my payload (a payload of the transfer I am representing). */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private ApplicationForm applicationForm;

    /** Timepoint when I was created (so this is the timepoint of scheduling). */
    @Column(name = "transfer_begin_timeppoint")
    private Date transferStartTimepoint;

    /** Timepoint when I was successfully finished. In case of failed transfers this value stays null. */
    @Column(name = "transfer_end_timepoint")
    private Date transferFinishTimepoint;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ApplicationTransferStatus status;

    @Column(name = "ucl_user_id_received")
    private String uclUserIdReceived;

    @Column(name = "ucl_booking_ref_number_received")
    private String uclBookingReferenceReceived;

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

    public Date getTransferStartTimepoint() {
        return transferStartTimepoint;
    }

    public void setTransferStartTimepoint(Date transferStartTimepoint) {
        this.transferStartTimepoint = transferStartTimepoint;
    }

    public Date getTransferFinishTimepoint() {
        return transferFinishTimepoint;
    }

    public void setTransferFinishTimepoint(Date transferFinishTimepoint) {
        this.transferFinishTimepoint = transferFinishTimepoint;
    }

    public ApplicationTransferStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationTransferStatus status) {
        this.status = status;
    }

    public String getUclBookingReferenceReceived() {
        return uclBookingReferenceReceived;
    }

    public void setUclBookingReferenceReceived(String uclBookingReferenceReceived) {
        this.uclBookingReferenceReceived = uclBookingReferenceReceived;
    }

    public String getUclUserIdReceived() {
        return uclUserIdReceived;
    }

    public void setUclUserIdReceived(String uclUserIdReceived) {
        this.uclUserIdReceived = uclUserIdReceived;
    }

}
