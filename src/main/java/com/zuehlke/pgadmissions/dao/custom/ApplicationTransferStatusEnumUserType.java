package com.zuehlke.pgadmissions.dao.custom;

import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferStatus;

public class ApplicationTransferStatusEnumUserType extends EnumUserType<ApplicationTransferStatus> {

    public ApplicationTransferStatusEnumUserType() {
        super(ApplicationTransferStatus.class);
    }
}
