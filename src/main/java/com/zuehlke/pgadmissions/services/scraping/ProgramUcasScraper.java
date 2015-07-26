package com.zuehlke.pgadmissions.services.scraping;

import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.exceptions.ScrapingException;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedProgramImportDTO;
import com.zuehlke.pgadmissions.services.ImportedEntityService;

@Service
public class ProgramUcasScraper implements ImportedDataScraper {

    private static Logger log = LoggerFactory.getLogger(ProgramUcasScraper.class);

    private static String HOST = "http://search.ucas.com";

    private static String URL_PROGRAMS_TEMPLATE = HOST + "/search/results?";

    private static Pattern programNamePattern = Pattern.compile("(.+)\\s+\\(([A-Z0-9]{4})\\)( : Taught at \\d+ locations)?");

    private TreeMultiset<ImportedProgramImportDTO> programSet = TreeMultiset.create(
            (o1, o2) -> ComparisonChain.start().compare(o1.getInstitution(), o2.getInstitution()).compare(o1.getName(), o2.getName())
                    .compare(o1.getLevel(), o2.getLevel()).result());

    @Inject
    private ImportedEntityService importedEntityService;

    @Override
    public void scrape(Writer writer) throws ScrapingException {
        try {
            List<ImportedInstitution> institutions = importedEntityService.getInstitutionsWithUcasId();

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
        String initialURL = new URIBuilder(URL_PROGRAMS_TEMPLATE).addParameter("Vac", "1").addParameter("AvailableIn", yearOfInterest)
                .addParameter("providerids", institution.getUcasId()).toString();
        Document htmlDoc = Jsoup.connect(initialURL).get();
        Element resultsCountElement = htmlDoc.getElementsByClass("resultsCount").first();
        if (resultsCountElement == null) {
            return;
        }
        String resultsCount = resultsCountElement.text();

        List<String> urlsToScrape = Collections.singletonList(initialURL);
        if (resultsCount.contains("Showing 1000 of")) {
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

            ImportedProgramImportDTO program = new ImportedProgramImportDTO(programName).withInstitution(institution.getId())
                    .withLevel(level).withQualification(qualification).withSubjectAreas(ImmutableSet.of(subjectArea));
            programSet.add(program);
        }
        return false;
    }

    private String extractProgramLevelFromRawHTML(String html) {
        int start = html.lastIndexOf("</div>");
        return html.substring(start + 6, html.length()).trim();
    }

    private void writePrograms(Writer writer, TreeMultiset<ImportedProgramImportDTO> programSet) throws IOException {
        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator jsonGenerator = jsonFactory.createGenerator(writer);
        jsonGenerator.setCodec(new ObjectMapper());
        jsonGenerator.setPrettyPrinter(new DefaultPrettyPrinter());
        jsonGenerator.writeStartArray();

        for (Multiset.Entry<ImportedProgramImportDTO> programEntry : programSet.entrySet()) {
            ImportedProgramImportDTO program = programEntry.getElement();
            program.setWeight(programEntry.getCount());
            jsonGenerator.writeObject(program);
        }

        jsonGenerator.writeEndArray();
        jsonGenerator.close();
    }

}
