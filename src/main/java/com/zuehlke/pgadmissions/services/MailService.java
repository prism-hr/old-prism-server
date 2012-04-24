package com.zuehlke.pgadmissions.services;

import com.zuehlke.pgadmissions.domain.ApplicationForm;

public class MailService {
	public void printMe(ApplicationForm applicationForm) {
		System.out.println("Print " + applicationForm.getId());
	}
}
