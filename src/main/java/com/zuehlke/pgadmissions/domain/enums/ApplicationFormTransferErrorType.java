package com.zuehlke.pgadmissions.domain.enums;

public enum ApplicationFormTransferErrorType {
    WEBSERVICE_SOAP_FAULT,
    WEBSERVICE_PUBLISHER_SECURITY_PROBLEM,
    WEBSERVICE_UNREACHABLE,
    WEBSERVICE_UNEXPECTED_EXCEPTION,
    SFTP_HOST_UNREACHABLE,
    SFTP_LOGIN_FAILED,
    SFTP_UNEXPECTED_EXCEPTION,
    SFTP_DIRECTORY_NOT_AVAILABLE,
    PORTICO_SFTP_DOCUMENT_HANDLING_PROBLEM
}
