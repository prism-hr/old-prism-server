package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.PrismConstants.MAX_BATCH_INSERT_SIZE;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareRowsForSqlInsert;

import java.util.List;

import javax.inject.Inject;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.ImportedEntityDAO;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedAgeRange;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.services.helpers.extractors.ImportedEntityExtractor;

import uk.co.alumeni.prism.api.model.imported.request.ImportedEntityRequest;

@Service
@Transactional
public class ImportedEntityService {

    @Inject
    private ImportedEntityDAO importedEntityDAO;

    @Inject
    private EntityService entityService;

    @Inject
    private ApplicationContext applicationContext;

    public <T extends ImportedEntity<?>> T getById(Class<T> clazz, Object id) {
        return entityService.getById(clazz, id);
    }

    public <T extends ImportedEntity<?>> T getByName(Class<T> entityClass, String name) {
        return importedEntityDAO.getByName(entityClass, name);
    }

    public <T extends ImportedEntity<?>> List<T> searchByName(Class<T> entityClass, String searchTerm) {
        return importedEntityDAO.searchByName(entityClass, searchTerm);
    }

    public <T extends ImportedEntity<?>> List<T> getSimilarImportedEntities(Class<T> entityClass, String searchTerm) {
        return importedEntityDAO.getSimilarImportedEntities(entityClass, searchTerm);
    }

    public <T extends ImportedEntity<?>> List<T> getEnabledImportedEntities(PrismImportedEntity prismImportedEntity) {
        return importedEntityDAO.getEnabledImportedEntities(prismImportedEntity);
    }

    @SuppressWarnings("unchecked")
    public <T extends ImportedEntityRequest> void mergeImportedEntities(PrismImportedEntity prismImportedEntity, List<T> representations) {
        importedEntityDAO.disableImportedEntities(prismImportedEntity);
        entityService.flush();
        List<List<T>> definitionBatches = Lists.partition(representations, MAX_BATCH_INSERT_SIZE);
        for (List<T> definitionBatch : definitionBatches) {
            ImportedEntityExtractor<T> extractor = (ImportedEntityExtractor<T>) applicationContext.getBean(prismImportedEntity.getImportInsertExtractor());
            List<String> rows = extractor.extract(prismImportedEntity, definitionBatch, true);
            if (!rows.isEmpty()) {
                entityService.executeBulkInsertUpdate(prismImportedEntity.getImportInsertTable(), prismImportedEntity.getImportInsertColumns(),
                        prepareRowsForSqlInsert(rows), prismImportedEntity.getImportInsertOnDuplicateKeyUpdate());
            }
        }
        entityService.flush();
    }

    public ImportedAgeRange getAgeRange(Institution institution, Integer age) {
        return importedEntityDAO.getAgeRange(institution, age);
    }

    public void deleteImportedEntityTypes() {
        importedEntityDAO.deleteImportedEntityTypes();
    }

}
