package com.zuehlke.pgadmissions.services.uclexport;

import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;

class DeafListener implements TransferListener {
    @Override
    public void queued() {
        //ignore by design
    }

    @Override
    public void transferStarted() {
        //ignore by design
    }

    @Override
    public void webserviceCallCompleted() {
        //ignore by design
    }

    @Override
    public void attachmentsTransferStarted() {
        //ignore by design
    }

    @Override
    public void transferCompleted(String uclUserId, String uclBookingReferenceNumber) {
        //ignore by design
    }

    @Override
    public void transferFailed(ApplicationFormTransferError error) {
        //ignore by design
    }
}
