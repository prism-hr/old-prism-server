package com.zuehlke.pgadmissions.propertyeditors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.FilterConstraint;
import com.zuehlke.pgadmissions.domain.enums.ApplicationListFilterCategory;

public class ApplicationsFiltersPropertyEditorTest {

    private ApplicationsFiltersPropertyEditor editor;

    @Before
    public void initialize() {
        editor = new ApplicationsFiltersPropertyEditor();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldDeserializeTwoElements() {
        editor.setAsText("[{\"searchCategory\":\"APPLICATION_NUMBER\",\"searchTerm\":\"app_number\"},{\"searchCategory\":\"APPLICANT_NAME\",\"searchTerm\":\"Francishek Pieczka\"}]");
        
        List<FilterConstraint> list = (List<FilterConstraint>) editor.getValue();
        FilterConstraint numberFilter = list.get(0);
        FilterConstraint applicantFilter = list.get(1);
        
        assertEquals(ApplicationListFilterCategory.APPLICATION_NUMBER, numberFilter.getSearchCategory());
        assertEquals("app_number", numberFilter.getSearchTerm());
        
        assertEquals(ApplicationListFilterCategory.APPLICANT_NAME, applicantFilter.getSearchCategory());
        assertEquals("Francishek Pieczka", applicantFilter.getSearchTerm());
        
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldDeserializeEmptyList() {
        editor.setAsText("[]");
        List<Integer> list = (List<Integer>) editor.getValue();
        assertTrue(list.isEmpty());
    }

}
