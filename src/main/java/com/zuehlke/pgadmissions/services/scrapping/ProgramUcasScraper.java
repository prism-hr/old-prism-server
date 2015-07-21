package com.zuehlke.pgadmissions.services.scrapping;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.co.alumeni.prism.api.model.imported.request.ImportedProgramRequest;

import javax.inject.Inject;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProgramUcasScraper {

    private static Logger log = LoggerFactory.getLogger(ProgramUcasScraper.class);

    // search host
    private static String HOST = "http://search.ucas.com";

    private static String URL_PROGRAMS_TEMPLATE = HOST + "/search/results?";

    private HashSet<Integer> programCache = new HashSet<>();

    @Inject
    private ImportedEntityService importedEntityService;

    public void scrapePrograms(String yearOfInterest, Writer writer) throws IOException {
        log.debug("scrapePrograms() - start method");

        List<ImportedInstitution> institutions = importedEntityService.getInstitutionsWithUcasId();

        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator jg = jsonFactory.createGenerator(writer);
        jg.setCodec(new ObjectMapper());
        jg.setPrettyPrinter(new DefaultPrettyPrinter());
        jg.writeStartArray();

        for (ImportedInstitution institution : institutions) {
            log.info("Scraping institution " + institution.getUcasId() + ": " + institution.getName());
            try {
                scrapeProgramsForInstitution(jg, institution, yearOfInterest);
            } catch (URISyntaxException e) {
                throw new Error(e);
            }
        }

        jg.writeEndArray();
    }

    private void scrapeProgramsForInstitution(JsonGenerator jsonGenerator, ImportedInstitution institution, String yearOfInterest) throws IOException, URISyntaxException {
        String initialURL = new URIBuilder(URL_PROGRAMS_TEMPLATE).addParameter("Vac", "1").addParameter("AvailableIn", yearOfInterest).addParameter("providerids", institution.getUcasId()).toString();
        Document htmlDoc = getHtml(initialURL);
        Element resultsCountElement = htmlDoc.getElementsByClass("resultsCount").first();
        if (resultsCountElement == null) {
            return; // no programs
        }
        String resultsCount = resultsCountElement.text();

        List<String> urlsToScrape = Collections.singletonList(initialURL);
        if (resultsCount.contains("Showing 1000 of")) { // to many results, need to use filter
            Element subjectFilterElement = htmlDoc.getElementById("filtercategory-9");
            urlsToScrape = subjectFilterElement.getElementsByTag("a").stream()
                    .map(aElement -> aElement.attr("href"))
                    .filter(href -> href.contains("flt9"))
                    .map(href -> href.replace(".+flt9=", ""))
                    .map(filterValue -> {
                        try {
                            return new URIBuilder(initialURL).addParameter("flt9", filterValue).toString();
                        } catch (URISyntaxException e) {
                            throw new Error(e);
                        }
                    })
                    .collect(Collectors.toList());

        }

        for (String url : urlsToScrape) {
            int page = 1;
            while (!scrapeProgramPages(jsonGenerator, url, institution, page)) {
                page++;
            }
        }

    }

    /**
     * return <code>true</code> when there are no more pages
     */
    private boolean scrapeProgramPages(JsonGenerator jsonGenerator, String url, ImportedInstitution institution, int page)
            throws IOException, URISyntaxException {

        if (page > 50) {
            throw new RuntimeException("To many pages for url " + url);
        }

        url = new URIBuilder(url).addParameter("page", Integer.toString(page)).toString();
        Document htmlDoc = getHtml(url);
        Element resultsContainerElement = htmlDoc.getElementsByClass("resultscontainer").first();
        if (resultsContainerElement == null) {
            return true;
        }

        // iterate over programs for institution
        for (Element element : resultsContainerElement.getElementsByTag("li")) {
            if (!element.id().startsWith("result-")) {
                continue;
            }
            String programName = StringEscapeUtils.unescapeHtml(element.select(".coursenamearea").select("h4").text());
            Integer programHash = Objects.hash(element.getElementsByTag("a").attr("href"), institution.getId());
            if (!programCache.contains(programHash)) {
                programCache.add(programHash);

                String qualification = element.select(".courseinfooutcome").text();
                String level = extractProgramLevelFromRawHTML(element.select(".resultbottomarea").select(".coursequalarea").html());

                ImportedProgramRequest program = new ImportedProgramRequest().withInstitution(institution.getId())
                        .withLevel(level).withName(programName).withQualification(qualification);
                jsonGenerator.writeObject(program);
                log.info("Adding new program: " + programName);
            } else {
                log.info("Skipping program: " + programName);
            }
        }
        return false;
    }


    // get whatever is after </div>
    private String extractProgramLevelFromRawHTML(String html) {
        int start = html.lastIndexOf("</div>");
        return html.substring(start + 6, html.length()).trim();
    }

    // helper method
    private Document getHtml(String givenUrl) throws IOException {
        return Jsoup.connect(givenUrl).get();
    }

}
