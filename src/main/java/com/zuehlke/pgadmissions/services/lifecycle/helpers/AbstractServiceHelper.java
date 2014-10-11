package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.xml.bind.JAXBException;

import com.zuehlke.pgadmissions.exceptions.DataImportException;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;

public abstract class AbstractServiceHelper {

    public abstract void execute() throws DeduplicationException, DataImportException, IOException, IllegalAccessException, InvocationTargetException,
            NoSuchMethodException, JAXBException;

}
