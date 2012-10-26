package com.zuehlke.pgadmissions.referencedata.adapters;

import java.util.List;

import com.zuehlke.pgadmissions.domain.CodeObject;
import com.zuehlke.pgadmissions.domain.ImportedObject;


public interface ImportData extends CodeObject{
		ImportedObject createDomainObject(List<? extends CodeObject> currentData, List<? extends CodeObject> changes);
}
