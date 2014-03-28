package com.zuehlke.pgadmissions.components;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ImportedDataDAO;
import com.zuehlke.pgadmissions.domain.ImportedObject;
import com.zuehlke.pgadmissions.domain.SelfReferringImportedObject;

@Component
public class ImportDataReferenceUpdater {

    private final Logger logger = LoggerFactory.getLogger(ImportDataReferenceUpdater.class);

    @Autowired
    private ImportedDataDAO importedDataDAO;

    @Transactional
    public void updateReferences(Class<? extends ImportedObject> importedType) {
        if (!SelfReferringImportedObject.class.isAssignableFrom(importedType)) {
            return;
        }

        List<ImportedObject> disabledObjects = importedDataDAO.getDisabledImportedObjectsWithoutActiveReference(importedType);
        int counter = 0;
        for (ImportedObject disabledObject : disabledObjects) {
            SelfReferringImportedObject enabledObject = (SelfReferringImportedObject) importedDataDAO.getEnabledVersion(disabledObject);
            if (enabledObject != null) {
                SelfReferringImportedObject disabledSRObject = (SelfReferringImportedObject) disabledObject;
                disabledSRObject.setEnabledObject(enabledObject);
                counter++;
            } else {
                logger.warn("Could not find enabled object of type " + importedType.getSimpleName() + " with code: " + disabledObject.getCode());
            }
        }
        logger.info("" + counter + " disabled import objects of type " + importedType.getSimpleName() + " has been updated to point enable one");
    }

}
