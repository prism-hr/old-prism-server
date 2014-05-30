package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.services.ProgramService;

public class ProgramPropertyEditorTest {
	private ProgramPropertyEditor editor;

	private ProgramService programServiceMock;


	@Test	
	public void shouldLoadByIdAndSetAsValue(){
		Program program = new Program().withId(1);
		EasyMock.expect(programServiceMock.getProgramByCode("ABC")).andReturn(program);
		EasyMock.replay(programServiceMock);
		
		editor.setAsText("ABC");

		EasyMock.verify(programServiceMock);
		assertEquals(program, editor.getValue());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfCodeNotLoaded(){
		EasyMock.expect(programServiceMock.getProgramByCode("bob")).andThrow(new IllegalArgumentException("intentional.."));
		EasyMock.replay(programServiceMock);
		
		editor.setAsText("bob");			
	}
	
	@Test	
	public void shouldReturNullIfCodeIsNull(){			
		editor.setAsText(null);
		assertNull(editor.getValue());		
	}
	
	@Test	
	public void shouldReturNullIfCodeIsEmptyString(){			
		editor.setAsText(" ");
		assertNull(editor.getValue());		
	}
	@Test	
	public void shouldReturnNullIfCodeIsNull(){			
		editor.setValue(null);
		assertNull(editor.getAsText());
	}
	
	@Test	
	public void shouldReturnNullIfCodeValueIsNull(){			
		editor.setValue(new Program());
		assertNull(editor.getAsText());
	}
	
	@Test	
	public void shouldReturnIdAsString(){			
		editor.setValue(new Program().withId(5).withCode("ABC"));
		EasyMock.replay(programServiceMock);
		
		assertEquals("ABC", editor.getAsText());
		
		EasyMock.verify(programServiceMock);
	}
	
	@Before
	public void setup(){
		programServiceMock = EasyMock.createMock(ProgramService.class);
		editor = new ProgramPropertyEditor(programServiceMock);
	}
}
