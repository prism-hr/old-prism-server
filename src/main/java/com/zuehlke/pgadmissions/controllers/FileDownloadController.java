package com.zuehlke.pgadmissions.controllers;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/download")
public class FileDownloadController {

    private static final String PDF_CONTENT_TYPE = "application/pdf";
    private static final String TEXT_CONTENT_TYPE = "plain/text";

    @Autowired
    private DocumentService documentService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private EncryptionHelper encryptionHelper;

    @RequestMapping(method = RequestMethod.GET)
    public void downloadApplicationDocument(@RequestParam("documentId") String encryptedDocumentId, HttpServletResponse response) throws IOException {
        Document document = documentService.getByid(encryptionHelper.decryptToInteger(encryptedDocumentId));
        if (document == null || DocumentType.REFERENCE == document.getType()) {
            throw new ResourceNotFoundException();
        }

        sendDocument(response, document.getFileName(), PDF_CONTENT_TYPE, document.getContent());
    }

    @RequestMapping(value = "/reference", method = RequestMethod.GET)
    public void downloadReferenceDocument(@RequestParam("referenceId") String encryptedReferenceId, HttpServletResponse response) throws IOException {
        Comment reference = commentService.getById(encryptionHelper.decryptToInteger(encryptedReferenceId));
        User currentUser = userService.getCurrentUser();
        // FIXME check if user can see reference
        if (reference == null /* || !currentUser.canSeeReference(reference) */) {
            throw new ResourceNotFoundException();
        }

        Document document = reference.getDocuments().iterator().next();
        sendDocument(response, document.getFileName(), PDF_CONTENT_TYPE, document.getContent());

    }

    private void sendDocument(HttpServletResponse response, String fileName, String fileContentType, byte[] fileContent) throws IOException {
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
        response.setContentType(fileContentType);
        response.setContentLength(fileContent.length);
        OutputStream out = response.getOutputStream();
        try {
            out.write(fileContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                out.flush();
            } catch (Exception e) {
                // ignore
            }
            try {
                out.close();
            } catch (Exception e) {
                // ignore
            }
        }
    }

}
