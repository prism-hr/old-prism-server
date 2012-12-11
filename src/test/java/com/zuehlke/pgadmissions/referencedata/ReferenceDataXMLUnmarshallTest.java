package com.zuehlke.pgadmissions.referencedata;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import junit.framework.Assert;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

import au.com.bytecode.opencsv.CSVReader;

import com.zuehlke.pgadmissions.referencedata.jaxb.Countries;
import com.zuehlke.pgadmissions.referencedata.jaxb.Countries.Country;
import com.zuehlke.pgadmissions.referencedata.jaxb.Disabilities;
import com.zuehlke.pgadmissions.referencedata.jaxb.Disabilities.Disability;
import com.zuehlke.pgadmissions.referencedata.jaxb.Domiciles;
import com.zuehlke.pgadmissions.referencedata.jaxb.Domiciles.Domicile;
import com.zuehlke.pgadmissions.referencedata.jaxb.Ethnicities;
import com.zuehlke.pgadmissions.referencedata.jaxb.Ethnicities.Ethnicity;
import com.zuehlke.pgadmissions.referencedata.jaxb.Institutions;
import com.zuehlke.pgadmissions.referencedata.jaxb.Institutions.Institution;
import com.zuehlke.pgadmissions.referencedata.jaxb.Nationalities;
import com.zuehlke.pgadmissions.referencedata.jaxb.Nationalities.Nationality;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence;
import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence.ModeOfAttendance;
import com.zuehlke.pgadmissions.referencedata.jaxb.Qualifications;
import com.zuehlke.pgadmissions.referencedata.jaxb.Qualifications.Qualification;
import com.zuehlke.pgadmissions.referencedata.jaxb.SourcesOfInterest;
import com.zuehlke.pgadmissions.referencedata.jaxb.SourcesOfInterest.SourceOfInterest;

public class ReferenceDataXMLUnmarshallTest {

    @Test
    public void shouldUnmarshallCountriesOfBirth() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Countries.class);
        Unmarshaller u = jc.createUnmarshaller();

        File f = new File("src/test/resources/reference_data/countriesOfBirth.xml");
        Countries countries = (Countries) u.unmarshal(f);

        Assert.assertEquals(249, countries.getCountry().size());
        Assert.assertEquals("AD", countries.getCountry().get(0).getCode());
    }
    
    @Test
    @Ignore
    public void createCountrisOfBirthInsertStatement() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Countries.class);
        Unmarshaller u = jc.createUnmarshaller();

        File f = new File("src/test/resources/reference_data/countriesOfBirth.xml");
        Countries countries = (Countries) u.unmarshal(f);

        System.out.println(String.format("INSERT INTO COUNTRIES_TMP (name, code, enabled) VALUES "));
        for (Country cntry : countries.getCountry()) {
            System.out.println(StringEscapeUtils.escapeSql(String.format("(\"%s\", \"%s\", true), ", cntry.getName(), cntry.getCode())));
        }
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
    @Ignore
    public void createDisabilitiesInsertStatement() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Disabilities.class);
        Unmarshaller u = jc.createUnmarshaller();

        File f = new File("src/test/resources/reference_data/disabilities.xml");
        Disabilities disabilities = (Disabilities) u.unmarshal(f);

        System.out.println(String.format("INSERT INTO DISABILITY_TMP (name, code, enabled) VALUES "));
        for (Disability disability : disabilities.getDisability()) {
            System.out.println(StringEscapeUtils.escapeSql(String.format("(\"%s\", \"%s\", true), ", 
                    disability.getName(),
                    disability.getCode())));
        }
    }    
    
    @Test
    public void shouldUnmarshallCountriesOfDomicile() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Domiciles.class);
        Unmarshaller u = jc.createUnmarshaller();

        File f = new File("src/test/resources/reference_data/countriesOfDomicile.xml");
        Domiciles countries = (Domiciles) u.unmarshal(f);

        Assert.assertEquals(232, countries.getDomicile().size());
        Assert.assertEquals("AD", countries.getDomicile().get(0).getCode());
    }
    
    @Test
    @Ignore
    public void createCountriesOfDomicileInsertStatement() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Domiciles.class);
        Unmarshaller u = jc.createUnmarshaller();

        File f = new File("src/test/resources/reference_data/countriesOfDomicile.xml");
        Domiciles domiciles = (Domiciles) u.unmarshal(f);

        System.out.println(String.format("INSERT INTO DOMICILE_TMP (code, name, enabled) VALUES "));
        for (Domicile inst : domiciles.getDomicile()) {
            System.out.println(String.format("(\"%s\", \"%s\", true), ", 
                    mysql_escape_string(inst.getCode()), 
                    mysql_escape_string(inst.getName()))); 
        }
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
    @Ignore
    public void createEthnicitiesInsertStatement() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Ethnicities.class);
        Unmarshaller u = jc.createUnmarshaller();

        File f = new File("src/test/resources/reference_data/ethnicities.xml");
        Ethnicities ethnicities = (Ethnicities) u.unmarshal(f);

        System.out.println(String.format("INSERT INTO ETHNICITY_TMP (name, code, enabled) VALUES "));
        for (Ethnicity ethnicity : ethnicities.getEthnicity()) {
            System.out.println(StringEscapeUtils.escapeSql(String.format("(\"%s\", \"%s\", true), ", 
                    ethnicity.getName(),
                    ethnicity.getCode())));
        }
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
    @Ignore
    public void createInstitutionsInsertStatement() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Institutions.class);
        Unmarshaller u = jc.createUnmarshaller();

        File f = new File("src/test/resources/reference_data/institutions_with_code.xml");
        Institutions institutions = (Institutions) u.unmarshal(f);

        System.out.println(String.format("INSERT INTO INSTITUTION_TMP (code, name, country_name, enabled) VALUES "));
        for (Institution inst : institutions.getInstitution()) {
            System.out.println(String.format("(\"%s\", \"%s\", \"%s\", true), ", 
                    mysql_escape_string(inst.getCode()), 
                    mysql_escape_string(inst.getName()), 
                    mysql_escape_string(inst.getCountry())));
        }
    }
    
    @Test
    public void shouldUnmarshallNationalities() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Nationalities.class);
        Unmarshaller u = jc.createUnmarshaller();

        File f = new File("src/test/resources/reference_data/nationalities.xml");
        Nationalities nationalities = (Nationalities) u.unmarshal(f);

        Assert.assertEquals(228, nationalities.getNationality().size());
        Assert.assertEquals("AD", nationalities.getNationality().get(0).getCode());
    }
    
    @Test
    @Ignore
    public void createNationalitiesInsertStatement() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Nationalities.class);
        Unmarshaller u = jc.createUnmarshaller();

        File f = new File("src/test/resources/reference_data/nationalities.xml");
        Nationalities nationalities = (Nationalities) u.unmarshal(f);

        System.out.println(String.format("INSERT INTO LANGUAGE_TMP (name, code, enabled) VALUES "));
        for (Nationality nationality : nationalities.getNationality()) {
            System.out.println(StringEscapeUtils.escapeSql(String.format("(\"%s\", \"%s\", true), ", 
                    nationality.getName(),
                    nationality.getCode())));
        }
    }
    
    @Test
    public void shouldUnmarshallPrismProgrammes() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(ProgrammeOccurrences.class);
        Unmarshaller u = jc.createUnmarshaller();

        File f = new File("src/test/resources/reference_data/prismProgrammes.xml");
        ProgrammeOccurrences prismProgrammes = (ProgrammeOccurrences) u.unmarshal(f);

        Assert.assertEquals(38, prismProgrammes.getProgrammeOccurrence().size());
        Assert.assertEquals("DDNBENSING09", prismProgrammes.getProgrammeOccurrence().get(0).getProgramme().getCode());
    }
    
    @Test
    @Ignore
    public void createPrismProgrammesInsertStatements() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(ProgrammeOccurrences.class);
        Unmarshaller u = jc.createUnmarshaller();

        File f = new File("src/test/resources/reference_data/prismProgrammes.xml");
        ProgrammeOccurrences prismProgrammes = (ProgrammeOccurrences) u.unmarshal(f);
        
        System.out.println(String.format("INSERT INTO PROGRAM_TMP (code, name, academic_year, start_date, end_date, attendance_code, attendance_name, identifier, enabled) VALUES "));
        System.out.println(prismProgrammes.getProgrammeOccurrence().size());
        for (ProgrammeOccurrence prog : prismProgrammes.getProgrammeOccurrence()) {
            ModeOfAttendance attendance = prog.getModeOfAttendance();
            
            System.out.println(StringEscapeUtils.escapeSql(
                    String.format("(\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", true), ", 
                    prog.getProgramme().getCode(),
                    prog.getProgramme().getName(),
                    prog.getAcademicYear(),
                    prog.getStartDate(),
                    prog.getEndDate(),
                    attendance.getCode(),
                    prog.getIdentifier(),
                    attendance.getName())));
        }
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
    @Ignore
    public void createQualificationsInsertStatement() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Qualifications.class);
        Unmarshaller u = jc.createUnmarshaller();

        File f = new File("src/test/resources/reference_data/qualifications.xml");
        Qualifications qualifications = (Qualifications) u.unmarshal(f);

        System.out.println(String.format("INSERT INTO QUALIFICATION_TYPE_TMP (name, code, enabled) VALUES "));
        for (Qualification qualification : qualifications.getQualification()) {
            System.out.println(StringEscapeUtils.escapeSql(String.format("(\"%s\", \"%s\", true), ", 
                    StringUtils.trim(qualification.getName()),
                    StringUtils.trim(qualification.getCode()))));
        }
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
    
    @Test
    @Ignore
    public void createSourcesOfInterestInsertStatement() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(SourcesOfInterest.class);
        Unmarshaller u = jc.createUnmarshaller();

        File f = new File("src/test/resources/reference_data/sourcesOfInterest.xml");
        SourcesOfInterest sourcesOfInterest = (SourcesOfInterest) u.unmarshal(f);

        System.out.println(String.format("INSERT INTO SOURCES_OF_INTEREST (name, code, enabled) VALUES "));
        for (SourceOfInterest interest : sourcesOfInterest.getSourceOfInterest()) {
            System.out.println(StringEscapeUtils.escapeSql(String.format("(\"%s\", \"%s\", true), ", 
                    StringUtils.trim(interest.getName()),
                    StringUtils.trim(interest.getCode()))));
        }
    }
    
    @Test
    @Ignore
    public void createInstitutionsInsertStatementFromCleanedUpVersion() throws IOException {
        CSVReader reader = new CSVReader(new FileReader("src/main/resources/prism_institution_list_encoded_for_web.csv"));
        List<String[]> myEntries = reader.readAll();
        reader.close();
        
        System.out.println("INSERT INTO INSTITUTION (code, name, domicile_code, enabled) VALUES ");
        
        for (String[] entry : myEntries) {
            System.out.println(String.format("(\"%s\", \"%s\", \"%s\", true), ", entry[0], StringEscapeUtils.unescapeHtml(entry[1]), entry[2]));
        }
    }
    
    public static String mysql_escape_string(String str) {
        if (str.replaceAll("[a-zA-Z0-9_!@#$%^&*()-=+~.;:,\\Q[\\E\\Q]\\E<>{}\\/? ]", "").length() < 1) {
            return str;
        }

        String clean_string = str;
        clean_string = clean_string.replaceAll("\\\\", "\\\\\\\\");
        clean_string = clean_string.replaceAll("\\n", "\\\\n");
        clean_string = clean_string.replaceAll("\\r", "\\\\r");
        clean_string = clean_string.replaceAll("\\t", "\\\\t");
        clean_string = clean_string.replaceAll("\\00", "\\\\0");
        clean_string = clean_string.replaceAll("''", "\\\\''");
        clean_string = clean_string.replaceAll("\\\"", "\\\\\"");

        if (clean_string.replaceAll("[a-zA-Z0-9_!@#$%^&*()-=+~.;:,\\Q[\\E\\Q]\\E<>{}\\/?\\\\\"'' ]", "").length() < 1) {
            return clean_string;
        }
        
        return clean_string;
    }
}
