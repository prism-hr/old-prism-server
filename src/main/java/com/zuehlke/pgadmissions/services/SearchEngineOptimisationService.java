package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.dto.SitemapEntryDTO;
import com.zuehlke.pgadmissions.utils.PrismConstants;

@Service
@Transactional
public class SearchEngineOptimisationService {

    private static final String XML_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    private static final PrismScope[] SCOPES = { INSTITUTION, PROGRAM, PROJECT };

    private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();

    private static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();

    @Value("${application.url}")
    private String applicationUrl;

    @Value("${application.api.url}")
    private String applicationApiUrl;

    @Inject
    private InstitutionService institutionService;

    @Inject
    private ProgramService programService;

    @Inject
    private ProjectService projectService;

    @Inject
    private StateService stateService;

    public String getSitemapIndex() throws ParserConfigurationException, TransformerException {
        Document document = createXmlDocument();

        Element sitemapIndex = document.createElement("sitemapindex");
        sitemapIndex.setAttribute("xmlns", "http://www.sitemaps.org/schemas/sitemap/0.9");
        document.appendChild(sitemapIndex);

        DateTime baseline = new DateTime();
        Map<PrismScope, DateTime> latestUpdateTimestamps = Maps.newHashMap();

        List<PrismState> activeProjectStates = stateService.getActiveProjectStates();
        List<PrismState> activeProgramStates = stateService.getActiveProgramStates();

        latestUpdateTimestamps.put(PROJECT, projectService.getLatestUpdatedTimestampSitemap(activeProjectStates));
        latestUpdateTimestamps.put(PROGRAM, programService.getLatestUpdatedTimestampSitemap(activeProgramStates));
        latestUpdateTimestamps.put(INSTITUTION, institutionService.getLatestUpdatedTimestampSitemap(activeProgramStates, activeProjectStates));

        for (PrismScope scope : SCOPES) {
            Element sitemap = document.createElement("sitemap");
            sitemapIndex.appendChild(sitemap);

            Element location = document.createElement("loc");
            sitemap.appendChild(location);

            Text locationString = document.createTextNode(applicationApiUrl + "/sitemap/" + scope.getLowerCamelName() + "_sitemap.xml");
            location.appendChild(locationString);

            Element lastModifiedDateTime = document.createElement("lastmod");
            sitemap.appendChild(lastModifiedDateTime);

            DateTime updatedTimestamp = latestUpdateTimestamps.get(scope);
            updatedTimestamp = updatedTimestamp == null ? baseline : updatedTimestamp;

            Text lastModifiedDateTimeString = document.createTextNode(updatedTimestamp.toString(XML_DATE_FORMAT));
            lastModifiedDateTime.appendChild(lastModifiedDateTimeString);
        }

        return getStringFromXmlDocument(document);
    }

    public String getProjectSitemap() throws UnsupportedEncodingException, TransformerException, ParserConfigurationException {
        List<SitemapEntryDTO> sitemapEntries = projectService.getSitemapEntries();
        return buildSitemap(PROJECT, sitemapEntries);
    }

    public String getProgramSitemap() throws UnsupportedEncodingException, TransformerException, ParserConfigurationException {
        List<SitemapEntryDTO> sitemapEntries = programService.getSitemapEntries();
        return buildSitemap(PROGRAM, sitemapEntries);
    }

    public String getInstitutionSitemap() throws UnsupportedEncodingException, TransformerException, ParserConfigurationException {
        List<SitemapEntryDTO> sitemapEntries = institutionService.getSitemapEntries();
        return buildSitemap(INSTITUTION, sitemapEntries);
    }

    private String buildSitemap(PrismScope scope, List<SitemapEntryDTO> sitemapEntries) throws ParserConfigurationException, TransformerException,
            UnsupportedEncodingException {
        Document document = createXmlDocument();

        Element sitemap = document.createElement("urlset");
        sitemap.setAttribute("xmlns", "http://www.sitemaps.org/schemas/sitemap/0.9");
        document.appendChild(sitemap);

        Integer urlNodeByteLength = null;
        for (SitemapEntryDTO sitemapEntryDTO : sitemapEntries) {
            String xmlDocumentContent = getStringFromXmlDocument(document);

            if (urlNodeByteLength != null && getUtf8ByteLength(xmlDocumentContent) + urlNodeByteLength > 10485760) {
                break;
            }

            Element url = document.createElement("url");
            sitemap.appendChild(url);

            Element location = document.createElement("loc");
            url.appendChild(location);

            Text locationString = document.createTextNode(applicationUrl + "/" + PrismConstants.ANGULAR_HASH + "/?" + scope.getLowerCamelName() + "="
                    + sitemapEntryDTO.getResourceId());
            location.appendChild(locationString);

            Element lastModifiedTimestamp = document.createElement("lastmod");
            url.appendChild(lastModifiedTimestamp);

            Text lastModifiedTimestampString = document.createTextNode(sitemapEntryDTO.getLastModifiedTimestamp().toString(XML_DATE_FORMAT));
            lastModifiedTimestamp.appendChild(lastModifiedTimestampString);

            if (urlNodeByteLength == null) {
                urlNodeByteLength = getUtf8ByteLength(getStringFromXmlDocument(document)) - getUtf8ByteLength(xmlDocumentContent);
            }
        }

        return getStringFromXmlDocument(document);
    }

    private int getUtf8ByteLength(String string) throws UnsupportedEncodingException {
        return string.getBytes("UTF-8").length;
    }

    private String getStringFromXmlDocument(Document document) throws TransformerException {
        DOMSource xmlContent = new DOMSource(document);
        Transformer transformer = TRANSFORMER_FACTORY.newTransformer();
        StringWriter stringWriter = new StringWriter();
        transformer.transform(xmlContent, new StreamResult(stringWriter));
        return stringWriter.toString();
    }

    private Document createXmlDocument() throws ParserConfigurationException {
        DocumentBuilder documentBuilder = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
        return documentBuilder.newDocument();
    }

}
