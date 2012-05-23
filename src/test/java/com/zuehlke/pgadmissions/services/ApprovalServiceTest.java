package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ApprovalRoundDAO;
import com.zuehlke.pgadmissions.dao.ReviewRoundDAO;
import com.zuehlke.pgadmissions.dao.StageDurationDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.StageDurationBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;

public class ApprovalServiceTest {

	private ApprovalService approvalService;

	private ApplicationFormDAO applicationFormDAOMock;
	private ApprovalRoundDAO approvalRoundDAOMock;

	private StageDurationDAO stageDurationDAOMock;

	@Before
	public void setUp() {

		applicationFormDAOMock = EasyMock.createMock(ApplicationFormDAO.class);
		approvalRoundDAOMock = EasyMock.createMock(ApprovalRoundDAO.class);
		stageDurationDAOMock = EasyMock.createMock(StageDurationDAO.class);
		approvalService = new ApprovalService(applicationFormDAOMock, approvalRoundDAOMock, stageDurationDAOMock);
	}

	@Test
	public void shouldSetDueDateOnApplicationUpdateFormAndSaveBoth() throws ParseException {

		ApprovalRound approvalRound = new ApprovalRoundBuilder().id(1).toApprovalRound();
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).id(1).toApplicationForm();
		EasyMock.expect(stageDurationDAOMock.getByStatus(ApplicationFormStatus.APPROVAL)).andReturn(
				new StageDurationBuilder().duration(2).unit(DurationUnitEnum.DAYS).toStageDuration());
		approvalRoundDAOMock.save(approvalRound);
		applicationFormDAOMock.save(applicationForm);
		EasyMock.replay(approvalRoundDAOMock, applicationFormDAOMock, stageDurationDAOMock);

		approvalService.moveApplicationToApproval(applicationForm, approvalRound);
		assertEquals(DateUtils.truncate(DateUtils.addDays(new Date(), 2), Calendar.DATE), DateUtils.truncate(applicationForm.getDueDate(), Calendar.DATE));
		assertEquals(applicationForm, approvalRound.getApplication());
		assertEquals(approvalRound, applicationForm.getLatestApprovalRound());
		assertEquals(ApplicationFormStatus.APPROVAL, applicationForm.getStatus());
		EasyMock.verify(approvalRoundDAOMock, applicationFormDAOMock);

	}

	@Test
	public void shouldMoveToApprovalIfInApproval() throws ParseException {

		ApprovalRound approvalRound = new ApprovalRoundBuilder().id(1).toApprovalRound();
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVAL).id(1).toApplicationForm();
		EasyMock.expect(stageDurationDAOMock.getByStatus(ApplicationFormStatus.APPROVAL)).andReturn(
				new StageDurationBuilder().duration(2).unit(DurationUnitEnum.DAYS).toStageDuration());
		approvalRoundDAOMock.save(approvalRound);
		applicationFormDAOMock.save(applicationForm);
		EasyMock.replay(approvalRoundDAOMock, applicationFormDAOMock, stageDurationDAOMock);
		approvalService.moveApplicationToApproval(applicationForm, approvalRound);
		EasyMock.verify(approvalRoundDAOMock, applicationFormDAOMock);

	}

	@Test
	public void shouldFailIfApplicationInInvalidState() {
		ApplicationFormStatus[] values = ApplicationFormStatus.values();
		for (ApplicationFormStatus status : values) {
			if (status != ApplicationFormStatus.VALIDATION && status != ApplicationFormStatus.APPROVAL) {
				ApplicationForm application = new ApplicationFormBuilder().id(3).status(status).toApplicationForm();
				boolean threwException = false;
				try {
					approvalService.moveApplicationToApproval(application, new ApprovalRoundBuilder().id(1).toApprovalRound());
				} catch (IllegalStateException ise) {
					if (ise.getMessage().equals("Application in invalid status: '" + status + "'!")) {
						threwException = true;
					}
				}
				Assert.assertTrue(threwException);
			}
		}
	}
	
	@Test
	public void shouldSaveReviewRound(){
		ApprovalRound approvalRound = new ApprovalRoundBuilder().id(5).toApprovalRound();
		approvalRoundDAOMock.save(approvalRound);
		EasyMock.replay(approvalRoundDAOMock);
		approvalService.save(approvalRound);
		EasyMock.verify(approvalRoundDAOMock);
	}

}
