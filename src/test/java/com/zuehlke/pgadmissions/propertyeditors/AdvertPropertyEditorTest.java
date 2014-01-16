package com.zuehlke.pgadmissions.propertyeditors;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class AdvertPropertyEditorTest {
	private AdvertPropertyEditor editor;

	@Before
	public void setup() {
		editor = new AdvertPropertyEditor();
	}

	@Test
	public void parseString() {
		editor.setAsText("line\nline");
		Assert.assertEquals(editor.getValue(), "lineline");
	}

}