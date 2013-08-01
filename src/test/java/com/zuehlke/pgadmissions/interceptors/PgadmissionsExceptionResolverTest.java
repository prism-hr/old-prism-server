package com.zuehlke.pgadmissions.interceptors;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.interceptors.AlertDefinition.AlertType;

public class PgadmissionsExceptionResolverTest {

    private PgadmissionsExceptionResolver resolver;

    @Test
    public void shouldHandleMissingApplicationFormException() {

        // GIVEN
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        MissingApplicationFormException exception = new MissingApplicationFormException("app1");

        // WHEN
        ModelAndView modelAndView = resolver.resolveException(request, response, null, exception);

        // THEN
        AlertDefinition alert = (AlertDefinition) request.getSession().getAttribute("alertDefinition");
        assertEquals(AlertType.INFO, alert.getType());
        assertEquals("Missing application", alert.getTitle());
        assertEquals("Application does not exist: app1", alert.getDescription());

        assertEquals("redirect:/applications", modelAndView.getViewName());
    }
    
    @Test
    public void shouldHandleUnexpectedException() {

        // GIVEN
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        CookieTheftException exception = new CookieTheftException("Cookie monster!!");

        // WHEN
        ModelAndView modelAndView = resolver.resolveException(request, response, null, exception);

        // THEN
        assertEquals("redirect:error", modelAndView.getViewName());
    }

    @Before
    public void setup() {
        resolver = new PgadmissionsExceptionResolver();
    }

}
