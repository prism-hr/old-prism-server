package com.zuehlke.pgadmissions.services.scraping;

import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.exceptions.ScrapingException;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedProgramImportDTO;

@Service
@SuppressWarnings("rawtypes")
public class ProgramUcasScraper implements ImportedDataScraper {

    private static Logger log = LoggerFactory.getLogger(ProgramUcasScraper.class);

    private static String HOST = "http://search.ucas.com";

    private static Pattern programNamePattern = Pattern.compile("(.+)\\s+\\(([A-Z0-9]{4})\\)( : Taught at \\d+ locations)?");

    private static URIBuilder newURIBuilder(String string) {
        try {
            return new URIBuilder(string);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void scrape(Writer writer) throws ScrapingException {
        try {
            TreeMap<Pair, ImportedProgramScrapeDescriptor> programs = new TreeMap<>();

            List<String> topCategoryUrls = getTopCategoryUrls();
            // topCategoryUrls = topCategoryUrls.subList(0, 1); // FIXME remove,
            // just for tests
            List<String> subjectUrls = getSubjectUrls(topCategoryUrls);
            // subjectUrls = subjectUrls.subList(4, 5); // FIXME remove, just
            // for tests

            for (String subjectUrl : subjectUrls) {
                List<Document> documents = loadAllPages(subjectUrl);
                for (Document document : documents) {
                    scrapeUniversitySections(document, programs);
                }
            }

            writePrograms(writer, programs);
        } catch (Exception e) {
            throw new ScrapingException(e);
        }
    }

    private List<String> getTopCategoryUrls() throws IOException {
        return Jsoup.connect(HOST + "/subject").get().getElementById("subjectareas")
                .getElementsByTag("a").stream()
                .map(a -> HOST + a.attr("href"))
                .collect(Collectors.toList());
    }

    private List<String> getSubjectUrls(List<String> topCategoryUrls) throws IOException, URISyntaxException {
        Integer toyear = LocalDate.now().getYear();
        List<String> years = Lists.newArrayList(toyear.toString(), new Integer(toyear + 1).toString());

        List<String> subjectUrls = Lists.newLinkedList();

        for (String topCategoryUrl : topCategoryUrls) {
            Document document = Jsoup.connect(topCategoryUrl).get();
            String subjectCode = document.getElementById("SubjectCode").attr("value");
            String uriBase = new URIBuilder(HOST + "/search/providers").addParameter("Vac", "1").addParameter("SubjectCode", subjectCode).toString();
            List<String> urls = document.select("li.subjectsearchsteparea").stream()
                    .flatMap(area -> area.select("input[name=\"flt99\"]").stream())
                    .flatMap(input -> years.stream().map(year -> ImmutablePair.of(input, year)))
                    .map(pair -> (String) newURIBuilder(uriBase)
                            .addParameter(((Element) ((Pair) pair).getLeft()).attr("name"), 
                                    ((Element) ((Pair) pair).getLeft()).attr("value"))
                             .addParameter("AvailableIn", ((Pair)pair).getRight().toString())
                            .toString())
                    .collect(Collectors.toList());
            subjectUrls.addAll(urls);
        }
        return subjectUrls;
    }

    private boolean scrapeUniversitySections(Document document, TreeMap<Pair, ImportedProgramScrapeDescriptor> programs)
            throws IOException, URISyntaxException {
        Element resultsContainerElement = document.getElementsByClass("resultscontainer").first();

        for (Element element : resultsContainerElement.getElementsByTag("li")) {
            if (!element.id().startsWith("result-")) {
                continue;
            }
            String ucasInstitutionId = element.id().replace("result-", "");
            Element moreCoursesLink = element.select("div.morecourseslink").first();
            if (moreCoursesLink != null) {
                String baseUniversityUrl = HOST + moreCoursesLink.getElementsByTag("a").first().attr("href");
                List<Document> programDocuments = loadAllPages(baseUniversityUrl);
                for (Document programDocument : programDocuments) {
                    Elements resultsElements = programDocument.select("ol.resultscontainer");
                    for (Element resultsElement : resultsElements) {
                        scrapePrograms(resultsElement, ucasInstitutionId, programs);
                    }
                }
            } else {
                Element resultsElement = element.select("ol").first();
                scrapePrograms(resultsElement, ucasInstitutionId, programs);
            }
        }
        return false;
    }

    private void scrapePrograms(Element element, String ucasInstitutionId, TreeMap<Pair, ImportedProgramScrapeDescriptor> programs) {
        Elements courseResults = element.select("li");
        for (Element courseResult : courseResults) {
            String programName = StringEscapeUtils.unescapeHtml(courseResult.select(".coursenamearea").select("h4").text()).replace("\u00a0", " ");
            Matcher programNameMatcher = programNamePattern.matcher(programName);
            if (!programNameMatcher.matches()) {
                log.error("Could not match program name: " + programName);
                continue;
            }
            programName = programNameMatcher.group(1);
            String courseCode = programNameMatcher.group(2);

            Integer ucasProgramId = Integer.parseInt(courseResult.id().replace("result-", "").replace("course-", ""));

            String qualification = courseResult.select(".courseinfooutcome").text();
            String level = extractProgramLevelFromQualificationElement(courseResult.select(".resultbottomarea .coursequalarea").first());
            String programUrl = courseResult.select("div.coursenamearea a").first().attr("href");
            Integer subjectId = Integer.parseInt(URLEncodedUtils.parse(programUrl, Charsets.UTF_8).stream().filter(param -> param.getName().equals("flt99"))
                    .collect(Collectors.toList()).get(0).getValue());

            Pair programMapKey = new ImmutablePair<>(ucasInstitutionId, new ImmutablePair<>(programName, new ImmutablePair<>(courseCode, level)));
            if (!programs.containsKey(programMapKey)) {
                ImportedProgramImportDTO program = new ImportedProgramImportDTO(programName).withInstitution(Integer.parseInt(ucasInstitutionId))
                        .withLevel(level).withQualification(qualification).withSubjectAreas(Sets.newHashSet(subjectId.toString())).withWeight(1);
                ImportedProgramScrapeDescriptor programDescriptor = new ImportedProgramScrapeDescriptor(program, courseCode, subjectId, ucasProgramId);
                programs.put(programMapKey, programDescriptor);
            } else {
                ImportedProgramScrapeDescriptor programDescriptor = programs.get(programMapKey);
                if (!programDescriptor.getUcasProgramIds().contains(ucasProgramId)) {
                    Integer weight = programDescriptor.getProgram().getWeight();
                    programDescriptor.getProgram().setWeight(weight + 1);
                    programDescriptor.addUcasProgramid(ucasProgramId);
                }
                programDescriptor.getProgram().getSubjectAreas().add(subjectId.toString());
            }

        }
    }

    private List<Document> loadAllPages(String baseUrl) throws URISyntaxException, IOException {
        List<Document> documents = Lists.newLinkedList();
        int page = 1;
        while (true) {
            if (page >= 50) {
                throw new RuntimeException("To many pages for url " + baseUrl);
            }

            String pageUrl = new URIBuilder(baseUrl).setParameter("Page", Integer.toString(page)).toString();
            Document document = Jsoup.connect(pageUrl).get();
            Element resultsContainerElement = document.getElementsByClass("resultscontainer").first();
            if (resultsContainerElement == null || resultsContainerElement.children().isEmpty()) {
                if (documents.isEmpty()) {
                    log.warn("No results for " + baseUrl);
                }
                return documents;
            }
            documents.add(document);
            page++;
        }
    }

    private String extractProgramLevelFromQualificationElement(Element qualificationElement) {
        Element qualificationLevelElement = qualificationElement.select("span.qualificationLevel").first();
        if (qualificationLevelElement != null) {
            return qualificationLevelElement.text();
        }
        Element venueElement = qualificationElement.select("div.courseinfovenue").first();
        if (venueElement == null) {
            return qualificationElement.text().trim();
        }
        TextNode levelTextNode = (TextNode) venueElement.nextSibling();
        return levelTextNode.getWholeText().trim();
    }

    private void writePrograms(Writer writer, TreeMap<Pair, ImportedProgramScrapeDescriptor> programSet) throws IOException {
        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator jg = jsonFactory.createGenerator(writer);
        jg.setCodec(new ObjectMapper());
        jg.setPrettyPrinter(new DefaultPrettyPrinter());
        jg.writeStartArray();

        for (Map.Entry<Pair, ImportedProgramScrapeDescriptor> programEntry : programSet.entrySet()) {
            ImportedProgramScrapeDescriptor programDescriptor = programEntry.getValue();
            jg.writeObject(programDescriptor);
        }

        jg.writeEndArray();
        jg.close();
    }

    public static class ImportedProgramScrapeDescriptor {

        private ImportedProgramImportDTO program;

        private String courseCode;

        private Set<Integer> ucasProgramIds;

        public ImportedProgramScrapeDescriptor() {
        }

        public ImportedProgramScrapeDescriptor(ImportedProgramImportDTO program, String courseCode, Integer subjectIds, Integer ucasProgramId) {
            this.program = program;
            this.courseCode = courseCode;
            this.ucasProgramIds = Sets.newHashSet(ucasProgramId);
        }

        public void addUcasProgramid(Integer ucasProgramId) {
            this.ucasProgramIds.add(ucasProgramId);
        }

        public ImportedProgramImportDTO getProgram() {
            return program;
        }

        public void setProgram(ImportedProgramImportDTO program) {
            this.program = program;
        }

        public String getCourseCode() {
            return courseCode;
        }

        public void setCourseCode(String courseCode) {
            this.courseCode = courseCode;
        }

        public Set<Integer> getUcasProgramIds() {
            return ucasProgramIds;
        }

        public void setUcasProgramIds(Set<Integer> ucasProgramIds) {
            this.ucasProgramIds = ucasProgramIds;
        }
    }

}
