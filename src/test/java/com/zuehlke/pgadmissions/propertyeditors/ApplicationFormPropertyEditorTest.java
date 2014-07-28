package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.services.ApplicationService;

public class ApplicationFormPropertyEditorTest {

	private ApplicationService applicationsServiceMock;
	private ApplicationFormPropertyEditor editor;


	@Test	
	public void shouldLoadByIdAndSetAsValue(){
		Application form = new ApplicationFormBuilder().id(1).build();
		EasyMock.expect(applicationsServiceMock.getByCode("1")).andReturn(form);
		EasyMock.replay(applicationsServiceMock);
		
		editor.setAsText("1");
		assertEquals(form, editor.getValue());
		
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
	public void shouldReturnApplicationNumbersAsString(){			
		editor.setValue(new ApplicationFormBuilder().build());
		assertEquals("5", editor.getAsText());
	}
	
	@Before
	public void setup(){
		applicationsServiceMock = EasyMock.createMock(ApplicationService.class);
		editor = new ApplicationFormPropertyEditor(applicationsServiceMock);
	}
}
