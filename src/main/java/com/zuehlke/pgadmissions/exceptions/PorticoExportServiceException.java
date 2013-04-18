package com.zuehlke.pgadmissions.exceptions;

import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorHandlingDecision;

public class PorticoExportServiceException extends Exception {

    private static final long serialVersionUID = 6463518543774917277L;

    private final ApplicationFormTransferError transferError;
    
    public PorticoExportServiceException(ApplicationFormTransferError errorHandlingStrategy) {
        super();
        this.transferError = errorHandlingStrategy;
    }

    public PorticoExportServiceException(final String message, final ApplicationFormTransferError errorHandlingStrategy) {
        super(message);
        this.transferError = errorHandlingStrategy;
    }

    public PorticoExportServiceException(final Throwable cause, final ApplicationFormTransferError errorHandlingStrategy) {
        super(cause);
        this.transferError = errorHandlingStrategy;
    }

    public PorticoExportServiceException(final String message, final Throwable cause, final ApplicationFormTransferError errorHandlingStrategy) {
        super(message, cause);
        this.transferError = errorHandlingStrategy;
    }

    public ApplicationFormTransferErrorHandlingDecision getErrorHandlingStrategy() {
        return transferError.getErrorHandlingStrategy();
    }
    
    public ApplicationFormTransferError getTransferError() {
        return transferError;
    }
}
