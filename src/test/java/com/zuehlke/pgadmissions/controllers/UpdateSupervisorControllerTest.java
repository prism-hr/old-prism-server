package com.zuehlke.pgadmissions.controllers;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ProgrammeDetail;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.domain.enums.AwareStatus;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.services.ProgrammeService;
import com.zuehlke.pgadmissions.services.SupervisorService;

public class UpdateSupervisorControllerTest {

	private UpdateSupervisorController controller;
	private ProgrammeService programmeServiceMock;
	private SupervisorService supervisorServiceMock;
	
	@Test
	public void shouldUpdateSupervisor() {
		ApplicationForm form = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.UNSUBMITTED).applicant(new RegisteredUser()).toApplicationForm();
		Supervisor supervisor = new SupervisorBuilder().id(1).firstname("firstname").lastname("lastname").awareSupervisor(AwareStatus.NO).toSupervisor();
		ProgrammeDetail programmeDetail = new ProgrammeDetailsBuilder().supervisors(supervisor).id(5).applicationForm(form).toProgrammeDetails();
		
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
		supervisor.setFirstname("Mark");
		supervisorServiceMock.save(supervisor);
		EasyMock.replay(errorsMock, supervisorServiceMock);

		ModelAndView modelAndView = controller.editSupervisor(programmeDetail, supervisor, errorsMock);
		EasyMock.verify(supervisorServiceMock);
		Assert.assertEquals("Mark", ((ApplicationPageModel) modelAndView.getModel().get("model")).getApplicationForm().getProgrammeDetails().getSupervisors().get(0).getFirstname());

	}
	
	@Before
	public void setup() {
		

		supervisorServiceMock = EasyMock.createMock(SupervisorService.class);
		programmeServiceMock = EasyMock.createMock(ProgrammeService.class);
		controller = new UpdateSupervisorController(programmeServiceMock, supervisorServiceMock);

	}

}
