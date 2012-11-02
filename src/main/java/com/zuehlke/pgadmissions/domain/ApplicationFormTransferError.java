package com.zuehlke.pgadmissions.domain;

import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorHandlingDecision;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorType;
import org.hibernate.annotations.Type;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * I represent the error situation recognized during transfer of application form (PRISM ----> PORTICO).
 */
@Entity(name = "APPLICATION_FORM_TRANSFER_ERROR")
@Access(AccessType.FIELD)
public class ApplicationFormTransferError extends DomainObject<Long> {

    /** The transfer that I am documenting. */
    @ManyToOne
    @JoinColumn(name = "transfer_id")
    private ApplicationFormTransfer transfer;

    /** Time point when the error happened.*/
    @Column(name = "timepoint")
    private Date timepoint;

    /** Diagnostic information (like the stacktrace etc.) */
    @Column(name = "diagnostic_info")
    private String diagnosticInfo;

    /** Type of the problem as recognized by PRISM. */
    @Type(type = "com.zuehlke.pgadmissions.dao.custom.ApplicationFormTransferErrorTypeEnumUserType")
    @Column(name = "context")
    private ApplicationFormTransferErrorType context;

    /** Decision made on how to handle the situation. */
    @Type(type = "com.zuehlke.pgadmissions.dao.custom.ApplicationFormTransferErrorHandlingDecisionEnumUserType")
    @Column(name = "errorHandlingStrategy")
    private ApplicationFormTransferErrorHandlingDecision errorHandlingStrategy;

    @Override
    @Id
    @GeneratedValue
    @Access(AccessType.PROPERTY)
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

}
