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
		Referee referee = new RefereeBuilder().application(new ApplicationFormBuilder().status(new State().withId(ApplicationFormStatus.VALIDATION)).build()).build();
		assertFalse(referee.hasProvidedReference());
		referee.setReference(new ReferenceComment());		
		assertTrue(referee.hasProvidedReference());
	}
	
	@Test
	public void shouldReturnEditableTrueIfReferenceNotProvided(){
		Referee referee = new RefereeBuilder().application(new ApplicationFormBuilder().status(new State().withId(ApplicationFormStatus.VALIDATION)).build()).build();		
		assertTrue(referee.isEditable());
		referee.setReference(new ReferenceComment());		
		assertFalse(referee.isEditable());
	}
	
	@Test
	public void shouldReturnEditableTrueIfRefereeHasNotDeclined(){
		Referee referee = new RefereeBuilder().application(new ApplicationFormBuilder().status(new State().withId(ApplicationFormStatus.VALIDATION)).build()).build();
		assertTrue(referee.isEditable());
		referee.setDeclined(true);
		assertFalse(referee.isEditable());
	}
	@Test
	public void shouldReturnEditableFalseIfApplicationFormNotModifiable(){
		Referee referee = new RefereeBuilder().application(new ApplicationFormBuilder().status(new State().withId(ApplicationFormStatus.WITHDRAWN)).build()).build();		
		assertFalse(referee.isEditable());
	}
	@Test
	public void shouldReturnEditableTrueIfApplicationFormIsNull(){
		Referee referee = new RefereeBuilder().build();		
		assertTrue(referee.isEditable());
	}

	@Test
	public void shouldReturnRespondedTrueIfDeclined(){
		Referee referee = new RefereeBuilder().declined(true).build();		
		assertTrue(referee.hasResponded());
	
	}
	@Test
	public void shouldReturnRespondedTrueIfReferenceProcided(){
		Referee referee = new RefereeBuilder().declined(true).build();	
		referee.setReference(new ReferenceComment());		
		assertTrue(referee.hasResponded());
	
	}
	@Test
	public void shouldReturnFalseIfNeitherDeclineNorProvidedReference(){
		Referee referee = new RefereeBuilder().declined(false).build();
		
		assertFalse(referee.hasResponded());
	
	}
}
