package com.zuehlke.pgadmissions.rest.resource;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.ImmutableMap;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.services.DocumentService;

@RestController
@RequestMapping("/api/files")
public class FileResource {

    @Autowired
    private DocumentService documentService;

    @RequestMapping(method = RequestMethod.POST)
    public Map<String, Object> uploadFile(@RequestParam(value = "file-data") Part uploadStream) throws IOException {
        Document document = documentService.create(uploadStream);
        return ImmutableMap.of("id", (Object) document.getId());
    }

    @RequestMapping(value = "/{fileId}", method = RequestMethod.GET)
    public void downloadFile(@PathVariable(value = "fileId") Integer documentId, HttpServletResponse response) throws IOException {
        Document document = documentService.getById(documentId);

        response.setHeader("Content-Disposition", "attachment; filename=\"" + document.getFileName() + "\"");
        response.setHeader("File-Name", document.getFileName());
        response.setContentType(document.getContentType());
        // TODO: reinstate? response.setContentLength(document.getContent().length);

        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(document.getContent());
        // TODO: reinstate? response.flushBuffer();
    }

}
