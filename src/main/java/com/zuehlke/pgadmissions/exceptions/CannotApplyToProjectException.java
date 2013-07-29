package com.zuehlke.pgadmissions.exceptions;

import com.zuehlke.pgadmissions.domain.Project;

public class CannotApplyToProjectException extends PgadmissionsException {

	private static final long serialVersionUID = -3705962808249413522L;
	
	private Project project;

    public CannotApplyToProjectException(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }

}
