package com.zuehlke.pgadmissions.exceptions;

import com.zuehlke.pgadmissions.domain.ApplicationTransferError;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferErrorHandlingDecision;

public class ExportServiceException extends Exception {

    private static final long serialVersionUID = 6463518543774917277L;

    private final ApplicationTransferError transferError;
    
    public ExportServiceException(ApplicationTransferError errorHandlingStrategy) {
        super();
        this.transferError = errorHandlingStrategy;
    }

    public ExportServiceException(final String message, final ApplicationTransferError errorHandlingStrategy) {
        super(message);
        this.transferError = errorHandlingStrategy;
    }

    public ExportServiceException(final Throwable cause, final ApplicationTransferError errorHandlingStrategy) {
        super(cause);
        this.transferError = errorHandlingStrategy;
    }

    public ExportServiceException(final String message, final Throwable cause, final ApplicationTransferError errorHandlingStrategy) {
        super(message, cause);
        this.transferError = errorHandlingStrategy;
    }

    public ApplicationTransferErrorHandlingDecision getErrorHandlingStrategy() {
        return transferError.getErrorHandlingStrategy();
    }
    
    public ApplicationTransferError getTransferError() {
        return transferError;
    }
}
