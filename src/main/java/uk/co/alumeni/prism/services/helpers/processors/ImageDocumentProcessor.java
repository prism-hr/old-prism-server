package uk.co.alumeni.prism.services.helpers.processors;

public interface ImageDocumentProcessor {

    byte[] process(byte[] content, String contentType);

}
