package com.zuehlke.pgadmissions.services;

import com.zuehlke.pgadmissions.dao.ImportedEntityDAO;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by felipe on 02/06/2015.
 * This class will query http://search.ucas.com/search with a known URL we worked out manually that we could iterate
 * to get institution ID's, Programs, etc based on countries. UK to start with.
 * <p/>
 */
@Service
public class ScrapperService {
    private static Logger log = LoggerFactory.getLogger(ScrapperService.class);
    //static URL with a parameter at the end indicating which page.
    //We know for UK we've got 16 pages of institutions
    private static int NUMBER_OF_PAGES = 17;
    //search host
    private static String HOST = "http://search.ucas.com";
    //for institutions
    private static String URL_INSTITUTIONS_TEMPLATE = HOST + "/search/providers?CountryCode=1%7C2%7C3%7C4%7C5&Feather=7&flt8=2&Vac=1&AvailableIn=2015&Location=united%20kingdom&MaxResults=1000&page=";
    //for programs
    private static String URL_PROGRAMS_TEMPLATE = HOST + "/search/results?Vac=1&AvailableIn={yearOfInterest}&providerids={ucasId}";

    private HashMap<String, String> cache = new HashMap();

    private long counter = 0;

    @Autowired
    private ImportedEntityDAO importedEntityDAO;

    public List<Object> getInstitutionIdsBasedInUK() throws IOException {
        log.debug("getInstitutionIdsBasedInUK() - start method");
        ArrayList<Object> jsonResult = new ArrayList<Object>();
        int counter = 1;
        for (int i = counter; i < NUMBER_OF_PAGES; i++) {
            String url = URL_INSTITUTIONS_TEMPLATE + i;
            Document doc = getHtml(url);
            Elements e = doc.getElementsByTag("li");
            Iterator<Element> it = e.iterator();
            String jsonElement = "";
            while (it.hasNext()) {
                Element element = it.next();
                if (element.id().startsWith("result-")) {
                    jsonResult.add(jsonElement);
                }
            }
        }
        log.debug("getInstitutionIdsBasedInUK() - finish method");
        return jsonResult;
    }

    public void
    getProgramsForImportedInstitutions(String yearOfInterest) throws IOException, ParserConfigurationException, TransformerException {
        log.debug("getProgramsForImportedInstitutions() - start method");
        counter = 0;

        //iterate through the list of ids building the URI for each
        ArrayList<Object> jsonResult = new ArrayList<Object>();
        //get all ImportedInstitutionsId from mysql
        Iterator<ImportedInstitution> i = importedEntityDAO.getAllWhereUcasIdIsNotNull().iterator();
        //create root xml doc
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        org.w3c.dom.Document doc = docBuilder.newDocument();
        org.w3c.dom.Element rootElement = doc.createElement("programs");
        doc.appendChild(rootElement);
        //i contains all institutions with ucasId not null
        while (i.hasNext()) {
            //nested iteration for all programs given an institution id
            ImportedInstitution currentInstitution = i.next();
            getProgramsForAnInstitution(rootElement, currentInstitution, yearOfInterest, doc);
        }
        createXmlContentIntoFile(rootElement, yearOfInterest);
        //return createXmlStringRepresentation(rootElement);

    }

    private void iterateProgramResultSet(org.w3c.dom.Element rootElement, org.w3c.dom.Document doc, int page, String filterUrl, String currentInstitution) throws IOException {

        Document htmlDoc = getHtml(buildProperUrlForAnInstitutionWithFilterAndPage(filterUrl, page));
        //normal case when pagination reaches a page with no elements in the result
        Elements t = htmlDoc.getElementsByClass("resultscontainer");
        if (t.isEmpty()) {
            return;
        }
        Iterator<Element> it = t.get(0).getElementsByTag("li").iterator();
        if (!it.hasNext()) {
            return;
        }
        if (page > 50) {
            log.info("BREAK - more than 50 pages!");
            return;
        }
        while (it.hasNext()) {
            Element element = it.next();
            if (element.id().startsWith("result-")) {
                //we are now standing on a program for a given institution

                //get the ID of the program
                String hasKey = buildHashForInstitutionAndProgram(element.getElementsByTag("a").attr("href"), currentInstitution);
                if (!cache.containsKey(hasKey)) {
                    counter++;
                    log.info("[" + counter + "] Adding new " + hasKey);

                    // program elements
                    org.w3c.dom.Element program = doc.createElement("program");
                    rootElement.appendChild(program);


                    // program element itself
                    //id
                    org.w3c.dom.Element importedInstitutionId = doc.createElement("importedInstitutionId");
                    importedInstitutionId.appendChild(doc.createTextNode(currentInstitution));
                    program.appendChild(importedInstitutionId);

                    //qualification
                    org.w3c.dom.Element importedInstitutionQualification = doc.createElement("qualification");
                    importedInstitutionQualification.appendChild(doc.createTextNode(element.select(".courseinfooutcome").text()));
                    program.appendChild(importedInstitutionQualification);

                    //title
                    org.w3c.dom.Element importedInstitutionTitle = doc.createElement("title");
                    importedInstitutionTitle.appendChild(doc.createTextNode(StringEscapeUtils.escapeXml(element.select(".coursenamearea").select("h4").text())));
                    program.appendChild(importedInstitutionTitle);

                    //level
                    org.w3c.dom.Element importedInstitutionLevel = doc.createElement("level");
                    importedInstitutionLevel.appendChild(doc.createTextNode(extractProgramLevelFromRawHTML(element.select(".resultbottomarea").select(".coursequalarea").html())));
                    program.appendChild(importedInstitutionLevel);

                    //homepage
                    org.w3c.dom.Element importedInstitutionHomepage = doc.createElement("homepage");
                    importedInstitutionHomepage.appendChild(doc.createTextNode(HOST + element.select(".coursenamearea").select("h4").select("a").attr("href")));
                    program.appendChild(importedInstitutionHomepage);

                    rootElement.appendChild(program);
                    //add the program to the cache
                    cache.put(hasKey, null);
                } else {
                    log.info("Skipping program ID[" + element.getElementsByTag("a").attr("href") + "] on institution ID[" + currentInstitution + "]");
                }
            }
        }
        page++;
        iterateProgramResultSet(rootElement, doc, page, filterUrl, currentInstitution);
    }

    private String buildHashForInstitutionAndProgram(String searchUrl, String institutionId) {
        //institution id  = ucas_id
        String temp = "/summary/";
        int start = searchUrl.indexOf(temp) + temp.length();
        int end = searchUrl.indexOf("/", start);
        return "I[" + institutionId + "]" + "-P[" + searchUrl.substring(start, end) + "]";
    }

    private void getProgramsForAnInstitution(org.w3c.dom.Element rootElement, ImportedInstitution currentInstitution, String yearOfInterest, org.w3c.dom.Document doc) throws IOException {
        String url = buildProperUrlForAnInstitution(currentInstitution.getUcasId(), yearOfInterest);
        Document htmlDoc = getHtml(url);

        //get all possible filters
        Iterator<Element> filters = htmlDoc.getElementsByClass("filter").iterator();
        //iterate through the list of filters
        while (filters.hasNext()) {
            Element fe = filters.next();
            //this filter is same as ALL
            if (!fe.id().equals("filtercategory-7")) {
                Iterator<Element> filtersByCategory = fe.getElementsByClass("has-data").iterator();
                while (filtersByCategory.hasNext()) {
                    Element fbce = filtersByCategory.next();
                    iterateProgramResultSet(rootElement, doc, 1, buildTemplateUrlForAnInstitutionWithFilterSelected(fbce.getElementsByTag("a").attr("href")), currentInstitution.getUcasId());
                }
            }
        }
    }

    //get whatever is after </div>
    private String extractProgramLevelFromRawHTML(String html) {
        int start = html.lastIndexOf("</div>");
        return html.substring(start + 6, html.length()).trim();
    }

    private String buildTemplateUrlForAnInstitutionWithFilterSelected(String filter) {
        //clean from url the page so we can append it outside for iteration
        int start = filter.indexOf("Page=");
        int finish = filter.lastIndexOf("&");
        String pageParameter = filter.substring(start, finish);
        return filter.replace(pageParameter, "{page}");
    }

    //replace place holders in template by current values
    private String buildProperUrlForAnInstitution(String ucasId, String yearOfInterest) {
        String url = URL_PROGRAMS_TEMPLATE;
        url = url.replace("{ucasId}", ucasId);
        url = url.replace("{yearOfInterest}", yearOfInterest);
        log.info("built URL for INSTITUTION :" + url);
        return url;
    }

    private String buildProperUrlForAnInstitutionWithFilterAndPage(String filter, int page) {
        String url = HOST + filter.replace("{page}", "Page=" + Integer.toString(page));
        log.info("built URL for PROGRAM " + url);
        return url;
    }

    //
    private Document getHtml(String givenUrl) throws IOException {
        return Jsoup.connect(givenUrl).get();
    }

    private String createXmlStringRepresentation(org.w3c.dom.Element doc) throws TransformerException {
        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = transfac.newTransformer();
        trans.setOutputProperty(OutputKeys.METHOD, "xml");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", Integer.toString(2));

        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        DOMSource source = new DOMSource(doc);

        trans.transform(source, result);
        String xmlString = sw.toString();

        return xmlString;
    }

    private void createXmlContentIntoFile(org.w3c.dom.Element doc, String yearOfInterest) throws TransformerException {
        // write the content into xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File("~/programsUK-" + yearOfInterest + ".xml"));

        // Output to console for testing
        // StreamResult result = new StreamResult(System.out);

        transformer.transform(source, result);
    }

    public void fixDatabase() throws IOException, SAXException, ParserConfigurationException {
        File fXmlFile = new File("/Users/felipe/druidalabs/ucl/prism-server/src/main/resources/xml/defaultEntities/institution.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        org.w3c.dom.Document doc = dBuilder.parse(fXmlFile);
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("institution");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode;
                NodeList nUcasId = eElement.getElementsByTagName("ucasId");
                String ucasId = null;
                if (nUcasId != null && nUcasId.getLength() > 0) {
                    ucasId = eElement.getElementsByTagName("ucasId").item(0).getTextContent();
                }
                NodeList nFacebookId = eElement.getElementsByTagName("facebookId");
                String facebookId = null;
                if (nFacebookId != null && nFacebookId.getLength() > 0) {
                    facebookId = eElement.getElementsByTagName("facebookId").item(0).getTextContent();
                }
                String code = eElement.getElementsByTagName("code").item(0).getTextContent();
                String name = eElement.getElementsByTagName("name").item(0).getTextContent();
                String domicile = eElement.getElementsByTagName("domicile").item(0).getTextContent();
                if (facebookId != null || ucasId != null) {
                    importedEntityDAO.fixDatabase(ucasId, facebookId, code, domicile);
                }
            }
        }

    }

    public void importPrograms() throws IOException, SAXException, ParserConfigurationException {
        File fXmlFile = new File("/Users/felipe/druidalabs/ucl/prism-server/src/main/resources/xml/defaultEntities/programs2016.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        org.w3c.dom.Document doc = dBuilder.parse(fXmlFile);
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("program");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode;
                String importedInstitutionId = eElement.getElementsByTagName("importedInstitutionId").item(0).getTextContent();
                String qualification = eElement.getElementsByTagName("qualification").item(0).getTextContent();
                String title = eElement.getElementsByTagName("title").item(0).getTextContent();
                String homepage = eElement.getElementsByTagName("homepage").item(0).getTextContent();
                String level = eElement.getElementsByTagName("level").item(0).getTextContent();
                importedEntityDAO.importProgram(importedInstitutionId, qualification, title, homepage, level);
            }
        }
    }
}
