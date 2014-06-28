package com.zuehlke.pgadmissions.services.importers;

import com.google.common.io.Closeables;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.LanguageQualificationType;
import com.zuehlke.pgadmissions.services.EntityService;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.Map;

@Service
public class EntityExportService {

    @Autowired
    private EntityService entityService;


    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> void exportEntities(Class entityClass, String entityName) throws Exception {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(new File(entityName + ".xml"));

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            document.setXmlVersion("1.0");

            Element mainElement = document.createElement(pluralize(entityName));
            document.appendChild(mainElement);

            for (Object entity : entityService.getAll(entityClass)) {
                Element entityElement = document.createElement(entityName);
                Element codeElement = document.createElement("code");
                codeElement.setTextContent((String) PropertyUtils.getSimpleProperty(entity, "code"));
                entityElement.appendChild(codeElement);
                Element nameElement = document.createElement("name");
                nameElement.setTextContent((String) PropertyUtils.getSimpleProperty(entity, "name"));
                entityElement.appendChild(nameElement);
                if (entityClass.equals(ImportedInstitution.class)) {
                    Element domicileElement = document.createElement("domicile");
                    Domicile domicile = (Domicile) PropertyUtils.getSimpleProperty(entity, "domicile");
                    domicileElement.setTextContent(domicile.getCode());
                    entityElement.appendChild(domicileElement);
                } else if (entityClass.equals(LanguageQualificationType.class)) {
                    Map<String, Object> properties = PropertyUtils.describe(entity);
                    for (String property : properties.keySet()) {
                        if (property.endsWith("Score")) {
                            Element propertyElement = document.createElement(property);
                            BigDecimal value = (BigDecimal) properties.get(property);
                            if (value != null) {
                                propertyElement.setTextContent(value.toPlainString());
                                entityElement.appendChild(propertyElement);
                            }
                        }
                    }
                }
                mainElement.appendChild(entityElement);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            transformer.transform(new DOMSource(document), new StreamResult(fileWriter));


        } finally {
            Closeables.close(fileWriter, true);
        }
    }

    private String pluralize(String entityName) {
        if (entityName.equals("sourceOfInterest")) {
            return "sourcesOfInterest";
        } else if (entityName.endsWith("y")) {
            return entityName.substring(0, entityName.length() - 1) + "ies";
        }
        return entityName + "s";
    }

}
