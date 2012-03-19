package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.domain.enums.AwareStatus;
import com.zuehlke.pgadmissions.domain.enums.PrimaryStatus;


public class SupervisorJSONPropertyEditorTest {
	private SupervisorJSONPropertyEditor editor;

	@Test	
	public void shouldParseAndSetAsValue(){
		editor.setAsText("{\"email\": \"test@gmail.com\" , \"primarySupervisor\": \"YES\" , \"awareSupervisor\": \"YES\"}");
		Supervisor expected = new SupervisorBuilder().email("test@gmail.com").awareSupervisor(AwareStatus.YES).primarySupervisor(PrimaryStatus.YES).toSupervisor();
		Supervisor supervisor =   (Supervisor) editor.getValue();
		assertEquals(expected.getEmail(), supervisor.getEmail());
		assertEquals(expected.getAwareSupervisor(), supervisor.getAwareSupervisor());
		assertEquals(expected.getPrimarySupervisor(), supervisor.getPrimarySupervisor());
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
		editor.setValue(new SupervisorBuilder().email("test@gmail.com").awareSupervisor(AwareStatus.NO).primarySupervisor(PrimaryStatus.NO).toSupervisor());
		assertEquals("{\"email\": \"test@gmail.com\", \"primarySupervisor\": \"NO\", \"awareSupervisor\": \"NO\"}", editor.getAsText());
	}
	
	@Before
	public void setup(){
		
		editor = new SupervisorJSONPropertyEditor();
	}
}
