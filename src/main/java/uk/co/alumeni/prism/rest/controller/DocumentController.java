package uk.co.alumeni.prism.rest.controller;

import static uk.co.alumeni.prism.domain.document.PrismFileCategory.DOCUMENT;
import static uk.co.alumeni.prism.domain.document.PrismFileCategory.IMAGE;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.document.PrismFileCategory.PrismImageCategory;
import uk.co.alumeni.prism.exceptions.PrismBadRequestException;
import uk.co.alumeni.prism.exceptions.ResourceNotFoundException;
import uk.co.alumeni.prism.services.ApplicationDownloadService;
import uk.co.alumeni.prism.services.DocumentService;
import uk.co.alumeni.prism.services.EntityService;

@RestController
@RequestMapping("/api")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private ApplicationDownloadService applicationDownloadService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/documents", method = RequestMethod.POST)
    public Map<String, Object> uploadDocument(@RequestParam(value = "file-data") Part uploadStream) throws IOException {
        Document document = documentService.createDocument(uploadStream);
        return ImmutableMap.of("id", (Object) document.getId());
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/images", method = RequestMethod.POST)
    public Map<String, Object> uploadImage(
            @RequestParam(value = "file-data") Part uploadStream,
            @RequestParam(required = false) Integer entityId,
            @RequestParam PrismImageCategory imageCategory) throws IOException {
        Document document = documentService.createImage(uploadStream, entityId, imageCategory);
        return ImmutableMap.of("id", (Object) document.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/documents/blank", method = RequestMethod.GET)
    public void downloadFile(HttpServletResponse response) throws IOException {
        byte[] content = documentService.getSystemDocument("document/blank_qualification_explanation.pdf");
        sendFileToClient(response, new Document().withContent(content).withContentType("application/pdf").withFileName("explanation.pdf").withExported(false));
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/documents/{fileId}", method = RequestMethod.GET)
    public void downloadFile(@PathVariable(value = "fileId") Integer documentId, HttpServletResponse response) throws IOException {
        Document file = documentService.getById(documentId, DOCUMENT);
        if (file == null) {
            throw new ResourceNotFoundException("No document found");
        }
        documentService.validateDownload(file);
        sendFileToClient(response, file);
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/images/{fileId}", method = RequestMethod.GET)
    public void downloadImage(@PathVariable(value = "fileId") Integer fileId, HttpServletResponse response) throws IOException {
        Document file = documentService.getById(fileId, IMAGE);
        if (file == null) {
            throw new ResourceNotFoundException("No image found");
        }
        sendFileToClient(response, file);
    }

    private void sendFileToClient(HttpServletResponse response, Document document) throws IOException {
        response.setHeader("Content-Disposition", "attachment; filename=\"" + document.getFileName() + "\"");
        response.setHeader("File-Name", document.getFileName());
        response.setContentType(document.getContentType());
        byte[] content = documentService.getDocumentContent(document);
        response.setContentLength(content.length);
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(content);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/pdfDownload", method = RequestMethod.GET)
    public void downloadPdf(@RequestParam(value = "applicationIds") String applicationIds, HttpServletResponse response) throws IOException {
        List<Integer> ids;
        try {
            ids = Lists.newArrayList(Iterables.transform(Splitter.on(",").split(applicationIds), Ints.stringConverter()));
        } catch (Exception e) {
            throw new PrismBadRequestException("Expected comma-separated list of application IDs");
        }

        String fileName;
        if (ids.size() == 1) {
            Application application = entityService.getById(Application.class, ids.get(0));
            fileName = "application_" + application.getCode() + ".pdf";
        } else {
            fileName = "applications.pdf";
        }

        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setHeader("File-Name", fileName);
        response.setContentType("application/pdf");

        ServletOutputStream outputStream = response.getOutputStream();
        applicationDownloadService.build(outputStream, ids.toArray(new Integer[ids.size()]));
    }

}
