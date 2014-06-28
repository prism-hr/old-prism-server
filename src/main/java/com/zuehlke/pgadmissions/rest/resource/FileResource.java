package com.zuehlke.pgadmissions.rest.resource;

import com.google.common.collect.ImmutableMap;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.definitions.DocumentType;
import com.zuehlke.pgadmissions.services.EntityService;

import org.bouncycastle.util.io.Streams;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

}
