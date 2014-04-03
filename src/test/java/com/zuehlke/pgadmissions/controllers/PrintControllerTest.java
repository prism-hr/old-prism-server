package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.ServletRequestBindingException;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;

import com.itextpdf.text.DocumentException;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pdf.PdfDocumentBuilder;
import com.zuehlke.pgadmissions.pdf.PdfModelBuilder;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class PrintControllerTest {

    @Mock
    @InjectIntoByType
    private ApplicationFormService applicationSevice;

    @Mock
    @InjectIntoByType
    private PdfDocumentBuilder pdfDocumentBuilder;

    @Mock
    @InjectIntoByType
    private UserService userService;

    @Mock
    @InjectIntoByType
    private RefereeService refereeService;

    private PrintController controller;

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionIfUserCannotSeeApplicationForm() throws ServletRequestBindingException, IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("applicationFormId", "23");
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setOutputStreamAccessAllowed(true);

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).build();
        EasyMock.expect(applicationSevice.getByApplicationNumber("23")).andReturn(applicationForm).anyTimes();
        // EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(false);
        controller.printPage(request, response);

    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionIfApplicationFormDoesNotExist() throws ServletRequestBindingException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("applicationFormId", "23");
        MockHttpServletResponse response = new MockHttpServletResponse();
        EasyMock.expect(applicationSevice.getByApplicationNumber("23")).andReturn(null).anyTimes();
        EasyMock.replay(applicationSevice);
        controller.printPage(request, response);
    }

    @Test
    public void shouldBuildPDFForApplicationAndSend() throws IOException, ServletRequestBindingException {
        RegisteredUser applicant = new RegisteredUser();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("applicationFormId", "23");
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setOutputStreamAccessAllowed(true);

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).applicant(applicant).build();
        // EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
        EasyMock.expect(applicationSevice.getByApplicationNumber("23")).andReturn(applicationForm).anyTimes();
        byte[] bytes = "pdf".getBytes();
        EasyMock.expect(pdfDocumentBuilder.build(EasyMock.isA(PdfModelBuilder.class), EasyMock.eq(applicationForm))).andReturn(bytes);

        EasyMock.replay(applicationSevice, pdfDocumentBuilder);

        controller.printPage(request, response);

        assertArrayEquals(bytes, response.getContentAsByteArray());
        assertEquals("0", response.getHeader("Expires"));
        assertEquals("must-revalidate, post-check=0, pre-check=0", response.getHeader("Cache-Control"));
        assertEquals("public", response.getHeader("Pragma"));
        assertEquals("inline; filename=\"UCL_PRISM_23.pdf\"", response.getHeader("Content-Disposition"));
        assertEquals("application/pdf", response.getContentType());
        assertEquals(bytes.length, response.getContentLength());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldBuildPDFForAllSelectedApplicationsAndSend() throws IOException, ServletRequestBindingException, DocumentException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("appList", "23;34;");
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setOutputStreamAccessAllowed(true);

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().id(2).applicant(new RegisteredUserBuilder().id(4).build()).build();
        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().id(3).applicant(new RegisteredUserBuilder().id(5).build()).build();
        // EasyMock.expect(currentUser.canSee(applicationFormOne)).andReturn(true);
        // EasyMock.expect(currentUser.canSee(applicationFormTwo)).andReturn(true);
        EasyMock.expect(applicationSevice.getByApplicationNumber("23")).andReturn(applicationFormOne).anyTimes();
        EasyMock.expect(applicationSevice.getByApplicationNumber("34")).andReturn(applicationFormTwo).anyTimes();
        byte[] bytes = "pdf".getBytes();
        EasyMock.expect(pdfDocumentBuilder.build(EasyMock.isA(HashMap.class))).andReturn(bytes);

        EasyMock.replay(applicationSevice, pdfDocumentBuilder);

        controller.printAll(request, response);

        assertArrayEquals(bytes, response.getContentAsByteArray());
        assertEquals("0", response.getHeader("Expires"));
        assertEquals("must-revalidate, post-check=0, pre-check=0", response.getHeader("Cache-Control"));
        assertEquals("public", response.getHeader("Pragma"));
        assertEquals("inline; filename=\"UCL_PRISM_timestamp.pdf\"", response.getHeader("Content-Disposition"));
        assertEquals("application/pdf", response.getContentType());
        assertEquals(bytes.length, response.getContentLength());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldSkipNullApplications() throws ServletRequestBindingException, DocumentException, IOException {
        RegisteredUser applicant = new RegisteredUser();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("appList", "23;34;");
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setOutputStreamAccessAllowed(true);

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().id(3).applicant(applicant).build();

        // EasyMock.expect(currentUser.canSee(applicationFormTwo)).andReturn(true);
        EasyMock.expect(applicationSevice.getByApplicationNumber("23")).andReturn(null).anyTimes();
        EasyMock.expect(applicationSevice.getByApplicationNumber("34")).andReturn(applicationFormTwo).anyTimes();
        byte[] bytes = "pdf".getBytes();
        EasyMock.expect(pdfDocumentBuilder.build(EasyMock.isA(HashMap.class))).andReturn(bytes);

        EasyMock.replay(applicationSevice, pdfDocumentBuilder);

        controller.printAll(request, response);

        assertArrayEquals(bytes, response.getContentAsByteArray());
        assertEquals("0", response.getHeader("Expires"));
        assertEquals("must-revalidate, post-check=0, pre-check=0", response.getHeader("Cache-Control"));
        assertEquals("public", response.getHeader("Pragma"));
        assertEquals("inline; filename=\"UCL_PRISM_timestamp.pdf\"", response.getHeader("Content-Disposition"));
        assertEquals("application/pdf", response.getContentType());
        assertEquals(bytes.length, response.getContentLength());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSkipApplicationsUserCannotSees() throws ServletRequestBindingException, DocumentException, IOException {
        RegisteredUser applicant = new RegisteredUser();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("appList", "23;34;");
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setOutputStreamAccessAllowed(true);

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().id(2).applicant(applicant).build();
        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().id(3).applicant(applicant).build();
        // EasyMock.expect(currentUser.canSee(applicationFormOne)).andReturn(false);
        // EasyMock.expect(currentUser.canSee(applicationFormTwo)).andReturn(true);
        EasyMock.expect(applicationSevice.getByApplicationNumber("23")).andReturn(applicationFormOne).anyTimes();
        EasyMock.expect(applicationSevice.getByApplicationNumber("34")).andReturn(applicationFormTwo).anyTimes();
        byte[] bytes = "pdf".getBytes();
        EasyMock.expect(pdfDocumentBuilder.build(EasyMock.isA(HashMap.class))).andReturn(bytes);

        EasyMock.replay(applicationSevice, pdfDocumentBuilder);

        controller.printAll(request, response);

        assertArrayEquals(bytes, response.getContentAsByteArray());
        assertEquals("0", response.getHeader("Expires"));
        assertEquals("must-revalidate, post-check=0, pre-check=0", response.getHeader("Cache-Control"));
        assertEquals("public", response.getHeader("Pragma"));
        assertEquals("inline; filename=\"UCL_PRISM_timestamp.pdf\"", response.getHeader("Content-Disposition"));
        assertEquals("application/pdf", response.getContentType());
        assertEquals(bytes.length, response.getContentLength());
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }
}
