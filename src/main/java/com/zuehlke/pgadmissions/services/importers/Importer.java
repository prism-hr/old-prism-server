package com.zuehlke.pgadmissions.services.importers;

import javax.xml.bind.JAXBException;

import org.springframework.scheduling.annotation.Async;

public interface Importer {
	@Async
	void importData() throws JAXBException;
}
