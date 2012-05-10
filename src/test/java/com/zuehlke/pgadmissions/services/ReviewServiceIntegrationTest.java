//package com.zuehlke.pgadmissions.services;
//
//import junit.framework.Assert;
//
//import org.hibernate.SessionFactory;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.zuehlke.pgadmissions.dao.ProgramDAO;
//import com.zuehlke.pgadmissions.dao.RoleDAO;
//import com.zuehlke.pgadmissions.dao.UserDAO;
//import com.zuehlke.pgadmissions.domain.Program;
//import com.zuehlke.pgadmissions.domain.RegisteredUser;
//import com.zuehlke.pgadmissions.domain.enums.Authority;
//import com.zuehlke.pgadmissions.utils.EncryptionUtils;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration("/integrationTestContext.xml")
//public class ReviewServiceIntegrationTest {
//
//	@Autowired
//	private ReviewService reviewService;
//
//	@Autowired
//	private ProgramDAO programDAO;
//
//	@Autowired
//	private UserDAO userDAO;
//
//	@Autowired
//	private RoleDAO roleDAO;
//
//	@Autowired
//	private EncryptionUtils encryptionUtils;
//
//	@Autowired
//	private SessionFactory sessionFactory;
//
//	@Test
//	@Transactional
//	@Rollback(true)
//	public void testAddExistingUserToProgramme() {
//		RegisteredUser reviewer = createNewReviewerUser("firs", "last", "email", Authority.ADMINISTRATOR);
//		userDAO.save(reviewer);
//		flushNClear();
//		Assert.assertNotNull(reviewer.getId());
//
//		Program programme = new Program();
//		programme.setCode("blablabal");
//		programDAO.save(programme);
//		Assert.assertNotNull(programme.getId());
//		flushNClear();
//
//		reviewService.addUserToProgramme(programme, reviewer);
//		flushNClear();
//
//		Program storedProgramme = programDAO.getProgramById(programme.getId());
//		Assert.assertTrue(storedProgramme.getProgramReviewers().contains(reviewer));
//		RegisteredUser storedUser = userDAO.get(reviewer.getId());
//		Assert.assertTrue(storedUser.getProgramsOfWhichReviewer().contains(programme));
//		Assert.assertTrue(reviewer.isInRole(Authority.REVIEWER));
//	}
//
//	@Test
//	@Transactional
//	@Rollback(true)
//	public void testAddExistingReviewerToProgramme() {
//		RegisteredUser reviewer = createNewReviewerUser("firs", "last", "email", Authority.REVIEWER);
//		userDAO.save(reviewer);
//		flushNClear();
//		Assert.assertNotNull(reviewer.getId());
//
//		Program programme = new Program();
//		programme.setCode("blablabal");
//		programDAO.save(programme);
//		Assert.assertNotNull(programme.getId());
//		flushNClear();
//
//		reviewService.addUserToProgramme(programme, reviewer);
//		flushNClear();
//
//		Program storedProgramme = programDAO.getProgramById(programme.getId());
//		Assert.assertTrue(storedProgramme.getProgramReviewers().contains(reviewer));
//		RegisteredUser storedUser = userDAO.get(reviewer.getId());
//		Assert.assertTrue(storedUser.getProgramsOfWhichReviewer().contains(programme));
//	}
//
//	@Test
//	@Transactional
//	@Rollback(true)
//	public void testAddNewReviewerToProject() {
//		Program programme = new Program();
//		programme.setCode("blablabal");
//		programDAO.save(programme);
//		Assert.assertNotNull(programme.getId());
//		flushNClear();
//		
//		RegisteredUser newReviewer = reviewService.createNewReviewerForProgramme(programme, "first", "last", "first@last.com");
//		flushNClear();
//		
//		Program storedProgramme = programDAO.getProgramById(programme.getId());
//		Assert.assertTrue(storedProgramme.getProgramReviewers().contains(newReviewer));
//		RegisteredUser storedUser = userDAO.get(newReviewer.getId());
//		Assert.assertTrue(storedUser.getProgramsOfWhichReviewer().contains(programme));
//	}
//
//	private void flushNClear() {
//		sessionFactory.getCurrentSession().flush();
//		sessionFactory.getCurrentSession().clear();
//	}
//
//	private RegisteredUser createNewReviewerUser(String firstName, String lastName, String email, Authority authority) {
//		RegisteredUser newReviewer = new RegisteredUser();
//		newReviewer.setFirstName(firstName);
//		newReviewer.setLastName(lastName);
//		newReviewer.setActivationCode(encryptionUtils.generateUUID());
//		newReviewer.setUsername(email);
//		newReviewer.setEmail(email);
//		newReviewer.setAccountNonExpired(true);
//		newReviewer.setAccountNonLocked(true);
//		newReviewer.setEnabled(false);
//		newReviewer.setCredentialsNonExpired(true);
//		if (authority != null) {
//			newReviewer.getRoles().add(roleDAO.getRoleByAuthority(authority));
//		}
//		return newReviewer;
//	}
//}
