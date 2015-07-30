package com.zuehlke.pgadmissions.services.scraping;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import uk.co.alumeni.prism.api.model.imported.request.ImportedInstitutionRequest;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.common.io.Resources;
import com.zuehlke.pgadmissions.exceptions.ScrapingException;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedInstitutionImportDTO;

@Service
public class InstitutionUcasScraper {

    private static Logger log = LoggerFactory.getLogger(InstitutionUcasScraper.class);

    public void scrape(Writer writer) throws ScrapingException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonFactory jsonFactory = new JsonFactory();
            JsonGenerator jg = jsonFactory.createGenerator(writer);
            jg.setCodec(objectMapper);
            jg.setPrettyPrinter(new DefaultPrettyPrinter());
            jg.writeStartArray();

            try (InputStream iStream = Resources.getResource("import/institution.json").openStream()) {
                CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, ImportedInstitutionRequest.class);
                List<ImportedInstitutionRequest> referenceEntities = objectMapper.readValue(iStream, collectionType);
                for (ImportedInstitutionRequest referenceEntity : referenceEntities) {
                    jg.writeObject(referenceEntity);
                }
            }

            for (int ucasId = 1; ucasId < 3000; ucasId++) {
                Document html = Jsoup.connect("http://search.ucas.com/provider/" + ucasId).get();
                Element nameElement = html.getElementsByClass("shortname").first();
                if (nameElement != null) {
                    String name = nameElement.text();
                    ImportedInstitutionRequest institution = new ImportedInstitutionImportDTO(name).withUcasId(ucasId);
                    jg.writeObject(institution);
                    log.info("Scraped institution " + ucasId + ": " + name);
                } else {
                    Element noFoundElement = html.getElementsByClass("details_notfound").first();
                    if (noFoundElement == null) {
                        throw new RuntimeException("Unexpected page for ID: " + ucasId);
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            jg.writeEndArray();
            jg.close();
        } catch (IOException e) {
            throw new ScrapingException(e);
        }
    }

}
