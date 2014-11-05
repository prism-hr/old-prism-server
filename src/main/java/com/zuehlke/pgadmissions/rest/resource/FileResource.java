package com.zuehlke.pgadmissions.rest.resource;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.exceptions.PrismBadRequestException;
import com.zuehlke.pgadmissions.services.ApplicationDownloadService;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class FileResource {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private ApplicationDownloadService applicationDownloadService;

    @RequestMapping(value = "/files", method = RequestMethod.POST)
    public Map<String, Object> uploadFile(@RequestParam(value = "file-data") Part uploadStream) throws IOException {
        Document document = documentService.create(uploadStream);
        return ImmutableMap.of("id", (Object) document.getId());
    }

    @RequestMapping(value = "/files/{fileId}", method = RequestMethod.GET)
    public void downloadFile(@PathVariable(value = "fileId") Integer documentId, HttpServletResponse response) throws IOException {
        Document document = documentService.getById(documentId);

        response.setHeader("Content-Disposition", "attachment; filename=\"" + document.getFileName() + "\"");
        response.setHeader("File-Name", document.getFileName());
        response.setContentType(document.getContentType());
        // TODO: reinstate? response.setContentLength(document.getContent().length);

        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(document.getContent());
    }

    @RequestMapping(value = "/pdfDownload", method = RequestMethod.GET)
    public void downloadPdf(@RequestParam(value = "applicationIds") String applicationIds, HttpServletResponse response) throws IOException {
        List<Integer> ids = null;
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
