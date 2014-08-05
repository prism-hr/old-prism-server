package com.zuehlke.pgadmissions.services.importers;

import java.io.InputStreamReader;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import au.com.bytecode.opencsv.CSVReader;

import com.google.common.base.Charsets;
import com.zuehlke.pgadmissions.dao.ImportedEntityDAO;
import com.zuehlke.pgadmissions.domain.OpportunityCategory;
import com.zuehlke.pgadmissions.exceptions.DataImportException;
import com.zuehlke.pgadmissions.services.EntityService;

@Service
public class AdvertCategoryImportService {

    private static final Logger log = LoggerFactory.getLogger(AdvertCategoryImportService.class);

    @Value("${import.advertCategory.location}")
    private String importLocation;
    
    @Autowired
    private ImportedEntityDAO importedEntityDAO;

    @Autowired
    private EntityService entityService;

    @Autowired
    private ApplicationContext applicationContext;

    public void importEntities() throws DataImportException {
        AdvertCategoryImportService thisBean = applicationContext.getBean(AdvertCategoryImportService.class);
        log.info("Starting the import from file: " + importLocation);

        try {
            URL fileUrl = new DefaultResourceLoader().getResource(importLocation).getURL();
            CSVReader reader = new CSVReader(new InputStreamReader(fileUrl.openStream(), Charsets.UTF_8));

            thisBean.mergeCategories(reader);
        } catch (Exception e) {
            throw new DataImportException("Error during the import of file: " + importLocation, e);
        }
    }

    public void mergeCategories(CSVReader reader) throws Exception {
        AdvertCategoryImportService thisBean = applicationContext.getBean(AdvertCategoryImportService.class);

        thisBean.disableAllCategories();
        String[] row;
        while ((row = reader.readNext()) != null) {
            CategoryRowDescriptor rowDescriptor = getDescriptor(row);
            if (rowDescriptor != null) {
                OpportunityCategory parentCategory = entityService.getById(OpportunityCategory.class, rowDescriptor.getId() / 10);
                OpportunityCategory category = new OpportunityCategory().withId(rowDescriptor.getId()).withEnabled(true).withName(rowDescriptor.getName()).withParentCategory(parentCategory);
                entityService.merge(category);
            }
        }
    }

    private CategoryRowDescriptor getDescriptor(String[] row) {
        if (row.length < 5) {
            return null;
        }
        for (int i = 0; i < 4; i++) {
            try {
                int id = Integer.parseInt(row[i]);
                return new CategoryRowDescriptor(id, row[4]);
            } catch (NumberFormatException e) {
            }
        }
        return null;
    }

    private class CategoryRowDescriptor {

        private Integer id;

        private String name;

        private CategoryRowDescriptor(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    @Transactional
    public void disableAllCategories() {
        importedEntityDAO.disableAllEntities(OpportunityCategory.class);
    }

}
