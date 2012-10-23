package com.zuehlke.pgadmissions.referencedata.adapters;

import com.zuehlke.pgadmissions.domain.CodeObject;
import com.zuehlke.pgadmissions.domain.ImportedObject;


public interface ImportData extends CodeObject{
		ImportedObject createDomainObject();
		boolean equalAttributes(ImportedObject other);
}
