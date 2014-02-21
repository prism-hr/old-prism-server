package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.CompleteApprovalComment;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalEvaluationCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class ApprovalEvaluationCommentMappingTest extends AutomaticRollbackTestCase {

    @Test
    public void shouldSaveAndLoadInterviewEvaluationComment() {
        Program program = (Program) sessionFactory.getCurrentSession().get(Program.class, 63);

        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

        ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(user).program(program).build();

        save(user, applicationForm);

        flushAndClearSession();

        CompleteApprovalComment approvalEvaluationComment = new ApprovalEvaluationCommentBuilder().application(applicationForm).comment("hi").user(user)
                .build();
        save(approvalEvaluationComment);
        assertNotNull(approvalEvaluationComment.getId());

        CompleteApprovalComment reloadedComment = (CompleteApprovalComment) sessionFactory.getCurrentSession().get(CompleteApprovalComment.class,
                approvalEvaluationComment.getId());
        assertSame(approvalEvaluationComment, reloadedComment);

        flushAndClearSession();
        reloadedComment = (CompleteApprovalComment) sessionFactory.getCurrentSession().get(CompleteApprovalComment.class, approvalEvaluationComment.getId());
        assertNotSame(approvalEvaluationComment, reloadedComment);
        assertEquals(approvalEvaluationComment.getId(), reloadedComment.getId());

        assertEquals(applicationForm.getId(), reloadedComment.getApplication().getId());
        assertEquals("hi", reloadedComment.getContent());
        assertEquals(user.getId(), reloadedComment.getUser().getId());
    }

}
