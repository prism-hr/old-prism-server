package com.zuehlke.pgadmissions.domain;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class RegisteredUserTest {

    @Test
    public void shouldReturnTrueIfUserIsInRole() {
        RegisteredUser user = new RegisteredUserBuilder().roles(new RoleBuilder().id(Authority.APPLICANT).build(),
                new RoleBuilder().id(Authority.ADMINISTRATOR).build()).build();
        assertTrue(user.isInRole(Authority.APPLICANT));

    }

    @Test
    public void shouldReturnFalseIfUserIsNotInRole() {
        RegisteredUser user = new RegisteredUserBuilder().roles(new RoleBuilder().id(Authority.APPLICANT).build(),
                new RoleBuilder().id(Authority.ADMINISTRATOR).build()).build();
        assertFalse(user.isInRole(Authority.REVIEWER));

    }

    @Test
    public void shouldReturnTrueIfUserIsInRolePassedAsString() {
        RegisteredUser user = new RegisteredUserBuilder().roles(new RoleBuilder().id(Authority.APPLICANT).build(),
                new RoleBuilder().id(Authority.ADMINISTRATOR).build()).build();
        assertTrue(user.isInRole("APPLICANT"));

    }

    @Test
    public void shouldReturnFalseIfUserIsNotInRolePassedAsString() {
        RegisteredUser user = new RegisteredUserBuilder().roles(new RoleBuilder().id(Authority.APPLICANT).build(),
                new RoleBuilder().id(Authority.ADMINISTRATOR).build()).build();
        assertFalse(user.isInRole("REVIEWER"));

    }

    @Test
    public void shouldReturnFalseIStringIsNotAuthorityValue() {
        RegisteredUser user = new RegisteredUserBuilder().roles(new RoleBuilder().id(Authority.APPLICANT).build(),
                new RoleBuilder().id(Authority.ADMINISTRATOR).build()).build();
        assertFalse(user.isInRole("bob"));

    }

    @Test
    public void shouldReturnListOfAuthoritiesForProgram() {
        Program program = new ProgramBuilder().id(1).build();
        RegisteredUser user = new RegisteredUserBuilder().programsOfWhichAdministrator(program).programsOfWhichApprover(program).build();
        List<Authority> authorities = user.getAuthoritiesForProgram(program);
        assertThat(authorities, hasItems(Authority.ADMINISTRATOR, Authority.APPROVER));
    }

    @Test
    public void shouldReturnListOfAuthoritiesForProgramWithoutNullPointerException() {
        Program program = new ProgramBuilder().id(1).build();
        RegisteredUser user = new RegisteredUserBuilder().programsOfWhichAdministrator(program).programsOfWhichApprover(program).build();
        List<Authority> authorities = user.getAuthoritiesForProgram(null);
        assertEquals(0, authorities.size());
    }

    @Test
    public void shouldReturnCommaSeparatedListOfAuthoritiesForProgram() {
        Program program = new ProgramBuilder().id(1).build();
        RegisteredUser user = new RegisteredUserBuilder().programsOfWhichAdministrator(program).programsOfWhichApprover(program).build();
        assertEquals("Administrator, Approver", user.getAuthoritiesForProgramAsString(program));

    }

    @Test
    public void shouldAddSuperAdminToReturnCommaSeparatedListIfSuperadmin() {
        Program program = new ProgramBuilder().id(1).build();
        RegisteredUser user = new RegisteredUserBuilder().role(new RoleBuilder().id(Authority.SUPERADMINISTRATOR).build())
                .programsOfWhichAdministrator(program).programsOfWhichApprover(program).build();
        assertEquals("Superadministrator, Administrator, Approver", user.getAuthoritiesForProgramAsString(program));

    }

    @Test
    public void shouldReturnTrueIfUserHasRoleForProgram() {
        Program program = new ProgramBuilder().id(1).build();
        RegisteredUser user1 = new RegisteredUserBuilder().role(new RoleBuilder().id(Authority.ADMINISTRATOR).build()).programsOfWhichApprover(program).build();
        assertFalse(user1.isInRoleInProgram(Authority.ADMINISTRATOR, program));
        RegisteredUser user2 = new RegisteredUserBuilder().role(new RoleBuilder().id(Authority.ADMINISTRATOR).build()).programsOfWhichAdministrator(program)
                .build();
        assertTrue(user2.isInRoleInProgram(Authority.ADMINISTRATOR, program));
    }

    @Test
    public void shouldReturnTrueForSuperadmins() {
        Program program = new ProgramBuilder().id(1).build();
        RegisteredUser user = new RegisteredUserBuilder().role(new RoleBuilder().id(Authority.SUPERADMINISTRATOR).build()).build();
        assertTrue(user.isInRoleInProgram(Authority.SUPERADMINISTRATOR, program));
    }

    @Test
    public void shouldReturnTrueIfUserIsAdminAndOfAProgramme() {
        RegisteredUser user = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.ADMINISTRATOR).build()).build();
        Program program = new ProgramBuilder().id(1).administrators(user).build();
        assertTrue(user.isAdminInProgramme(program));
    }

    @Test
    public void shouldReturnFalseIfUserDoesNotBelongToTheProgramme() {
        RegisteredUser user = new RegisteredUserBuilder().id(1).role(new RoleBuilder().id(Authority.REVIEWER).build()).build();
        Program program = new ProgramBuilder().id(1).build();
        assertFalse(user.isAdminInProgramme(program));
    }

    @Test
    public void shouldReturnTrueIfHasRefereesInApplicationForm() {
        ApplicationForm form = new ApplicationFormBuilder().id(1).build();
        Referee referee1 = new RefereeBuilder().id(1).firstname("ref").lastname("erre").email("emailemail1@test.com").application(form).build();
        Referee referee2 = new RefereeBuilder().id(2).firstname("ref").lastname("erre").email("emailemail2@test.com").build();
        Referee referee3 = new RefereeBuilder().id(3).firstname("ref").lastname("erre").email("emailemail3@test.com").build();

        RegisteredUser user = new RegisteredUserBuilder().id(1).referees(referee1, referee2, referee3).role(new RoleBuilder().id(Authority.REVIEWER).build())
                .build();

        assertTrue(user.hasRefereesInApplicationForm(form));
    }

    @Test
    public void shouldReturnFalseIfDoesntHaveRefereesInApplicationForm() {
        ApplicationForm form = new ApplicationFormBuilder().id(1).build();
        Referee referee1 = new RefereeBuilder().id(1).firstname("ref").lastname("erre").email("emailemail1@test.com").build();
        Referee referee2 = new RefereeBuilder().id(2).firstname("ref").lastname("erre").email("emailemail2@test.com").build();
        Referee referee3 = new RefereeBuilder().id(3).firstname("ref").lastname("erre").email("emailemail3@test.com").build();

        RegisteredUser user = new RegisteredUserBuilder().id(1).referees(referee1, referee2, referee3).role(new RoleBuilder().id(Authority.REVIEWER).build())
                .build();

        assertFalse(user.hasRefereesInApplicationForm(form));
    }

    @Test
    public void shouldReturnFalseIfUserIsApplicant() {
        RegisteredUser user = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().id(Authority.APPLICANT).build()).build();
        assertFalse(user.canSeeReference(new ReferenceCommentBuilder().id(1).build()));
    }

    @Test
    public void shouldReturnFalseIfUserIsRefereeAndNotUploadingReferee() {
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
        @SuppressWarnings("serial")
        RegisteredUser user = new RegisteredUser() {
            @Override
            public boolean isRefereeOfApplicationForm(ApplicationForm form) {
                return true;
            }

        };

        RegisteredUser refereeUser = new RegisteredUserBuilder().id(8).build();
        ReferenceComment reference = new ReferenceCommentBuilder().id(1).user(refereeUser).application(applicationForm).build();
        assertFalse(user.canSeeReference(reference));
    }

    @Test
    public void shouldReturnNullIfDeclinedToRefereeForApplicationForm() {
        RegisteredUser user = new RegisteredUserBuilder().id(8).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(7).build();
        Referee refereeOne = new RefereeBuilder().id(7).user(user).declined(true).application(applicationForm).build();
        Referee refereeTwo = new RefereeBuilder().id(8).user(new RegisteredUserBuilder().id(9).build())
                .application(new ApplicationFormBuilder().id(78).build()).build();
        user.getReferees().addAll(Arrays.asList(refereeOne, refereeTwo));
        assertNull(user.getRefereeForApplicationForm(applicationForm));
    }

    @Test
    public void shouldHaveAdminRightsOnAppIfAdministratorInApplicationProgram() {
        RegisteredUser user = new RegisteredUserBuilder().id(8).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().advert(new ProgramBuilder().administrators(user).build())
                .status(ApplicationFormStatus.VALIDATION).build();
        assertTrue(user.hasAdminRightsOnApplication(applicationForm));
    }

    @Test
    public void shouldHaveAdminRightsOnAppIfAdministratorInApplicationProject() {
        RegisteredUser user = new RegisteredUserBuilder().id(8).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().advert(new Program()).advert(new ProjectBuilder().contactUser(user).build())
                .status(ApplicationFormStatus.VALIDATION).build();
        assertTrue(user.hasAdminRightsOnApplication(applicationForm));
    }

    @Test
    public void shouldHaveAdminRightsOnAppIfSuperadmin() {
        RegisteredUser user = new RegisteredUserBuilder().id(8).roles(new RoleBuilder().id(Authority.SUPERADMINISTRATOR).build()).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).build();
        assertTrue(user.hasAdminRightsOnApplication(applicationForm));
    }

    @Test
    public void shouldNotHaveAdminRightsOnAppNeihterAdministratorOfProgramOrApplication() {
        RegisteredUser user = new RegisteredUserBuilder().id(8).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().advert(new Program()).status(ApplicationFormStatus.VALIDATION).build();
        assertFalse(user.hasAdminRightsOnApplication(applicationForm));
    }

}
