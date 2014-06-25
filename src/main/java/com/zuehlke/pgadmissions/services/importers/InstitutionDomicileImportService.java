package com.zuehlke.pgadmissions.services.importers;

import com.zuehlke.pgadmissions.dao.ImportedEntityDAO;
import com.zuehlke.pgadmissions.domain.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.InstitutionDomicileRegion;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.exceptions.XMLDataImportException;
import com.zuehlke.pgadmissions.iso.jaxb.CountryCodesType;
import com.zuehlke.pgadmissions.iso.jaxb.CountryType;
import com.zuehlke.pgadmissions.iso.jaxb.FullNameType;
import com.zuehlke.pgadmissions.iso.jaxb.ShortNameType;
import com.zuehlke.pgadmissions.mail.MailService;
import com.zuehlke.pgadmissions.services.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import java.net.Authenticator;
import java.net.URL;
import java.util.List;

@Service
public class InstitutionDomicileImportService {

    private static final Logger log = LoggerFactory.getLogger(InstitutionDomicileImportService.class);

    private static DateTimeFormatter dtFormatter = DateTimeFormat.forPattern("dd-MMM-yy");

    @Autowired
    private ImportedEntityDAO importedEntityDAO;

    @Autowired
    private EntityService entityService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private ProgramInstanceService programInstanceService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MailService mailSendingService;

    @Autowired
    private RoleService roleService;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void importEntities(String fileLocation) throws XMLDataImportException {
        InstitutionDomicileImportService thisBean = applicationContext.getBean(InstitutionDomicileImportService.class);
        log.info("Starting the import from file: " + fileLocation);

        try {
            List<CountryType> unmarshalled = thisBean.unmarshall(fileLocation);

            thisBean.mergeDomiciles(unmarshalled);
        } catch (Exception e) {
            throw new XMLDataImportException("Error during the import of file: " + fileLocation, e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<CountryType> unmarshall(final String fileLocation) throws Exception {
        try {
            URL fileUrl = new DefaultResourceLoader().getResource(fileLocation).getURL();
            JAXBContext jaxbContext = JAXBContext.newInstance(CountryCodesType.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement unmarshaled = (JAXBElement) unmarshaller.unmarshal(fileUrl);
            CountryCodesType countryCodes = (CountryCodesType) unmarshaled.getValue();
            return countryCodes.getCountry();
        } finally {
            Authenticator.setDefault(null);
        }
    }

    public void mergeDomiciles(List<CountryType> countries) throws XMLDataImportException {
        InstitutionDomicileImportService thisBean = applicationContext.getBean(InstitutionDomicileImportService.class);

        thisBean.disableAllDomicilesAndRegions();

        for (CountryType country : countries) {
            String status = null;
            String countryName = null;
            String alpha3Code = null;
            for (JAXBElement<?> element : country.getAlpha2CodeOrAlpha3CodeOrNumericCode()) {
                String elementName = element.getName().getLocalPart();
                if(elementName.equals("status")) {
                    status = (String) element.getValue();
                } else if(elementName.equals("short-name")) {
                    ShortNameType shortName = (ShortNameType) element.getValue();
                    if(shortName.getLang3Code().equals("eng")) {
                        countryName = shortName.getValue();
                    }
                } else if(elementName.equals("alpha-3-code")) {
                    alpha3Code = (String) element.getValue();
                }
            }

            if(status.equals("exceptionally-reserved") || status.equals("indeterminately-reserved")) {
                continue;
            }

            InstitutionDomicile institutionDomicile = new InstitutionDomicile().withId(alpha3Code).withName(countryName).withEnabled(true);
            entityService.merge(institutionDomicile);
            
        }
    }

    @Transactional
    public void disableAllDomicilesAndRegions() {
        importedEntityDAO.disableAllEntities(InstitutionDomicile.class);
        importedEntityDAO.disableAllEntities(InstitutionDomicileRegion.class);
    }

    @Transactional
    public void attemptInsert(Object entity) {
        entityService.save(entity);
    }

    @Transactional
    public void attemptUpdate(ProgramInstance programInstance) {
        ProgramInstance persistentProgramInstance = programInstanceService.getByProgramAndAcademicYearAndStudyOption(programInstance.getProgram(),
                programInstance.getAcademicYear(), programInstance.getStudyOption());
        persistentProgramInstance.setIdentifier(programInstance.getIdentifier());
        persistentProgramInstance.setApplicationStartDate(programInstance.getApplicationStartDate());
        persistentProgramInstance.setApplicationDeadline(programInstance.getApplicationDeadline());
        persistentProgramInstance.setEnabled(true);
    }

}
