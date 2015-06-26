package com.zuehlke.pgadmissions.domain.comment;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;

@Embeddable
public class CommentExport {

    @Lob
    @Column(name = "application_export_request")
    private String exportRequest;

    @Column(name = "application_export_reference")
    private String exportReference;

    @Lob
    @Column(name = "application_export_exception")
    private String exportException;

    public String getExportRequest() {
        return exportRequest;
    }

    public void setExportRequest(String exportRequest) {
        this.exportRequest = exportRequest;
    }

    public String getExportReference() {
        return exportReference;
    }

    public void setExportReference(String exportReference) {
        this.exportReference = exportReference;
    }

    public String getExportException() {
        return exportException;
    }

    public void setExportException(String exportException) {
        this.exportException = exportException;
    }
    
    public CommentExport withExportRequest(String exportRequest) {
        this.exportRequest = exportRequest;
        return this;
    }
    
    public CommentExport withExportReference(String exportReference) {
        this.exportReference = exportReference;
        return this;
    }
    
    public CommentExport withExportException(String exportException) {
        this.exportException = exportException;
        return this;
    }
    
}
