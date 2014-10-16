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
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.user.User;
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
    
    @Value("${integration.linked.in.people.search.uri}")
    private String linkedinPeopleSearchUri;

    @Autowired
    private RestTemplate restTemplate;

    public SocialPresenceRepresentation getPotentialUserProfiles(String firstName, String lastName) throws IOException {        
        String encodedSearchTerm = URLEncoder.encode(firstName + lastName, "UTF-8");
        SocialPresenceRepresentation representation = getPotentialProfiles(User.class, encodedSearchTerm);
        
        String encodedFirstName = URLEncoder.encode(firstName, "UTF-8");
        String encodedLastName = URLEncoder.encode(lastName, "UTF-8");
        
        try {
            addLinkedinPersonProfiles(representation, encodedFirstName, encodedLastName);
        } catch (IOException e) {
            logger.error("Unable to parse linkedin person profile", e);
        }
        
        return representation;
    }
    
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

            boolean isLinkedinCompany = presence == SocialPresence.LINKEDIN_COMPANY;
            List<Item> items = response.getItems();

            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                String link = item.getLink();
                if (link.replace("http://", "https://").replaceAll(presence.getSearchEngineUri(), "").contains("/")) {
                    continue;
                } else {
                    if (isLinkedinCompany) {
                        try {
                            ExtendedSocialProfile profile = new ExtendedSocialProfile().withTitle(item.getTitle()).withUri(item.getLink());
                            representation.addPotentialProfile(presence, getLinkedinCompanyProfile(profile));
                        } catch (IOException e) {
                            logger.error("Unable to parse linkedin company profile", e);
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
        List<Element> logoDivs = bodyDiv.getElementsByClass("image-wrapper");
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

    private void addLinkedinPersonProfiles(SocialPresenceRepresentation representation, String encodedFirstName, String encodedLastName) throws IOException {
        Document bodyDiv = Jsoup.connect(linkedinPeopleSearchUri + "?first=" + encodedFirstName + "&last=" + encodedLastName).get();
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
                    title = title + (extractedPosition == null ? "" : title + " - " + WordUtils.capitalizeFully(extractedPosition));
                    ExtendedSocialProfile profile = new ExtendedSocialProfile().withTitle(title).withUri(profileA.attr("href"));
                    List<Element> portraitSrcs = profileA.getElementsByTag("img");
                    for (Element portraitSrc : portraitSrcs) {
                        profile.setImageUri(portraitSrc.attr("src"));
                    }
                    representation.addPotentialLinkedinProfile(profile);
                }
            }
        }
    }

    private String getLinkedinPersonCurrentRole(Element vCardLi) {
        List<Element> titleDds = vCardLi.getElementsByClass("title");
        for (Element titleDd : titleDds) {
            return titleDd.text();
        }
        return null;
    }

}
