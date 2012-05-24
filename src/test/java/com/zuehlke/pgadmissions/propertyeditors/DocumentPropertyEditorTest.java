package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.services.DocumentService;

public class DocumentPropertyEditorTest {
	private DocumentService documentServiceMock;
	private DocumentPropertyEditor editor;


	@Test	
	public void shouldLoadByIdAndSetAsValue(){
		Document document = new DocumentBuilder().id(1).toDocument();
		EasyMock.expect(documentServiceMock.getDocumentById(1)).andReturn(document);
		EasyMock.replay(documentServiceMock);
		
		editor.setAsText("1");
		assertEquals(document, editor.getValue());
		
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
		editor.setValue(new DocumentBuilder().toDocument());
		assertNull(editor.getAsText());
	}
	
	@Test	
	public void shouldReturnIdAsString(){			
		editor.setValue(new DocumentBuilder().id(5).toDocument());
		assertEquals("5", editor.getAsText());
	}
	
	@Before
	public void setup(){
		documentServiceMock = EasyMock.createMock(DocumentService.class);
		editor = new DocumentPropertyEditor(documentServiceMock);
	}
}
