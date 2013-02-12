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

import org.hibernate.annotations.Type;

import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferStatus;

/**
 * I represent a single transfer of application form.
 * Applications forms are transfered from PRISM system to UCL-PORTICO system.
 * I represent the whole lifecycle of the transfer - from scheduling up to finishing (successfully or unsuccessfully).<p/>
 *
 * Remark: the actual data transfer is supposed to happen via a mixture of webservice published by UCL-PORTICO and SFTP transfer.
 * For any given application form several transfers may happen during the history of the system.
 * Business logic is deciding that at some point given application form should be transferred to UCL by creating
 * an ApplicationFormTransfer instance with status set to QUEUED_FOR_WEBSERVICE_CALL.
 */
@Entity(name = "APPLICATION_FORM_TRANSFER")
public class ApplicationFormTransfer implements Serializable {

    private static final long serialVersionUID = 9133196638104217546L;
    
    @Id
    @GeneratedValue
    private Long id;

    /** The application form that constitutes my payload (a payload of the transfer I am representing). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private ApplicationForm applicationForm;

    /** Timepoint when I was created (so this is the timepoint of scheduling). */
    @Column(name = "transfer_begin_timeppoint")
    private Date transferStartTimepoint;

    /** Timepoint when I was successfully finished. In case of failed transfers this value stays null. */
    @Column(name = "transfer_end_timepoint")
    private Date transferFinishTimepoint;

	@Type(type = "com.zuehlke.pgadmissions.dao.custom.ApplicationTransferStatusEnumUserType")
    @Column(name = "status")
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
