package com.zuehlke.pgadmissions.domain;

import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferStatus;
import org.hibernate.annotations.Type;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

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
@Access(AccessType.FIELD)
public class ApplicationFormTransfer extends DomainObject<Long> {

    /** The application form that constitutes my payload (a payload of the transfer I am representing). */
    @ManyToOne
    @JoinColumn(name = "application_id")
    private ApplicationForm application;

    /** Timepoint when I was created (so this is the timepoint of scheduling). */
    @Column(name = "transfer_begin_timepoint")
    private Date transferStartTimepoint;

    /** Timepoint when I was successfully finished. In case of failed transfers this value stays null. */
    @Column(name = "transfer_end_timepoint")
    private Date transferFinishTimepoint;

	@Type(type = "com.zuehlke.pgadmissions.dao.custom.ApplicationTransferStatusEnumUserType")
    @Column(name = "status")
    private ApplicationTransferStatus status;

    @Column(name = "ucl_ref_number")
    private String uclBookingReferenceReceived;

    @Column(name = "ucl")
    private String uclUserIdReceived;

    @Override
    @Id
    @GeneratedValue
    @Access(AccessType.PROPERTY)
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public ApplicationForm getApplication() {
        return application;
    }

    public void setApplication(ApplicationForm application) {
        this.application = application;
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
}
