package com.zuehlke.pgadmissions.services;

import junit.framework.Assert;

import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.dao.QualificationInstitutionDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.RejectReason;
import com.zuehlke.pgadmissions.domain.Rejection;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RejectReasonBuilder;
import com.zuehlke.pgadmissions.domain.builders.RejectionBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.mail.MailSendingService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testIntegrationContext.xml")
public class RejectServiceIntegrationTest {

    @Autowired
    private RejectService rejectsService;

    @Autowired
    private ApplicationFormDAO applicationDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private ProgramDAO programDao;
    
    @Autowired
    private QualificationInstitutionDAO qualificationInstitutionDAO;

    @Autowired
    private MailSendingService mailSendingService;

    @Autowired
    private SessionFactory sessionFactory;

    @Test
    @Transactional
    @Rollback(true)
    public void testMoveApplicationToReject() {
        RejectReason reason1 = createReason("r1");

        sessionFactory.getCurrentSession().saveOrUpdate(reason1);

        Role approverRole = roleDAO.getRoleByAuthority(Authority.APPROVER);
        RegisteredUser approver = new RegisteredUserBuilder().firstName("Some").lastName("Aprove").email("sdfajklsdf@test.com").username("sdfakd")//
                .role(approverRole)//
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
        userDAO.save(approver);

        QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a").countryCode("AE").enabled(true).build();
        qualificationInstitutionDAO.save(institution);
        
        Program program = new ProgramBuilder().title("alelele").code("blabjk").institution(institution).build();
        program.getApprovers().add(approver);
        programDao.save(program);

        ApplicationForm application = new ApplicationForm();
        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
        userDAO.save(user);
        application.setApplicant(user);

        application.setProgram(program);
        applicationDAO.save(application);

        flushNClear();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);

        authenticationToken.setDetails(user);
        SecurityContextImpl secContext = new SecurityContextImpl();
        secContext.setAuthentication(authenticationToken);
        SecurityContextHolder.setContext(secContext);

        Rejection rejection = new RejectionBuilder().rejectionReason(reason1).build();
        rejectsService.moveApplicationToReject(application, rejection);
        flushNClear();

        ApplicationForm storedAppl = applicationDAO.get(application.getId());

        Assert.assertEquals(ApplicationFormStatus.REJECTED, storedAppl.getStatus());
        Assert.assertEquals(reason1.getId(), storedAppl.getRejection().getRejectionReason().getId());
    }

    private void flushNClear() {
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();
    }

    private RejectReason createReason(String text) {
        return new RejectReasonBuilder().text(text).build();
    }
}
