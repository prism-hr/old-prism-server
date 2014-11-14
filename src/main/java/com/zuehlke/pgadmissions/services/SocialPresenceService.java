package com.zuehlke.pgadmissions.services;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.zuehlke.pgadmissions.domain.definitions.SocialPresence;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.dto.json.InstitutionSearchResponseDTO;
import com.zuehlke.pgadmissions.dto.json.InstitutionSearchResponseDTO.Item;
import com.zuehlke.pgadmissions.rest.representation.SocialPresenceRepresentation;
import com.zuehlke.pgadmissions.rest.representation.SocialPresenceRepresentation.SocialProfile;

@Service
public class SocialPresenceService {

    @Value("${integration.google.api.key}")
    private String googleApiKey;

    @Value("${integration.google.search.api.uri}")
    private String googleSearchApiUri;

    @Value("${integration.linked.in.people.search.uri}")
    private String linkedinPeopleSearchUri;

    @Autowired
    private RestTemplate restTemplate;

    public SocialPresenceRepresentation getPotentialInstitutionProfiles(String institutionTitle) throws IOException {
        String encodedSearchTerm = URLEncoder.encode(institutionTitle, "UTF-8");
        return getPotentialProfiles(Institution.class, encodedSearchTerm);
    }

    private SocialPresenceRepresentation getPotentialProfiles(Class<?> subscriber, String encodedSearchTerm) throws IOException {
        SocialPresenceRepresentation representation = new SocialPresenceRepresentation();

        for (SocialPresence presence : SocialPresence.getClassSubscriptions(subscriber)) {
            URI request = new DefaultResourceLoader().getResource(
                    googleSearchApiUri + "?q=" + encodedSearchTerm + "&key=" + googleApiKey + "&cx=" + presence.getSearchEngineKey() + "&format=json").getURI();
            InstitutionSearchResponseDTO response = restTemplate.getForObject(request, InstitutionSearchResponseDTO.class);

            for (Item item : response.getItems()) {
                String link = item.getLink();
                if (link.replace("http://", "https://").replaceAll(presence.getSearchEngineUri(), "").contains("/")) {
                    continue;
                } else {
                    SocialProfile profile = new SocialProfile().withTitle(item.getTitle()).withUri(item.getLink());
                    representation.addPotentialProfile(presence, profile);
                }
            }
        }

        return representation;
    }

}
