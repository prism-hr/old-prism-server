package com.zuehlke.pgadmissions.services.importers;

import java.net.MalformedURLException;
import java.util.List;

import javax.xml.bind.JAXBException;

import com.zuehlke.pgadmissions.domain.ProgramImport;

public interface IProgrammesImporter extends Importer {

    public List<ProgramImport> getProgramFeeds();

    public void importData(ProgramImport programFeed) throws JAXBException, MalformedURLException;

}