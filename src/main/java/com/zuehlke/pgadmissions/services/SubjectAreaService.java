package com.zuehlke.pgadmissions.services;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.imported.ImportedSubjectArea;
import com.zuehlke.pgadmissions.rest.representation.SubjectAreaRepresentation;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class SubjectAreaService {

    @Inject
    private EntityService entityService;

    @Inject
    private ImportedEntityService importedEntityService;

    @Inject
    private ApplicationContext applicationContext;

    @Cacheable("importedSubjectAreas")
    public Map<Integer, ImportedSubjectArea> getAllSubjectAreas() {
        List<ImportedSubjectArea> subjectAreas = entityService.listByProperty(ImportedSubjectArea.class, "enabled", true);
        return subjectAreas.stream().collect(Collectors.toMap(subjectArea -> subjectArea.getId(), Function.identity()));
    }

    public List<SubjectAreaRepresentation> searchSubjectAreas(String searchTerm) {
        SubjectAreaService thisBean = applicationContext.getBean(SubjectAreaService.class);
        Map<Integer, ImportedSubjectArea> allSubjectAreas = thisBean.getAllSubjectAreas();

        List<ImportedSubjectArea> importedSubjectAreas = importedEntityService.searchByName(ImportedSubjectArea.class, searchTerm);
        // use only detached, cached items
        importedSubjectAreas = importedSubjectAreas.stream().map(sa -> allSubjectAreas.get(sa.getId())).collect(Collectors.toList());

        Map<Integer, SubjectAreaRepresentation> initialMap = new HashMap<>();
        initialMap.put(-1, new SubjectAreaRepresentation(-1, null));

        Map<Integer, SubjectAreaRepresentation> representations = importedSubjectAreas.stream()
                .flatMap(subjectArea -> subjectArea.getAncestorsPath().stream()) // flat map all paths from leaves to root
                .reduce(initialMap, (map, sa) -> { // populate tree top down
                    SubjectAreaRepresentation representation = new SubjectAreaRepresentation(sa.getId(), sa.getName());
                    map.put(sa.getId(), representation);
                    int parentId = sa.getParent() != null ? sa.getParent().getId() : -1;
                    map.get(parentId).addChild(representation);
                    return map;
                }, (map1, map2) -> {
                    throw new UnsupportedOperationException();
                });

        return Lists.newArrayList(representations.get(-1).getChildren());
    }

}
