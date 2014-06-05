package com.zuehlke.pgadmissions.interceptors;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.unitils.easymock.EasyMockUnitils.replay;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.cookie.MalformedCookieException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.AlertDefinition.AlertType;
import com.zuehlke.pgadmissions.services.UserService;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class PgadmissionsExceptionResolverTest {

    @Mock
    @InjectIntoByType
    private UserService userServiceMock;
    
    @TestedObject
    private PgadmissionsExceptionResolver resolver;

    @Test
    public void shouldHandleMissingApplicationFormException() {

        // GIVEN
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        ResourceNotFoundException exception = new ResourceNotFoundException("app1");

        // WHEN
        ModelAndView modelAndView = resolver.resolveException(request, response, null, exception);

        // THEN
        AlertDefinition alert = (AlertDefinition) request.getSession().getAttribute("alertDefinition");
        assertEquals(AlertType.INFO, alert.getType());
        assertEquals("Missing application", alert.getTitle());
        assertEquals("The application does not exist: app1.", alert.getDescription());

        assertEquals("redirect:/applications", modelAndView.getViewName());
    }
    
    @Test
    public void shouldHandleUnexpectedException() {

        // GIVEN
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        MalformedCookieException exception = new MalformedCookieException("Cookie monster!!");

        expect(userServiceMock.getCurrentUser()).andReturn(new User());
        
        // WHEN
        replay();
        ModelAndView modelAndView = resolver.resolveException(request, response, null, exception);

        // THEN
        assertEquals("redirect:error", modelAndView.getViewName());
    }


}
