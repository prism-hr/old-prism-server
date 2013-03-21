package com.zuehlke.pgadmissions.exceptions;

import com.zuehlke.pgadmissions.domain.Program;

public class CannotApplyToProgramException extends PgadmissionsException {

    private static final long serialVersionUID = -1058592315562054622L;

    private Program program;

    public CannotApplyToProgramException(Program program) {
        this.program = program;
    }

    public Program getProgram() {
        return program;
    }

}
