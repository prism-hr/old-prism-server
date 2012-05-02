package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.util.Calendar;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class ValidationCommentMappingTest  extends AutomaticRollbackTestCase{

	@Test
	public void shouldSaveAndLoadValidationComment(){
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
		
		
		ValidationComment validationComment = new ValidationComment();
		validationComment.setComment("This is a validationComment");
		validationComment.setUser(reviewer);
		validationComment.setApplication(applicationForm);		

		
		save(validationComment);
		
		assertNotNull(validationComment.getId());
		Integer id = validationComment.getId();
		ValidationComment reloadedComment = (ValidationComment) sessionFactory.getCurrentSession().get(ValidationComment.class, id);
		assertSame(validationComment, reloadedComment);

		flushAndClearSession();

		reloadedComment  =(ValidationComment) sessionFactory.getCurrentSession().get(ValidationComment.class, id);
		assertNotSame(validationComment, reloadedComment);
		assertEquals(validationComment, reloadedComment);

		assertEquals(reviewer, reloadedComment.getUser());
		assertEquals("This is a validationComment", reloadedComment.getComment());
		assertEquals(DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE), DateUtils.truncate(reloadedComment.getCreatedTimestamp(), Calendar.DATE));
		
	}
	
}
