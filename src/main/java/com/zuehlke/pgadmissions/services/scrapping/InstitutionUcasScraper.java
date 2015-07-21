package com.zuehlke.pgadmissions.services.scrapping;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.co.alumeni.prism.api.model.imported.request.ImportedInstitutionRequest;

import java.io.IOException;
import java.io.Writer;

@Service
public class InstitutionUcasScraper {

    private static Logger log = LoggerFactory.getLogger(InstitutionUcasScraper.class);

    public void scrapeInstitutions(Writer writer) throws IOException {
        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator jg = jsonFactory.createGenerator(writer);
        jg.setCodec(new ObjectMapper());
        jg.setPrettyPrinter(new DefaultPrettyPrinter());
        jg.writeStartArray();

        for (int ucasId = 1; ucasId < 3000; ucasId++) {
            Document html = Jsoup.connect("http://search.ucas.com/provider/" + ucasId).get();
            Element nameElement = html.getElementsByClass("shortname").first();
            if (nameElement != null) {
                String name = nameElement.text();
                ImportedInstitutionRequest institution = new ImportedInstitutionRequest(name).withUcasId("" + ucasId);
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
    }

}
