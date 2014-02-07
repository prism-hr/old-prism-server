package com.zuehlke.pgadmissions.services.importers;

import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.dao.ProgramFeedDAO;
import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.domain.ImportedObject;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramFeed;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgramType;
import com.zuehlke.pgadmissions.domain.enums.ProgramTypeId;
import com.zuehlke.pgadmissions.exceptions.XMLDataImportException;
import com.zuehlke.pgadmissions.referencedata.adapters.PrismProgrammeAdapter;
import com.zuehlke.pgadmissions.referencedata.v2.jaxb.ProgrammeOccurrences;
import com.zuehlke.pgadmissions.referencedata.v2.jaxb.ProgrammeOccurrences.ProgrammeOccurrence;
import com.zuehlke.pgadmissions.referencedata.v2.jaxb.ProgrammeOccurrences.ProgrammeOccurrence.Programme;

@Service
public class ProgrammesImporter implements IProgrammesImporter {

    private final Logger log = LoggerFactory.getLogger(ProgrammesImporter.class);

    private ApplicationContext applicationContext;
    private final JAXBContext context;
    private final ProgramInstanceDAO programInstanceDAO;
    private final ProgramDAO programDAO;
    private final ProgramFeedDAO programFeedDAO;
    private final ImportService importService;

    private final String user;
    private final String password;

    public ProgrammesImporter() throws JAXBException {
        this(null, null, null, null, null, null, null);
    }

    @Autowired
    public ProgrammesImporter(ApplicationContext applicationContext, ProgramInstanceDAO programDAO, ProgramDAO programDao, ProgramFeedDAO programFeedDAO, ImportService importService,
            @Value("${xml.data.import.prismProgrammes.user}") String user, @Value("${xml.data.import.prismProgrammes.password}") String password)
            throws JAXBException {
        this.applicationContext = applicationContext;
        this.programInstanceDAO = programDAO;
        this.programDAO = programDao;
        this.programFeedDAO = programFeedDAO;
        this.importService = importService;
        this.user = user;
        this.password = password;
        context = JAXBContext.newInstance(ProgrammeOccurrences.class);
    }

    @Override
    public void importData() throws XMLDataImportException {
        IProgrammesImporter thisBean = applicationContext.getBean(IProgrammesImporter.class);
        List<ProgramFeed> programFeeds = thisBean.getProgramFeeds();
        for (ProgramFeed programFeed : programFeeds) {
            log.info("Starting the import from xml file: " + programFeed.getFeedUrl());
            try {
                thisBean.importData(programFeed);
            } catch (Exception e) {
                log.error("Error during the import of programmes file: " + programFeed.getFeedUrl(), e);
            }
        }
    }

    @Override
    @Transactional
    public List<ProgramFeed> getProgramFeeds() {
        return programFeedDAO.getAllProgramFeeds();
    }

    @Override
    @Transactional
    public void importData(ProgramFeed programFeed) throws JAXBException, MalformedURLException {
        String feedUrl = programFeed.getFeedUrl();
        ProgrammeOccurrences programmes = unmarshallXML(feedUrl);
        List<PrismProgrammeAdapter> importData = createAdapter(programmes);
        List<ProgramInstance> currentData = programInstanceDAO.getAllProgramInstances(programFeed);
        List<ProgramInstance> changes = importService.merge(currentData, importData);

        for (ProgramInstance programInstance : changes) {
            programInstanceDAO.save(programInstance);
            Program program = programInstance.getProgram();
            if (program.getId() == null) {
                program.setProgramFeed(programFeed);
                program.setInstitution(programFeed.getInstitution());
                ProgramTypeId programTypeId = ProgramTypeId.findValueFromString(program.getTitle());
                Preconditions.checkNotNull(programTypeId, "Tried to import a program: " + program.getCode() + " with no known type");
                ProgramType programType = programDAO.getProgramTypeById(programTypeId);
                program.setProgramType(programType);
                program.setStudyDuration(programType.getDefaultStudyDuration());
                programDAO.save(program);
            }
        }

        // Update the require ATAS flag in our PRISM domain object
        for (ProgrammeOccurrence programmeOccurence : programmes.getProgrammeOccurrence()) {
            Programme programme = programmeOccurence.getProgramme();
            Program prismProgram = programDAO.getProgramByCode(programme.getCode());
            if (prismProgram != null) {
                prismProgram.setAtasRequired(BooleanUtils.isTrue(programme.isAtasRegistered()));
                programDAO.save(prismProgram);
            }
        }

    }

    private ProgrammeOccurrences unmarshallXML(String fileLocation) throws JAXBException, MalformedURLException {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Authenticator.setDefault(null);
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password.toCharArray());
            }
        });
        ProgrammeOccurrences programmes = (ProgrammeOccurrences) unmarshaller.unmarshal(new URL(fileLocation));
        Authenticator.setDefault(null);
        return programmes;
    }

    private List<PrismProgrammeAdapter> createAdapter(ProgrammeOccurrences programmes) {
        List<PrismProgrammeAdapter> result = new ArrayList<PrismProgrammeAdapter>(programmes.getProgrammeOccurrence().size());
        for (ProgrammeOccurrence programme : programmes.getProgrammeOccurrence()) {
            result.add(new PrismProgrammeAdapter(programme));
        }
        return result;
    }

    @Override
    public Class<? extends ImportedObject> getImportedType() {
        return ProgramInstance.class;
    }

}
