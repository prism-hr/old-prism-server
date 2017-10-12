package uk.co.alumeni.prism.rest.controller;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.document.PrismFileCategory.PrismImageCategory;
import uk.co.alumeni.prism.exceptions.PrismBadRequestException;
import uk.co.alumeni.prism.exceptions.ResourceNotFoundException;
import uk.co.alumeni.prism.rest.representation.resource.application.ApplicationBatchedDownloadRepresentation;
import uk.co.alumeni.prism.services.ApplicationDownloadService;
import uk.co.alumeni.prism.services.DocumentService;
import uk.co.alumeni.prism.services.EntityService;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static uk.co.alumeni.prism.domain.document.PrismFileCategory.DOCUMENT;
import static uk.co.alumeni.prism.domain.document.PrismFileCategory.IMAGE;

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
    public Map<String, Object> uploadDocument(@RequestParam(value = "file") Part uploadStream) throws IOException {
        Document document = documentService.createDocument(uploadStream);
        return ImmutableMap.of("id", (Object) document.getId());
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/images", method = RequestMethod.POST)
    public Map<String, Object> uploadImage(
            @RequestParam(value = "file") Part uploadStream,
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

    // Expected to be used for one of downloads
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/pdfDownload", method = RequestMethod.GET)
    public void downloadPdf(@RequestParam(value = "applicationIds") String applicationIds, HttpServletResponse response) throws IOException {
        List<Integer> ids = getApplicationIds(applicationIds);

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
        applicationDownloadService.build(ids, outputStream);
    }

    // Expected to be used for batched downloads
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/pdfDownload/batch", method = RequestMethod.POST)
    public String downloadPdfBatch(@RequestBody List<Integer> applicationIds) throws IOException {
        return applicationDownloadService.build(applicationIds);
    }

    // Polling endpoint to find out if batched download is ready
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/pdfDownload/batch/status/{uuid}", method = RequestMethod.GET)
    public ApplicationBatchedDownloadRepresentation getPdfBatchStatus(@PathVariable String uuid) throws IOException {
        return applicationDownloadService.getStatus(uuid);
    }

    // Endpoint to get the batched download when ready
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/pdfDownload/batch/{uuid}", method = RequestMethod.GET)
    public void getPdfBatch(@PathVariable String uuid, HttpServletResponse response) throws IOException, IllegalAccessException {
        response.setHeader("Content-Disposition", "attachment; filename=\"applications.pdf\"");
        response.setHeader("File-Name", "applications.pdf");
        response.setContentType("application/pdf");
        applicationDownloadService.getPdfBatch(uuid, response);
    }

    private List<Integer> getApplicationIds(@RequestParam(value = "applicationIds") String applicationIds) {
        List<Integer> ids;
        try {
            ids = Lists.newArrayList(Iterables.transform(Splitter.on(",").split(applicationIds), Ints.stringConverter()));
        } catch (Exception e) {
            throw new PrismBadRequestException("Expected comma-separated list of application IDs");
        }
        return ids;
    }

}
