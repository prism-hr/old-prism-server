package uk.co.alumeni.prism.services;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.INSTITUTION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PROGRAM;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PROJECT;

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
import uk.co.alumeni.prism.PrismConstants;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationSitemap;

@Service
@Transactional
public class SearchEngineOptimisationService {

    private static final String XML_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();

    private static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();

    @Value("${application.url}")
    private String applicationUrl;

    @Value("${application.api.url}")
    private String applicationApiUrl;

    @Inject
    private ResourceService resourceService;

    public String getSitemapIndex() throws ParserConfigurationException, TransformerException {
        Document document = createXmlDocument();

        Element sitemapIndex = document.createElement("sitemapindex");
        sitemapIndex.setAttribute("xmlns", "http://www.sitemaps.org/schemas/sitemap/0.9");
        document.appendChild(sitemapIndex);

        DateTime baseline = new DateTime();
        Map<PrismScope, DateTime> latestUpdateTimestamps = Maps.newLinkedHashMap();

        latestUpdateTimestamps.put(PROJECT, resourceService.getLatestUpdatedTimestampSitemap(PROJECT));
        latestUpdateTimestamps.put(PROGRAM, resourceService.getLatestUpdatedTimestampSitemap(PROGRAM));
        latestUpdateTimestamps.put(DEPARTMENT, resourceService.getLatestUpdatedTimestampSitemap(DEPARTMENT));
        latestUpdateTimestamps.put(INSTITUTION, resourceService.getLatestUpdatedTimestampSitemap(INSTITUTION));

        for (PrismScope scope : latestUpdateTimestamps.keySet()) {
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

    public String getProjectSitemap() throws Exception {
        List<ResourceRepresentationSitemap> sitemapEntries = resourceService.getResourceSitemapRepresentations(PROJECT);
        return buildSitemap(PROJECT, sitemapEntries);
    }

    public String getProgramSitemap() throws Exception {
        List<ResourceRepresentationSitemap> sitemapEntries = resourceService.getResourceSitemapRepresentations(PROGRAM);
        return buildSitemap(PROGRAM, sitemapEntries);
    }

    public String getDepartmentSitemap() throws Exception {
        List<ResourceRepresentationSitemap> sitemapEntries = resourceService.getResourceSitemapRepresentations(DEPARTMENT);
        return buildSitemap(DEPARTMENT, sitemapEntries);
    }

    public String getInstitutionSitemap() throws Exception {
        List<ResourceRepresentationSitemap> sitemapEntries = resourceService.getResourceSitemapRepresentations(INSTITUTION);
        return buildSitemap(INSTITUTION, sitemapEntries);
    }

    private String buildSitemap(PrismScope scope, List<ResourceRepresentationSitemap> sitemapEntries) throws Exception {
        Document document = createXmlDocument();

        Element sitemap = document.createElement("urlset");
        sitemap.setAttribute("xmlns", "http://www.sitemaps.org/schemas/sitemap/0.9");
        document.appendChild(sitemap);

        Integer urlNodeByteLength = null;
        for (ResourceRepresentationSitemap sitemapEntryDTO : sitemapEntries) {
            String xmlDocumentContent = getStringFromXmlDocument(document);

            if (urlNodeByteLength != null && getUtf8ByteLength(xmlDocumentContent) + urlNodeByteLength > 10485760) {
                break;
            }

            Element url = document.createElement("url");
            sitemap.appendChild(url);

            Element location = document.createElement("loc");
            url.appendChild(location);

            Text locationString = document.createTextNode(applicationUrl + "/" + PrismConstants.ANGULAR_HASH + "/?" + scope.getLowerCamelName() + "="
                    + sitemapEntryDTO.getId());
            location.appendChild(locationString);

            Element lastModifiedTimestamp = document.createElement("lastmod");
            url.appendChild(lastModifiedTimestamp);

            Text lastModifiedTimestampString = document.createTextNode(sitemapEntryDTO.getUpdatedTimestampSitemap().toString(XML_DATE_FORMAT));
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
