package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class RefereeTest {

	@Test
	public void shouldReturnTrueIfReferenceProvided(){
		Referee referee = new RefereeBuilder().application(new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).build()).toReferee();
		assertFalse(referee.hasProvidedReference());
		referee.setReference(new ReferenceComment());		
		assertTrue(referee.hasProvidedReference());
	}
	
	@Test
	public void shouldReturnEditableTrueIfReferenceNotProvided(){
		Referee referee = new RefereeBuilder().application(new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).build()).toReferee();		
		assertTrue(referee.isEditable());
		referee.setReference(new ReferenceComment());		
		assertFalse(referee.isEditable());
	}
	
	@Test
	public void shouldReturnEditableTrueIfRefereeHasNotDeclined(){
		Referee referee = new RefereeBuilder().application(new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).build()).toReferee();
		assertTrue(referee.isEditable());
		referee.setDeclined(true);
		assertFalse(referee.isEditable());
	}
	@Test
	public void shouldReturnEditableFalseIfApplicationFormNotModifiable(){
		Referee referee = new RefereeBuilder().application(new ApplicationFormBuilder().status(ApplicationFormStatus.WITHDRAWN).build()).toReferee();		
		assertFalse(referee.isEditable());
	}
	@Test
	public void shouldReturnEditableTrueIfApplicationFormIsNull(){
		Referee referee = new RefereeBuilder().toReferee();		
		assertTrue(referee.isEditable());
	}

	@Test
	public void shouldReturnRespondedTrueIfDeclined(){
		Referee referee = new RefereeBuilder().declined(true).toReferee();		
		assertTrue(referee.hasResponded());
	
	}
	@Test
	public void shouldReturnRespondedTrueIfReferenceProcided(){
		Referee referee = new RefereeBuilder().declined(true).toReferee();	
		referee.setReference(new ReferenceComment());		
		assertTrue(referee.hasResponded());
	
	}
	@Test
	public void shouldReturnFalseIfNeitherDeclineNorProvidedReference(){
		Referee referee = new RefereeBuilder().declined(false).toReferee();
		
		assertFalse(referee.hasResponded());
	
	}
}
