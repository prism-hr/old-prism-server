package com.zuehlke.pgadmissions.propertyeditors;

import static junit.framework.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class DurationOfStudyPropertyEditorTest {

    private DurationOfStudyPropertyEditor editor;

    @Before
    public void setUp() {
        editor = new DurationOfStudyPropertyEditor();
    }

    @Test
    public void shouldSetErrorValueForEmptyDurationValue() {
        editor.setAsText("{\"value\":\"\",\"unit\":\"Months\"}");
        assertEquals(DurationOfStudyPropertyEditor.ERROR_VALUE_FOR_DURATION_OF_STUDY, editor.getValue());
    }

    @Test
    public void shouldSetErrorValueForEmptyDurationUnit() {
        editor.setAsText("{\"value\":\"2\",\"unit\":\"\"}");
        assertEquals(DurationOfStudyPropertyEditor.ERROR_VALUE_FOR_DURATION_OF_STUDY, editor.getValue());
    }

    @Test
    public void shouldSetErrorValueForEmptyDurationValueAndUnit() {
        editor.setAsText("{\"value\":\"\",\"unit\":\"\"}");
        assertEquals(DurationOfStudyPropertyEditor.ERROR_VALUE_FOR_DURATION_OF_STUDY, editor.getValue());
    }

    @Test
    public void shouldSetValidValueForMonths() {
        editor.setAsText("{\"value\":\"2\",\"unit\":\"Months\"}");
        assertEquals(2, editor.getValue());
    }

    @Test
    public void shouldSetValidValueForYears() {
        editor.setAsText("{\"value\":\"2\",\"unit\":\"Years\"}");
        assertEquals(24, editor.getValue());
    }

    @Test
    public void shouldSetErrorValueForNonIntegerValue() {
        editor.setAsText("{\"value\":\"2.1\",\"unit\":\"Years\"}");
        assertEquals(DurationOfStudyPropertyEditor.ERROR_VALUE_FOR_DURATION_OF_STUDY, editor.getValue());
    }

    @Test
    public void shouldSetErrorValueForNonNumericValue() {
        editor.setAsText("{\"value\":\"abc\",\"unit\":\"Years\"}");
        assertEquals(DurationOfStudyPropertyEditor.ERROR_VALUE_FOR_DURATION_OF_STUDY, editor.getValue());
    }

    @Test
    public void shouldSetValidValueForValidValueWithSpace() {
        editor.setAsText("{\"value\":\"1      \",\"unit\":\"Years\"}");
        assertEquals(12, editor.getValue());
    }

}
