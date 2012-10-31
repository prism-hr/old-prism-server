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
 * Stores information about a single transfer of application from PRISM to UCL-PORTICO system.
 * This transfer is supposed to happen via a mixture of webservice published by UCL-PORTICO and SFTP transfer.
 * For any given application several transfers may happen during the history of the system.
 */
@Entity(name = "APPLICATION_TRANSFER_LOG_ITEM")
@Access(AccessType.FIELD)
public class ApplicationTransferLogItem extends DomainObject<Long> {

    @ManyToOne
    @JoinColumn(name = "application_id")
    private ApplicationForm application;

    @Column(name = "transfer_begin_timepoint")
    private Date transferStartTimepoint;

    @Column(name = "transfer_end_timepoint")
    private Date transferFinishTimepoint;

    @Column(name = "was_webservice_call_successful")
    private Boolean wasWebserviceCallSuccessful;

    @Column(name = "was_files_transfer_successful")
    private Boolean wasFilesTransferSuccessful;

	@Type(type = "com.zuehlke.pgadmissions.dao.custom.ApplicationTransferStatusEnumUserType")
    @Column(name = "status")
    private ApplicationTransferStatus status;

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    @Id
    @GeneratedValue
    @Access(AccessType.PROPERTY)
    public Long getId() {
        return id;
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

    public Boolean getWasWebserviceCallSuccessful() {
        return wasWebserviceCallSuccessful;
    }

    public void setWasWebserviceCallSuccessful(Boolean wasWebserviceCallSuccessful) {
        this.wasWebserviceCallSuccessful = wasWebserviceCallSuccessful;
    }

    public Boolean getWasFilesTransferSuccessful() {
        return wasFilesTransferSuccessful;
    }

    public void setWasFilesTransferSuccessful(Boolean wasFilesTransferSuccessful) {
        this.wasFilesTransferSuccessful = wasFilesTransferSuccessful;
    }

    public ApplicationTransferStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationTransferStatus status) {
        this.status = status;
    }
}
