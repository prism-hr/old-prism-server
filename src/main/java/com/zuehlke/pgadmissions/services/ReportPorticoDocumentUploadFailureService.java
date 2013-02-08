package com.zuehlke.pgadmissions.services;

import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.mail.DataExportMailSender;

@Service
public class ReportPorticoDocumentUploadFailureService {

    private final DataExportMailSender dataExportMailSender;
    
    public ReportPorticoDocumentUploadFailureService() {
        this(null);
    }
    
    public ReportPorticoDocumentUploadFailureService(DataExportMailSender dataExportMailSender) {
        this.dataExportMailSender = dataExportMailSender;
    }
    
    public void sendErrorMessage(final String message, final Exception exception) {
        this.dataExportMailSender.sendErrorMessage(message, exception);
    }

    public void sendErrorMessage(final String message) {
        this.dataExportMailSender.sendErrorMessage(message);
    }
}
