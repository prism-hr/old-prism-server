package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.services.ProgramsService;

public class ProgramPropertyEditorTest {

	private ProgramsService programServiceMock;
	private ProgramPropertyEditor editor;


	@Test	
	public void shouldLoadByIdAndSetAsValue(){
		Program program = new ProgramBuilder().id(1).toProgram();
		EasyMock.expect(programServiceMock.getProgramById(1)).andReturn(program);
		EasyMock.replay(programServiceMock);
		
		editor.setAsText("1");
		assertEquals(program, editor.getValue());
		
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
	public void shouldReturNullIfIdIsEmptyString(){			
		editor.setAsText(" ");
		assertNull(editor.getValue());		
	}
	@Test	
	public void shouldReturnNullIfValueIsNull(){			
		editor.setValue(null);
		assertNull(editor.getAsText());
	}
	
	@Test	
	public void shouldReturnNullIfValueIdIsNull(){			
		editor.setValue(new ProgramBuilder().toProgram());
		assertNull(editor.getAsText());
	}
	
	@Test	
	public void shouldReturnIdAsString(){			
		editor.setValue(new ProgramBuilder().id(5).toProgram());
		assertEquals("5", editor.getAsText());
	}
	
	@Before
	public void setup(){
		programServiceMock = EasyMock.createMock(ProgramsService.class);
		editor = new ProgramPropertyEditor(programServiceMock);
	}
}
