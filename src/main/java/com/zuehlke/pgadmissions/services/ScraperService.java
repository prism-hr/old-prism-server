package com.zuehlke.pgadmissions.services;

import au.com.bytecode.opencsv.CSVReader;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.services.scoring.ImportedProgram;
import com.zuehlke.pgadmissions.services.scoring.ScoringManager;
import com.zuehlke.pgadmissions.services.scrapping.ImportedSubjectAreaScraping;
import com.zuehlke.pgadmissions.services.scrapping.ScrappingManager;
import org.apache.commons.lang.StringEscapeUtils;
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

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

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
    private static String URL_PROGRAMS_TEMPLATE = HOST + "/search/results?Vac=1&AvailableIn={yearOfInterest}&providerids={ucasId}";
    // for subject areas
    private static String URL_SUBJECT_AREAS = "https://www.hesa.ac.uk/component/content/article?id=1787";

    private HashMap<String, String> cache = Maps.newHashMap();

    private long counter = 0;

    @Inject
    private ImportedEntityService importedEntityService;

    public void scrapeInstitutionsByIteratingIds(Writer writer) throws IOException {
        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator jg = jsonFactory.createGenerator(writer);
        jg.setCodec(new ObjectMapper());
        jg.setPrettyPrinter(new DefaultPrettyPrinter());
        jg.writeStartArray();

        for(int ucasId = 1 ; ucasId < 3000 ; ucasId++) {
            Document html = getHtml("http://search.ucas.com/provider/" + ucasId);
            Element nameElement = html.getElementsByClass("shortname").first();
            String name;
            if(nameElement != null) {
                name = nameElement.text();
                ImportedInstitutionRequest institution = new ImportedInstitutionRequest(name).withUcasId("" + ucasId);
                jg.writeObject(institution);
            } else {
                Element noFoundElement = html.getElementsByClass("details_notfound").first();
                if(noFoundElement == null) {
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
            if(elementsList.isEmpty()) {
                log.info("Finished parsing institution at page " + i);
                break;
            }
            for (Element element : elementsList) {
                if (element.id().startsWith("result-")) {
                    String ucasId = element.id().replace("result-", "");
                    if(!processedIds.add(ucasId)) {
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

    // this method is to scrap from UCAS website all programs for each
    // imported_institution
    public void getProgramsForImportedInstitutions(String yearOfInterest) throws IOException, ParserConfigurationException, TransformerException {
        log.debug("getProgramsForImportedInstitutions() - start method");
        counter = 0;
        // iterate through the list of ids building the URI for each
        // get all ImportedInstitutionsId from mysql
        Iterator<ImportedInstitution> i = importedEntityService.getInstitutionsWithUcasId().iterator();
        // create root xml doc
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        org.w3c.dom.Document doc = docBuilder.newDocument();
        org.w3c.dom.Element rootElement = doc.createElement("programs");
        doc.appendChild(rootElement);
        // i contains all institutions with ucasId not null
        while (i.hasNext()) {
            // nested iteration for all programs given an institution id
            ImportedInstitution currentInstitution = i.next();
            getProgramsForAnInstitution(rootElement, currentInstitution, yearOfInterest, doc);
        }
        createXmlContentIntoFile(rootElement, yearOfInterest);
        // return createXmlStringRepresentation(rootElement); - disabled at the
        // moment and replaced by previous line to write into disk directly as
        // the size is huge
    }

    // helper method to iterate through all programs given an institution
    private void iterateProgramResultSet(org.w3c.dom.Element rootElement, org.w3c.dom.Document doc, int page, String filterUrl, String currentInstitution)
            throws IOException {

        Document htmlDoc = getHtml(buildProperUrlForAnInstitutionWithFilterAndPage(filterUrl, page));
        // normal case when pagination reaches a page with no elements in the
        // result
        Elements t = htmlDoc.getElementsByClass("resultscontainer");
        if (t.isEmpty()) {
            return;
        }
        Iterator<Element> it = t.get(0).getElementsByTag("li").iterator();
        if (!it.hasNext()) {
            return;
        }
        if (page > 50) {
            log.info("BREAK - more than 50 pages!"); // bug at UCAS website
            // leading to infinite loop
            return;
        }
        int programCounter = 0;
        while (it.hasNext()) {
            programCounter++;
            Element element = it.next();
            if (element.id().startsWith("result-")) {
                // we are now standing on a program for a given institution

                // get the ID of the program
                String hasKey = buildHashForInstitutionAndProgram(element.getElementsByTag("a").attr("href"), currentInstitution);
                if (!cache.containsKey(hasKey)) {
                    counter++;
                    log.info("[" + counter + "] Adding new " + hasKey);

                    // program elements
                    org.w3c.dom.Element program = doc.createElement("program");
                    rootElement.appendChild(program);

                    // program element itself
                    // id
                    org.w3c.dom.Element importedInstitutionId = doc.createElement("importedInstitutionId");
                    importedInstitutionId.appendChild(doc.createTextNode(currentInstitution));
                    program.appendChild(importedInstitutionId);

                    // qualification
                    org.w3c.dom.Element importedInstitutionQualification = doc.createElement("qualification");
                    importedInstitutionQualification.appendChild(doc.createTextNode(element.select(".courseinfooutcome").text()));
                    program.appendChild(importedInstitutionQualification);

                    // title
                    org.w3c.dom.Element importedInstitutionTitle = doc.createElement("title");
                    importedInstitutionTitle
                            .appendChild(doc.createTextNode(StringEscapeUtils.escapeXml(element.select(".coursenamearea").select("h4").text())));
                    program.appendChild(importedInstitutionTitle);

                    // level
                    org.w3c.dom.Element importedInstitutionLevel = doc.createElement("level");
                    importedInstitutionLevel.appendChild(doc.createTextNode(extractProgramLevelFromRawHTML(element.select(".resultbottomarea")
                            .select(".coursequalarea").html())));
                    program.appendChild(importedInstitutionLevel);

                    // homepage
                    org.w3c.dom.Element importedInstitutionHomepage = doc.createElement("homepage");
                    importedInstitutionHomepage.appendChild(doc.createTextNode(HOST + element.select(".coursenamearea").select("h4").select("a").attr("href")));
                    program.appendChild(importedInstitutionHomepage);

                    rootElement.appendChild(program);
                    // add the program to the cache
                    cache.put(hasKey, null); // key matters but not value in the
                    // cache
                } else {
                    log.info("Skipping program ID[" + element.getElementsByTag("a").attr("href") + "] on institution ID[" + currentInstitution + "]");
                }
            }
        }
        page++;
        log.info("***[" + currentInstitution + "] => [" + programCounter + "]***");
        iterateProgramResultSet(rootElement, doc, page, filterUrl, currentInstitution);
    }

    // helper method to build a unique key for each institution + program so
    // when we run the service with different parameters (yearOfInteres) we
    // avoid duplicates
    private String buildHashForInstitutionAndProgram(String searchUrl, String institutionId) {
        // institution id = ucas_id
        String temp = "/summary/";
        int start = searchUrl.indexOf(temp) + temp.length();
        int end = searchUrl.indexOf("/", start);
        return "I[" + institutionId + "]" + "-P[" + searchUrl.substring(start, end) + "]";
    }

    // helper method
    private void getProgramsForAnInstitution(org.w3c.dom.Element rootElement, ImportedInstitution currentInstitution, String yearOfInterest,
                                             org.w3c.dom.Document doc) throws IOException {
        String url = buildProperUrlForAnInstitution(currentInstitution.getUcasId(), yearOfInterest);
        Document htmlDoc = getHtml(url);

        // get all possible filters
        Iterator<Element> filters = htmlDoc.getElementsByClass("filter").iterator();
        // iterate through the list of filters
        while (filters.hasNext()) {
            Element fe = filters.next();
            // this filter is same as ALL
            if (!fe.id().equals("filtercategory-7")) {
                Iterator<Element> filtersByCategory = fe.getElementsByClass("has-data").iterator();
                while (filtersByCategory.hasNext()) {
                    Element fbce = filtersByCategory.next();
                    iterateProgramResultSet(rootElement, doc, 1, buildTemplateUrlForAnInstitutionWithFilterSelected(fbce.getElementsByTag("a").attr("href")),
                            currentInstitution.getUcasId());
                }
            }
        }
    }

    // get whatever is after </div>
    private String extractProgramLevelFromRawHTML(String html) {
        int start = html.lastIndexOf("</div>");
        return html.substring(start + 6, html.length()).trim();
    }

    // helper method
    private String buildTemplateUrlForAnInstitutionWithFilterSelected(String filter) {
        // clean from url the page so we can append it outside for iteration
        int start = filter.indexOf("Page=");
        int finish = filter.lastIndexOf("&");
        String pageParameter = filter.substring(start, finish);
        return filter.replace(pageParameter, "{page}");
    }

    // helper method - replace place holders in template by current values
    private String buildProperUrlForAnInstitution(String ucasId, String yearOfInterest) {
        String url = URL_PROGRAMS_TEMPLATE;
        url = url.replace("{ucasId}", ucasId);
        url = url.replace("{yearOfInterest}", yearOfInterest);
        log.info("built URL for INSTITUTION :" + url);
        return url;
    }

    // helper method
    private String buildProperUrlForAnInstitutionWithFilterAndPage(String filter, int page) {
        String url = HOST + filter.replace("{page}", "Page=" + Integer.toString(page));
        log.info("built URL for PROGRAM " + url);
        return url;
    }

    // helper method
    private Document getHtml(String givenUrl) throws IOException {
        return Jsoup.connect(givenUrl).get();
    }

    // helper method - write XML content to disk directly
    private void createXmlContentIntoFile(org.w3c.dom.Element doc, String yearOfInterest) throws TransformerException {
        // write the content into xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File("programsUK-" + yearOfInterest + ".xml"));
        // actual write to disk
        transformer.transform(source, result);
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
        Iterator<Element> iterator = container.get(0).children().iterator();
        int count = 0;
        ImportedSubjectAreaScraping currentRootSubject = null;
        while (iterator.hasNext()) {
            Element next = (Element) iterator.next();
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
        return;
    }

}
