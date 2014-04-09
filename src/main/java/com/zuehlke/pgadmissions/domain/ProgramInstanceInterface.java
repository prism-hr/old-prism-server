package com.zuehlke.pgadmissions.domain;

import java.util.Date;

public interface ProgramInstanceInterface extends CodeObject {

	StudyOption getStudyOption();

	String getAcademicYear();

	Date getApplicationStartDate();

	Date getApplicationDeadline();

	String getIdentifier();
	
	Boolean isAtasRequired();

}
