package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.domain.enums.AwareStatus;


public class SupervisorJSONPropertyEditorTest {
	private SupervisorJSONPropertyEditor editor;

	@Test	
	public void shouldParseAndSetAsValue(){
		editor.setAsText("{\"firstname\": \"Mark\",\"lastname\": \"Johnson\",\"email\": \"test@gmail.com\" , \"awareSupervisor\": \"YES\"}");
		Supervisor expected = new SupervisorBuilder().id(1).firstname("Mark").lastname("Johnson").email("test@gmail.com").awareSupervisor(AwareStatus.YES).toSupervisor();
		Supervisor supervisor =   (Supervisor) editor.getValue();
		assertEquals(expected.getFirstname(), supervisor.getFirstname());
		assertEquals(expected.getLastname(), supervisor.getLastname());
		assertEquals(expected.getEmail(), supervisor.getEmail());
		assertEquals(expected.getAwareSupervisor(), supervisor.getAwareSupervisor());
	}
	
	@Test	
	public void shouldParseEmptyIdAsNull(){
		editor.setAsText("{\"firstname\": \"Mark\",\"lastname\": \"Johnson\",\"email\": \"test@gmail.com\" , \"primarySupervisor\": \"YES\" , \"awareSupervisor\": \"YES\"}");		
		Supervisor supervisor =   (Supervisor) editor.getValue();
		assertNull (supervisor.getId());
		
	}
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfAStringNotInTheRightFormat(){			
		editor.setAsText("{email: 'test@gmail.com' primarySupervisor: 'YES' awareSupervisor: 'YES'}");		
	}
	
	@Test	
	public void shouldReturNullIfStringIsNull(){			
		editor.setAsText(null);
		assertNull(editor.getValue());		
	}
	@Test	
	public void shouldReturNullIfStringIsEmpty(){			
		editor.setAsText("");
		assertNull(editor.getValue());		
	}
	
	@Test	
	public void shouldReturnNullIfValueIsNull(){			
		editor.setValue(null);
		assertNull(editor.getAsText());
	}
	
	@Test	
	public void shouldReturnCorrectjsonString(){			
		editor.setValue(new SupervisorBuilder().firstname("Mark").id(1).lastname("Johnson").email("test@gmail.com").awareSupervisor(AwareStatus.NO).toSupervisor());
		assertEquals("{\"firstname\": \"Mark\",\"lastname\": \"Johnson\",\"email\": \"test@gmail.com\", \"awareSupervisor\": \"NO\"}", editor.getAsText());
	}
	
	@Before
	public void setup(){
		
		editor = new SupervisorJSONPropertyEditor();
	}
}
