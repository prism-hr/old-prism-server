package com.zuehlke.pgadmissions.services.scrapping;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zuehlke.pgadmissions.exceptions.ScrapingException;
import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.co.alumeni.prism.api.model.imported.request.ImportedSubjectAreaRequest;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

@Service
public class SubjectAreaHesaScraper {

    private static Logger log = LoggerFactory.getLogger(SubjectAreaHesaScraper.class);

    public void scrapeSubjectAreas(Writer writer) throws IOException {
        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator jg = jsonFactory.createGenerator(writer);
        jg.setCodec(new ObjectMapper());
        jg.setPrettyPrinter(new DefaultPrettyPrinter());
        jg.writeStartArray();

        Document html = Jsoup.connect("https://www.hesa.ac.uk/component/content/article?id=1787").get();
        Element container = html.getElementsByAttributeValue("itemprop", "articlebody").first();
        Iterator<Element> elementsIterator = container.children().iterator();
        elementsIterator.next(); // skip first one

        while(elementsIterator.hasNext()) {
            Element topLevelElement = elementsIterator.next();
            if(!topLevelElement.tagName().equals("h3")) {
                throw new ScrapingException("Expected tag: h3, found: " + topLevelElement.tagName());
            }
            String[] split = topLevelElement.text().split(" - ", 2);

            ImportedSubjectAreaRequest subjectArea = new ImportedSubjectAreaRequest(split[1]).withJacsCode(split[0]);
            jg.writeObject(subjectArea);
            Elements secondaryElements = elementsIterator.next().getElementsByTag("tr");
            for (Element secondaryElement : secondaryElements) {
                Elements paragraphs = secondaryElement.getElementsByTag("p");
                String name = StringEscapeUtils.unescapeHtml(paragraphs.get(3).text());
                String jacs = paragraphs.get(2).text();
                String description = StringEscapeUtils.unescapeHtml(paragraphs.get(4).text());
                jg.writeObject(new ImportedSubjectAreaRequest(name).withJacsCode(jacs).withDescription(description));
            }

        }
        jg.writeEndArray();
    }

}
