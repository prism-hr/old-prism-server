package uk.co.alumeni.prism.mapping;

import org.springframework.stereotype.Service;
import uk.co.alumeni.prism.rest.representation.CompetenceRepresentation;
import uk.co.alumeni.prism.rest.representation.TagRepresentation;
import uk.co.alumeni.prism.services.TagService;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TagMapper {

    @Inject
    private TagService tagService;

    public List<TagRepresentation> getThemes(String searchTerm) {
        return tagService.getThemes(searchTerm).stream()
                .map(competence -> new TagRepresentation().withId(competence.getId()).withName(competence.getName()))
                .collect(Collectors.toList());
    }

    public List<CompetenceRepresentation> getCompetences(String searchTerm) {
        return tagService.getCompetences(searchTerm).stream()
                .map(competence -> new CompetenceRepresentation().withId(competence.getId()).withName(competence.getName()).withDescription(competence.getDescription()))
                .collect(Collectors.toList());
    }

}
