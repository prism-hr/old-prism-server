package com.zuehlke.pgadmissions.dao.custom;

import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorHandlingDecision;

public class ApplicationFormTransferErrorHandlingDecisionEnumUserType extends EnumUserType<ApplicationFormTransferErrorHandlingDecision> {

    public ApplicationFormTransferErrorHandlingDecisionEnumUserType() {
        super(ApplicationFormTransferErrorHandlingDecision.class);
    }
}
