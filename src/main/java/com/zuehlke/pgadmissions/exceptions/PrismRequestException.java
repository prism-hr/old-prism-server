package com.zuehlke.pgadmissions.exceptions;

public abstract class PrismRequestException extends RuntimeException {

    public abstract Object getResponseData();

}
