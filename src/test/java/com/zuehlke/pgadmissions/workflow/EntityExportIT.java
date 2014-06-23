package com.zuehlke.pgadmissions.workflow;

import com.zuehlke.pgadmissions.domain.*;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.importers.EntityExportService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testWorkflowContext.xml")
public class EntityExportIT {

    @Autowired
    private EntityExportService entityExportService;

    @Autowired
    private EntityService entityService;

    @Ignore
    @Test
    public void exportEntities() throws Exception {
        Class[] exportClasses = new Class[]{Country.class, Domicile.class, Disability.class, Ethnicity.class, Language.class, QualificationType.class, ReferralSource.class, Title.class, FundingSource.class, StudyOption.class, ImportedInstitution.class, LanguageQualificationType.class};
        String[] entityNames = new String[]{"country", "domicile", "disability", "ethnicity", "nationality", "qualificationType", "sourceOfInterest", "title", "fundingSource", "studyOption", "institution", "languageQualificationType"};

        for (int i = 0; i < exportClasses.length; i++) {
            Class<?> exportClass = exportClasses[i];
            String entityName = entityNames[i];
            entityExportService.exportEntities(exportClass, entityName);
        }
    }
}
