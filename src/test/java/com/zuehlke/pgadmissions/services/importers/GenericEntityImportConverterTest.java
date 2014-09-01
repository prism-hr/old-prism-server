package com.zuehlke.pgadmissions.services.importers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;

import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.referencedata.jaxb.Disabilities.Disability;
import com.zuehlke.pgadmissions.services.converters.ImportedEntityConverter;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class GenericEntityImportConverterTest {

    @Test
    public void shouldConvertImportEntity() {
        Disability disability = new Disability();
        disability.setCode("kod");
        disability.setName("nazwa");

        Institution institution = new Institution();
        
        ImportedEntityConverter<com.zuehlke.pgadmissions.domain.Disability> converter = ImportedEntityConverter
                .create(institution, com.zuehlke.pgadmissions.domain.Disability.class);
        com.zuehlke.pgadmissions.domain.Disability converted = converter.apply(disability);
        
        assertEquals("kod", converted.getCode());
        assertEquals("nazwa", converted.getName());
        assertSame(institution, converted.getInstitution());
        assertTrue(converted.isEnabled());
    }

}
