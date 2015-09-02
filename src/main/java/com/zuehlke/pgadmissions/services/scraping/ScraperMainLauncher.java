package com.zuehlke.pgadmissions.services.scraping;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedInstitutionImportDTO;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedProgramImportDTO;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedSubjectAreaImportDTO;
import uk.co.alumeni.prism.api.model.imported.request.ImportedSubjectAreaRequest;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ScraperMainLauncher {

    private static String urlPattern = "https://graph.facebook.com/{0}?access_token={1}";

    public static void main(String[] args) throws IOException, URISyntaxException {
        if (args.length < 1) {
            System.err.println("Missing args");
            System.exit(1);
        }

        ProgramUcasScraper programScraper = new ProgramUcasScraper();
        switch (args[0]) {
            case "facebookDefinitions":
                getFacebookDefinitions();
                break;
            case "subjectAreas":
                importSubjectAreas(args[1]);
                break;
            case "institutions":
                InstitutionUcasScraper institutionScraper = new InstitutionUcasScraper();
                try (InputStreamReader initialDataReader = new InputStreamReader(new FileInputStream(args[1]), Charsets.UTF_8);
                     OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(args[2]), Charsets.UTF_8)) {
                    institutionScraper.scrape(initialDataReader, writer);
                }
                break;
            case "programs":
                programScraper.scrape(new OutputStreamWriter(new FileOutputStream(args[1]), Charsets.UTF_8));
                break;
            case "processPrograms":
                programScraper.processProgramDescriptors(new InputStreamReader(new FileInputStream(args[1]), Charsets.UTF_8),
                        new OutputStreamWriter(new FileOutputStream(args[2]), Charsets.UTF_8));
                break;
            case "applyCodeMappings":
                applyCodeMapping(args[1]);
                break;
            case "institutionHesaIds":
                try (InputStreamReader hesaDataReader = new InputStreamReader(new FileInputStream(args[1]), Charsets.UTF_8);
                     InputStreamReader institutionReader = new InputStreamReader(new FileInputStream(args[2]), Charsets.UTF_8);
                     OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(args[3]), Charsets.UTF_8)) {
                    associateInstitutionsWithHesaIds(hesaDataReader, institutionReader, writer);
                }
                break;
            case "campuses":
                try (InputStreamReader institutionReader = new InputStreamReader(new FileInputStream(args[1]), Charsets.UTF_8);
                     InputStreamReader programReader = new InputStreamReader(new FileInputStream(args[2]), Charsets.UTF_8);
                     OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(args[3]), Charsets.UTF_8)) {
                    extractInstitutionsAndCampuses(institutionReader, programReader, writer);
                }
                break;
        }

    }

    private static void extractInstitutionsAndCampuses(InputStreamReader institutionReader, InputStreamReader programReader, OutputStreamWriter writer) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, ImportedInstitutionImportDTO.class);
        List<ImportedInstitutionImportDTO> institutionList = objectMapper.readValue(institutionReader, listType);
        institutionList = institutionList.stream().filter(i -> i.getUcasIds() != null).collect(Collectors.toList());
        Map<Integer, ImportedInstitutionImportDTO> institutions = new HashMap<>();
        for(ImportedInstitutionImportDTO institution : institutionList) {
            for (Integer ucasId : institution.getUcasIds()) {
                institutions.put(ucasId, institution);
            }
        }

        listType = objectMapper.getTypeFactory().constructCollectionType(List.class, ProgramUcasScraper.ImportedProgramScrapeDescriptor.class);
        List<ProgramUcasScraper.ImportedProgramScrapeDescriptor> programs = objectMapper.readValue(programReader, listType);

        TreeMap<Integer, Multiset<String>> campusesByInstitutions = new TreeMap<>();
        for (ProgramUcasScraper.ImportedProgramScrapeDescriptor programDescriptor : programs) {
            ImportedProgramImportDTO program = programDescriptor.getProgram();
            Integer institutionId = program.getInstitution();
            if (!campusesByInstitutions.containsKey(institutionId)) {
                campusesByInstitutions.put(institutionId, HashMultiset.create());
            }
            Multiset<String> campuses = campusesByInstitutions.get(institutionId);
            campuses.addAll(program.getCampuses());
        }

        CSVWriter campusesWriter = new CSVWriter(writer, ';');
        for (Integer institutionId : institutions.keySet()) {
            ImportedInstitutionImportDTO institution = institutions.get(institutionId);
            Multiset<String> campuses = campusesByInstitutions.get(institutionId);
            if (campuses != null) {
                for (String campus : campuses.elementSet()) {
                    campusesWriter.writeNext(new String[]{institution.getName(), campus, Integer.toString(campuses.count(campus))});
                }
            }
        }
        campusesWriter.flush();
    }

    private static void importSubjectAreas(String filename) throws IOException {
        TreeMap<String, ImportedSubjectAreaImportDTO> subjectAreas = new TreeMap<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(filename), Charsets.UTF_8))) {
            String[] line = reader.readNext();
            while (line != null) {
                Integer id = Integer.parseInt(line[0]);
                String jacsCode = line[1];
                String jacsCodeOld = Strings.emptyToNull(line[2]);
                String name = line[3];
                String description = line[4];
                Integer ucasSubject = Integer.parseInt(line[5]);
                Integer parent = Integer.parseInt(Strings.emptyToNull(line[6]));

                subjectAreas.put(jacsCode, new ImportedSubjectAreaImportDTO().withId(id).withJacsCode(jacsCode).withJacsCodeOld(jacsCodeOld).withName(name)
                        .withDescription(description).withUcasSubject(ucasSubject).withParent(parent));
                line = reader.readNext();
            }
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator jsonGenerator = jsonFactory.createGenerator(System.out);
        jsonGenerator.setCodec(objectMapper);
        jsonGenerator.setPrettyPrinter(new DefaultPrettyPrinter());
        jsonGenerator.writeStartArray();

        for (String code : subjectAreas.keySet()) {
            ImportedSubjectAreaRequest subjectAreaRequest = subjectAreas.get(code);
            jsonGenerator.writeObject(subjectAreaRequest);
        }

        jsonGenerator.writeEndArray();
        jsonGenerator.close();
    }

    private static void getFacebookDefinitions() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = br.readLine();
        while (line != null) {
            if (line.isEmpty()) {
                System.out.println();
            } else {
                ObjectMapper m = new ObjectMapper();
                String accessToken = "CAAFC28vPcMgBAK739J9JsDyaIZBIOUy72ZBPyekArRAKubOxyQ7VLp2pVuVaYa4qbegX1ADdZBnKc3XuphUBgzlB1JRKQ9KL7FCvsU4mO5EZBYIqGBURJoT5HxCxYbc9HXMKWT6tYZAarBpNNrr0Szce56AwGeHX2nZBjBfURzqSXIokINRKQxije3cqqsBKeZCsVh6wfUL5VCtGnMLhZBmO";
                String url = MessageFormat.format(urlPattern, line, accessToken);
                JsonNode rootNode = m.readTree(new URL(url));
                JsonNode name = rootNode.get("name");
                System.out.println(name);
            }

            line = br.readLine();
        }
    }

    private static void applyCodeMapping(String filename) throws IOException {
        TreeMap<String, ImportedSubjectAreaRequest> subjectAreas = new TreeMap<>();
        Map<String, String> codeMap = new HashMap<>();
        try (BufferedReader mapReader = new BufferedReader(new InputStreamReader(new FileInputStream(
                "docs/JACS/JACS_3_to_2_aliases.txt"), Charsets.UTF_8))) {
            String line = mapReader.readLine();
            while (line != null) {
                String[] split = line.split(" -> ");
                codeMap.put(split[0], split[1]);
                line = mapReader.readLine();
            }

        }

        try (CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(filename), Charsets.UTF_8))) {
            String[] line = reader.readNext();
            while (line != null) {
                String code = line[0];
                String name = line[1];
                String description = line[2];
                subjectAreas.put(code, new ImportedSubjectAreaRequest().withName(name).withJacsCode(code).withDescription(description));
                line = reader.readNext();
            }
        }

        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(filename), Charsets.UTF_8))) {
            for (String code : subjectAreas.keySet()) {
                ImportedSubjectAreaRequest area = subjectAreas.get(code);
                writer.writeNext(new String[]{code, area.getName(), Strings.nullToEmpty(area.getDescription()), Strings.nullToEmpty(codeMap.get(code))});
            }
        }
    }

    private static void associateInstitutionsWithHesaIds(InputStreamReader hesaDataReader, InputStreamReader institutionReader, OutputStreamWriter writer) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, ImportedInstitutionImportDTO.class);
        List<ImportedInstitutionImportDTO> institutions = objectMapper.readValue(institutionReader, listType);

        CSVReader hesaCsvReader = new CSVReader(hesaDataReader, ';');
        hesaCsvReader.readNext();
        hesaCsvReader.readNext();
        String[] line = hesaCsvReader.readNext();
        while (line != null) {
            if (!line[0].isEmpty()) {
                String[] arr = line[0].split(" ", 2);
                Integer hesaId = Integer.parseInt(arr[0]);
                String hesaName = arr[1];
                List<ImportedInstitutionImportDTO> matchedInstitutions = institutions.stream()
                        .filter(i -> i.getDomicile() == 527)
                        .filter(i -> {
                            String name = i.getName();
                            return hesaName.equals(name) || hesaName.contains(name) || name.contains(hesaName);
                        }).collect(Collectors.toList());

                if (matchedInstitutions.isEmpty()) {
                    System.out.println("Could not match institution with name: " + hesaName);
                } else if (matchedInstitutions.size() > 1) {
                    System.out.println(
                            "To many matched institutions for: " + hesaName + ", matches: " + matchedInstitutions.stream().map(i -> i.getName()).collect(Collectors.joining(",")));
                } else {
                    matchedInstitutions.get(0).setHesaId(hesaId);
                }
            }
            line = hesaCsvReader.readNext();
        }

        hesaCsvReader.close();

        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator jg = jsonFactory.createGenerator(writer);
        jg.setCodec(objectMapper);
        jg.setPrettyPrinter(new DefaultPrettyPrinter());
        jg.writeStartArray();

        for (ImportedInstitutionImportDTO importedInstitutionImportDTO : institutions) {
            jg.writeObject(importedInstitutionImportDTO);
        }

        jg.writeEndArray();
        jg.close();
    }

    private static final class CampusDescriptor {

        private String name;

        private Integer programsCount = 0;

        public CampusDescriptor(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public Integer getProgramsCount() {
            return programsCount;
        }

        public void incrementProgramCount() {
            programsCount++;
        }
    }
}
