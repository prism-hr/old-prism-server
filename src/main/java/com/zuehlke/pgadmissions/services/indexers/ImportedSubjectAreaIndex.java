package com.zuehlke.pgadmissions.services.indexers;

import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.HashMultimap;
import com.zuehlke.pgadmissions.dto.ImportedSubjectAreaDTO;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.utils.PrismStringUtils;

@Service
@Transactional
public class ImportedSubjectAreaIndex {

    private HashMultimap<String, Integer> index;

    @Inject
    private ImportedEntityService importedEntityService;

    @PostConstruct
    public void index() {
        this.index = HashMultimap.create();
        List<ImportedSubjectAreaDTO> subjectAreas = importedEntityService.getImportedSubjectAreas();
        for (ImportedSubjectAreaDTO subjectArea : subjectAreas) {
            Integer subjectAreaId = subjectArea.getId();
            Set<String> tokens = PrismStringUtils.tokenize(subjectArea.getName());
            for (String token : tokens) {
                index.put(token, subjectAreaId);
            }
        }
    }

    public Set<Integer> getMatchingSubjectAreas(String word) {
        return index.get(word);
    }

}
