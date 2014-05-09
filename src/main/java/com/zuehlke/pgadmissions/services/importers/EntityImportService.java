package com.zuehlke.pgadmissions.services.importers;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.List;

import javax.transaction.Transactional;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConstructorUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.zuehlke.pgadmissions.dao.ImportedEntityDAO;
import com.zuehlke.pgadmissions.domain.ImportedEntity;
import com.zuehlke.pgadmissions.domain.ImportedEntityFeed;
import com.zuehlke.pgadmissions.exceptions.XMLDataImportException;
import com.zuehlke.pgadmissions.mail.MailSendingService;
import com.zuehlke.pgadmissions.referencedata.v2.jaxb.Disabilities;
import com.zuehlke.pgadmissions.referencedata.v2.jaxb.Disabilities.Disability;

@Service
public class EntityImportService {

    private static final Logger log = LoggerFactory.getLogger(EntityImportService.class);

    @Autowired
    private ImportedEntityDAO importedEntityDAO;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MailSendingService mailSendingService;

    @SuppressWarnings("unchecked")
    public void importEntities(ImportedEntityFeed importedEntityFeed) throws XMLDataImportException {
        EntityImportService thisBean = applicationContext.getBean(EntityImportService.class);
        String fileLocation = importedEntityFeed.getLocation();
        log.info("Starting the import from file: " + fileLocation);

        try {
            List<Disability> unmarshalled = thisBean.unmarshall(importedEntityFeed);

            Class<ImportedEntity> entityClass = (Class<ImportedEntity>) importedEntityFeed.getImportedEntityType().getEntityClass();
            ImportEntityConverter<ImportedEntity> entityConverter = ImportEntityConverter.create(entityClass);

            Iterable<ImportedEntity> newEntities = Iterables.transform(unmarshalled, entityConverter);

            thisBean.mergeImportedEntities(entityClass, newEntities);
        } catch (Exception e) {
            throw new XMLDataImportException("Error during the import of file: " + fileLocation, e);
        }
    }

    protected List<Disability> unmarshall(final ImportedEntityFeed importedEntityFeed) throws Exception {
        try {
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(importedEntityFeed.getUsername(), importedEntityFeed.getPassword().toCharArray());
                }
            });

            URL fileUrl = new DefaultResourceLoader().getResource(importedEntityFeed.getLocation()).getURL();
            JAXBContext jaxbContext = JAXBContext.newInstance(importedEntityFeed.getImportedEntityType().getJaxbClass());
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Disabilities unmarshaled = (Disabilities) unmarshaller.unmarshal(fileUrl);
            return unmarshaled.getDisability();
        } finally {
            Authenticator.setDefault(null);
        }
    }

    protected void mergeImportedEntities(Class<ImportedEntity> entityClass, Iterable<ImportedEntity> entities) {
        EntityImportService thisBean = applicationContext.getBean(EntityImportService.class);
        thisBean.disableAllEntities(entityClass);
        for (ImportedEntity entity : entities) {
            try {
                thisBean.attemptInsert(entity);
            } catch (ConstraintViolationException e) {
                try {
                    thisBean.attemptUpdateByCode(entityClass, entity);
                } catch (Exception e1) {
                    try {
                        thisBean.attemptUpdateByName(entityClass, entity);
                    } catch (Exception e2) {
                        log.error("Couldn't update entity by code", e1);
                        log.error("Couldn't update entity by name", e2);
                        mailSendingService.sendImportErrorMessage("Could not merge: " + entity + " due to a data integrity problem in the import feed.");
                    }
                }
            }
        }
    }

    @Transactional
    public void disableAllEntities(Class<ImportedEntity> entityClass) {
        importedEntityDAO.disableAllEntities(entityClass);
    }

    @Transactional
    public void attemptInsert(ImportedEntity entity) {
        importedEntityDAO.save(entity);
    }

    @Transactional
    public void attemptUpdateByCode(Class<ImportedEntity> entityClass, ImportedEntity entity) {
        ImportedEntity entityByCode = importedEntityDAO.getByCode(entityClass, entity.getCode());
        entityByCode.setName(entity.getName());
        entityByCode.setEnabled(true);
    }

    @Transactional
    public void attemptUpdateByName(Class<ImportedEntity> entityClass, ImportedEntity entity) {
        ImportedEntity entityByName = importedEntityDAO.getByName(entityClass, entity.getName());
        entityByName.setCode(entity.getCode());
        entityByName.setEnabled(true);
    }

    public List<ImportedEntityFeed> getImportedEntityFeeds() {
        return importedEntityDAO.getImportedEntityFeeds();
    }

    protected static class ImportEntityConverter<E extends ImportedEntity> implements Function<Object, E> {

        private Class<E> importedEntityType;

        private ImportEntityConverter(Class<E> importedEntityType) {
            this.importedEntityType = importedEntityType;
        }

        public static <E extends ImportedEntity> ImportEntityConverter<E> create(Class<E> importedEntityType) {
            return new ImportEntityConverter<E>(importedEntityType);
        }

        @SuppressWarnings("unchecked")
        @Override
        public E apply(Object input) {
            try {
                E importedEntity = (E) ConstructorUtils.invokeConstructor(importedEntityType, null);
                String name = BeanUtils.getSimpleProperty(input, "name");
                String code = BeanUtils.getSimpleProperty(input, "code");
                importedEntity.setName(name);
                importedEntity.setCode(code);
                importedEntity.setEnabled(true);
                return importedEntity;
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
    }

}
