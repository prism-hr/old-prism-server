package com.zuehlke.pgadmissions.services.importers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.referencedata.v2.jaxb.Disabilities.Disability;
import com.zuehlke.pgadmissions.services.importers.EntityImportService.ImportEntityConverter;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class EntityImportServiceTest {

    @TestedObject
    private EntityImportService entityImportService;

    @Test
    public void shouldConvertImportEntity() {
        Disability disability = new Disability();
        disability.setCode("kod");
        disability.setName("nazwa");

        ImportEntityConverter<com.zuehlke.pgadmissions.domain.Disability> converter = ImportEntityConverter
                .create(com.zuehlke.pgadmissions.domain.Disability.class);
        com.zuehlke.pgadmissions.domain.Disability converted = converter.apply(disability);
        
        assertEquals("kod", converted.getCode());
        assertEquals("nazwa", converted.getName());
        assertTrue(converted.isEnabled());
    }

}
