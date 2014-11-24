package com.zuehlke.pgadmissions.rest.resource;

import com.zuehlke.pgadmissions.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = { "api/programs" })
public class ProgramResource {

    @Autowired
    private ProgramService programService;

    @RequestMapping(value = "/{programId}/locations/{location}/divisions", method = RequestMethod.GET)
    public List<String> listSuggestedDivisions(@PathVariable Integer programId, @PathVariable String location)
            throws Exception {
        return programService.listSuggestedDivisions(programId, location);
    }

    @RequestMapping(value = "/{programId}/locations/{location}/divisions/{division}/studyAreas", method = RequestMethod.GET)
    public List<String> listSuggestedStudyAreas(@PathVariable Integer programId, @PathVariable String location, @PathVariable String division)
            throws Exception {
        return programService.listSuggestedStudyAreas(programId, location, division);
    }

}
