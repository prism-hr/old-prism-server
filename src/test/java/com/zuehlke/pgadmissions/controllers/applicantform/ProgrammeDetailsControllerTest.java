package com.zuehlke.pgadmissions.controllers.applicantform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.SourcesOfInterest;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.SourcesOfInterestBuilder;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.LocalDatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.SuggestedSupervisorJSONPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.ProgramDetailsService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.validators.ProgramDetailsValidator;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ProgrammeDetailsControllerTest {

    @Mock
    @InjectIntoByType
    private ApplicationService applicationFormService;

    @Mock
    @InjectIntoByType
    private ApplicationFormPropertyEditor applicationFormPropertyEditor;

    @Mock
    @InjectIntoByType
    private LocalDatePropertyEditor datePropertyEditor;

    @Mock
    @InjectIntoByType
    private ProgramDetailsValidator programmeDetailsValidator;

    @Mock
    @InjectIntoByType
    private ProgramDetailsService programDetailsService;

    @Mock
    @InjectIntoByType
    private ProgramService programService;

    @Mock
    @InjectIntoByType
    private ImportedEntityService sourcesOfInterestService;

    @Mock
    @InjectIntoByType
    private SuggestedSupervisorJSONPropertyEditor supervisorJSONPropertyEditor;

    @TestedObject
    private ProgramDetailsController controller;

    @Test
    public void shouldReturnAvaialbeStudyOptionLevels() {
        final String applicationNumber = "1";
        Program program = new Program().withId(7);
        final Application applicationForm = new ApplicationFormBuilder().id(1).applicationNumber(applicationNumber).program(program).build();

        StudyOption option1 = new StudyOption("1", "Full-time");
        StudyOption option2 = new StudyOption("31", "Part-time");

        List<StudyOption> optionsList = Arrays.asList(option1, option2);

        EasyMock.expect(programService.getAvailableStudyOptions(program)).andReturn(optionsList);
        EasyMock.replay(programService);
        List<StudyOption> studyOptions = controller.getStudyOptions(applicationNumber);
        assertSame(studyOptions, optionsList);
    }

    @Test
    public void shouldReturnAllSourcesOfInterest() {
        SourcesOfInterest sourcesOfInterest = new SourcesOfInterestBuilder().id(1).code("ZZ").name("ZZ").build();
        // EasyMock.expect(programDetailsServiceMock.getAllEnabledSourcesOfInterest()).andReturn(Collections.singletonList(sourcesOfInterest));
        // EasyMock.replay(programDetailsServiceMock);
        assertEquals(controller.getSourcesOfInterests().get(0), sourcesOfInterest);
    }

}