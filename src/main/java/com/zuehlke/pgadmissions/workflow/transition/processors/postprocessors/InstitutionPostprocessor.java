package com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors;

import static com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity.PROGRAM;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.ImportedEntityServiceHelperInstitution;
import com.zuehlke.pgadmissions.workflow.transition.processors.ResourceProcessor;

@Component
public class InstitutionPostprocessor implements ResourceProcessor {

    @Inject
    private ImportedEntityService importedEntityService;

    @Inject
    private ImportedEntityServiceHelperInstitution importedEntityServiceHelperInstitution;

    @Override
    public void process(Resource resource, Comment comment) throws Exception {
        Institution institution = (Institution) resource;

        if (comment.isInstitutionApproveComment()) {
            initializeInstitution(institution);
        }
    }

    public void initializeInstitution(Institution institution) {
        importedEntityService.setInstitutionImportedEntityFeeds(institution);
        importedEntityServiceHelperInstitution.execute(institution.getId(), PROGRAM);
    }

}
