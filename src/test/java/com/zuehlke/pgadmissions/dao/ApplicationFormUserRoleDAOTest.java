package com.zuehlke.pgadmissions.dao;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ActionBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormUserRoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.NotificationMethod;
import com.zuehlke.pgadmissions.dto.ActionDefinition;

public class ApplicationFormUserRoleDAOTest extends AutomaticRollbackTestCase {

    private ActionDAO actionDAO;
    private RoleDAO roleDAO;
    private ApplicationFormUserRoleDAO applicationFormUserRoleDAO;
    private Action comment;
    private Action eligibility;
    private Action reference;
    private RegisteredUser user;
    private Program program;
    private ApplicationForm application;

    @Test
    public void shouldSaveAndLoadApplicationFormUserRole() throws ParseException {

        Role refereeRole = roleDAO.getRoleByAuthority(Authority.REFEREE);

        ApplicationFormUserRole applicationFormUserRole = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(refereeRole)
                .interestedInApplicant(true).build();

        ApplicationFormActionRequired actionRequired1 = new ApplicationFormActionRequired();
        actionRequired1.setAction(reference);
        actionRequired1.setBindDeadlineToDueDate(false);
        actionRequired1.setDeadlineTimestamp(new Date());
        applicationFormUserRole.getActions().add(actionRequired1);

        flushAndClearSession();

        applicationFormUserRoleDAO.save(applicationFormUserRole);

        ApplicationFormUserRole returned = (ApplicationFormUserRole) sessionFactory.getCurrentSession().get(ApplicationFormUserRole.class,
                applicationFormUserRole.getId());

        assertEquals("ABC", returned.getApplicationForm().getApplicationNumber());
        assertEquals("Jane", returned.getUser().getFirstName());
        assertEquals(refereeRole.getId(), returned.getRole().getId());

        assertEquals(1, returned.getActions().size());
        ApplicationFormActionRequired actionRequired = returned.getActions().get(0);
        assertEquals(reference, actionRequired.getAction());
        assertFalse(actionRequired.getBindDeadlineToDueDate());

    }

    @Test
    public void shouldFindApplicationFormUserRoleByApplicationFormAndUserAndAuthority() throws ParseException {

        RegisteredUser secondUser = new RegisteredUserBuilder().firstName("Franciszek").lastName("Pieczka").email("franek@123.com").username("franek")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
        sessionFactory.getCurrentSession().save(secondUser);

        Role refereeRole = roleDAO.getRoleByAuthority(Authority.REFEREE);
        Role admitterRole = roleDAO.getRoleByAuthority(Authority.ADMITTER);

        ApplicationFormUserRole role1 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(refereeRole)
                .interestedInApplicant(true).build();
        ApplicationFormUserRole role2 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(admitterRole)
                .interestedInApplicant(true).build();
        ApplicationFormUserRole role3 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(secondUser).role(refereeRole)
                .interestedInApplicant(true).build();
        save(role1, role2, role3);

        ApplicationFormUserRole role = applicationFormUserRoleDAO.findByApplicationFormAndUserAndAuthority(application, user, Authority.REFEREE);
        assertEquals(role1, role);
    }

    @Test
    public void shouldFindApplicationFormUserRoleByApplicationFormAndAuthority() throws ParseException {

        Role refereeRole = roleDAO.getRoleByAuthority(Authority.REFEREE);
        Role admitterRole = roleDAO.getRoleByAuthority(Authority.ADMITTER);
        Role superAdministratorRole = roleDAO.getRoleByAuthority(Authority.SUPERADMINISTRATOR);

        ApplicationFormUserRole role1 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(refereeRole)
                .interestedInApplicant(true).build();
        ApplicationFormUserRole role2 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(admitterRole)
                .interestedInApplicant(true).build();
        ApplicationFormUserRole role3 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(superAdministratorRole)
                .interestedInApplicant(true).build();
        save(role1, role2, role3);

        List<ApplicationFormUserRole> roles = applicationFormUserRoleDAO.findByApplicationFormAndAuthorities(application, Authority.REFEREE,
                Authority.SUPERADMINISTRATOR);
        assertThat(roles, contains(role1, role3));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldFindRequiredActionsByUserAndApplicationForm() {
        Role refereeRole = roleDAO.getRoleByAuthority(Authority.REFEREE);
        Role admitterRole = roleDAO.getRoleByAuthority(Authority.ADMITTER);
        Role superAdministratorRole = roleDAO.getRoleByAuthority(Authority.SUPERADMINISTRATOR);

        ApplicationFormUserRole role1 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(refereeRole)
                .interestedInApplicant(true).build();
        ApplicationFormUserRole role2 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(admitterRole)
                .interestedInApplicant(true).build();
        ApplicationFormUserRole role3 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(superAdministratorRole)
                .interestedInApplicant(true).build();

        role1.getActions().add(new ApplicationFormActionRequired(comment, new Date(), false, false));
        role1.getActions().add(new ApplicationFormActionRequired(reference, new Date(), false, false));
        role1.getActions().add(new ApplicationFormActionRequired(eligibility, new Date(), false, false));

        role2.getActions().add(new ApplicationFormActionRequired(eligibility, new Date(), false, false));

        save(role1, role2, role3);

        List<ActionDefinition> actions = applicationFormUserRoleDAO.findActionsByUserAndApplicationForm(user, application);
        System.out.println(actions);

        assertThat(actions, containsInAnyOrder( //
                allOf(hasProperty("action", equalTo(ApplicationFormAction.COMMENT)), hasProperty("raisesUrgentFlag", equalTo(false))), //
                allOf(hasProperty("action", equalTo(ApplicationFormAction.PROVIDE_REFERENCE)), hasProperty("raisesUrgentFlag", equalTo(false))), //
                allOf(hasProperty("action", equalTo(ApplicationFormAction.CONFIRM_ELIGIBILITY)), hasProperty("raisesUrgentFlag", equalTo(false))), //
                allOf(hasProperty("action", equalTo(ApplicationFormAction.EMAIL_APPLICANT)), hasProperty("raisesUrgentFlag", equalTo(false))), //
                allOf(hasProperty("action", equalTo(ApplicationFormAction.VIEW)), hasProperty("raisesUrgentFlag", equalTo(false))))); //
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldFindOptionalActionsByUserAndApplicationForm() {
        Role refereeRole = roleDAO.getRoleByAuthority(Authority.REFEREE);
        Role admitterRole = roleDAO.getRoleByAuthority(Authority.ADMITTER);
        Role superAdministratorRole = roleDAO.getRoleByAuthority(Authority.SUPERADMINISTRATOR);

        ApplicationFormUserRole role1 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(refereeRole)
                .interestedInApplicant(true).build();
        ApplicationFormUserRole role2 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(admitterRole)
                .interestedInApplicant(true).build();
        ApplicationFormUserRole role3 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(superAdministratorRole)
                .interestedInApplicant(true).build();

        save(role1, role2, role3);

        List<ActionDefinition> actions = applicationFormUserRoleDAO.findActionsByUserAndApplicationForm(user, application);
        System.out.println(actions);

        assertThat(actions, containsInAnyOrder( //
                allOf(hasProperty("action", equalTo(ApplicationFormAction.COMMENT)), hasProperty("raisesUrgentFlag", equalTo(false))), //
                allOf(hasProperty("action", equalTo(ApplicationFormAction.EMAIL_APPLICANT)), hasProperty("raisesUrgentFlag", equalTo(false))), //
                allOf(hasProperty("action", equalTo(ApplicationFormAction.VIEW)), hasProperty("raisesUrgentFlag", equalTo(false))))); //
    }

    @Test
    public void shouldFindRaisesUpdateFlagByUserAndApplicationForm() {
        Role refereeRole = roleDAO.getRoleByAuthority(Authority.REFEREE);
        Role admitterRole = roleDAO.getRoleByAuthority(Authority.ADMITTER);
        Role superAdministratorRole = roleDAO.getRoleByAuthority(Authority.SUPERADMINISTRATOR);

        ApplicationFormUserRole role1 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(refereeRole)
                .interestedInApplicant(true).build();
        ApplicationFormUserRole role2 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(admitterRole)
                .interestedInApplicant(true).build();
        ApplicationFormUserRole role3 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(superAdministratorRole)
                .interestedInApplicant(true).raisesUpdateFlag(true).build();

        save(role1, role2, role3);

        Boolean raisesUpdateFlag = applicationFormUserRoleDAO.findRaisesUpdateFlagByUserAndApplicationForm(user, application);
        assertTrue(raisesUpdateFlag);
    }

    @Test
    public void shouldActionBeAvailableForUserAndApplicationFormIfRequiredActionExists() {
        Role refereeRole = roleDAO.getRoleByAuthority(Authority.REFEREE);

        ApplicationFormUserRole role1 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(refereeRole)
                .interestedInApplicant(true).build();

        role1.getActions().add(new ApplicationFormActionRequired(reference, new Date(), false, false));

        save(role1);

        Boolean actionAvailable = applicationFormUserRoleDAO.checkActionAvailableForUserAndApplicationForm(user, application,
                ApplicationFormAction.PROVIDE_REFERENCE);
        assertTrue(actionAvailable);
    }

    @Test
    public void shouldActionBeAvailableForUserAndApplicationFormIfOptionalActionAvailable() {
        Role refereeRole = roleDAO.getRoleByAuthority(Authority.REFEREE);

        ApplicationFormUserRole role1 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(refereeRole)
                .interestedInApplicant(true).build();

        save(role1);

        Boolean actionAvailable = applicationFormUserRoleDAO.checkActionAvailableForUserAndApplicationForm(user, application, ApplicationFormAction.COMMENT);
        assertTrue(actionAvailable);
    }

    @Test
    public void shouldActionBeNotAvailableForUserAndApplicationForm() {
        Role refereeRole = roleDAO.getRoleByAuthority(Authority.REFEREE);

        ApplicationFormUserRole role1 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(refereeRole)
                .interestedInApplicant(true).build();

        role1.getActions().add(new ApplicationFormActionRequired(reference, new Date(), false, false));

        save(role1);

        Boolean actionAvailable = applicationFormUserRoleDAO.checkActionAvailableForUserAndApplicationForm(user, application,
                ApplicationFormAction.PROVIDE_REVIEW);
        assertFalse(actionAvailable);
    }

    @Before
    public void prepare() throws ParseException {
        roleDAO = new RoleDAO(sessionFactory);
        applicationFormUserRoleDAO = new ApplicationFormUserRoleDAO(sessionFactory);

        user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

        QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a").countryCode("AE").enabled(true).build();
        
        program = new ProgramBuilder().code("doesntexist").title("another title").institution(institution).build();
        
        comment = new ActionBuilder().id(ApplicationFormAction.COMMENT).notification(null).build();
        reference = new ActionBuilder().id(ApplicationFormAction.PROVIDE_REFERENCE).notification(NotificationMethod.INDIVIDUAL).build();
        eligibility = new ActionBuilder().id(ApplicationFormAction.CONFIRM_ELIGIBILITY).notification(NotificationMethod.INDIVIDUAL).build();

        Date lastUpdatedDate = new SimpleDateFormat("dd MM yyyy hh:mm:ss").parse("01 06 2011 14:05:23");
        application = new ApplicationForm();
        application.setApplicant(user);
        application.setLastUpdated(lastUpdatedDate);
        application.setProgram(program);
        application.setStatus(ApplicationFormStatus.APPROVED);
        application.setApplicationNumber("ABC");

        save(user, institution, program, application);

        flushAndClearSession();
    }

}
