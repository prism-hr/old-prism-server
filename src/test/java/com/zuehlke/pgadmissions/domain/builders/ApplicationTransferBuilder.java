package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationTransfer;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferState;

public class ApplicationTransferBuilder {
    
    private Long id;
    
    private Date createdTimestamp;

    private ApplicationForm applicationForm;

    private Date transferStartTimepoint;

    private Date transferFinishTimepoint;

    private ApplicationTransferState status;

    private String uclUserIdReceived;

    private String uclBookingReferenceReceived;
    
    public ApplicationTransferBuilder id(final Long id) {
        this.id = id;
        return this;
    }

    public ApplicationTransferBuilder createdTimestamp(final Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
        return this;
    }

    public ApplicationTransferBuilder applicationForm(final ApplicationForm form) {
        this.applicationForm = form;
        return this;
    }
    
    public ApplicationTransferBuilder transferStartTimepoint(final Date date) {
        this.transferStartTimepoint = date;
        return this;
    }
    
    public ApplicationTransferBuilder transferFinishTimepoint(final Date date) {
        this.transferFinishTimepoint = date;
        return this;
    }
    
    public ApplicationTransferBuilder status(final ApplicationTransferState status) {
        this.status = status;
        return this;
    }
    
    public ApplicationTransferBuilder uclUserIdReceived(final String id) {
        this.uclUserIdReceived = id;
        return this;
    }
    
    public ApplicationTransferBuilder uclBookingReferenceReceived(final String id) {
        this.uclBookingReferenceReceived = id;
        return this;
    }
    
    public ApplicationTransfer build() {
        ApplicationTransfer transfer = new ApplicationTransfer();
        transfer.setId(id);
        transfer.setCreatedTimestamp(createdTimestamp);
        transfer.setApplicationForm(applicationForm);
        transfer.setState(status);
        transfer.setEndedTimestamp(transferFinishTimepoint);
        transfer.setBeganTimestamp(transferStartTimepoint);
        transfer.setExternalTransferReference(uclBookingReferenceReceived);
        transfer.setExternalApplicantReference(uclUserIdReceived);
        return transfer;
    }
}
