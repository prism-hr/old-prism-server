package com.zuehlke.pgadmissions.services;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.zuehlke.pgadmissions.domain.definitions.SocialPresence;
import com.zuehlke.pgadmissions.dto.json.InstitutionSearchResponseDTO;
import com.zuehlke.pgadmissions.dto.json.InstitutionSearchResponseDTO.Item;
import com.zuehlke.pgadmissions.rest.representation.SocialPresenceRepresentation;
import com.zuehlke.pgadmissions.rest.representation.SocialPresenceRepresentation.ExtendedSocialProfile;
import com.zuehlke.pgadmissions.rest.representation.SocialPresenceRepresentation.SocialProfile;

@Service
public class SocialPresenceService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${integration.google.api.key}")
    private String googleApiKey;

    @Value("${integration.google.search.api.uri}")
    private String googleSearchApiUri;

    @Autowired
    private RestTemplate restTemplate;

    public SocialPresenceRepresentation getPotentialProfiles(Class<?> subscriber, String searchTerm) throws IOException {
        String encodedSearchTerm = URLEncoder.encode(searchTerm, "UTF-8");

        SocialPresenceRepresentation representation = new SocialPresenceRepresentation();
        for (SocialPresence presence : SocialPresence.getClassSubscriptions(subscriber)) {
            URI request = new DefaultResourceLoader().getResource(
                    googleSearchApiUri + "?q=" + encodedSearchTerm + "&key=" + googleApiKey + "&cx=" + presence.getSearchEngine() + "&format=json").getURI();
            InstitutionSearchResponseDTO response = restTemplate.getForObject(request, InstitutionSearchResponseDTO.class);

            boolean isLinkedinCompany = presence == SocialPresence.LINKEDIN_COMPANY;
            boolean isLinkedinPerson = presence == SocialPresence.LINKEDIN_PERSON;

            List<Item> items = response.getItems();
            int gotItems = items.size();
            int maxItems = presence.getResultsToConsider();

            for (int i = 0; i < (gotItems < maxItems ? gotItems : maxItems); i++) {
                Item item = items.get(i);
                String link = item.getLink();
                if (SocialPresence.doExclude(presence, link)) {
                    continue;
                } else {
                    if (isLinkedinCompany) {
                        try {
                            ExtendedSocialProfile profile = new ExtendedSocialProfile().withTitle(item.getTitle()).withUri(item.getLink());
                            representation.addPotentialProfile(presence, getLinkedinCompanyProfile(profile));
                        } catch (IOException e) {
                            logger.error("Unable to parse linked in company profile", e);
                        }
                    } else if (isLinkedinPerson) {
                        try {
                            unpackLinkedinPersonProfile(representation, item);
                        } catch (IOException e) {
                            logger.error("Unable to parse linked in person profile", e);
                        }
                    } else {
                        SocialProfile profile = new SocialProfile().withTitle(item.getTitle()).withUri(item.getLink());
                        representation.addPotentialProfile(presence, profile);
                    }
                }
            }

        }

        return representation;
    }

    private ExtendedSocialProfile getLinkedinCompanyProfile(ExtendedSocialProfile profile) throws IOException {
        Document bodyDiv = Jsoup.connect(profile.getUri()).get();
        setLinkedinCompanyLogoImage(profile, bodyDiv);
        setLinkedinCompanySummary(profile, bodyDiv);
        setLinkedinCompanyHomepage(profile, bodyDiv);
        return profile;
    }

    private void setLinkedinCompanyLogoImage(ExtendedSocialProfile extendedProfile, Document bodyDiv) {
        List<Element> logoDivs = bodyDiv.getElementsByClass("div.image-wrapper");
        for (Element logoDiv : logoDivs) {
            List<Element> logoImgs = logoDiv.getElementsByTag("img");
            for (Element logoImg : logoImgs) {
                extendedProfile.setImageUri(logoImg.attr("src"));
                return;
            }
            return;
        }
    }

    private void setLinkedinCompanySummary(ExtendedSocialProfile extendedProfile, Document bodyDiv) {
        List<Element> summaryDivs = bodyDiv.getElementsByClass("text-logo");
        for (Element summaryDiv : summaryDivs) {
            String summaryHtml = summaryDiv.html();
            if (summaryHtml == null) {
                return;
            }
            extendedProfile.setSummary(Jsoup.parse(summaryHtml).text());
        }
    }

    private void setLinkedinCompanyHomepage(ExtendedSocialProfile extendedProfile, Document bodyDiv) {
        List<Element> homepageLis = bodyDiv.getElementsByClass("website");
        for (Element homepageLi : homepageLis) {
            List<Element> homepageAs = homepageLi.getElementsByTag("a");
            for (Element homepageA : homepageAs) {
                extendedProfile.setHomepageUri(homepageA.text());
                return;
            }
            return;
        }
    }

    private void unpackLinkedinPersonProfile(SocialPresenceRepresentation representation, Item item) throws IOException {
        Document bodyDiv = Jsoup.connect(item.getLink()).get();
        List<Element> vCardLis = bodyDiv.getElementsByClass("vcard");
        for (Element vCardLi : vCardLis) {
            List<Element> profileAs = vCardLi.getElementsByClass("profile-photo");
            for (Element profileA : profileAs) {
                String extractedTitle = profileA.attr("title");
                if (extractedTitle == null) {
                    break;
                } else {
                    String title = WordUtils.capitalizeFully(extractedTitle);
                    String extractedPosition = getLinkedinPersonCurrentRole(vCardLi);
                    title = title + extractedPosition == null ? "" : " - " + WordUtils.capitalizeFully(extractedPosition);
                    ExtendedSocialProfile profile = new ExtendedSocialProfile().withTitle(title).withUri(profileA.attr("href"));
                    List<Element> portraitSrcs = profileA.getElementsByTag("src");
                    for (Element portraitSrc : portraitSrcs) {
                        profile.setImageUri(portraitSrc.attr("src"));
                    }
                    representation.addPotentialProfile(SocialPresence.LINKEDIN_PERSON, profile);
                }
            }
        }
    }

    private String getLinkedinPersonCurrentRole(Element vCardLi) {
        List<Element> currentContentDds = vCardLi.getElementsByClass("current-content");
        for (Element currentContentDd : currentContentDds) {
            List<Element> spans = currentContentDd.getElementsByTag("span");
            for (Element span : spans) {
                return span.text();
            }
        }
        return null;
    }

}
