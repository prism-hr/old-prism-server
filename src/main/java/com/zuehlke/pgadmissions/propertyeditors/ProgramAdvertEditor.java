package com.zuehlke.pgadmissions.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.dao.ProgramAdvertDAO;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramAdvert;
import com.zuehlke.pgadmissions.services.ProgramsService;

@Component
public class ProgramAdvertEditor extends PropertyEditorSupport {

    public static final Integer ERROR_VALUE_FOR_DURATION_OF_STUDY_IN_MONTH = -1;
    private final ProgramAdvertDAO programAdvertDAO;
    private final ProgramsService programsService;

    public ProgramAdvertEditor() {
        this(null, null);
    }

    @Autowired
    public ProgramAdvertEditor(ProgramAdvertDAO programAdvertDAO, ProgramsService programsService) {
        this.programAdvertDAO = programAdvertDAO;
        this.programsService = programsService;
    }

    @Override
    public void setAsText(String strId) throws IllegalArgumentException {
        if (StringUtils.isBlank(strId)) {
            setValue(null);
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Map<String, Object> properties = objectMapper.readValue(strId, Map.class);
            ProgramAdvert programAdvert = null;

            String programIdAsString = (String) properties.get("programId");
            if (StringUtils.isNotBlank(programIdAsString)) {
                Integer programId = Integer.valueOf(programIdAsString);
                Program program = programsService.getProgramById(programId);
                programAdvert = programAdvertDAO.getProgramAdvertByProgramId(programId);
            } else {
                programAdvert = new ProgramAdvert();
            }

            programAdvert.setDescription((String) properties.get("description"));

            String durationOfStudyAsString = (String) properties.get("durationOfStudy");
            String durationOfStudyUnitAsString = (String) properties.get("durationOfStudyUnit");
            if (StringUtils.isNotBlank(durationOfStudyAsString) && StringUtils.isNotBlank(durationOfStudyUnitAsString)) {
                Double durationOfStudyAsDouble = Double.valueOf(durationOfStudyAsString);
                if (durationOfStudyAsDouble.isNaN() || (Math.floor(durationOfStudyAsDouble) != durationOfStudyAsDouble)) {
                    programAdvert.setDurationOfStudyInMonth(ERROR_VALUE_FOR_DURATION_OF_STUDY_IN_MONTH);
                } else {
                    Integer durationOfStudyInMonths = Integer.valueOf(durationOfStudyAsString);
                    if (durationOfStudyUnitAsString.equals("Years")) {
                        durationOfStudyInMonths = durationOfStudyInMonths * 12;
                    }
                    programAdvert.setDurationOfStudyInMonth(durationOfStudyInMonths);
                }
            }

            programAdvert.setFundingInformation((String) properties.get("fundingInformation"));

            String isCurrentlyAcceptingApplicationsAsString = (String) properties.get("isCurrentlyAcceptingApplications");
            if (StringUtils.isNotBlank(isCurrentlyAcceptingApplicationsAsString)) {
                Boolean isCurrentlyAcceptingApplications = isCurrentlyAcceptingApplicationsAsString.equals("yes") ? true : false;
                programAdvert.setIsCurrentlyAcceptingApplications(isCurrentlyAcceptingApplications);
            }

            setValue(programAdvert);

        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

    }

}
