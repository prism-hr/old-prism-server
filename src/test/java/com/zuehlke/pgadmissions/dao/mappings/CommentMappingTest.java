package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class CommentMappingTest extends AutomaticRollbackTestCase {

    @Test
    public void shouldSaveAndLoadComment() {
        QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a50").domicileCode("AE").enabled(true).build();
        Program program = new ProgramBuilder().code("doesntexist").title("another title").institution(institution).build();
        save(institution, program);

        RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

        RegisteredUser reviewer = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

        Document documentOne = new DocumentBuilder().content("hi".getBytes()).fileName("bob").build();
        Document documentTwo = new DocumentBuilder().content("hello".getBytes()).fileName("fre").build();
        save(applicant, reviewer, documentOne, documentTwo);

        ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(applicant).program(program).build();
        save(applicationForm);

        flushAndClearSession();

        Comment comment = new CommentBuilder().application(applicationForm).comment("comment").user(reviewer).build();
        comment.getDocuments().addAll(Arrays.asList(documentOne, documentTwo));
        save(comment);

        assertNotNull(comment.getId());
        Integer id = comment.getId();
        Comment reloadedComment = (Comment) sessionFactory.getCurrentSession().get(Comment.class, id);
        assertSame(comment, reloadedComment);

        flushAndClearSession();

        reloadedComment = (Comment) sessionFactory.getCurrentSession().get(Comment.class, id);
        assertNotSame(comment, reloadedComment);
        assertEquals(comment.getId(), reloadedComment.getId());

        assertEquals(reviewer.getId(), reloadedComment.getUser().getId());
        assertEquals("comment", reloadedComment.getComment());
        assertEquals(DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE), DateUtils.truncate(reloadedComment.getDate(), Calendar.DATE));
        assertEquals(2, reloadedComment.getDocuments().size());
        assertTrue(listContainsId(documentOne, reloadedComment.getDocuments()));
        assertTrue(listContainsId(documentTwo, reloadedComment.getDocuments()));
    }

    private boolean listContainsId(Document document, List<Document> documents) {
        for (Document entry : documents) {
            if (entry.getId().equals(document.getId())) {
                return true;
            }
        }
        return false;
    }

}
