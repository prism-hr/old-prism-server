package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.util.Calendar;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class ReferenceCommentMappingTest extends AutomaticRollbackTestCase {

	@Test
	public void shouldSaveAndLoadReferenceComment(){
		Program program = new ProgramBuilder().code("doesntexist").title("another title").build();		
		save(program);
		
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
		
		RegisteredUser refereeUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
		
		RegisteredUser commentProviderUser = new RegisteredUserBuilder().firstName("Bob").lastName("Kowalski").email("bob@test.com").username("bob").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
		
		ApplicationForm application = new ApplicationFormBuilder().applicant(applicant).id(2).build();
		
		Country country = new CountryBuilder().name("nae").code("aa").enabled(true).build(); 
		save(applicant, application, country, commentProviderUser);
		
		Referee referee = new RefereeBuilder().application(application).email("email@test.com").firstname("bob")
				.lastname("smith").addressCountry(country).address1("london").jobEmployer("zuhlke").jobTitle("se")
				.messenger("skypeAddress").phoneNumber("hallihallo").user(refereeUser).toReferee();
		save(refereeUser, referee);
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(applicant).program(program).build();		
		save(applicationForm);
		
		flushAndClearSession();
		
		ReferenceComment referenceComment = new ReferenceCommentBuilder().referee(referee)
				.comment("This is a reference comment").suitableForProgramme(false).user(refereeUser).application(applicationForm)
				.providedBy(commentProviderUser).build();
		referee.setReference(referenceComment);
		save(referenceComment, referee);
		
		assertNotNull(referenceComment.getId());
		assertNotNull(referenceComment.getReferee());
		Integer id = referenceComment.getId();
		
		ReferenceComment reloadedComment = (ReferenceComment) sessionFactory.getCurrentSession().get(ReferenceComment.class, id);
		assertSame(referenceComment, reloadedComment);

		flushAndClearSession();

		reloadedComment = (ReferenceComment) sessionFactory.getCurrentSession().get(ReferenceComment.class, id);
		assertNotSame(referenceComment, reloadedComment);
		assertNotNull(referenceComment.getReferee());
		assertEquals("This is a reference comment", reloadedComment.getComment());
		assertFalse(reloadedComment.getSuitableForProgramme());
		assertFalse(reloadedComment.getSuitableForUCL());
		assertEquals(DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE), DateUtils.truncate(reloadedComment.getDate(), Calendar.DATE));
		assertEquals(referee.getId(), reloadedComment.getReferee().getId());
		assertEquals(commentProviderUser.getId(), reloadedComment.getProvidedBy().getId());
	}
}

