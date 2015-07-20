package com.zuehlke.pgadmissions.services;

import au.com.bytecode.opencsv.CSVReader;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.services.scoring.ImportedProgram;
import com.zuehlke.pgadmissions.services.scoring.ScoringManager;
import com.zuehlke.pgadmissions.services.scrapping.ImportedSubjectAreaScraping;
import com.zuehlke.pgadmissions.services.scrapping.ScrappingManager;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import uk.co.alumeni.prism.api.model.imported.request.ImportedInstitutionRequest;
import uk.co.alumeni.prism.api.model.imported.request.ImportedProgramRequest;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by felipe on 02/06/2015. This class will query
 * http://search.ucas.com/search with a known URL we worked out manually that we
 * could iterate to get institution ID's, Programs, etc based on countries. UK
 * to start with.
 * <p>
 */
@Service
public class ScraperService {
    private static Logger log = LoggerFactory.getLogger(ScraperService.class);

    // search host
    private static String HOST = "http://search.ucas.com";
    // for institutions
    private static String URL_INSTITUTIONS_TEMPLATE = HOST
            + "/search/providers?CountryCode=1%7C2%7C3%7C4%7C5&Feather=7&flt8=2&Vac=1&AvailableIn=2015&Location=united%20kingdom&MaxResults=1000&page=";
    // for programs
    private static String URL_PROGRAMS_TEMPLATE = HOST + "/search/results?";
    // for subject areas
    private static String URL_SUBJECT_AREAS = "https://www.hesa.ac.uk/component/content/article?id=1787";

    private HashSet<Integer> programCache = new HashSet<>();

    @Inject
    private ImportedEntityService importedEntityService;

    public void scrapeInstitutionsByIteratingIds(Writer writer) throws IOException {
        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator jg = jsonFactory.createGenerator(writer);
        jg.setCodec(new ObjectMapper());
        jg.setPrettyPrinter(new DefaultPrettyPrinter());
        jg.writeStartArray();

        for (int ucasId = 1; ucasId < 3000; ucasId++) {
            Document html = getHtml("http://search.ucas.com/provider/" + ucasId);
            Element nameElement = html.getElementsByClass("shortname").first();
            String name;
            if (nameElement != null) {
                name = nameElement.text();
                ImportedInstitutionRequest institution = new ImportedInstitutionRequest(name).withUcasId("" + ucasId);
                jg.writeObject(institution);
            } else {
                Element noFoundElement = html.getElementsByClass("details_notfound").first();
                if (noFoundElement == null) {
                    throw new RuntimeException("Unexpected page for ID: " + ucasId);
                }
                name = "Empty";
            }
            System.out.println("" + ucasId + ": " + name);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        jg.writeEndArray();
    }

    public void scrapeInstitutions(Writer writer) throws IOException {
        log.debug("scrapeInstitutions() - start method");

        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator jg = jsonFactory.createGenerator(writer);
        jg.setCodec(new ObjectMapper());
        jg.setPrettyPrinter(new DefaultPrettyPrinter());
        jg.writeStartArray();

        Set<String> processedIds = new HashSet<>();
        for (int i = 0; i <= 20; i++) {
            String url = URL_INSTITUTIONS_TEMPLATE + i;
            Document doc = getHtml(url);

            ArrayList<Element> elementsList = Lists.newArrayList(doc.getElementsByTag("li").iterator());
            if (elementsList.isEmpty()) {
                log.info("Finished parsing institution at page " + i);
                break;
            }
            for (Element element : elementsList) {
                if (element.id().startsWith("result-")) {
                    String ucasId = element.id().replace("result-", "");
                    if (!processedIds.add(ucasId)) {
                        break;
                    }
                    String name = element.getElementsByTag("h3").first().text();

                    ImportedInstitutionRequest institution = new ImportedInstitutionRequest(name).withUcasId(ucasId);
                    jg.writeObject(institution);
                }
            }
        }
        jg.writeEndArray();
        log.debug("scrapeInstitutions() - finish method");
    }

    public void scrapePrograms(String yearOfInterest, Writer writer) throws IOException {
        log.debug("scrapePrograms() - start method");

        List<ImportedInstitution> institutions = importedEntityService.getInstitutionsWithUcasId();
//        List<ImportedInstitution> institutions = Lists.newArrayList(new ImportedInstitution().withUcasId("1").withId(1));

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
//        createXmlContentIntoFile(rootElement, yearOfInterest);
    }

    private void scrapeProgramsForInstitution(JsonGenerator jsonGenerator, ImportedInstitution institution, String yearOfInterest) throws IOException, URISyntaxException {
        String initialURL = new URIBuilder(URL_PROGRAMS_TEMPLATE).addParameter("Vac", "1").addParameter("AvailableIn", yearOfInterest).addParameter("providerids", institution.getUcasId()).toString();
        Document htmlDoc = getHtml(initialURL);
        Element resultscount = htmlDoc.getElementsByClass("resultscount").first();
        if (resultscount == null) {
            return; // no programs
        }
        String resultsCount = resultscount.text();

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

    // helper method to get XML program content
    private NodeList readProgramsFromXML(String absolutePath) throws ParserConfigurationException, IOException, SAXException {
        File fXmlFile = new File(absolutePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        org.w3c.dom.Document doc = dBuilder.parse(fXmlFile);
        doc.getDocumentElement().normalize();
        return doc.getElementsByTagName("program");
    }

    private ArrayList<String> loadSubjectAreaCVSFileContent(String absolutePathToFile) throws IOException {
        log.info("loading Subject's CVS file: " + absolutePathToFile);
        CSVReader reader = new CSVReader(new FileReader(absolutePathToFile));
        try {
            ArrayList<String> result = Lists.newArrayList();
            String[] nextLine;
            int c = 0;
            while ((nextLine = reader.readNext()) != null) {
                c++;
                // nextLine[] is an array of values from the line; 0 first
                // element
                // and so on
                result.add(nextLine[0]);

            }
            log.info("[" + c + "] records loaded");
            return result;
        } finally {
            reader.close();
        }
    }

    private ArrayList<ImportedProgram> loadProgramsFromXMLFileContent(String absolutePathToFile) throws IOException, ParserConfigurationException, SAXException {
        log.info("loading Program's XML file " + absolutePathToFile);
        ArrayList<ImportedProgram> result = Lists.newArrayList();
        NodeList nList = readProgramsFromXML(absolutePathToFile);
        int c = 0;
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                c++;
                org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode;
                String importedInstitutionId = eElement.getElementsByTagName("importedInstitutionId").item(0).getTextContent();
                String title = eElement.getElementsByTagName("title").item(0).getTextContent();
                ImportedProgram ii = new ImportedProgram(subtractSubjectId(title), importedInstitutionId);
                result.add(ii);
            }
        }
        log.info("[" + c + "] records loaded");
        return result;
    }

    private String subtractSubjectId(String title) {
        int start = title.lastIndexOf("(") + 1;
        int finish = title.lastIndexOf(")");
        return title.substring(start, finish);
    }

    public void generateScoringForProgramsAndSubjectAreas() throws IOException, ParserConfigurationException, SAXException {
        // paths relative to /bin of JBOSS
        String absolutePathToXMLProgramFile = "programs2015.xml";
        String absolutePathToCVSSubjectAreaFile = "JACS3_20120529.csv";
        // structured to score
        ArrayList<String> subjectArray = loadSubjectAreaCVSFileContent(absolutePathToCVSSubjectAreaFile);
        ArrayList<ImportedProgram> programArray = loadProgramsFromXMLFileContent(absolutePathToXMLProgramFile);
        // safety check
        Assert.notEmpty(programArray);
        Assert.notEmpty(subjectArray);

        ScoringManager scoringManager = new ScoringManager();
        scoringManager.setLip(programArray);
        scoringManager.setLsa(subjectArray);
        // for each program do score
        scoringManager.generateScoring();

    }

    //FIXME Scrapping Manager class is not there
    public void importSubjectAreas() throws IOException {
        ScrappingManager scrappingManager = new ScrappingManager();
        Document html = getHtml(URL_SUBJECT_AREAS);
        Elements container = html.getElementsByAttributeValue("itemprop", "articlebody");
        List<Element> elements = container.get(0).children();
        int count = 0;
        ImportedSubjectAreaScraping currentRootSubject = null;
        for (Element next : elements)
            if (next.tag().getName().equals("h3") && count > 0) {
                currentRootSubject = scrappingManager.addSubjectArea(ImportedSubjectAreaScraping.readH3(next), null);
                // we know it's a root node
            } else if (count > 0) {
                // it's a table
                int nestedCount = 0;
                Iterator<Element> it = next.children().get(0).children().iterator();
                while (it.hasNext()) {
                    Element e = (Element) it.next();
                    if (nestedCount == 0)
                        scrappingManager.addSubjectArea(ImportedSubjectAreaScraping.readTrHead(e), currentRootSubject);
                    else
                        scrappingManager.addSubjectArea(ImportedSubjectAreaScraping.readTrTail(e), currentRootSubject);
                    nestedCount++;
                }
                // =
                // next.children().get(0).children().get(0).getElementsByTag("tr");
            }
        // Elements e = next.getElementsByClass("he");
        count++;
    }

}
