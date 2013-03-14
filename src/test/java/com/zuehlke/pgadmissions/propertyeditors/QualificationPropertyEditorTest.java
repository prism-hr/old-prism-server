package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.builders.QualificationBuilder;
import com.zuehlke.pgadmissions.services.QualificationService;

public class QualificationPropertyEditorTest {
	private QualificationService qualificationServiceMock;
	private QualificationPropertyEditor editor;

	@Test	
	public void shouldLoadByIdAndSetAsValue(){
		Qualification qualification = new QualificationBuilder().id(1).build();
		EasyMock.expect(qualificationServiceMock.getQualificationById(1)).andReturn(qualification);
		EasyMock.replay(qualificationServiceMock);
		
		editor.setAsText("1");
		assertEquals(qualification, editor.getValue());
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
		editor.setValue(new QualificationBuilder().build());
		assertNull(editor.getAsText());
	}
	
	@Test	
	public void shouldReturnIdAsString(){			
		editor.setValue(new QualificationBuilder().id(5).build());
		assertEquals("5", editor.getAsText());
	}
	
	@Before
	public void setup(){
		qualificationServiceMock = EasyMock.createMock(QualificationService.class);
		editor = new QualificationPropertyEditor(qualificationServiceMock);
	}
}
