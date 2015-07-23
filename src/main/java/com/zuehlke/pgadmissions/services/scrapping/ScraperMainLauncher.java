package com.zuehlke.pgadmissions.services.scrapping;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import uk.co.alumeni.prism.api.model.imported.request.ImportedSubjectAreaRequest;

import java.io.*;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ScraperMainLauncher {

    private static String urlPattern = "https://graph.facebook.com/{0}?access_token={1}";

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Missing args");
            System.exit(1);
        }

        switch (args[0]) {
            case "facebookDefinitions":
                getFacebookDefinitions();
                break;
            case "subjectAreas":
                importSubjectAreas(args);
                break;
            case "institutions":
                InstitutionUcasScraper institutionScraper = new InstitutionUcasScraper();
                institutionScraper.scrape(new OutputStreamWriter(new FileOutputStream(args[1])));
                break;
            case "programs":
                ProgramUcasScraper programScraper = new ProgramUcasScraper();
                programScraper.scrape(new OutputStreamWriter(new FileOutputStream(args[1])));
                break;
            case "applyCodeMappings":
                applyCodeMapping(args[2]);
        }
    }

    private static void importSubjectAreas(String[] args) throws IOException {
        TreeMap<String, ImportedSubjectAreaRequest> subjectAreas = new TreeMap<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(args[1]), Charsets.UTF_8))) {
            String[] line = reader.readNext();
            while (line != null) {
                String jacsCode = line[0];
                String name = line[1];
                String description = line[2];
                String oldJacsCode = Strings.emptyToNull(line[3]);
                Integer ucasId = null;
                if(line.length > 4) {
                    ucasId = Integer.parseInt(Strings.emptyToNull(line[4]));
                }
                subjectAreas.put(jacsCode, new ImportedSubjectAreaRequest(name).withJacsCode(jacsCode)
                        .withDescription(description).withOldJacsCode(oldJacsCode).withUcasId(ucasId));
                line = reader.readNext();
            }
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator jg = jsonFactory.createGenerator(System.out);
        jg.setCodec(objectMapper);
        jg.setPrettyPrinter(new DefaultPrettyPrinter());
        jg.writeStartArray();

        for (String code : subjectAreas.keySet()) {
            ImportedSubjectAreaRequest subjectAreaRequest = subjectAreas.get(code);
            jg.writeObject(subjectAreaRequest);
        }

        jg.writeEndArray();
        jg.close();
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
        try (BufferedReader mapReader = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Users\\Pojebe\\prism\\repo\\prism-server\\docs\\JACS\\JACS_3_to_2_aliases.txt"), Charsets.UTF_8))) {
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
                subjectAreas.put(code, new ImportedSubjectAreaRequest(name).withJacsCode(code).withDescription(description));
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
}
