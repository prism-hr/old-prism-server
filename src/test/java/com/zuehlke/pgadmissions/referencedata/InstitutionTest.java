package com.zuehlke.pgadmissions.referencedata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.junit.Test;

public class InstitutionTest {

    @Test
    public void setup() {
        Institutions institutions = null;
        try {
            JAXBContext context = JAXBContext.newInstance(Institutions.class);
            institutions = (Institutions) context.createUnmarshaller().unmarshal(new File("src/test/resources/reference_data/institutions.xml"));
        } catch (JAXBException e) {
            fail(e.getMessage());
        }
        
        assertNotNull(institutions);
        assertEquals(5871, institutions.institutions.size());
        
        
        for (Institution i : institutions.institutions) {
            i.name = i.name.replace("'", "\\'");
            i.name = i.name.replace("\"", "\\\"");
            System.out.println(String.format( "\t('%s', '%s', TRUE),", i.name, i.country));
            
        }
    }
}

@XmlRootElement(name = "institutions")
class Institutions {

    @XmlElement(name = "institution")
    public List<Institution> institutions = new ArrayList<Institution>();
}

class Institution {

    @XmlElement
    public String name;

    @XmlElement
    public String country;
}
