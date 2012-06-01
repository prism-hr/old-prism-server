package com.zuehlke.pgadmissions.domain.enums;

import java.util.Comparator;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

public enum SortCategory {

	APPLICANT_NAME(new ApplicantNameCmp()), //
	PROGRAMME_NAME(new ProgrammeNameCmp()), // 
	APPLICATION_STATUS(new ApplicationStatusCmp()), // 
	APPLICATION_DATE(new ApplicationDateCmp());

	private final ApplicationComparator cmp;

	private SortCategory(ApplicationComparator comparator) {
		cmp = comparator;
	}

	public Comparator<ApplicationForm> getComparator(SortOrder order) {
		cmp.setSortOrder(order);
		return cmp;
	}
}

abstract class ApplicationComparator implements Comparator<ApplicationForm> {
	protected int flipper;

	public void setSortOrder(SortOrder sortOrder) {
		flipper = sortOrder == SortOrder.ASCENDING ? 1 : -1;
	}

	@Override
	public final int compare(ApplicationForm app1, ApplicationForm app2) {
		return sortAscending(app1, app2) * flipper;
	}

	protected abstract int sortAscending(ApplicationForm app1, ApplicationForm app2);
}

class ApplicantNameCmp extends ApplicationComparator {

	@Override
	protected int sortAscending(ApplicationForm app1, ApplicationForm app2) {
		RegisteredUser applicant1 = app1.getApplicant();
		RegisteredUser applicant2 = app2.getApplicant();
		return applicant1.compareTo(applicant2);
	}
}

class ProgrammeNameCmp extends ApplicationComparator {

	@Override
	protected int sortAscending(ApplicationForm app1, ApplicationForm app2) {
		return app1.getProgram().getTitle().compareTo(app2.getProgram().getTitle());
	}
}

class ApplicationStatusCmp extends ApplicationComparator {

	@Override
	protected int sortAscending(ApplicationForm app1, ApplicationForm app2) {
		return app1.getStatus().compareTo(app2.getStatus());
	}
}

class ApplicationDateCmp extends ApplicationComparator {

	@Override
	protected int sortAscending(ApplicationForm app1, ApplicationForm app2) {
		return app1.compareTo(app2);
	}
}
