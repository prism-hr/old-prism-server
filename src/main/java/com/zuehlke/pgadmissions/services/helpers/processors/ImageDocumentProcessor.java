package com.zuehlke.pgadmissions.services.helpers.processors;

public interface ImageDocumentProcessor {

    byte[] process(byte[] content, String contentType);

}
