package com.zuehlke.pgadmissions.services.scraping;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
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
import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.exceptions.ScrapingException;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedProgramImportDTO;

@Service
public class ProgramUcasScraper {

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

    @SuppressWarnings("rawtypes")
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

    public void processProgramDescriptors(Reader reader, Writer writer) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        CollectionType oldProgramType = objectMapper.getTypeFactory().constructCollectionType(List.class, ImportedProgramImportDTO.class);
        List<ImportedProgramImportDTO> oldPrograms = objectMapper.readValue(reader, oldProgramType);

        List<ImportedProgramImportDTO> programs = new LinkedList<>();

        for (ImportedProgramImportDTO program : oldPrograms) {
            Set<String> jacsCodes = deriveJacsCodes(program.getCode());
            program.setJacsCodes(jacsCodes);
            programs.add(program);
        }

        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator jg = jsonFactory.createGenerator(writer);
        jg.setCodec(new ObjectMapper());
        jg.setPrettyPrinter(new DefaultPrettyPrinter());

        jg.writeObject(programs);

        jg.close();
    }

    private Set<String> deriveJacsCodes(String courseCode) {
        char[] c = courseCode.toCharArray();
        if(Character.isLetter(c[0]) && Character.isDigit(c[1]) && Character.isDigit(c[2]) && Character.isDigit(c[3])) {
            return Collections.singleton(courseCode);
        } else if(Character.isLetter(c[0]) && Character.isDigit(c[1]) && Character.isLetter(c[2]) && Character.isDigit(c[3])) {
            return Sets.newHashSet("" + c[0] + c[1] + "00", "" + c[2] + c[3] + "00");
        } else if(Character.isLetter(c[0]) && Character.isLetter(c[1]) && Character.isDigit(c[2]) && Character.isDigit(c[3])) {
            return Sets.newHashSet("" + c[0] + c[2] + "00", "" + c[1] + c[3] + "00");
        } else if(Character.isLetter(c[0]) && Character.isDigit(c[1])) {
            return Sets.newHashSet("" + c[0] + c[1] + "00");
        } else {
            return null;
        }

    }

    private List<String> getTopCategoryUrls() throws IOException {
        return loadPage(HOST + "/subject").getElementById("subjectareas")
                .getElementsByTag("a").stream()
                .map(a -> HOST + a.attr("href"))
                .collect(Collectors.toList());
    }

    private List<String> getSubjectUrls(List<String> topCategoryUrls) throws IOException, URISyntaxException {
        Integer toyear = LocalDate.now().getYear();
        List<String> years = Lists.newArrayList(toyear.toString(), new Integer(toyear + 1).toString());

        List<String> subjectUrls = Lists.newLinkedList();

        for (String topCategoryUrl : topCategoryUrls) {
            Document document = loadPage(topCategoryUrl);
            String subjectCode = document.getElementById("SubjectCode").attr("value");
            String uriBase = new URIBuilder(HOST + "/search/providers").addParameter("Vac", "1").addParameter("SubjectCode", subjectCode).toString();
            List<String> urls = document
                    .select("li.subjectsearchsteparea")
                    .stream()
                    .flatMap(area -> area.select("input[name=\"flt99\"]").stream())
                    .<Pair<Element, String>>flatMap(input -> years.stream().map(year -> ImmutablePair.of(input, year)))
                    .map(pair -> newURIBuilder(uriBase).addParameter(pair.getLeft().attr("name"), pair.getLeft().attr("value"))
                            .addParameter("AvailableIn", pair.getRight()).toString())
                    .collect(Collectors.toList());
            subjectUrls.addAll(urls);
        }
        return subjectUrls;
    }

    /**
     * return <code>true</code> when there are no more pages
     */
    @SuppressWarnings("rawtypes")
    private boolean scrapeUniversitySections(Document document, TreeMap<Pair, ImportedProgramScrapeDescriptor> programs)
            throws IOException, URISyntaxException {
        Element resultsContainerElement = document.getElementsByClass("resultscontainer").first();

        // iterate over institutions
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

    @SuppressWarnings("rawtypes")
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
                Set<String> jacsCodes = deriveJacsCodes(courseCode);
                ImportedProgramImportDTO program = new ImportedProgramImportDTO(programName).withInstitution(Integer.parseInt(ucasInstitutionId))
                        .withLevel(level).withQualification(qualification).withUcasSubjects(Sets.newHashSet(subjectId)).withWeight(1)
                        .withCode(courseCode).withJacsCodes(jacsCodes);
                ImportedProgramScrapeDescriptor programDescriptor = new ImportedProgramScrapeDescriptor(program, ucasProgramId);
                programs.put(programMapKey, programDescriptor);
            } else {
                ImportedProgramScrapeDescriptor programDescriptor = programs.get(programMapKey);
                if (!programDescriptor.getUcasProgramIds().contains(ucasProgramId)) {
                    Integer weight = programDescriptor.getProgram().getWeight();
                    programDescriptor.getProgram().setWeight(weight + 1);
                    programDescriptor.addUcasProgramId(ucasProgramId);
                }
                programDescriptor.getProgram().getUcasSubjects().add(subjectId);
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
            Document document = loadPage(pageUrl);
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

    private Document loadPage(String pageUrl) throws IOException {
        int attempt = 1;
        int delay = 1000;
        while (true) {
            try {
                return Jsoup.connect(pageUrl).get();
            } catch (SocketTimeoutException e) {
                if (attempt == 10) {
                    throw new RuntimeException("Could not load page due to timeout", e);
                }
                try {
                    log.error("Requesting page " + pageUrl + " time-outed - waiting for " + delay + " ms...");
                    Thread.sleep(delay);
                    delay *= 4;
                    attempt += 1;
                } catch (InterruptedException e1) {
                    System.exit(1);
                }
            }
        }
    }

    private String extractProgramLevelFromQualificationElement(Element qualificationElement) {
        // get whatever is after </div>
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

    @SuppressWarnings("rawtypes")
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

        private Set<Integer> ucasProgramIds;

        public ImportedProgramScrapeDescriptor() {
            return;
        }

        public ImportedProgramScrapeDescriptor(ImportedProgramImportDTO program, Integer ucasProgramId) {
            this.program = program;
            this.ucasProgramIds = Sets.newHashSet(ucasProgramId);
        }

        public void addUcasProgramId(Integer ucasProgramId) {
            this.ucasProgramIds.add(ucasProgramId);
        }

        public ImportedProgramImportDTO getProgram() {
            return program;
        }

        public void setProgram(ImportedProgramImportDTO program) {
            this.program = program;
        }

        public Set<Integer> getUcasProgramIds() {
            return ucasProgramIds;
        }

        public void setUcasProgramIds(Set<Integer> ucasProgramIds) {
            this.ucasProgramIds = ucasProgramIds;
        }
    }

}
