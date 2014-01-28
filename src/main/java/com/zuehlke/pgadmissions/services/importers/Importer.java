package com.zuehlke.pgadmissions.services.importers;

import com.zuehlke.pgadmissions.domain.ImportedObject;
import com.zuehlke.pgadmissions.exceptions.XMLDataImportException;

public interface Importer {
    
	void importData() throws XMLDataImportException;

    Class<? extends ImportedObject> getImportedType();
	
	
}
