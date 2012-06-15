package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.util.Calendar;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class CommentMappingTest extends AutomaticRollbackTestCase{

	@Test
	public void shouldSaveAndLoadComment(){
		Program program = new ProgramBuilder().code("doesntexist").title("another title").toProgram();		
		save( program);
		
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		
		RegisteredUser reviewer = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		
		save(applicant, reviewer);
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(applicant).program(program).toApplicationForm();		
		save(applicationForm);
		
		flushAndClearSession();
		
		
		Comment comment = new CommentBuilder().application(applicationForm).comment("comment").user(reviewer).toComment();
		save(comment);
		
		assertNotNull(comment.getId());
		Integer id = comment.getId();
		Comment reloadedComment = (Comment) sessionFactory.getCurrentSession().get(Comment.class, id);
		assertSame(comment, reloadedComment);

		flushAndClearSession();

		reloadedComment  =(Comment) sessionFactory.getCurrentSession().get(Comment.class, id);
		assertNotSame(comment, reloadedComment);
		assertEquals(comment, reloadedComment);

		assertEquals(reviewer, reloadedComment.getUser());
		assertEquals("comment", reloadedComment.getComment());
		assertEquals(DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE), DateUtils.truncate(reloadedComment.getDate(), Calendar.DATE));
		
	
	}

}
