package com.zuehlke.pgadmissions.rest.resource;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.bouncycastle.util.io.Streams;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.ImmutableMap;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.definitions.DocumentType;
import com.zuehlke.pgadmissions.services.EntityService;

@RestController
@RequestMapping("/api/files")
public class FileResource {

    @Autowired
    private EntityService entityService;

    @RequestMapping(method = RequestMethod.POST)
    public Map<String, Object> uploadFile(@RequestParam(value = "file-data", required = false) Part part) throws IOException {
        Document document = new Document().withContent(Streams.readAll(part.getInputStream())).withContentType(part.getContentType()).withCreatedTimestamp(new DateTime()).withFileName(getFileName(part)).withType(DocumentType.COMMENT);
        Integer id = (Integer) entityService.save(document);
        return ImmutableMap.of("id", (Object) id);
    }

    @RequestMapping(value = "/{fileId}", method = RequestMethod.GET)
    public void downloadFile(@PathVariable Integer fileId, HttpServletResponse response) throws IOException {
        Document document = entityService.getById(Document.class, fileId);

        response.setHeader("Content-Disposition", "attachment; filename=\"" + document.getFileName() + "\"");
        response.setHeader("File-Name", document.getFileName());
        response.setContentType(document.getContentType());
//        response.setContentLength(document.getContent().length);

        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(document.getContent());
//        response.flushBuffer();
    }

    private String getFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim()
                        .replace("\"", "");
            }
        }
        return null;
    }

}
