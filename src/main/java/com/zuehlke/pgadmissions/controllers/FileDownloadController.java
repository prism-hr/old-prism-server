package com.zuehlke.pgadmissions.controllers;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.ReferenceService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.exporters.ApplicationFormTransferService;

@Controller
@RequestMapping("/download")
public class FileDownloadController {

    private static final String PDF_CONTENT_TYPE = "application/pdf";
    private static final String TEXT_CONTENT_TYPE = "plain/text";

    @Autowired
    private ApplicationFormTransferService applicationFormTransferService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private ReferenceService referenceService;

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
        ReferenceComment reference = referenceService.getReferenceById(encryptionHelper.decryptToInteger(encryptedReferenceId));
        RegisteredUser currentUser = userService.getCurrentUser();
        // FIXME check if user can see reference
        if (reference == null /* || !currentUser.canSeeReference(reference) */) {
            throw new ResourceNotFoundException();
        }

        Document document = reference.getDocuments().get(0);
        sendDocument(response, document.getFileName(), PDF_CONTENT_TYPE, document.getContent());

    }

    @RequestMapping(value = "/transferErrorReport", method = RequestMethod.GET)
    public void downloadTransferErrorReport(@RequestParam("transferErrorId") Long transferErrorId, HttpServletResponse response) throws IOException {
        ApplicationFormTransferError applicationFormTransferError = applicationFormTransferService.getErrorById(transferErrorId);
        if (applicationFormTransferError == null) {
            throw new ResourceNotFoundException();
        }
        sendDocument(response, applicationFormTransferError.getTransfer().getApplicationForm().getApplicationNumber() + "_transfer_error.txt",
                TEXT_CONTENT_TYPE, applicationFormTransferError.getDiagnosticInfo().getBytes());
    }

    @RequestMapping(value = "/transferSoapRequest", method = RequestMethod.GET)
    public void downloadTransferSoapRequest(@RequestParam("transferErrorId") Long transferErrorId, HttpServletResponse response) throws IOException {
        ApplicationFormTransferError applicationFormTransferError = applicationFormTransferService.getErrorById(transferErrorId);
        if (applicationFormTransferError == null) {
            throw new ResourceNotFoundException();
        }
        String request = applicationFormTransferError.getRequestCopy();
        if (request == null) {
            throw new ResourceNotFoundException();
        }
        sendDocument(response, applicationFormTransferError.getTransfer().getApplicationForm().getApplicationNumber() + "_transfer_request.txt",
                TEXT_CONTENT_TYPE, applicationFormTransferError.getRequestCopy().getBytes());
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
