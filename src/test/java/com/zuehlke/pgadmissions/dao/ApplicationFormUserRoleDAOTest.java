package com.zuehlke.pgadmissions.dao;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormActionRequired;
import com.zuehlke.pgadmissions.domain.ApplicationFormUserRole;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormUserRoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.UserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class ApplicationFormUserRoleDAOTest extends AutomaticRollbackTestCase {

    private StateDAO stateDAO;
    private RoleDAO roleDAO;
    private ApplicationFormUserRoleDAO applicationFormUserRoleDAO;
    private Action comment;
    private Action eligibility;
    private Action reference;
    private User user;
    private Program program;
    private ApplicationForm application;

    @Test
    public void shouldSaveAndLoadApplicationFormUserRole() throws ParseException {

        Role refereeRole = roleDAO.getById(Authority.REFEREE);

        ApplicationFormUserRole applicationFormUserRole = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(refereeRole)
                .interestedInApplicant(true).build();

        ApplicationFormActionRequired actionRequired1 = new ApplicationFormActionRequired(reference, new Date(), false, null);
        applicationFormUserRole.getActions().add(actionRequired1);

        flushAndClearSession();

        roleDAO.saveUserRole(applicationFormUserRole);

        ApplicationFormUserRole returned = (ApplicationFormUserRole) sessionFactory.getCurrentSession().get(ApplicationFormUserRole.class,
                applicationFormUserRole.getId());

        assertEquals("ABC", returned.getId().getApplicationForm().getApplicationNumber());
        assertEquals("Jane", returned.getId().getUser().getFirstName());
        assertEquals(refereeRole.getId(), returned.getId().getRole().getId());

        assertEquals(1, returned.getActions().size());
        ApplicationFormActionRequired actionRequired = returned.getActions().iterator().next();
        assertEquals(reference, actionRequired.getId().getAction());
        assertFalse(actionRequired.getBindDeadlineToDueDate());

    }

    @Test
    public void shouldFindApplicationFormUserRoleByApplicationFormAndUserAndAuthority() throws ParseException {

        User secondUser = new UserBuilder().firstName("Franciszek").lastName("Pieczka").email("franek@123.com").username("franek")
                .password("password").enabled(false).build();
        sessionFactory.getCurrentSession().save(secondUser);

        Role refereeRole = roleDAO.getById(Authority.REFEREE);
        Role admitterRole = roleDAO.getById(Authority.ADMITTER);

        ApplicationFormUserRole role1 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(refereeRole)
                .interestedInApplicant(true).build();
        ApplicationFormUserRole role2 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(admitterRole)
                .interestedInApplicant(true).build();
        ApplicationFormUserRole role3 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(secondUser).role(refereeRole)
                .interestedInApplicant(true).build();
        save(role1, role2, role3);

        List<ApplicationFormUserRole> role = applicationFormUserRoleDAO.getByApplicationFormAndUserAndAuthorities(application, user, Authority.REFEREE);
        assertEquals(role1, role);
    }

    @Test
    public void shouldFindApplicationFormUserRoleByApplicationFormAndAuthority() throws ParseException {

        Role refereeRole = roleDAO.getById(Authority.REFEREE);
        Role admitterRole = roleDAO.getById(Authority.ADMITTER);
        Role superAdministratorRole = roleDAO.getById(Authority.SUPERADMINISTRATOR);

        ApplicationFormUserRole role1 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(refereeRole)
                .interestedInApplicant(true).build();
        ApplicationFormUserRole role2 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(admitterRole)
                .interestedInApplicant(true).build();
        ApplicationFormUserRole role3 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(superAdministratorRole)
                .interestedInApplicant(true).build();
        save(role1, role2, role3);

        List<ApplicationFormUserRole> roles = applicationFormUserRoleDAO.getByApplicationFormAndAuthorities(application, Authority.REFEREE,
                Authority.SUPERADMINISTRATOR);
        assertThat(roles, contains(role1, role3));
    }

    // TODO move it to ActionServiceTest
    // @SuppressWarnings("unchecked")
    // @Test
    // public void shouldFindRequiredActionsByUserAndApplicationForm() {
    // Role refereeRole = roleDAO.getById(Authority.REFEREE);
    // Role admitterRole = roleDAO.getById(Authority.ADMITTER);
    // Role superAdministratorRole = roleDAO.getById(Authority.SUPERADMINISTRATOR);
    //
    // ApplicationFormUserRole role1 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(refereeRole)
    // .interestedInApplicant(true).build();
    // ApplicationFormUserRole role2 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(admitterRole)
    // .interestedInApplicant(true).build();
    // ApplicationFormUserRole role3 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(superAdministratorRole)
    // .interestedInApplicant(true).build();
    //
    // role1.getActions().add(new ApplicationFormActionRequired(comment, new Date(), false, false));
    // role1.getActions().add(new ApplicationFormActionRequired(reference, new Date(), false, false));
    // role1.getActions().add(new ApplicationFormActionRequired(eligibility, new Date(), false, false));
    //
    // role2.getActions().add(new ApplicationFormActionRequired(eligibility, new Date(), false, false));
    //
    // save(role1, role2, role3);
    //
    // List<ActionDefinition> actions = applicationFormUserRoleDAO.selectUserActions(user.getId(), application.getId(), application.getStatus());
    // System.out.println(actions);
    //
    // assertThat(actions, containsInAnyOrder( //
    // allOf(hasProperty("action", equalTo(ApplicationFormAction.COMMENT)), hasProperty("raisesUrgentFlag", equalTo(false))), //
    // allOf(hasProperty("action", equalTo(ApplicationFormAction.PROVIDE_REFERENCE)), hasProperty("raisesUrgentFlag", equalTo(false))), //
    // allOf(hasProperty("action", equalTo(ApplicationFormAction.CONFIRM_ELIGIBILITY)), hasProperty("raisesUrgentFlag", equalTo(false))), //
    // allOf(hasProperty("action", equalTo(ApplicationFormAction.EMAIL_APPLICANT)), hasProperty("raisesUrgentFlag", equalTo(false))) //
    // )); //
    // }
    //
    // @SuppressWarnings("unchecked")
    // @Test
    // public void shouldFindOptionalActionsByUserAndApplicationForm() {
    // Role refereeRole = roleDAO.getById(Authority.REFEREE);
    // Role admitterRole = roleDAO.getById(Authority.ADMITTER);
    // Role superAdministratorRole = roleDAO.getById(Authority.SUPERADMINISTRATOR);
    //
    // ApplicationFormUserRole role1 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(refereeRole)
    // .interestedInApplicant(true).build();
    // ApplicationFormUserRole role2 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(admitterRole)
    // .interestedInApplicant(true).build();
    // ApplicationFormUserRole role3 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(superAdministratorRole)
    // .interestedInApplicant(true).build();
    //
    // save(role1, role2, role3);
    //
    // List<ActionDefinition> actions = applicationFormUserRoleDAO.selectUserActions(user.getId(), application.getId(), application.getStatus());
    // System.out.println(actions);
    //
    // assertThat(actions, containsInAnyOrder( //
    // allOf(hasProperty("action", equalTo(ApplicationFormAction.COMMENT)), hasProperty("raisesUrgentFlag", equalTo(false))), //
    // allOf(hasProperty("action", equalTo(ApplicationFormAction.EMAIL_APPLICANT)), hasProperty("raisesUrgentFlag", equalTo(false))) //
    // )); //
    // }
    //
    // @Test
    // public void shouldFindRaisesUpdateFlagByUserAndApplicationForm() {
    // Role refereeRole = roleDAO.getById(Authority.REFEREE);
    // Role admitterRole = roleDAO.getById(Authority.ADMITTER);
    // Role superAdministratorRole = roleDAO.getById(Authority.SUPERADMINISTRATOR);
    //
    // ApplicationFormUserRole role1 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(refereeRole)
    // .interestedInApplicant(true).build();
    // ApplicationFormUserRole role2 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(admitterRole)
    // .interestedInApplicant(true).build();
    // ApplicationFormUserRole role3 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(superAdministratorRole)
    // .interestedInApplicant(true).raisesUpdateFlag(true).build();
    //
    // save(role1, role2, role3);
    //
    // Boolean raisesUpdateFlag = applicationFormUserRoleDAO.findRaisesUpdateFlagByUserAndApplicationForm(user, application);
    // assertTrue(raisesUpdateFlag);
    // }
    //
    // @Test
    // public void shouldActionBeAvailableForUserAndApplicationFormIfRequiredActionExists() {
    // Role refereeRole = roleDAO.getById(Authority.REFEREE);
    //
    // ApplicationFormUserRole role1 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(refereeRole)
    // .interestedInApplicant(true).build();
    //
    // role1.getActions().add(new ApplicationFormActionRequired(reference, new Date(), false, false));
    //
    // save(role1);
    //
    // Boolean actionAvailable = applicationFormUserRoleDAO.checkActionAvailableForUserAndApplicationForm(user, application,
    // ApplicationFormAction.PROVIDE_REFERENCE);
    // assertTrue(actionAvailable);
    // }
    //
    // @Test
    // public void shouldActionBeAvailableForUserAndApplicationFormIfOptionalActionAvailable() {
    // Role refereeRole = roleDAO.getById(Authority.REFEREE);
    //
    // ApplicationFormUserRole role1 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(refereeRole)
    // .interestedInApplicant(true).build();
    //
    // save(role1);
    //
    // Boolean actionAvailable = applicationFormUserRoleDAO.checkActionAvailableForUserAndApplicationForm(user, application, ApplicationFormAction.COMMENT);
    // assertTrue(actionAvailable);
    // }
    //
    // @Test
    // public void shouldActionBeNotAvailableForUserAndApplicationForm() {
    // Role refereeRole = roleDAO.getById(Authority.REFEREE);
    //
    // ApplicationFormUserRole role1 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(refereeRole)
    // .interestedInApplicant(true).build();
    //
    // role1.getActions().add(new ApplicationFormActionRequired(reference, new Date(), false, false));
    //
    // save(role1);
    //
    // Boolean actionAvailable = applicationFormUserRoleDAO.checkActionAvailableForUserAndApplicationForm(user, application,
    // ApplicationFormAction.PROVIDE_REVIEW);
    // assertFalse(actionAvailable);
    // }

    @Before
    public void prepare() throws ParseException {
        roleDAO = new RoleDAO(sessionFactory);
        applicationFormUserRoleDAO = new ApplicationFormUserRoleDAO(sessionFactory);

        user = new UserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password").enabled(false)
                .build();

        Date lastUpdatedDate = new SimpleDateFormat("dd MM yyyy hh:mm:ss").parse("01 06 2011 14:05:23");

        program = testObjectProvider.getEnabledProgram();

        application = new ApplicationForm();
        application.setApplicant(user);
        application.setLastUpdated(lastUpdatedDate);
        application.setAdvert(program);
        application.setStatus(stateDAO.getById(ApplicationFormStatus.APPROVED));
        application.setApplicationNumber("ABC");

        save(user, application);
        flushAndClearSession();

        comment = testObjectProvider.getAction(ApplicationFormAction.COMMENT);
        reference = testObjectProvider.getAction(ApplicationFormAction.PROVIDE_REFERENCE);
        eligibility = testObjectProvider.getAction(ApplicationFormAction.CONFIRM_ELIGIBILITY);
    }

}
