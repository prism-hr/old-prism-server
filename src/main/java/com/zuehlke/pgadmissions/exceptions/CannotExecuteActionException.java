package com.zuehlke.pgadmissions.exceptions;

import com.zuehlke.pgadmissions.domain.PrismResource;

public class CannotExecuteActionException extends PrismException {

    private static final long serialVersionUID = -1058592315562054622L;

    private PrismResource prismScope;

    public CannotExecuteActionException(PrismResource prismScope) {
        this.prismScope = prismScope;
    }

    public PrismResource getPrismScope() {
        return prismScope;
    }

}
