package com.zuehlke.pgadmissions.dao.custom;

import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorType;

public class ApplicationFormTransferErrorTypeEnumUserType extends EnumUserType<ApplicationFormTransferErrorType> {

    public ApplicationFormTransferErrorTypeEnumUserType() {
        super(ApplicationFormTransferErrorType.class);
    }
}
