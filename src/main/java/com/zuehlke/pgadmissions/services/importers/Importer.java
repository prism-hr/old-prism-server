package com.zuehlke.pgadmissions.services.importers;

import javax.xml.bind.JAXBException;

public interface Importer {
	void importData() throws JAXBException;
}
