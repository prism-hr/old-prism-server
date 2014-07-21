package com.zuehlke.pgadmissions.rest.resource;

import com.google.common.collect.ImmutableMap;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.definitions.DocumentType;
import com.zuehlke.pgadmissions.services.EntityService;
import org.bouncycastle.util.io.Streams;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileResource {

    @Autowired
    private EntityService entityService;

    @RequestMapping(method = RequestMethod.POST)
    public Map<String, Object> uploadFile(@RequestParam(value = "file-data", required = false) Part part) throws IOException {
        Document document = new Document().withContent(Streams.readAll(part.getInputStream())).withContentType(part.getContentType()).withCreatedTimestamp(new DateTime()).withFileName(part.getName()).withType(DocumentType.COMMENT);
        Integer id = (Integer) entityService.save(document);
        return ImmutableMap.of("id", (Object) id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public void downloadFile(@PathVariable Integer fileId, HttpServletResponse response) throws IOException {
        Document document = entityService.getById(Document.class, fileId);

        response.setHeader("Content-Disposition", "attachment; filename=\"" + document.getFileName() + "\"");
        response.setContentType(document.getContentType());

        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(document.getContent());
        response.flushBuffer();
    }

}
