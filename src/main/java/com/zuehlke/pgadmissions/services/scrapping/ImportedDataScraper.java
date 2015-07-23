package com.zuehlke.pgadmissions.services.scrapping;

import com.zuehlke.pgadmissions.exceptions.ScrapingException;

import java.io.Writer;

public interface ImportedDataScraper {

    void scrape(Writer writer) throws ScrapingException;
}
