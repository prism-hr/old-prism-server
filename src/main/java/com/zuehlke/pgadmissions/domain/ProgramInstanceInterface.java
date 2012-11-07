package com.zuehlke.pgadmissions.domain;

import java.util.Date;

public interface ProgramInstanceInterface extends CodeObject {

	Integer getStudyOptionCode();

	String getStudyOption();

	String getAcademic_year();

	Date getApplicationStartDate();

	Date getApplicationDeadline();

}
