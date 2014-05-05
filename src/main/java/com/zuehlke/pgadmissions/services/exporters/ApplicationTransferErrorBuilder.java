package com.zuehlke.pgadmissions.services.exporters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import org.springframework.ws.FaultAwareWebServiceMessage;

import com.zuehlke.pgadmissions.domain.ApplicationTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationTransferError;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferErrorHandlingDecision;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferErrorType;
import com.zuehlke.pgadmissions.utils.DiagnosticInfoPrintUtils;

public class ApplicationTransferErrorBuilder {

    private ApplicationTransfer transfer;
    
    private Date timepoint = new Date();
    
    private Throwable diagnosticInfo;

    private String requestCopy;

    private String responseCopy;
    
    private ApplicationTransferErrorType errorType;
    
    private ApplicationTransferErrorHandlingDecision errorHandlingStrategy;
    
    public ApplicationTransferErrorBuilder() {
    }
    
    public ApplicationTransferErrorBuilder transfer(final ApplicationTransfer transfer) {
        this.transfer = transfer;
        return this;
    }
    
    public ApplicationTransferErrorBuilder timepoint(final Date date) {
        this.timepoint = date;
        return this;
    }
    
    public ApplicationTransferErrorBuilder diagnosticInfo(final Throwable diagnosticInfo) {
        this.diagnosticInfo = diagnosticInfo;
        return this;
    }
    
    public ApplicationTransferErrorBuilder requestCopy(final String requestCopy) {
        this.requestCopy = requestCopy;
        return this;
    }

    public ApplicationTransferErrorBuilder responseCopy(final String responseCopy) {
        this.responseCopy = responseCopy;
        return this;
    }
    
    public ApplicationTransferErrorBuilder responseCopy(final FaultAwareWebServiceMessage message) {
        ByteArrayOutputStream responseMessageBuffer = new ByteArrayOutputStream(5000);
        try {
            message.writeTo(responseMessageBuffer);
        } catch (IOException e1) {
            // do nothing
        }
        this.responseCopy = responseMessageBuffer.toString();
        return this;
    }
    
    public ApplicationTransferErrorBuilder problemClassification(final ApplicationTransferErrorType errorType) {
        this.errorType = errorType;
        return this;
    }
    
    public ApplicationTransferErrorBuilder errorHandlingStrategy(final ApplicationTransferErrorHandlingDecision errorHandlingStrategy) {
        this.errorHandlingStrategy = errorHandlingStrategy;
        return this;
    }
    
    public ApplicationTransferError build() {
        ApplicationTransferError error = new ApplicationTransferError();
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
