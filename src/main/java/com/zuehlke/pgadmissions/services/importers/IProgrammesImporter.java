package com.zuehlke.pgadmissions.services.importers;

import java.net.MalformedURLException;
import java.util.List;

import javax.xml.bind.JAXBException;

import com.zuehlke.pgadmissions.domain.ProgramFeed;

public interface IProgrammesImporter extends Importer {

    public List<ProgramFeed> getProgramFeeds();

    public void importData(ProgramFeed programFeed) throws JAXBException, MalformedURLException;

}