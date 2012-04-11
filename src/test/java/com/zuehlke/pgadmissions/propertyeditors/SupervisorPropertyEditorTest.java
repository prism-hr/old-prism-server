package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.services.SupervisorService;

public class SupervisorPropertyEditorTest {

	private SupervisorService supervisorServiceMock;
	private SupervisorPropertyEditor editor;


	@Test	
	public void shouldLoadByIdAndSetAsValue(){
		Supervisor supervisor = new SupervisorBuilder().id(1).toSupervisor();
		EasyMock.expect(supervisorServiceMock.getSupervisorWithId(1)).andReturn(supervisor);
		EasyMock.replay(supervisorServiceMock);
		
		editor.setAsText("1");
		assertEquals(supervisor, editor.getValue());
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfIdNotInteger(){			
		editor.setAsText("bob");			
	}
	
	@Test	
	public void shouldReturNullIfIdIsNull(){			
		editor.setAsText(null);
		assertNull(editor.getValue());		
	}
	
	@Test	
	public void shouldReturnNullIfValueIsNull(){			
		editor.setValue(null);
		assertNull(editor.getAsText());
	}
	
	@Test	
	public void shouldReturnNullIfValueIdIsNull(){			
		editor.setValue(new SupervisorBuilder().toSupervisor());
		assertNull(editor.getAsText());
	}
	
	@Test	
	public void shouldReturnIsAsString(){			
		editor.setValue(new SupervisorBuilder().id(5).toSupervisor());
		assertEquals("5", editor.getAsText());
	}
	
	@Before
	public void setup(){
		supervisorServiceMock = EasyMock.createMock(SupervisorService.class);
		editor = new SupervisorPropertyEditor(supervisorServiceMock);
	}
}
