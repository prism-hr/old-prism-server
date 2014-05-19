package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.services.ApplicationFormService;

public class ApplicationFormPropertyEditorTest {

	private ApplicationFormService applicationsServiceMock;
	private ApplicationFormPropertyEditor editor;


	@Test	
	public void shouldLoadByIdAndSetAsValue(){
		Application form = new ApplicationFormBuilder().id(1).build();
		EasyMock.expect(applicationsServiceMock.getByApplicationNumber("1")).andReturn(form);
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
		editor.setValue(new ApplicationFormBuilder().applicationNumber("5").build());
		assertEquals("5", editor.getAsText());
	}
	
	@Before
	public void setup(){
		applicationsServiceMock = EasyMock.createMock(ApplicationFormService.class);
		editor = new ApplicationFormPropertyEditor(applicationsServiceMock);
	}
}
