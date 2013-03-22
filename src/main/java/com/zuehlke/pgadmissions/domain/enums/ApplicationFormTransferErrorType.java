package com.zuehlke.pgadmissions.domain.enums;

public enum ApplicationFormTransferErrorType {
    WEBSERVICE_SOAP_FAULT,
    WEBSERVICE_UNREACHABLE,
    SFTP_HOST_UNREACHABLE,
    SFTP_UNEXPECTED_EXCEPTION,
    SFTP_DIRECTORY_NOT_AVAILABLE,
    PORTICO_SFTP_DOCUMENT_HANDLING_PROBLEM
}
