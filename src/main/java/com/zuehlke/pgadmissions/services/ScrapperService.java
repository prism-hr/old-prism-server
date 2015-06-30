package com.zuehlke.pgadmissions.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by felipe on 02/06/2015.
 * This class will query http://search.ucas.com/search with a known URL we worked out we could iterate
 * to get institution ID's based on countries. UK to start with. From UI we saw 16 pages to go through, hence the iteration limit = 17 in the code.
 * <p/>
 */
@Service
public class ScrapperService {
    private static Logger log = LoggerFactory.getLogger(ScrapperService.class);
    //static URL with a parameter at the end indicating which page. We know for UK we've got 16 pages of institutions
    private static String URL = "http://search.ucas.com/search/providers?CountryCode=1%7C2%7C3%7C4%7C5&Feather=7&flt8=2&Vac=1&AvailableIn=2015&Location=united%20kingdom&MaxResults=1000&page=";

    public List<Object> getInstitutionIdsBasedInUK() throws IOException {
        log.debug("getInstitutionIdsBasedInUK() - start method");
        ArrayList<Object> jsonResult = new ArrayList<Object>();
        int counter = 1;
        for (int i = counter; i < 17; i++) {
            String url = URL + i;
            Document doc = Jsoup.connect(url).get();
            Elements e = doc.getElementsByTag("li");
            Iterator<Element> it = e.iterator();
            String jsonElement = "";
            while (it.hasNext()) {
                Element element = it.next();
                if (element.id().startsWith("result-")) {
                    jsonResult.add(jsonElement);
                }
            }
        }
        log.debug("getInstitutionIdsBasedInUK() - finish method");
        return jsonResult;
    }
}
