package com.zuehlke.pgadmissions.referencedata;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import junit.framework.Assert;

import org.junit.Test;

import com.zuehlke.pgadmissions.referencedata.jaxb.Countries;
import com.zuehlke.pgadmissions.referencedata.jaxb.Disabilities;
import com.zuehlke.pgadmissions.referencedata.jaxb.Domiciles;
import com.zuehlke.pgadmissions.referencedata.jaxb.Ethnicities;
import com.zuehlke.pgadmissions.referencedata.jaxb.Institutions;
import com.zuehlke.pgadmissions.referencedata.jaxb.Nationalities;
import com.zuehlke.pgadmissions.referencedata.jaxb.Programmes;
import com.zuehlke.pgadmissions.referencedata.jaxb.Qualifications;
import com.zuehlke.pgadmissions.referencedata.jaxb.SourcesOfInterest;

public class ReferenceDataXMLUnmarshallTest {

    @Test
    public void shouldUnmarshallCountriesOfBirth() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Countries.class);
        Unmarshaller u = jc.createUnmarshaller();

        File f = new File("src/test/resources/reference_data/countriesOfBirth.xml");
        Countries countries = (Countries) u.unmarshal(f);

        Assert.assertEquals(224, countries.getCountry().size());
        Assert.assertEquals("AD", countries.getCountry().get(0).getCode());
    }
    
    @Test
    public void shouldUnmarshallDisabilities() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Disabilities.class);
        Unmarshaller u = jc.createUnmarshaller();

        File f = new File("src/test/resources/reference_data/disabilities.xml");
        Disabilities disabilities = (Disabilities) u.unmarshal(f);

        Assert.assertEquals(10, disabilities.getDisability().size());
        Assert.assertEquals("0", disabilities.getDisability().get(0).getCode());
    }
    
    @Test
    public void shouldUnmarshallCountriesOfDomicile() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Domiciles.class);
        Unmarshaller u = jc.createUnmarshaller();

        File f = new File("src/test/resources/reference_data/countriesOfDomicile.xml");
        Domiciles countries = (Domiciles) u.unmarshal(f);

        Assert.assertEquals(229, countries.getDomicile().size());
        Assert.assertEquals("AD", countries.getDomicile().get(0).getCode());
    }
    
    @Test
    public void shouldUnmarshallEthnicities() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Ethnicities.class);
        Unmarshaller u = jc.createUnmarshaller();

        File f = new File("src/test/resources/reference_data/ethnicities.xml");
        Ethnicities ethnicities = (Ethnicities) u.unmarshal(f);

        Assert.assertEquals(17, ethnicities.getEthnicity().size());
        Assert.assertEquals("10", ethnicities.getEthnicity().get(0).getCode());
    }
    
    @Test
    public void shouldUnmarshallInstitutions() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Institutions.class);
        Unmarshaller u = jc.createUnmarshaller();

        File f = new File("src/test/resources/reference_data/institutions.xml");
        Institutions institutions = (Institutions) u.unmarshal(f);

        Assert.assertEquals(5871, institutions.getInstitution().size());
        Assert.assertEquals("AE0001", institutions.getInstitution().get(0).getCode());
    }
    
    @Test
    public void shouldUnmarshallNationalities() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Nationalities.class);
        Unmarshaller u = jc.createUnmarshaller();

        File f = new File("src/test/resources/reference_data/nationalities.xml");
        Nationalities nationalities = (Nationalities) u.unmarshal(f);

        Assert.assertEquals(224, nationalities.getNationality().size());
        Assert.assertEquals("AA", nationalities.getNationality().get(0).getCode());
    }
    
    @Test
    public void shouldUnmarshallPrismProgrammes() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Programmes.class);
        Unmarshaller u = jc.createUnmarshaller();

        File f = new File("src/test/resources/reference_data/prismProgrammes.xml");
        Programmes prismProgrammes = (Programmes) u.unmarshal(f);

        Assert.assertEquals(66, prismProgrammes.getProgramme().size());
        Assert.assertEquals("DDNBENSING09", prismProgrammes.getProgramme().get(0).getCode());
    }
    
    @Test
    public void shouldUnmarshallQualifications() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Qualifications.class);
        Unmarshaller u = jc.createUnmarshaller();

        File f = new File("src/test/resources/reference_data/qualifications.xml");
        Qualifications qualifications = (Qualifications) u.unmarshal(f);

        Assert.assertEquals(53, qualifications.getQualification().size());
        Assert.assertEquals("6", qualifications.getQualification().get(0).getCode());
    }
    
    @Test
    public void shouldUnmarshallSourcesOfInterest() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(SourcesOfInterest.class);
        Unmarshaller u = jc.createUnmarshaller();

        File f = new File("src/test/resources/reference_data/sourcesOfInterest.xml");
        SourcesOfInterest sourcesOfInterest = (SourcesOfInterest) u.unmarshal(f);

        Assert.assertEquals(12, sourcesOfInterest.getSourceOfInterest().size());
        Assert.assertEquals("BRIT_COUN", sourcesOfInterest.getSourceOfInterest().get(0).getCode());
    }
    
}
