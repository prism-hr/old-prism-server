package com.zuehlke.pgadmissions.referencedata;

import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import junit.framework.Assert;

import org.junit.Test;

import com.google.common.io.Resources;
import com.zuehlke.pgadmissions.referencedata.jaxb.Countries;
import com.zuehlke.pgadmissions.referencedata.jaxb.Disabilities;
import com.zuehlke.pgadmissions.referencedata.jaxb.Domiciles;
import com.zuehlke.pgadmissions.referencedata.jaxb.Ethnicities;
import com.zuehlke.pgadmissions.referencedata.jaxb.Institutions;
import com.zuehlke.pgadmissions.referencedata.jaxb.Nationalities;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences;
import com.zuehlke.pgadmissions.referencedata.jaxb.Qualifications;
import com.zuehlke.pgadmissions.referencedata.jaxb.SourcesOfInterest;

public class ReferenceDataXMLUnmarshallTest {

    @Test
    public void shouldUnmarshallCountriesOfBirth() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Countries.class);
        Unmarshaller u = jc.createUnmarshaller();


        URL file = Resources.getResource("reference_data/2013-05-22/countriesOfBirth.xml");
        Countries countries = (Countries) u.unmarshal(file);

        Assert.assertEquals(249, countries.getCountry().size());
        Assert.assertEquals("AD", countries.getCountry().get(0).getCode());
    }
    
    @Test
    public void shouldUnmarshallDisabilities() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Disabilities.class);
        Unmarshaller u = jc.createUnmarshaller();

        URL file = Resources.getResource("reference_data/2013-05-22/disabilities.xml");
        Disabilities disabilities = (Disabilities) u.unmarshal(file);

        Assert.assertEquals(10, disabilities.getDisability().size());
        Assert.assertEquals("0", disabilities.getDisability().get(0).getCode());
    }
    
    @Test
    public void shouldUnmarshallCountriesOfDomicile() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Domiciles.class);
        Unmarshaller u = jc.createUnmarshaller();

        URL file = Resources.getResource("reference_data/2013-05-22/countriesOfDomicile.xml");
        Domiciles countries = (Domiciles) u.unmarshal(file);

        Assert.assertEquals(232, countries.getDomicile().size());
        Assert.assertEquals("AD", countries.getDomicile().get(0).getCode());
    }
    
    @Test
    public void shouldUnmarshallEthnicities() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Ethnicities.class);
        Unmarshaller u = jc.createUnmarshaller();

        URL file = Resources.getResource("reference_data/2013-05-22/ethnicities.xml");
        Ethnicities ethnicities = (Ethnicities) u.unmarshal(file);

        Assert.assertEquals(17, ethnicities.getEthnicity().size());
        Assert.assertEquals("10", ethnicities.getEthnicity().get(0).getCode());
    }

    @Test
    public void shouldUnmarshallInstitutions() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Institutions.class);
        Unmarshaller u = jc.createUnmarshaller();

        URL file = Resources.getResource("reference_data/2013-05-22/institutions.xml");
        Institutions institutions = (Institutions) u.unmarshal(file);

        Assert.assertEquals(5871, institutions.getInstitution().size());
        Assert.assertEquals("AE0001", institutions.getInstitution().get(0).getCode());
    }
    
    @Test
    public void shouldUnmarshallNationalities() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Nationalities.class);
        Unmarshaller u = jc.createUnmarshaller();

        URL file = Resources.getResource("reference_data/2013-05-22/nationalities.xml");
        Nationalities nationalities = (Nationalities) u.unmarshal(file);

        Assert.assertEquals(228, nationalities.getNationality().size());
        Assert.assertEquals("AD", nationalities.getNationality().get(0).getCode());
    }
    
    @Test
    public void shouldUnmarshallPrismProgrammes() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(ProgrammeOccurrences.class);
        Unmarshaller u = jc.createUnmarshaller();

        URL file = Resources.getResource("reference_data/2013-05-22/prismProgrammes.xml");
        ProgrammeOccurrences prismProgrammes = (ProgrammeOccurrences) u.unmarshal(file);

        Assert.assertEquals(38, prismProgrammes.getProgrammeOccurrence().size());
        Assert.assertEquals("DDNBENSING09", prismProgrammes.getProgrammeOccurrence().get(0).getProgramme().getCode());
    }
    
    @Test
    public void shouldUnmarshallQualifications() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Qualifications.class);
        Unmarshaller u = jc.createUnmarshaller();

        URL file = Resources.getResource("reference_data/2013-05-22/qualifications.xml");
        Qualifications qualifications = (Qualifications) u.unmarshal(file);

        Assert.assertEquals(53, qualifications.getQualification().size());
        Assert.assertEquals("6", qualifications.getQualification().get(0).getCode());
    }
    
    @Test
    public void shouldUnmarshallSourcesOfInterest() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(SourcesOfInterest.class);
        Unmarshaller u = jc.createUnmarshaller();

        URL file = Resources.getResource("reference_data/2013-05-22/sourcesOfInterest.xml");
        SourcesOfInterest sourcesOfInterest = (SourcesOfInterest) u.unmarshal(file);

        Assert.assertEquals(12, sourcesOfInterest.getSourceOfInterest().size());
        Assert.assertEquals("BRIT_COUN", sourcesOfInterest.getSourceOfInterest().get(0).getCode());
    }
    
}
