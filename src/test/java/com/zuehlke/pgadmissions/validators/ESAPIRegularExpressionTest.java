package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Before;
import org.junit.Test;
import org.owasp.esapi.ESAPI;

public class ESAPIRegularExpressionTest {

    private String[] validEmailAddresses = new String[] { "mkyong@yahoo.com", "mkyong-100@yahoo.com", "mkyong.100@yahoo.com", "mkyong111@mkyong.com",
            "mkyong-100@mkyong.net", "mkyong.100@mkyong.com.au", "mkyong@1.com", "mkyong@gmail.com.com", "mkyong+100@gmail.com", "mkyong-100@yahoo-test.com" };

    private String[] invalidEmailAddresses = new String[] { "mkyong", "mkyong@.com.my", "mkyong123@.com", "mkyong123@.com.com", ".mkyong@mkyong.com",
            "mkyong()*@gmail.com", "mkyong..2002@gmail.com", "mkyong.@gmail.com", "mkyong@mkyong@gmail.com", "richard. taylor@ucl.ac.uk" };

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
    public void shouldAllowCarriageReturn() {
        assertTrue(ESAPI.validator().isValidInput("Input", "\nDenver", "ExtendedAscii", 30, false));
        assertTrue(ESAPI.validator().isValidInput("Input", "\r\nDenver", "ExtendedAscii", 30, false));
    }

    @Test
    public void shouldDisallowNonExtendedAscii() {
        String chineseName = StringEscapeUtils.unescapeJava("\\u5b9d\\u8912\\u82de\\n");
        String russianName = StringEscapeUtils.unescapeJava("\\u0410\\u0444\\u0430\\u043d\\u0430\\u0441\\u0438\\u0439");
        assertFalse(ESAPI.validator().isValidInput("Input", chineseName, "ExtendedAscii", 30, false));
        assertFalse(ESAPI.validator().isValidInput("Input", russianName, "ExtendedAscii", 30, false));
    }

    @Test
    public void shouldAcceptCommentWithNewlineAndTab() {
        String comment = "" + "Lorem ipsum dolor sit amet, tempus pellentesque eget, nec risus curabitur, \n\t"
                + "arcu in mauris mattis, luctus tempor. Ipsum tincidunt, sit quam per vestibulum \n\t"
                + "vehicula morbi elit, nunc eu id praesent, velit elementum vel lacus ipsum.";

        assertFalse(ESAPI.validator().isValidInput("Input", comment, "ExtendedAscii", 250, false));
    }

    @Test
    public void shouldAcceptValidEmailAddresses() {
        for (String emailAddress : validEmailAddresses) {
            assertTrue(String.format("The email address [%s] should be valid.", emailAddress),
                    ESAPI.validator().isValidInput("Email", emailAddress, "Email", 255, false));
        }
    }

    @Test
    public void shouldRejectInValidEmailAddresses() {
        for (String emailAddress : invalidEmailAddresses) {
            assertFalse(String.format("The email address [%s] should be invalid.", emailAddress),
                    ESAPI.validator().isValidInput("Email", emailAddress, "Email", 255, false));
        }
    }

    @Test
    public void shouldAcceptPhoneNumber() {
        assertTrue(ESAPI.validator().isValidInput("PhoneNumber", "+44 (0) 20 7911 5000", "PhoneNumber", 255, false));
    }

    @Test
    public void shouldRejectPhoneNumber() {
        assertFalse(ESAPI.validator().isValidInput("PhoneNumber", "+44 (0)20-7911 5000  368664149 ", "PhoneNumber", 255, false));
    }

    @Test
    public void shouldAcceptESAPI200Words() {
        String comment = "Aa aa aa aa aa, aa aa \\u5b9d\\u8912\\u82aa aa aa. \n\t" + "Aa aa aa aa aa, aa aa aa aa aa. \n\t" //
                + "Aa aa aa aa aa, aa aa aa aa aa. \n\t" + "Aa aa aa aa aa, aa aa aa aa aa. \n\t" //
                + "Aa aa aa aa aa, aa aa aa aa aa. \n\t" + "Aa aa aa aa aa, aa aa aa aa aa. \n\t" //
                + "Aa aa aa aa aa, aa aa aa aa aa. \n\t" + "Aa aa aa aa aa, aa aa aa aa aa. \n\t" //
                + "Aa aa aa aa aa, aa aa aa aa aa. \n\t" + "Aa aa aa aa aa, aa aa aa aa aa. \n\t" //
                + "Aa aa aa aa aa, aa aa aa aa aa. \n\t" + "Aa aa aa aa aa, aa aa aa aa aa. \n\t" //
                + "Aa aa aa aa aa, aa aa aa aa aa. \n\t" + "Aa aa aa aa aa, aa aa aa aa aa. \n\t" //
                + "Aa aa aa aa aa, aa aa aa aa aa. \n\t" + "Aa aa aa aa aa, aa aa aa aa aa. \n\t" //
                + "Aa aa aa aa aa, aa aa aa aa aa. \n\t" + "Aa aa aa aa aa, aa aa aa aa aa. \n\t" //
                + "Aa aa aa aa aa, aa aa aa aa aa. \n\t" + "Aa aa aa aa aa, aa aa aa aa aa.";

        assertTrue(ESAPI.validator().isValidInput("ATAS", comment, "ATAS", Integer.MAX_VALUE, false));
    }

    @Test
    public void shouldRejectESAPI201Words() {
        String comment = "Aa aa aa aa aa, aa aa aa aa aa. \n\t" + "Aa aa aa aa aa, aa aa aa aa aa. \n\t" //
                + "Aa aa aa aa aa, aa aa aa aa aa. \n\t" + "Aa aa aa aa aa, aa aa aa aa aa. \n\t" //
                + "Aa aa aa aa aa, aa aa aa aa aa. \n\t" + "Aa aa aa aa aa, aa aa aa aa aa. \n\t" //
                + "Aa aa aa aa aa, aa aa aa aa aa. \n\t" + "Aa aa aa aa aa, aa aa aa aa aa. \n\t" //
                + "Aa aa aa aa aa, aa aa aa aa aa. \n\t" + "Aa aa aa aa aa, aa aa aa aa aa\\u5b9d\\u8912\\u82 \n\t" //
                + "Aa aa aa aa aa, aa aa aa aa aa. \n\t" + "Aa aa aa aa aa, aa aa aa aa aa. \n\t" //
                + "Aa aa aa aa aa, aa aa aa aa aa. \n\t" + "Aa aa aa aa aa, aa aa aa aa aa. \n\t" //
                + "Aa aa aa aa aa, aa aa aa aa aa. \n\t" + "Aa aa aa aa aa, aa aa aa aa aa. \n\t" //
                + "Aa aa aa aa aa, aa aa aa aa aa. \n\t" + "Aa aa aa aa aa, aa aa aa aa aa. \n\t" //
                + "Aa aa aa aa aa, aa aa aa aa aa. \n\t" + "Aa aa aa aa aa, aa aa aa aa aa. bbb! \n\t";

        assertFalse(ESAPI.validator().isValidInput("ATAS", comment, "ATAS", Integer.MAX_VALUE, false));
    }

    @Test
    public void shouldAcceptESAPIWithLeadingAndTrailingSpaces() {
        assertTrue(ESAPI.validator().isValidInput("ATAS", "\t\n    \rfdfd\rfdsfsd \n\t\r", "ATAS", Integer.MAX_VALUE, false));
    }
}
