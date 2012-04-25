package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Ethnicity;
import com.zuehlke.pgadmissions.domain.builders.EthnicityBuilder;
import com.zuehlke.pgadmissions.services.EthnicityService;

public class EthnicityPropertyEditorTest {

	private EthnicityService ethnicityServiceMock;
	private EthnicityPropertyEditor editor;

	@Before
	public void setup() {
		ethnicityServiceMock = EasyMock.createMock(EthnicityService.class);
		editor = new EthnicityPropertyEditor(ethnicityServiceMock);
	}

	@Test
	public void shouldLoadByIdAndSetAsValue() {
		Ethnicity ethnicity = new EthnicityBuilder().id(1).toEthnicity();
		EasyMock.expect(ethnicityServiceMock.getEthnicityById(1)).andReturn(ethnicity);
		EasyMock.replay(ethnicityServiceMock);

		editor.setAsText("1");
		assertEquals(ethnicity, editor.getValue());

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
		editor.setValue(new EthnicityBuilder().toEthnicity());
		assertNull(editor.getAsText());
	}

	@Test
	public void shouldReturnIdAsString() {
		editor.setValue(new EthnicityBuilder().id(5).toEthnicity());
		assertEquals("5", editor.getAsText());
	}
}
