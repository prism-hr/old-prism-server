package com.zuehlke.pgadmissions.rest.resource;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zuehlke.pgadmissions.rest.validation.validator.comment.CommentDTOValidator;
import com.zuehlke.pgadmissions.services.InstitutionService;

@RestController
@RequestMapping(value = {"api/institutions"})
public class InstitutionResource {

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private Mapper dozerBeanMapper;

    @Autowired
    private CommentDTOValidator commentDTOValidator;

    @InitBinder(value = "commentDTO")
    public void configureCommentBinding(WebDataBinder binder) {
        binder.setValidator(commentDTOValidator);
    }
}
