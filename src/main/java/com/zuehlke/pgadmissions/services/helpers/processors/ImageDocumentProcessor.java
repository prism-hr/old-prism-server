package com.zuehlke.pgadmissions.services.helpers.processors;

public interface ImageDocumentProcessor {

    public byte[] process(byte[] content, String contentType) throws Exception;
    
}
