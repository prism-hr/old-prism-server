package com.zuehlke.pgadmissions.services.scrapping;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.*;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.exceptions.ScrapingException;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedProgramInternalRequest;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ProgramUcasScraper implements ImportedDataScraper {

    private static Logger log = LoggerFactory.getLogger(ProgramUcasScraper.class);

    // search host
    private static String HOST = "http://search.ucas.com";

    private static String URL_PROGRAMS_TEMPLATE = HOST + "/search/results?";

    private static Pattern programNamePattern = Pattern.compile("(.+)\\s+\\(([A-Z0-9]{4})\\)( : Taught at \\d+ locations)?");

    private TreeMultiset<ImportedProgramInternalRequest> programSet = TreeMultiset.create(
            (o1, o2) -> ComparisonChain.start().compare(o1.getInstitution(), o2.getInstitution()).compare(o1.getName(), o2.getName()).compare(o1.getLevel(), o2.getLevel()).result());

    @Inject
    private ImportedEntityService importedEntityService;

    @Override
    public void scrape(Writer writer) throws ScrapingException {
        try {
            List<ImportedInstitution> institutions = importedEntityService.getInstitutionsWithUcasId();
//            List<ImportedInstitution> institutions = Lists.newArrayList(new ImportedInstitution().withId(1421).withUcasId("1421"));

            programSet.clear();
            for (ImportedInstitution institution : institutions) {
                log.info("Scraping institution " + institution.getUcasId() + ": " + institution.getName());
                try {
                    scrapeProgramsForInstitution(institution, Integer.toString(LocalDate.now().getYear()));
                } catch (URISyntaxException e) {
                    throw new Error(e);
                }
            }

            writePrograms(writer, programSet);
        } catch (IOException e) {
            throw new ScrapingException(e);
        }
    }

    private void scrapeProgramsForInstitution(ImportedInstitution institution, String yearOfInterest) throws IOException, URISyntaxException {
        String initialURL = new URIBuilder(URL_PROGRAMS_TEMPLATE).addParameter("Vac", "1").addParameter("AvailableIn", yearOfInterest).addParameter("providerids", institution.getUcasId()).toString();
        Document htmlDoc = Jsoup.connect(initialURL).get();
        Element resultsCountElement = htmlDoc.getElementsByClass("resultsCount").first();
        if (resultsCountElement == null) {
            return; // no programs
        }
        String resultsCount = resultsCountElement.text();

        List<String> urlsToScrape = Collections.singletonList(initialURL);
        if (resultsCount.contains("Showing 1000 of")) { // to many results, need to use filter
            Element subjectFilterElement = htmlDoc.getElementById("filtercategory-9");
            urlsToScrape = subjectFilterElement.getElementsByTag("a").stream()
                    .map(aElement -> HOST + aElement.attr("href"))
                    .filter(href -> href.contains("flt9"))
                    .collect(Collectors.toList());
        }

        for (String url : urlsToScrape) {
            int page = 1;
            while (!scrapeProgramPages(url, institution, page)) {
                page++;
            }
        }
    }

    /**
     * return <code>true</code> when there are no more pages
     */
    private boolean scrapeProgramPages(String url, ImportedInstitution institution, int page)
            throws IOException, URISyntaxException {

        if (page > 50) {
            throw new RuntimeException("To many pages for url " + url);
        }

        url = new URIBuilder(url).setParameter("Page", Integer.toString(page)).toString();
        Document htmlDoc = Jsoup.connect(url).get();
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
            Matcher programNameMatcher = programNamePattern.matcher(programName);
            if (!programNameMatcher.matches()) {
                log.error("Could not match program name: " + programName);
                continue;
            }
            programName = programNameMatcher.group(1);

            String subjectArea = programNameMatcher.group(2);
            String qualification = element.select(".courseinfooutcome").text();
            String level = extractProgramLevelFromRawHTML(element.select(".resultbottomarea").select(".coursequalarea").html());

            ImportedProgramInternalRequest program = new ImportedProgramInternalRequest(programName).withInstitution(institution.getId())
                    .withLevel(level).withQualification(qualification).withSubjectAreas(ImmutableSet.of(subjectArea));
            programSet.add(program);
        }
        return false;
    }


    // get whatever is after </div>
    private String extractProgramLevelFromRawHTML(String html) {
        int start = html.lastIndexOf("</div>");
        return html.substring(start + 6, html.length()).trim();
    }

    private void writePrograms(Writer writer, TreeMultiset<ImportedProgramInternalRequest> programSet) throws IOException {
        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator jg = jsonFactory.createGenerator(writer);
        jg.setCodec(new ObjectMapper());
        jg.setPrettyPrinter(new DefaultPrettyPrinter());
        jg.writeStartArray();

        for (Multiset.Entry<ImportedProgramInternalRequest> programEntry : programSet.entrySet()) {
            ImportedProgramInternalRequest program = programEntry.getElement();
            program.setWeight(programEntry.getCount());
            jg.writeObject(program);
        }

        jg.writeEndArray();
        jg.close();
    }

}
