package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferStatus;

public class ApplicationFormTransferBuilder {
    
    private Long id;

    private ApplicationForm applicationForm;

    private Date transferStartTimepoint;

    private Date transferFinishTimepoint;

    private ApplicationTransferStatus status;

    private String uclUserIdReceived;

    private String uclBookingReferenceReceived;

    public ApplicationFormTransferBuilder id(final Long id) {
        this.id = id;
        return this;
    }

    public ApplicationFormTransferBuilder applicationForm(final ApplicationForm form) {
        this.applicationForm = form;
        return this;
    }
    
    public ApplicationFormTransferBuilder transferStartTimepoint(final Date date) {
        this.transferStartTimepoint = date;
        return this;
    }
    
    public ApplicationFormTransferBuilder transferFinishTimepoint(final Date date) {
        this.transferFinishTimepoint = date;
        return this;
    }
    
    public ApplicationFormTransferBuilder status(final ApplicationTransferStatus status) {
        this.status = status;
        return this;
    }
    
    public ApplicationFormTransferBuilder uclUserIdReceived(final String id) {
        this.uclUserIdReceived = id;
        return this;
    }
    
    public ApplicationFormTransferBuilder uclBookingReferenceReceived(final String id) {
        this.uclBookingReferenceReceived = id;
        return this;
    }
    
    public ApplicationFormTransfer build() {
        ApplicationFormTransfer transfer = new ApplicationFormTransfer();
        transfer.setApplicationForm(applicationForm);
        transfer.setId(id);
        transfer.setStatus(status);
        transfer.setTransferFinishTimepoint(transferFinishTimepoint);
        transfer.setTransferStartTimepoint(transferStartTimepoint);
        transfer.setUclBookingReferenceReceived(uclBookingReferenceReceived);
        transfer.setUclUserIdReceived(uclUserIdReceived);
        return transfer;
    }
}
