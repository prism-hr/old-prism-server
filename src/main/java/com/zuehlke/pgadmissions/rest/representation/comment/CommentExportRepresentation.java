package com.zuehlke.pgadmissions.rest.representation.comment;

public class CommentExportRepresentation {

    private String exportRequest;

    private String exportException;

    private String exportReference;

    public String getExportRequest() {
        return exportRequest;
    }

    public void setExportRequest(String exportRequest) {
        this.exportRequest = exportRequest;
    }

    public String getExportException() {
        return exportException;
    }

    public void setExportException(String exportException) {
        this.exportException = exportException;
    }

    public String getExportReference() {
        return exportReference;
    }

    public void setExportReference(String exportReference) {
        this.exportReference = exportReference;
    }
    
    public CommentExportRepresentation withExportRequest(String exportRequest) {
        this.exportRequest = exportRequest;
        return this;
    }
    
    public CommentExportRepresentation withExportException(String exportException) {
        this.exportException = exportException;
        return this;
    }
    
    public CommentExportRepresentation withExportReference(String exportReference) {
        this.exportReference = exportReference;
        return this;
    }
    
}
