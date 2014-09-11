package com.zuehlke.pgadmissions.services.helpers;

import java.io.IOException;

import com.zuehlke.pgadmissions.exceptions.DataImportException;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;

public abstract class AbstractServiceHelper {

    public abstract void execute() throws DeduplicationException, DataImportException, IOException;
    
}
