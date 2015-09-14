package com.zuehlke.pgadmissions.services.scraping;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.rest.dto.AddressAdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedAdvertDomicileDTO;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedInstitutionImportDTO;

@Service
public class InstitutionUcasScraper {

    private static Logger logger = LoggerFactory.getLogger(InstitutionUcasScraper.class);

    public void scrape(Reader initialDataReader, Writer writer) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, ImportedInstitutionImportDTO.class);
        List<ImportedInstitutionImportDTO> institutions = objectMapper.readValue(initialDataReader, listType);
        List<ImportedInstitutionImportDTO> nonUcasInstitutions = institutions.stream().filter(i -> i.getUcasIds() == null).collect(Collectors.toList());
        List<ImportedInstitutionImportDTO> ucasInstitutionList = institutions.stream().filter(i -> i.getUcasIds() != null).collect(Collectors.toList());
        TreeMap<Integer, ImportedInstitutionImportDTO> ucasInstitutions = new TreeMap<>();
        for (ImportedInstitutionImportDTO ucasInstitution : ucasInstitutionList) {
            for (Integer ucasId : ucasInstitution.getUcasIds()) {
                ucasInstitutions.put(ucasId, ucasInstitution);
            }
        }

        TreeMap<Integer, ImportedInstitutionImportDTO> newInstitutionMap = new TreeMap<>();
        Set<Integer> encounteredUcasIds = new HashSet<>();

        for (int ucasId = 1; ucasId < 3500; ucasId++) {
            if (ucasId % 100 == 0) {
                System.out.println("Scraping " + ucasId);
            }

            Document html = getInstitutionHtmlDocument(ucasId);
            Element nameElement = html.getElementsByClass("shortname").first();
            if (nameElement != null) {
                encounteredUcasIds.add(ucasId);
                String name = nameElement.text();
                ImportedInstitutionImportDTO institution = ucasInstitutions.get(ucasId);
                if (institution == null) {
                    institution = new ImportedInstitutionImportDTO(name).withUcasIds(Collections.singletonList(ucasId));
                    newInstitutionMap.put(ucasId, institution);
                }
                Element numberOfStudentsElement = html.getElementsByClass("numberofstudents").first();
                if (numberOfStudentsElement != null) {
                    String numberOfStudentsText = numberOfStudentsElement.getElementsByTag("p").first().text();
                    int studentsNumber = Integer.parseInt(numberOfStudentsText.replace("\u00a0", " ").trim());
                    institution.setStudentsNumber(studentsNumber);
                } else {
                    System.out.println("No students number for " + ucasId + ": " + name);
                }
            } else {
                Element noFoundElement = html.getElementsByClass("details_notfound").first();
                if (noFoundElement == null) {
                    throw new RuntimeException("Unexpected page for ID: " + ucasId);
                }
            }
        }

        System.out.println("New institutions:");
        newInstitutionMap.forEach((ucasId, institution) -> System.out.println("" + ucasId + ": " + institution.getName()));
        System.out.println("Removed institutions (retained in resulted file): " + Sets.difference(ucasInstitutions.keySet(), encounteredUcasIds));

        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator jg = jsonFactory.createGenerator(writer);
        jg.setCodec(objectMapper);
        jg.setPrettyPrinter(new DefaultPrettyPrinter());
        jg.writeStartArray();

        List<ImportedInstitutionImportDTO> finalInstitutions = nonUcasInstitutions;
        finalInstitutions.addAll(ucasInstitutions.values());

        for (ImportedInstitutionImportDTO importedInstitutionImportDTO : finalInstitutions) {
            jg.writeObject(importedInstitutionImportDTO);
        }

        jg.writeEndArray();
        jg.close();
    }

    public UcasInstitutionData getInstitutionData(List<Integer> ucasIds) {
        Integer ucasId = ucasIds.get(0);
        Document document = null;
        try {
            document = getInstitutionHtmlDocument(ucasId);
        } catch (IOException e) {
            logger.error("Could not read UCAS page for institution ID: " + ucasId);
        }

        AddressAdvertDTO addressDTO = null;
        String telephone = null;
        String homepage = null;

        Element addressElement = document.getElementsByClass("provcontactaddress").first();
        if (addressElement != null) {
            addressDTO = new AddressAdvertDTO();
            String countryString = Optional.ofNullable(addressElement.getElementById("country")).map(e -> e.text()).orElse(null);
            addressDTO.setAddressLine1(Optional.ofNullable(addressElement.getElementById("street")).map(e -> e.text()).orElse(null));
            Element townElement = ObjectUtils.firstNonNull(addressElement.getElementById("town"), addressElement.getElementById("locality"),
                    addressElement.getElementById("county"));
            addressDTO.setAddressTown(Optional.ofNullable(townElement).map(e -> e.text()).orElse(null));
            addressDTO.setAddressCode(Optional.ofNullable(addressElement.getElementById("postCode")).map(e -> e.text()).orElse(null));
            addressDTO.setAddressRegion(Optional.ofNullable(addressElement.getElementById("county")).map(e -> e.text()).orElse(null));
            addressDTO.setDomicile(new ImportedAdvertDomicileDTO().withName(countryString));
        }

        Element contactElement = document.getElementsByClass("provider_details_contact").first();
        if (contactElement != null) {
            telephone = Optional.ofNullable(contactElement.select("li.provider_contact_tel").first()).map(e -> e.getElementsByTag("span").last().text()).orElse(null);
            homepage = Optional.ofNullable(contactElement.select("li.provider_contact_web").first()).map(e -> e.getElementsByTag("a").first().text()).orElse(null);
        }

        String summary = Optional.ofNullable(document.getElementById("marketing")).map(e -> e.text()).orElse(null);

        return new UcasInstitutionData(addressDTO, telephone, homepage, summary);
    }

    private Document getInstitutionHtmlDocument(int ucasId) throws IOException {
        return Jsoup.connect("http://search.ucas.com/provider/" + ucasId).get();
    }

    public static class UcasInstitutionData {

        private AddressAdvertDTO address;

        private String telephone;

        private String homepage;

        private String summary;

        public UcasInstitutionData(AddressAdvertDTO address, String telephone, String homepage, String summary) {
            this.address = address;
            this.telephone = telephone;
            this.homepage = homepage;
            this.summary = summary;
        }

        public AddressAdvertDTO getAddress() {
            return address;
        }

        public String getTelephone() {
            return telephone;
        }

        public String getHomepage() {
            return homepage;
        }

        public String getSummary() {
            return summary;
        }
    }

}
