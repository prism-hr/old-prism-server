package com.zuehlke.pgadmissions.scripts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zuehlke.pgadmissions.services.ScraperService;

import java.io.*;
import java.net.URL;
import java.text.MessageFormat;

public class ScraperLauncher {

    private static String urlPattern = "https://graph.facebook.com/{0}?access_token={1}";

    public static void main(String[] args) throws IOException {
        if(args.length < 2 ) {
            System.err.println("Missing args");
            System.exit(1);
        }

        ScraperService scraperService = new ScraperService();
        switch (args[0]) {
            case "facebookDefinitions":
                getFacebookDefinitions();
                break;
            case "institutions":
                scraperService.scrapeInstitutionsByIteratingIds(new OutputStreamWriter(new FileOutputStream(args[1])));
                break;
            case "programs":
                scraperService.scrapePrograms("2015", new OutputStreamWriter(new FileOutputStream(args[1])));
        }
    }

    private static void getFacebookDefinitions() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = br.readLine();
        while(line != null) {
            if(line.isEmpty()) {
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
}
