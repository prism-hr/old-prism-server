package com.zuehlke.pgadmissions.services;

import java.util.List;

import junit.framework.Assert;

import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.RejectReason;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RejectReasonBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/integrationTestContext.xml")
public class RejectServiceIntegrationTest {

	@Autowired
	private RejectService rejectsService;

	@Autowired
	private ApplicationFormDAO applicationDAO;

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private SessionFactory sessionFactory;

	@Test
	@Transactional
	@Rollback(true)
	public void testMoveApplicationToReject() {
		RejectReason reason1 = createReason("r1");
		RejectReason reason2 = createReason("r2");
		sessionFactory.getCurrentSession().saveOrUpdate(reason1);
		sessionFactory.getCurrentSession().saveOrUpdate(reason2);

		ApplicationForm application = new ApplicationForm();
		RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		userDAO.save(user);

		application.setApplicant(user);
		applicationDAO.save(application);
		flushNClear();

		rejectsService.moveApplicationToReject(application, reason1, reason2);
		flushNClear();

		ApplicationForm storedAppl = applicationDAO.get(application.getId());
		
		Assert.assertEquals(ApplicationFormStatus.REJECTED, storedAppl.getStatus());
		List<RejectReason> rejectReasons = storedAppl.getRejectReasons();
		Assert.assertNotNull(rejectReasons);
		Assert.assertEquals(2, rejectReasons.size());
		for (RejectReason storedReason : rejectReasons) {
			String reasonText = storedReason.getText();
			if (!"r1".equals(reasonText) && !"r2".equals(reasonText)) {
				Assert.fail("unexpected reason text: " + reasonText);
			}
		}
	}

	private void flushNClear() {
		sessionFactory.getCurrentSession().flush();
		sessionFactory.getCurrentSession().clear();
	}

	private RejectReason createReason(String text) {
		return new RejectReasonBuilder().text(text).toRejectReason();
	}
}
