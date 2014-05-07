package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletResponse;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.exporters.ApplicationTransferService;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class FileDownloadControllerTest {

    @Mock
    @InjectIntoByType
    private ApplicationTransferService applicationFormTransferServiceMock;

    @Mock
    @InjectIntoByType
    private DocumentService documentServiceMock;

    @Mock
    @InjectIntoByType
    private CommentService commentServiceMock;

    @Mock
    @InjectIntoByType
    private UserService userServiceMock;

    @Mock
    @InjectIntoByType
    private EncryptionHelper encryptionHelperMock;

    @TestedObject
    private FileDownloadController controller;

    @Test
    public void shouldGetApplicationFormDocumentFromServiceAndWriteContentToResponse() throws IOException {
        EasyMock.expect(encryptionHelperMock.decryptToInteger("encryptedId")).andReturn(1);
        Document document = new Document().withContent("aaaa".getBytes()).withId(1);
        EasyMock.expect(documentServiceMock.getByid(1)).andReturn(document);
        EasyMock.replay(documentServiceMock, encryptionHelperMock);

        HttpServletResponse responseMock = EasyMock.createMock(HttpServletResponse.class);
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ServletOutputStream servletOutputStream = new ServletOutputStream() {

            @Override
            public void write(int b) throws IOException {
                byteArrayOutputStream.write(b);
            }
        };

        EasyMock.expect(responseMock.getOutputStream()).andReturn(servletOutputStream);
        responseMock.setHeader("Expires", "0");
        responseMock.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        responseMock.setHeader("Pragma", "public");
        responseMock.setHeader("Content-Disposition", "inline; filename=\"" + document.getFileName() + "\"");
        responseMock.setContentType("application/pdf");
        responseMock.setContentLength(document.getContent().length);
        EasyMock.replay(responseMock);
        controller.downloadApplicationDocument("encryptedId", responseMock);

        EasyMock.verify(documentServiceMock, responseMock);

        byte[] byteArray = byteArrayOutputStream.toByteArray();
        assertEquals("aaaa", new String(byteArray));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionIfDocumentTypeIsReference() throws IOException {
        EasyMock.expect(encryptionHelperMock.decryptToInteger("encryptedId")).andReturn(1);
        Document document = new Document().withType(DocumentType.REFERENCE).withContent("aaaa".getBytes()).withId(1);
        EasyMock.expect(documentServiceMock.getByid(1)).andReturn(document);
        EasyMock.replay(documentServiceMock, encryptionHelperMock);

        controller.downloadApplicationDocument("encryptedId", new MockHttpServletResponse());
    }

    @Test
    public void shouldNotThrowResourceNotFoundExceptionIfDocumentTypeIsProofOfAward() throws IOException {
        EasyMock.expect(encryptionHelperMock.decryptToInteger("encryptedId")).andReturn(1);
        Document document = new Document().withType(DocumentType.PROOF_OF_AWARD).withContent("aaaa".getBytes()).withId(1);

        EasyMock.expect(documentServiceMock.getByid(1)).andReturn(document);
        EasyMock.replay(documentServiceMock, encryptionHelperMock);

        controller.downloadApplicationDocument("encryptedId", new MockHttpServletResponse());
    }

    @Test
    public void shouldGetReferenceDocumentFromServiceAndWriteContentToResponse() throws IOException {
        EasyMock.expect(encryptionHelperMock.decryptToInteger("encryptedId")).andReturn(1);
        EasyMock.replay(encryptionHelperMock);
        Document document = new Document().withContent("aaaa".getBytes()).withId(101);
        ReferenceComment reference = new ReferenceCommentBuilder().id(1).document(document).build();
        HttpServletResponse responseMock = EasyMock.createMock(HttpServletResponse.class);

        EasyMock.expect(commentServiceMock.getById(1)).andReturn(reference);
        EasyMock.replay(commentServiceMock);

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ServletOutputStream servletOutputStream = new ServletOutputStream() {

            @Override
            public void write(int b) throws IOException {
                byteArrayOutputStream.write(b);
            }
        };

        EasyMock.expect(responseMock.getOutputStream()).andReturn(servletOutputStream);
        responseMock.setHeader("Expires", "0");
        responseMock.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        responseMock.setHeader("Pragma", "public");
        responseMock.setHeader("Content-Disposition", "inline; filename=\"" + document.getFileName() + "\"");
        responseMock.setContentType("application/pdf");
        responseMock.setContentLength(document.getContent().length);
        EasyMock.replay(responseMock);
        controller.downloadReferenceDocument("encryptedId", responseMock);

        EasyMock.verify(commentServiceMock, responseMock);

        byte[] byteArray = byteArrayOutputStream.toByteArray();
        assertEquals("aaaa", new String(byteArray));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionIfReferenceDoesNotHaveDocument() throws IOException {
        EasyMock.expect(encryptionHelperMock.decryptToInteger("encryptedId")).andReturn(1);
        EasyMock.replay(encryptionHelperMock);
        ReferenceComment reference = new ReferenceCommentBuilder().id(1).build();
        HttpServletResponse responseMock = EasyMock.createMock(HttpServletResponse.class);
        EasyMock.expect(commentServiceMock.getById(1)).andReturn(reference);
        EasyMock.replay(commentServiceMock);
        controller.downloadReferenceDocument("encryptedId", responseMock);

        EasyMock.verify(commentServiceMock);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionIfReferenceDoesNotExistt() throws IOException {
        EasyMock.expect(encryptionHelperMock.decryptToInteger("encryptedId")).andReturn(1);
        EasyMock.replay(encryptionHelperMock);
        HttpServletResponse responseMock = EasyMock.createMock(HttpServletResponse.class);
        EasyMock.expect(commentServiceMock.getById(1)).andReturn(null);
        EasyMock.replay(commentServiceMock);
        controller.downloadReferenceDocument("encryptedId", responseMock);

        EasyMock.verify(commentServiceMock);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionUserCannotSeeReference() throws IOException {
        EasyMock.expect(encryptionHelperMock.decryptToInteger("encryptedId")).andReturn(1);
        EasyMock.replay(encryptionHelperMock);
        Document document = new Document().withContent("aaaa".getBytes()).withId(101);
        ReferenceComment reference = new ReferenceCommentBuilder().id(1).document(document).build();
        HttpServletResponse responseMock = EasyMock.createMock(HttpServletResponse.class);
        EasyMock.expect(commentServiceMock.getById(1)).andReturn(reference);
        EasyMock.replay(commentServiceMock);

        controller.downloadReferenceDocument("encryptedId", responseMock);

        EasyMock.verify(commentServiceMock);
    }
}
