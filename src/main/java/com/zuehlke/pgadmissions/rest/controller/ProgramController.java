package com.zuehlke.pgadmissions.rest.controller;

import java.util.List;

import javax.inject.Inject;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zuehlke.pgadmissions.services.ProgramService;

@RestController
@RequestMapping(value = {"api/programs"})
@PreAuthorize("isAuthenticated()")
public class ProgramController {

    @Inject
    private ProgramService programService;

    @RequestMapping(value = "/{programId}/locations/{location}/divisions", method = RequestMethod.GET)
    public List<String> listSuggestedDivisions(@PathVariable Integer programId, @PathVariable String location) {
        return programService.getSuggestedDivisions(programId, location);
    }

    @RequestMapping(value = "/{programId}/locations/{location}/divisions/{division}/studyAreas", method = RequestMethod.GET)
    public List<String> listSuggestedStudyAreas(@PathVariable Integer programId, @PathVariable String location, @PathVariable String division) {
        return programService.getSuggestedStudyAreas(programId, location, division);
    }

}
