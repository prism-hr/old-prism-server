package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.builders.DisabilityBuilder;
import com.zuehlke.pgadmissions.services.DisabilityService;

public class DisabilityPropertyEditorTest {

	private DisabilityService disabilityServiceMock;
	private DisabilityPropertyEditor editor;

	@Before
	public void setup() {
		disabilityServiceMock = EasyMock.createMock(DisabilityService.class);
		editor = new DisabilityPropertyEditor(disabilityServiceMock);
	}

	@Test
	public void shouldLoadByIdAndSetAsValue() {
		Disability disability = new DisabilityBuilder().id(1).toDisability();
		EasyMock.expect(disabilityServiceMock.getDisabilityById(1)).andReturn(disability);
		EasyMock.replay(disabilityServiceMock);

		editor.setAsText("1");
		assertEquals(disability, editor.getValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfIdNotInteger() {
		editor.setAsText("bob");
	}

	@Test
	public void shouldReturNullIfIdIsNull() {
		editor.setAsText(null);
		assertNull(editor.getValue());
	}

	@Test
	public void shouldReturNullIfIdIsEmptyString() {
		editor.setAsText(" ");
		assertNull(editor.getValue());
	}

	@Test
	public void shouldReturnNullIfValueIsNull() {
		editor.setValue(null);
		assertNull(editor.getAsText());
	}

	@Test
	public void shouldReturnNullIfValueIdIsNull() {
		editor.setValue(new DisabilityBuilder().toDisability());
		assertNull(editor.getAsText());
	}

	@Test
	public void shouldReturnIdAsString() {
		editor.setValue(new DisabilityBuilder().id(5).toDisability());
		assertEquals("5", editor.getAsText());
	}
}
