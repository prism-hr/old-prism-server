package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.services.ApplicationsService;

public class ApplicationFormPropertyEditorTest {

	private ApplicationsService applicationsServiceMock;
	private ApplicationFormPropertyEditor editor;


	@Test	
	public void shouldLoadByIdAndSetAsValue(){
		ApplicationForm form = new ApplicationFormBuilder().id(1).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(form);
		EasyMock.replay(applicationsServiceMock);
		
		editor.setAsText("1");
		assertEquals(form, editor.getValue());
		
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
		editor.setValue(new ApplicationFormBuilder().toApplicationForm());
		assertNull(editor.getAsText());
	}
	
	@Test	
	public void shouldReturnIsAsString(){			
		editor.setValue(new ApplicationFormBuilder().id(5).toApplicationForm());
		assertEquals("5", editor.getAsText());
	}
	
	@Before
	public void setup(){
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		editor = new ApplicationFormPropertyEditor(applicationsServiceMock);
	}
}
