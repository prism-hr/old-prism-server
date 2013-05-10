package com.zuehlke.pgadmissions.services.exporters;

import java.net.URL;

import javax.xml.namespace.QName;

import uk.ac.ucl.isd.registry.studentrecordsdata_v1.AdmissionsApplicationsServiceV10;
import uk.ac.ucl.isd.registry.studentrecordsdata_v1.AdmissionsApplicationsServiceV10_Service;

public class WebServiceFactory {

    public WebServiceFactory() {
    }
    
    public AdmissionsApplicationsServiceV10 createAdmissionsApplicationsService() {
        URL wsdl = getClass().getResource("/xsd/export.v2/admissionsapplicationsservice.wsdl");
        AdmissionsApplicationsServiceV10_Service service = new AdmissionsApplicationsServiceV10_Service(wsdl, new QName("http://ucl.ac.uk/isd/registry/studentrecordsdata_V1", "admissionsApplicationsService_v1_0"));
        return service.getAdmissionsApplicationsServiceSoap11V10();
    }
}
