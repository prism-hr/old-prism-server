package com.zuehlke.pgadmissions.services.indices;

import static com.zuehlke.pgadmissions.utils.PrismStringUtils.tokenize;
import static com.zuehlke.pgadmissions.utils.PrismTargetingUtils.STOP_WORDS;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.imported.ImportedSubjectArea;
import com.zuehlke.pgadmissions.dto.TokenizedStringDTO;
import com.zuehlke.pgadmissions.services.ImportedEntityService;

@Service
@Transactional
public class ImportedSubjectAreaIndex {

    private Map<Integer, ImportedSubjectArea> byId = Maps.newHashMap();

    private Map<String, ImportedSubjectArea> byJacsCode = Maps.newHashMap();

    private Map<String, ImportedSubjectArea> byJacsCodeOld = Maps.newHashMap();

    private HashMultimap<Integer, ImportedSubjectArea> byUcasSubject = HashMultimap.create();

    private HashMultimap<String, ImportedSubjectArea> byKeyword = HashMultimap.create();

    private Map<ImportedSubjectArea, Integer> uniqueTokenCounts = Maps.newHashMap();

    private HashMultimap<ImportedSubjectArea, ImportedSubjectArea> parents = HashMultimap.create();

    @Inject
    private ImportedEntityService importedEntityService;

    public void index() {
        List<ImportedSubjectArea> subjectAreas = importedEntityService.getImportedSubjectAreas();
        for (ImportedSubjectArea subjectArea : subjectAreas) {
            byId.put(subjectArea.getId(), subjectArea);

            for (String jacsCode : subjectArea.getJacsCode().split("\\|")) {
                byJacsCode.put(jacsCode, subjectArea);
            }

            String jacsCodesOld = subjectArea.getJacsCodeOld();
            if (jacsCodesOld != null) {
                for (String jacsCodeOld : jacsCodesOld.split("\\|")) {
                    byJacsCodeOld.put(jacsCodeOld, subjectArea);
                }
            }

            byUcasSubject.put(subjectArea.getUcasSubject(), subjectArea);

            TokenizedStringDTO tokens = tokenize(subjectArea.getName(), STOP_WORDS);
            for (String token : tokens.getTokens()) {
                byKeyword.put(token, subjectArea);
            }
            uniqueTokenCounts.put(subjectArea, tokens.getUniqueTokenCount());
            setParents(subjectArea, subjectArea.getParent());
        }
    }

    public ImportedSubjectArea getById(Integer id) {
        return byId.get(id);
    }

    public ImportedSubjectArea getByJacsCode(String jacsCode) {
        return byJacsCode.get(jacsCode);
    }

    public ImportedSubjectArea getByJacsCodeOld(String jacsCodeOld) {
        return byJacsCodeOld.get(jacsCodeOld);
    }

    public Set<ImportedSubjectArea> getByUcasSubject(Integer ucasSubject) {
        return byUcasSubject.get(ucasSubject);
    }

    public Set<ImportedSubjectArea> getByKeyword(String keyword) {
        return byKeyword.get(keyword);
    }

    public Integer getUniqueTokenCount(ImportedSubjectArea subjectArea) {
        return uniqueTokenCounts.get(subjectArea);
    }

    public Set<ImportedSubjectArea> getParents(ImportedSubjectArea subjectArea) {
        return parents.get(subjectArea);
    }

    private void setParents(ImportedSubjectArea subjectArea, ImportedSubjectArea subjectAreaParent) {
        if (subjectAreaParent == null) {
            return;
        }
        parents.put(subjectArea, subjectAreaParent);
        setParents(subjectAreaParent, subjectAreaParent.getParent());
    }

}
