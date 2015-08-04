package com.zuehlke.pgadmissions.services.scraping;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedInstitutionImportDTO;

@Service
public class InstitutionUcasScraper {

    public void scrape(Reader initialDataReader, Writer writer) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, ImportedInstitutionImportDTO.class);
        List<ImportedInstitutionImportDTO> institutions = objectMapper.readValue(initialDataReader, listType);
        List<ImportedInstitutionImportDTO> nonUcasInstitutions = institutions.stream().filter(i -> i.getUcasId() == null).collect(Collectors.toList());
        TreeMap<Integer, ImportedInstitutionImportDTO> ucasInstitutions = new TreeMap<>(institutions.stream()
                .filter(i -> i.getUcasId() != null)
                .collect(Collectors.toMap(o -> ((ImportedInstitutionImportDTO) o).getUcasId(), i -> i)));

        TreeMap<Integer, ImportedInstitutionImportDTO> newInstitutionMap = new TreeMap<>();
        Set<Integer> encounteredUcasIds = new HashSet<>();

        for (int ucasId = 1; ucasId < 3500; ucasId++) {
            if (ucasId % 100 == 0) {
                System.out.println("Scraping " + ucasId);
            }

            Document html = Jsoup.connect("http://search.ucas.com/provider/" + ucasId).get();
            Element nameElement = html.getElementsByClass("shortname").first();
            if (nameElement != null) {
                encounteredUcasIds.add(ucasId);
                String name = nameElement.text();
                ImportedInstitutionImportDTO institution = ucasInstitutions.get(ucasId);
                if (institution == null) {
                    institution = new ImportedInstitutionImportDTO(name).withUcasId(ucasId);
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

}
