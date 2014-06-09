package com.zuehlke.pgadmissions.controllers.prospectus;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import java.util.Arrays;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.ProgramPropertyEditor;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.ProgramInstanceService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.UserService;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ProgramConfigurationControllerTest {

    @Mock
    @InjectIntoByType
    private ProgramService programsService;

    @Mock
    @InjectIntoByType
    private ImportedEntityService importedEntityService;

    @Mock
    @InjectIntoByType
    private EncryptionHelper encryptionHelper;

    @Mock
    @InjectIntoByType
    private ProgramInstanceService programInstanceService;

    @Mock
    @InjectIntoByType
    private ApplyTemplateRenderer templateRenderer;

    @Mock
    @InjectIntoByType
    private ProgramPropertyEditor programPropertyEditor;

    @Mock
    @InjectIntoByType
    private UserService userService;

    @Mock
    @InjectIntoByType
    private ApplicationContext applicationContext;

    @InjectIntoByType
    private Gson gson = new Gson();

    @TestedObject
    private ProgramConfigurationController controller;

    @Test
    @SuppressWarnings("unchecked")
    public void shouldGetOpportunityData() {
        InstitutionDomicile domicile = new InstitutionDomicile();
        Program program = new Program().withCode("07").withInstitution(new Institution().withDomicile(domicile)).withId(999)
                .withTitle("Dlaczego w pizdzie nie ma krzesel?").withDescription("Zeby chuj stal").withStudyDuration(8).withFunding("Ni ma kasy")
                .withState(new State().withId(PrismState.PROGRAM_APPROVED)).withRequireProjectDefinition(false);

        Map<String, Object> dataMap = Maps.newHashMap();
        dataMap.put("advertId", 999);
        expect(programsService.getProgramByCode("07")).andReturn(program);
        expect(encryptionHelper.encrypt(88)).andReturn("encPL");
        expect(programInstanceService.getAdvertisingDeadlineYear(program)).andReturn(2084);
        expect(templateRenderer.renderButton(dataMap)).andReturn("button");
        expect(templateRenderer.renderLink(dataMap)).andReturn("button");

        replay();
        String result = controller.getOpportunityData("07");
        verify();

        Map<String, Object> resultMap = new Gson().fromJson(result, Map.class);

        assertEquals(999, ((Double) resultMap.get("programId")).intValue());
        assertEquals("Dlaczego w pizdzie nie ma krzesel?", resultMap.get("programTitle"));
        assertEquals("Zeby chuj stal", resultMap.get("programDescription"));
        assertEquals(8, ((Double) resultMap.get("programStudyDuration")).intValue());
        assertEquals("Ni ma kasy", resultMap.get("programFunding"));
        assertTrue((Boolean) resultMap.get("programIsActive"));
        assertTrue((Boolean) resultMap.get("isCustomProgram"));
        assertTrue((Boolean) resultMap.get("isCustomProgram"));
        assertFalse((Boolean) resultMap.get("atasRequired"));
        assertEquals("INTERNSHIP", resultMap.get("programType"));
        assertEquals("encPL", resultMap.get("institutionCountryCode"));
        assertEquals("inst", resultMap.get("institutionCode"));
        assertTrue((Boolean) resultMap.get("programLock"));
        assertEquals(2084, ((Double) resultMap.get("advertisingDeadline")).intValue());
        assertEquals(Arrays.asList("opt1", "opt2"), resultMap.get("studyOptions"));
    }
    
}
