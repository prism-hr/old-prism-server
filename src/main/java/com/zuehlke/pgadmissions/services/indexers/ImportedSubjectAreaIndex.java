package com.zuehlke.pgadmissions.services.indexers;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.dto.ImportedSubjectAreaDTO;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.utils.PrismStringUtils;

@Service
@Transactional
public class ImportedSubjectAreaIndex {

    private Map<Integer, ImportedSubjectAreaDTO> byId = Maps.newHashMap();

    private Map<String, ImportedSubjectAreaDTO> byJacsCode = Maps.newHashMap();

    private Map<String, ImportedSubjectAreaDTO> byJacsCodeOld = Maps.newHashMap();

    private HashMultimap<Integer, ImportedSubjectAreaDTO> byUcasSubject = HashMultimap.create();

    private HashMultimap<String, ImportedSubjectAreaDTO> byKeyword = HashMultimap.create();

    @Inject
    private ImportedEntityService importedEntityService;

    @PostConstruct
    public void index() {
        List<ImportedSubjectAreaDTO> subjectAreas = importedEntityService.getImportedSubjectAreas();
        for (ImportedSubjectAreaDTO subjectArea : subjectAreas) {
            subjectArea.prepare();
            byId.put(subjectArea.getId(), subjectArea);

            for (String jacsCode : subjectArea.getJacsCodes()) {
                byJacsCode.put(jacsCode, subjectArea);
            }

            String[] jacsCodesOld = subjectArea.getJacsCodesOld();
            if (jacsCodesOld != null) {
                for (String jacsCodeOld : jacsCodesOld) {
                    byJacsCodeOld.put(jacsCodeOld, subjectArea);
                }
            }

            byUcasSubject.put(subjectArea.getUcasSubject(), subjectArea);

            Set<String> tokens = PrismStringUtils.tokenize(subjectArea.getName());
            for (String token : tokens) {
                byKeyword.put(token, subjectArea);
            }
        }
    }

    public ImportedSubjectAreaDTO getById(Integer id) {
        return byId.get(id);
    }

    public ImportedSubjectAreaDTO getByJacsCode(String jacsCode) {
        return byJacsCode.get(jacsCode);
    }

    public ImportedSubjectAreaDTO getByJacsCodeOld(String jacsCodeOld) {
        return byJacsCodeOld.get(jacsCodeOld);
    }

    public Set<ImportedSubjectAreaDTO> getByUcasSubject(Integer ucasSubject) {
        return byUcasSubject.get(ucasSubject);
    }

    public Set<ImportedSubjectAreaDTO> getByKeyword(String keyword) {
        return byKeyword.get(keyword);
    }

}
