package com.zuehlke.pgadmissions.scoring;

import java.io.File;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.scoring.jaxb.CustomQuestions;

@Component
public class ScoringDefinitionParser {

    private Unmarshaller u;

    public ScoringDefinitionParser() throws Exception {
        File file = new DefaultResourceLoader().getResource("classpath:scoringConfig.xsd").getFile();

        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = sf.newSchema(file);

        JAXBContext jc = JAXBContext.newInstance(CustomQuestions.class);
        u = jc.createUnmarshaller();
        u.setSchema(schema);
    }

    public CustomQuestions parseScoringDefinition(String definitionXml) throws JAXBException {
        CustomQuestions scoringDefinition = (CustomQuestions) u.unmarshal(new StringReader(definitionXml));
        return scoringDefinition;
    }

}
