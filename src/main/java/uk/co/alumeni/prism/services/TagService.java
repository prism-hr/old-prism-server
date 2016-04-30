package uk.co.alumeni.prism.services;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.alumeni.prism.dao.TagDAO;
import uk.co.alumeni.prism.domain.Competence;
import uk.co.alumeni.prism.domain.Tag;
import uk.co.alumeni.prism.domain.Theme;
import uk.co.alumeni.prism.rest.dto.TagDTO;
import uk.co.alumeni.prism.rest.dto.advert.AdvertCompetenceDTO;

@Service
@Transactional
public class TagService {

    @Inject
    private TagDAO tagDAO;

    @Inject
    private EntityService entityService;

    public <T extends Tag> T getById(Class<T> tagClass, Integer id) {
        return entityService.getById(tagClass, id);
    }

    public <T extends Tag> T createOrUpdateTag(Class<T> tagClass, TagDTO tagDTO) {
        DateTime baseline = new DateTime();

        T transientTag = BeanUtils.instantiate(tagClass);
        transientTag.setName(tagDTO.getName());

        if (tagClass.equals(Competence.class)) {
            ((Competence) transientTag).setDescription(((AdvertCompetenceDTO) tagDTO).getDescription());
        }

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

    public List<Theme> getThemes(String searchTerm) {
        return tagDAO.getTags(Theme.class, searchTerm);
    }

    public List<Competence> getCompetences(String searchTerm) {
        return tagDAO.getTags(Competence.class, searchTerm);
    }

}
