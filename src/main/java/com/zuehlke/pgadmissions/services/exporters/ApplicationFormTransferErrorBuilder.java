package com.zuehlke.pgadmissions.services.exporters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import org.springframework.ws.FaultAwareWebServiceMessage;

import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorHandlingDecision;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorType;
import com.zuehlke.pgadmissions.utils.DiagnosticInfoPrintUtils;

public class ApplicationFormTransferErrorBuilder {

    private ApplicationFormTransfer transfer;
    
    private Date timepoint = new Date();
    
    private Throwable diagnosticInfo;

    private String requestCopy;

    private String responseCopy;
    
    private ApplicationFormTransferErrorType errorType;
    
    private ApplicationFormTransferErrorHandlingDecision errorHandlingStrategy;
    
    public ApplicationFormTransferErrorBuilder() {
    }
    
    public ApplicationFormTransferErrorBuilder transfer(final ApplicationFormTransfer transfer) {
        this.transfer = transfer;
        return this;
    }
    
    public ApplicationFormTransferErrorBuilder timepoint(final Date date) {
        this.timepoint = date;
        return this;
    }
    
    public ApplicationFormTransferErrorBuilder diagnosticInfo(final Throwable diagnosticInfo) {
        this.diagnosticInfo = diagnosticInfo;
        return this;
    }
    
    public ApplicationFormTransferErrorBuilder requestCopy(final String requestCopy) {
        this.requestCopy = requestCopy;
        return this;
    }

    public ApplicationFormTransferErrorBuilder responseCopy(final String responseCopy) {
        this.responseCopy = responseCopy;
        return this;
    }
    
    public ApplicationFormTransferErrorBuilder responseCopy(final FaultAwareWebServiceMessage message) {
        ByteArrayOutputStream responseMessageBuffer = new ByteArrayOutputStream(5000);
        try {
            message.writeTo(responseMessageBuffer);
        } catch (IOException e1) {
            // do nothing
        }
        this.responseCopy = responseMessageBuffer.toString();
        return this;
    }
    
    public ApplicationFormTransferErrorBuilder problemClassification(final ApplicationFormTransferErrorType errorType) {
        this.errorType = errorType;
        return this;
    }
    
    public ApplicationFormTransferErrorBuilder errorHandlingStrategy(final ApplicationFormTransferErrorHandlingDecision errorHandlingStrategy) {
        this.errorHandlingStrategy = errorHandlingStrategy;
        return this;
    }
    
    public ApplicationFormTransferError build() {
        ApplicationFormTransferError error = new ApplicationFormTransferError();
        error.setTransfer(transfer);
        error.setTimepoint(timepoint);
        error.setProblemClassification(errorType);
        error.setDiagnosticInfo(DiagnosticInfoPrintUtils.printRootCauseStackTrace(diagnosticInfo));
        error.setErrorHandlingStrategy(errorHandlingStrategy);
        error.setRequestCopy(requestCopy);
        error.setResponseCopy(responseCopy);
        return error;
    }
}
