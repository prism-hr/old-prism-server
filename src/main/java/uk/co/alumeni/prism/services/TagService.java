package uk.co.alumeni.prism.services;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.alumeni.prism.domain.Tag;
import uk.co.alumeni.prism.rest.dto.TagDTO;

@Service
@Transactional
public class TagService {

    @Inject
    private EntityService entityService;

    public <T extends Tag> T getById(Class<T> tagClass, Integer id) {
        return entityService.getById(tagClass, id);
    }

    public <T extends Tag> T createOrUpdateTag(Class<T> tagClass, TagDTO tagDTO) {
        DateTime baseline = new DateTime();

        T transientTag = BeanUtils.instantiate(tagClass);
        transientTag.setName(tagDTO.getName());
        transientTag.setDescription(tagDTO.getDescription());
        transientTag.setAdoptedCount(1);
        transientTag.setCreatedTimestamp(baseline);
        transientTag.setUpdatedTimestamp(baseline);

        T persistentTag = entityService.getDuplicateEntity(transientTag);
        if (persistentTag == null) {
            entityService.save(transientTag);
            return transientTag;
        } else {
            persistentTag.setAdoptedCount(persistentTag.getAdoptedCount() + 1);
            persistentTag.setUpdatedTimestamp(baseline);
            return persistentTag;
        }
    }

}
