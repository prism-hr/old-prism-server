package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.exceptions.DataImportException;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.services.ApplicationExportService;
import com.zuehlke.pgadmissions.services.ApplicationService;

@Component
public class ApplicationExportServiceHelper extends AbstractServiceHelper {

    @Autowired
    private ApplicationService applicationService;
    
    @Autowired
    private ApplicationExportService applicationExportService;
    
    @Override
    public void execute() throws DeduplicationException, DataImportException, IOException, IllegalAccessException, InvocationTargetException,
            NoSuchMethodException, JAXBException {
        List<Integer> applicationIds = applicationService.getApplicationsForExport();
        for (Integer applicationId : applicationIds) {
            applicationExportService.submitExportRequest(applicationId);
        }
    }

}
