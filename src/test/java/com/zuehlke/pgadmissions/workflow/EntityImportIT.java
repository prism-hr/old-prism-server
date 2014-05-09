package com.zuehlke.pgadmissions.workflow;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.enums.ImportedEntityType;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.importers.EntityImportService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testWorkflowContext.xml")
public class EntityImportIT {

    @Autowired
    private EntityImportService entityImportService;

    @Autowired
    private ImportedEntityService importedEntityService;

    @Test
    public void testConflictsInImport() throws Exception {
        ImportedEntityFeed importedEntityFeed = new ImportedEntityFeed();
        importedEntityFeed.setImportedEntityType(ImportedEntityType.DISABILITY);
        importedEntityFeed.setLocation("reference_data/conflicts/initialDisabilities.xml");

        entityImportService.importEntities(importedEntityFeed);

        assertEquals(2, importedEntityService.getAllDisabilities().size());
        assertEquals("disability1", importedEntityService.getByCode(Disability.class, "0").getName());
        assertEquals("otherDisability", importedEntityService.getByCode(Disability.class, "99").getName());

        importedEntityFeed.setLocation("reference_data/conflicts/changeName.xml");
        entityImportService.importEntities(importedEntityFeed);
        
        assertEquals(2, importedEntityService.getAllDisabilities().size());
        assertEquals("disability2", importedEntityService.getByCode(Disability.class, "0").getName());
        assertEquals("otherDisability", importedEntityService.getByCode(Disability.class, "99").getName());

        importedEntityFeed.setLocation("reference_data/conflicts/changeCode.xml");
        entityImportService.importEntities(importedEntityFeed);
        
        assertEquals(2, importedEntityService.getAllDisabilities().size());
        assertEquals("disability2", importedEntityService.getByCode(Disability.class, "1").getName());
        assertEquals("otherDisability", importedEntityService.getByCode(Disability.class, "99").getName());
    }

}
