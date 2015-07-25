package com.zuehlke.pgadmissions.services.scraping;

import java.io.Writer;

import com.zuehlke.pgadmissions.exceptions.ScrapingException;

public interface ImportedDataScraper {

    void scrape(Writer writer) throws ScrapingException;
}
