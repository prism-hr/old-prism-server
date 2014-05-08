package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferErrorHandlingDecision;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferErrorType;

/**
 * I represent the error situation recognized during transfer of application form (PRISM ----> PORTICO). Remark: This enties actually constitute a log that can
 * be accessed by application administrator on UCL side.
 */
@Entity
@Table(name = "APPLICATION_TRANSFER_ERROR")
public class ApplicationTransferError implements Serializable {

    private static final long serialVersionUID = -8609731063290824582L;

    @Id
    @GeneratedValue
    private Long id;

    /** The transfer that I am documenting. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transfer_id")
    private ApplicationTransfer transfer;

    /** Time point when the error happened. */
    @Column(name = "handling_time")
    private Date timepoint;

    /** Diagnostic information (like the stacktrace etc.) */
    @Column(name = "diagnostic_info")
    private String diagnosticInfo;

    /** Webservice request message copy. */
    @Column(name = "request_copy")
    private String requestCopy;

    /** Webservice response message copy. */
    @Column(name = "response_copy")
    private String responseCopy;

    /** Type of the problem as recognized by PRISM. */
    @Enumerated(EnumType.STRING)
    @Column(name = "problem_classification")
    private ApplicationTransferErrorType problemClassification;

    /** Decision (programatically) made by PRISM on how to handle the situation. */
    @Column(name = "error_handling_strategy")
    @Enumerated(EnumType.STRING)
    private ApplicationTransferErrorHandlingDecision errorHandlingStrategy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ApplicationTransfer getTransfer() {
        return transfer;
    }

    public void setTransfer(ApplicationTransfer transfer) {
        this.transfer = transfer;
    }

    public Date getTimepoint() {
        return timepoint;
    }

    public void setTimepoint(Date timepoint) {
        this.timepoint = timepoint;
    }

    public String getDiagnosticInfo() {
        return diagnosticInfo;
    }

    public void setDiagnosticInfo(String diagnosticInfo) {
        this.diagnosticInfo = diagnosticInfo;
    }

    public String getRequestCopy() {
        return requestCopy;
    }

    public void setRequestCopy(String requestCopy) {
        this.requestCopy = requestCopy;
    }

    public String getResponseCopy() {
        return responseCopy;
    }

    public void setResponseCopy(String responseCopy) {
        this.responseCopy = responseCopy;
    }

    public ApplicationTransferErrorType getProblemClassification() {
        return problemClassification;
    }

    public void setProblemClassification(ApplicationTransferErrorType problemClassification) {
        this.problemClassification = problemClassification;
    }

    public ApplicationTransferErrorHandlingDecision getErrorHandlingStrategy() {
        return errorHandlingStrategy;
    }

    public void setErrorHandlingStrategy(ApplicationTransferErrorHandlingDecision errorHandlingStrategy) {
        this.errorHandlingStrategy = errorHandlingStrategy;
    }
}
