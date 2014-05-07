package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.ImportedObject;
import com.zuehlke.pgadmissions.domain.builders.DisabilityBuilder;

public class ImportedDataDAOTest extends AutomaticRollbackTestCase {

    private ImportedDataDAO importedDataDAO;

    @Test
    public void shouldGetDisabledImportedObjectsWithoutActiveReference() {
        Disability disability1 = new DisabilityBuilder().code("idTest1").name("name1").enabled(true).enabledObject(null).build();
        Disability disability2 = new DisabilityBuilder().code("idTest1").name("name2").enabled(true).enabledObject(disability1).build();
        Disability disability3 = new DisabilityBuilder().code("idTest1").name("name3").enabled(false).enabledObject(null).build();
        Disability disability4 = new DisabilityBuilder().code("idTest1").name("name4").enabled(false).enabledObject(disability1).build();
        save(disability1, disability2, disability3, disability4);
        
        List<Disability> testDisabilities = new ArrayList<Disability>();
        testDisabilities.add(disability1);
        testDisabilities.add(disability2);
        testDisabilities.add(disability3);
        testDisabilities.add(disability4);

        List<ImportedObject> returned = importedDataDAO.getDisabledImportedObjectsWithoutActiveReference(Disability.class);
        
        assertTrue(returned.contains(disability3));
        
        int selectCounter = 0;
        for (ImportedObject object: testDisabilities) {
            if (returned.contains(object)) {
                selectCounter++;
            }
        }
        
        assertEquals(1, selectCounter);
    }

    @Test
    public void shouldGetEnabledVersion() {
        Disability disability1 = new DisabilityBuilder().code("idTest2").name("name1").enabled(true).enabledObject(null).build();
        Disability disability2 = new DisabilityBuilder().code("idTest2").name("name2").enabled(false).enabledObject(disability1).build();

        save(disability1, disability2);

        ImportedObject returned = importedDataDAO.getEnabledVersion(disability2);
        assertEquals("name1", returned.getName());
    }

    @Before
    public void prepare() {
        importedDataDAO = new ImportedDataDAO(sessionFactory);
    }

}
