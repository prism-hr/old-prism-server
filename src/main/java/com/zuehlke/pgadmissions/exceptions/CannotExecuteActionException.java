package com.zuehlke.pgadmissions.exceptions;

import com.zuehlke.pgadmissions.domain.PrismScope;

public class CannotExecuteActionException extends PrismException {

    private static final long serialVersionUID = -1058592315562054622L;

    private PrismScope prismScope;

    public CannotExecuteActionException(PrismScope prismScope) {
        this.prismScope = prismScope;
    }

    public PrismScope getPrismScope() {
        return prismScope;
    }

}
