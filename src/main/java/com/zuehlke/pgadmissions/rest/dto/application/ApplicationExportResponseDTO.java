package com.zuehlke.pgadmissions.rest.dto.application;

import javax.validation.constraints.NotNull;

public class ApplicationExportResponseDTO {

    @NotNull
    private Integer userId;

    @NotNull
    private Integer applicationId;

    @NotNull
    private Boolean exportSucceeded;

    private String exportRequest;

    private String exportId;

    private String exportUserId;

    private String exportException;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public Boolean getExportSucceeded() {
        return exportSucceeded;
    }

    public void setExportSucceeded(Boolean exportSucceeded) {
        this.exportSucceeded = exportSucceeded;
    }

    public String getExportRequest() {
        return exportRequest;
    }

    public void setExportRequest(String exportRequest) {
        this.exportRequest = exportRequest;
    }

    public String getExportId() {
        return exportId;
    }

    public void setExportId(String exportId) {
        this.exportId = exportId;
    }

    public String getExportUserId() {
        return exportUserId;
    }

    public void setExportUserId(String exportUserId) {
        this.exportUserId = exportUserId;
    }

    public String getExportException() {
        return exportException;
    }

    public void setExportException(String exportException) {
        this.exportException = exportException;
    }
    
    public ApplicationExportResponseDTO withUserId(Integer userId) {
        this.userId = userId;
        return this;
    }
    
    public ApplicationExportResponseDTO withApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
        return this;
    }
    
    public ApplicationExportResponseDTO withExportSucceeded(Boolean exportSucceeded) {
        this.exportSucceeded = exportSucceeded;
        return this;
    }
    
    public ApplicationExportResponseDTO withExportRequest(String exportRequest) {
        this.exportRequest = exportRequest;
        return this;
    }
    
    public ApplicationExportResponseDTO withExportId(String exportId) {
        this.exportId = exportId;
        return this;
    }
    
    public ApplicationExportResponseDTO withExportUserId(String exportUserId) {
        this.exportUserId = exportUserId;
        return this;
    }
    
    public ApplicationExportResponseDTO withExportException(String exportException) {
        this.exportException = exportException;
        return this;
    }

}
