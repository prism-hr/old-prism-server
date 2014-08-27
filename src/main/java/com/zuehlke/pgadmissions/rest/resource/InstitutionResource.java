package com.zuehlke.pgadmissions.rest.resource;

import com.zuehlke.pgadmissions.dto.ActionOutcome;
import com.zuehlke.pgadmissions.rest.dto.CommentDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.representation.ActionOutcomeRepresentation;
import com.zuehlke.pgadmissions.rest.validation.validator.CommentDTOValidator;
import com.zuehlke.pgadmissions.services.InstitutionService;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = {"api/institutions"})
public class InstitutionResource {

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private Mapper dozerBeanMapper;

    @Autowired
    private CommentDTOValidator commentDTOValidator;

    @RequestMapping(value = "/{institutionId}/comments", method = RequestMethod.POST)
    public ActionOutcomeRepresentation performAction(@PathVariable Integer institutionId, @Valid @RequestBody CommentDTO commentDTO) {
        ActionOutcome actionOutcome = institutionService.performAction(institutionId, commentDTO);
        return dozerBeanMapper.map(actionOutcome, ActionOutcomeRepresentation.class);
    }

    @InitBinder(value = "commentDTO")
    public void configureCommentBinding(WebDataBinder binder) {
        binder.setValidator(commentDTOValidator);
    }
}