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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by felipe on 02/06/2015.
 * This class will query http://search.ucas.com/search with a known URL we worked out manually that we could iterate
 * to get institution ID's, Programs, etc based on countries. UK to start with.
 * From UI we saw 16 pages to go through, hence the iteration limit = 17 in the code.
 * <p/>
 */
@Service
public class ScraperService {
    private static Logger log = LoggerFactory.getLogger(ScraperService.class);
    //static URL with a parameter at the end indicating which page.
    //We know for UK we've got 16 pages of institutions
    private static int NUMBER_OF_PAGES = 17;
    //search host
    private static String HOST = "http://search.ucas.com";
    //for institutions
    private static String URL_INSTITUTIONS_TEMPLATE = HOST + "/search/providers?CountryCode=1%7C2%7C3%7C4%7C5&Feather=7&flt8=2&Vac=1&AvailableIn=2015&Location=united%20kingdom&MaxResults=1000&page=";
    //for programs
    private static String URL_PROGRAMS_TEMPLATE = HOST + "/search/results?flt8=2&Vac=1&AvailableIn={yearOfInterest}&providerids={ucasId}&page=";

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

    public Object getProgramsForImportedInstitutions(String yearOfInterest) throws IOException, ParserConfigurationException, TransformerException {
        log.debug("getProgramsForImportedInstitutions() - start method");

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

        while (i.hasNext()) {
            int page = 1;
            //nested iteration for all programs given an institution id
            ImportedInstitution currentInstitution = i.next();
            String url = buildProperUrl(currentInstitution.getUcasId(), yearOfInterest, page);
            Document htmlDoc = getHtml(url);
            Iterator<Element> it = htmlDoc.getElementsByTag("li").iterator();

            while (it.hasNext()) {
                Element element = it.next();
                if (element.id().startsWith("result-")) {
                    //we are now standing on a program for a given institution

                    // program elements
                    org.w3c.dom.Element program = doc.createElement("program");
                    rootElement.appendChild(program);

                    // program element itself
                    //id
                    org.w3c.dom.Element importedInstitutionId = doc.createElement("importedInstitutionId");
                    importedInstitutionId.appendChild(doc.createTextNode(currentInstitution.getUcasId()));
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
                }
            }
            page++;

        }

        return createXmlStringRepresentation(rootElement);
    }

    //get whatever is after </div>
    private String extractProgramLevelFromRawHTML(String html) {
        int start = html.lastIndexOf("</div>");
        return html.substring(start + 6, html.length()).trim();
    }

    //replace place holders in template by current values
    private String buildProperUrl(String ucasId, String yearOfInterest, int page) {
        String url = URL_PROGRAMS_TEMPLATE;
        url = url.replace("{ucasId}", ucasId);
        url = url.replace("{yearOfInterest}", yearOfInterest);
        url = url + page;
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
}
