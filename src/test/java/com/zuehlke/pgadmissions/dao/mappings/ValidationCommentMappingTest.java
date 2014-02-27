package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ValidationCommentBuilder;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;

public class ValidationCommentMappingTest extends AutomaticRollbackTestCase {
	@Test
	public void shouldSaveAndLoadValidationComment() {
		RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

		ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(user).advert(testObjectProvider.getEnabledProgram()).build();
		save(user, applicationForm);

		flushAndClearSession();

		ValidationComment validationComment = new ValidationCommentBuilder().application(applicationForm).comment("hi")
				.englishCompentencyOk(ValidationQuestionOptions.UNSURE).homeOrOverseas(HomeOrOverseas.HOME).qualifiedForPhd(ValidationQuestionOptions.NO)
				.type(CommentType.VALIDATION).user(user).build();
		save(validationComment);
		assertNotNull(validationComment.getId());
		
		ValidationComment reloadedComment = (ValidationComment) sessionFactory.getCurrentSession().get(ValidationComment.class, validationComment.getId());
		assertSame(validationComment, reloadedComment);
		
		flushAndClearSession();
		reloadedComment = (ValidationComment) sessionFactory.getCurrentSession().get(ValidationComment.class, validationComment.getId());
		assertNotSame(validationComment, reloadedComment);
		assertEquals(validationComment.getId(), reloadedComment.getId());
		
		assertEquals(applicationForm.getId(), reloadedComment.getApplication().getId());
		assertEquals("hi", reloadedComment.getComment());
		assertEquals(ValidationQuestionOptions.UNSURE, reloadedComment.getEnglishCompentencyOk());
		assertEquals(HomeOrOverseas.HOME, reloadedComment.getHomeOrOverseas());
		assertEquals(ValidationQuestionOptions.NO, reloadedComment.getQualifiedForPhd());
		assertEquals(CommentType.VALIDATION, reloadedComment.getType());
		assertEquals(user.getId(), reloadedComment.getUser().getId());
	}
}
