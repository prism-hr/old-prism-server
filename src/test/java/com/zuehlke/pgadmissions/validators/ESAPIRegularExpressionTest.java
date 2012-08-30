package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Before;
import org.junit.Test;
import org.owasp.esapi.ESAPI;

public class ESAPIRegularExpressionTest {

    @Before
    public void setUp() {
        System.setProperty("org.owasp.esapi.resources", "src/main/resources/");
    }
    
    @Test
    public void shouldAllowExtendedAscii() {
        assertTrue(ESAPI.validator().isValidInput("Input", "Denver", "ExtendedAscii", 30, false));
        assertTrue(ESAPI.validator().isValidInput("Input", "Müller", "ExtendedAscii", 30, false));
        assertTrue(ESAPI.validator().isValidInput("Input", "Pascalé", "ExtendedAscii", 30, false));
        assertTrue(ESAPI.validator().isValidInput("Input", "Hans-Peter", "ExtendedAscii", 30, false));
    }
    
    @Test
    public void shouldDisallowNonExtendedAscii() {
        String chineseName = StringEscapeUtils.unescapeJava("\\u5b9d\\u8912\\u82de\\n");
        String russianName = StringEscapeUtils.unescapeJava("\\u0410\\u0444\\u0430\\u043d\\u0430\\u0441\\u0438\\u0439");
        assertFalse(ESAPI.validator().isValidInput("Input", chineseName, "ExtendedAscii", 30, false));
        assertFalse(ESAPI.validator().isValidInput("Input", russianName, "ExtendedAscii", 30, false));        
    }
}
