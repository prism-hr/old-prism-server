package com.zuehlke.pgadmissions.rest.controller;

import com.zuehlke.pgadmissions.rest.representation.SubjectAreaRepresentation;
import com.zuehlke.pgadmissions.services.SubjectAreaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

@RestController
@RequestMapping("/api/subjectAreas")
@PreAuthorize("permitAll")
public class SubjectAreaController {

    @Inject
    private SubjectAreaService subjectAreaService;

    @RequestMapping(method = RequestMethod.GET)
    public List<SubjectAreaRepresentation> searchSubjectAreas(String searchTerm) {
        return subjectAreaService.searchSubjectAreas(searchTerm);
    }

}
