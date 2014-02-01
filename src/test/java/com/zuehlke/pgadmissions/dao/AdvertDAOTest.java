package com.zuehlke.pgadmissions.dao;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.BooleanUtils;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormUserRole;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.AdvertBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormUserRoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

public class AdvertDAOTest extends AutomaticRollbackTestCase {

    private RoleDAO roleDAO;
    private AdvertDAO advertDAO;
    private EncryptionUtils encrypter;
    private QualificationInstitution institution;

    @Before
    public void setUp() {
        advertDAO = new AdvertDAO(sessionFactory);
        roleDAO = new RoleDAO(sessionFactory);
        encrypter = new EncryptionUtils();
        institution = new QualificationInstitutionBuilder().code("code").name("a").countryCode("AE").enabled(true).build();
        save(institution);

    }

    @Test
    public void shouldGetActiveAdverts() {
        Program programWithInactiveProgramAdvert = new ProgramBuilder().code("inactive").title("another title").institution(institution).build();
        Advert inactiveProgramAdvert = new AdvertBuilder().description("inactive program").studyDuration(9).active(false).build();

        Program programWithActiveProgramAdvert = new ProgramBuilder().code("program").title("another title").institution(institution).build();
        Advert programAdvert = new AdvertBuilder().description("program").studyDuration(66).build();

        save(institution, programWithInactiveProgramAdvert, programWithActiveProgramAdvert);
        save(inactiveProgramAdvert, programAdvert);
        flushAndClearSession();

        List<Advert> activeAdverts = advertDAO.getActiveAdverts();
        assertThat(activeAdverts.size(), greaterThanOrEqualTo(1));
        assertTrue(advertInList(programAdvert, activeAdverts));
    }

    @Test
    public void shouldGetRecommendedAdverts() {
        RegisteredUser applicant = new RegisteredUserBuilder().username("applicant").build();
        save(applicant);

        List<ApplicationForm> seedApplications = new ArrayList<ApplicationForm>(5);
        HashMap<RegisteredUser, ApplicationForm> applications = new HashMap<RegisteredUser, ApplicationForm>();

        for (Integer i = 0; i < 5; i++) {
            ApplicationForm application = buildTestApplication(applicant);
            seedApplications.add(application);
            applications.put(applicant, application);
        }

        Authority[] peerAuthorities = { Authority.APPLICANT, Authority.INTERVIEWER, Authority.REVIEWER, Authority.SUGGESTEDSUPERVISOR,
                Authority.STATEADMINISTRATOR, Authority.SUGGESTEDSUPERVISOR, Authority.SUPERVISOR };
        HashMap<Authority, RegisteredUser> peerUsers = new HashMap<Authority, RegisteredUser>(5 * peerAuthorities.length);

        for (Authority authority : peerAuthorities) {
            for (int i = 0; i < 5; i++) {
                peerUsers.put(authority, buildTestUserInAuthority(authority));
            }
        }

        for (Entry<Authority, RegisteredUser> entry : peerUsers.entrySet()) {
            int userKey = 0;
            if (entry.getKey() == Authority.APPLICANT) {
                for (int i = 0; i < seedApplications.size(); i++) {
                    RegisteredUser peerApplicant = entry.getValue();
                    if (i <= userKey) {
                        applications.put(peerApplicant, copyTestApplicationAsOwn(peerApplicant, seedApplications.get(i)));
                    } else {
                        applications.put(peerApplicant, buildTestApplication(peerApplicant));
                    }
                }
                userKey++;
            }
        }
        
        HashMap<RegisteredUser, ApplicationForm> relatedApplications = new HashMap<RegisteredUser, ApplicationForm>();
        for (Entry<RegisteredUser, ApplicationForm> entry : applications.entrySet()) {
            int applicationIndex = 0;
            for (Authority peerAuthority : peerAuthorities) {
                if (!Arrays.asList(Authority.APPLICANT, Authority.SUGGESTEDSUPERVISOR).contains(peerAuthority)) {
                    for (int i = 0; i < 5; i++) {
                        boolean doAssociateWithTestApplication = i == 0 || (applicationIndex % i) == 0;
                        if (BooleanUtils.isTrue(doAssociateWithTestApplication)) {
                            associateUserInAuthorityWithTestApplication(entry.getValue(), peerAuthority);
                        }
                        RegisteredUser otherApplicant = new RegisteredUserBuilder().username(encrypter.generateUUID()).build();
                        save(otherApplicant);
                        for (int j = 0; j < 5; j ++) {
                            ApplicationForm otherApplication = buildTestApplication(otherApplicant);
                            associateUserInAuthorityWithTestApplication(otherApplication, peerAuthority);
                            if (BooleanUtils.isTrue(doAssociateWithTestApplication)) { 
                                relatedApplications.put(otherApplicant, otherApplication);
                            }
                        }
                    }
                }
                applicationIndex++;
            }
        }
        applications.putAll(relatedApplications);

    }

    private ApplicationForm buildTestApplication(RegisteredUser applicant) {
        Advert programAdvert = new AdvertBuilder().description("test").funding("test").studyDuration(1).title("test").build();
        Advert projectAdvert = new AdvertBuilder().description("test").funding("test").studyDuration(1).title("test").build();

        Program program = new ProgramBuilder().code(encrypter.generateUUID()).title("test").institution(institution).build();

        RegisteredUser primarySupervisor = new RegisteredUserBuilder().username(encrypter.generateUUID()).build();
        RegisteredUser secondarySupervisor = new RegisteredUserBuilder().username(encrypter.generateUUID()).build();
        save(primarySupervisor, secondarySupervisor);
        
        Project project = new ProjectBuilder().advert(projectAdvert).author(primarySupervisor).primarySupervisor(primarySupervisor).program(program)
                .secondarySupervisor(secondarySupervisor).build();

        ApplicationForm application = new ApplicationFormBuilder().program(program).project(project).applicant(applicant).build();

        save(programAdvert, projectAdvert, program, project, application);
        initialiseApplicationRoles(application);

        return application;
    }

    private ApplicationForm copyTestApplicationAsOwn(RegisteredUser applicant, ApplicationForm application) {
        ApplicationForm copy = new ApplicationFormBuilder().program(application.getProgram()).project(application.getProject()).applicant(applicant).build();

        save(copy);
        initialiseApplicationRoles(copy);

        return copy;
    }

    private void initialiseApplicationRoles(ApplicationForm application) {
        ApplicationFormUserRole applicationFormUserRoleApplicant = new ApplicationFormUserRoleBuilder().applicationForm(application)
                .user(application.getApplicant()).role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).build();
        ApplicationFormUserRole applicationFormUserRolePrimarySupervisor = new ApplicationFormUserRoleBuilder().applicationForm(application)
                .user(application.getProject().getPrimarySupervisor()).role(roleDAO.getRoleByAuthority(Authority.PROJECTADMINISTRATOR)).build();
        ApplicationFormUserRole applicationFormUserRoleSecondarySupervisor = new ApplicationFormUserRoleBuilder().applicationForm(application)
                .user(application.getProject().getSecondarySupervisor()).role(roleDAO.getRoleByAuthority(Authority.SUGGESTEDSUPERVISOR)).build();
        save(applicationFormUserRoleApplicant, applicationFormUserRolePrimarySupervisor, applicationFormUserRoleSecondarySupervisor);
    }

    private RegisteredUser buildTestUserInAuthority(Authority authority) {
        RegisteredUser testUser = new RegisteredUserBuilder().username(encrypter.generateUUID()).build();
        save(testUser);
        return testUser;
    }
    
    private void associateUserInAuthorityWithTestApplication(ApplicationForm application, Authority authority) {
        ApplicationFormUserRole applicationFormUserRolePeerUser = new ApplicationFormUserRoleBuilder().applicationForm(application)
                .user(buildTestUserInAuthority(authority)).role(roleDAO.getRoleByAuthority(authority)).build();
        save(applicationFormUserRolePeerUser);
    }

    private boolean advertInList(Advert programAdvert, List<Advert> activeAdverts) {
        for (Advert loadedAdvert : activeAdverts) {
            if (loadedAdvert.getId().equals(programAdvert.getId())) {
                assertThat(loadedAdvert.getId(), equalTo(programAdvert.getId()));
                assertThat(loadedAdvert.getDescription(), equalTo(programAdvert.getDescription()));
                assertThat(loadedAdvert.getStudyDuration(), equalTo(programAdvert.getStudyDuration()));
                assertThat(loadedAdvert.getId(), equalTo(programAdvert.getId()));
                return true;
            }
        }
        return false;
    }

}
