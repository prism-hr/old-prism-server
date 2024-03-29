package uk.co.alumeni.prism.rest.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.exceptions.ResourceNotFoundException;
import uk.co.alumeni.prism.mapping.AdvertMapper;
import uk.co.alumeni.prism.rest.dto.OpportunityQueryDTO;
import uk.co.alumeni.prism.rest.representation.advert.*;
import uk.co.alumeni.prism.services.AdvertService;
import uk.co.alumeni.prism.services.WidgetService;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.removeEnd;

@RestController
@RequestMapping("/api/opportunities")
@PreAuthorize("permitAll")
public class OpportunityController {

    @Inject
    private AdvertService advertService;

    @Inject
    private AdvertMapper advertMapper;

    @Inject
    private WidgetService widgetService;

    @RequestMapping(method = RequestMethod.GET)
    public AdvertListRepresentation getAdverts(OpportunityQueryDTO query) {
        return advertMapper.getAdvertExtendedRepresentations(query);
    }

    @RequestMapping(method = RequestMethod.GET, value = "{resourceScope:projects|programs|departments|institutions}/{resourceId}")
    public AdvertRepresentationExtended getAdvert(@PathVariable String resourceScope, @PathVariable Integer resourceId) {
        Advert advert = advertService.getAdvert(PrismScope.valueOf(removeEnd(resourceScope, "s").toUpperCase()), resourceId);
        if (advert == null) {
            throw new ResourceNotFoundException("Advert not found");
        }
        return advertMapper.getAdvertRepresentationExtended(advert);
    }

    @RequestMapping(method = RequestMethod.GET, value = "{resourceScope:projects|programs|departments|institutions}/{resourceId}/badge", produces = "text/javascript")
    public String getAdvertBadge(@PathVariable String resourceScope, @PathVariable Integer resourceId, @RequestParam Optional<String> callback,
            @RequestParam String options, HttpServletResponse response) {
        response.setHeader("X-Frame-Options", null);
        Advert advert = advertService.getAdvert(PrismScope.valueOf(removeEnd(resourceScope, "s").toUpperCase()), resourceId);
        HashMap<String, String> widgetOptions = new Gson().fromJson(options, new TypeToken<HashMap<String, String>>() {
        }.getType());
        String badge = widgetService.getAdvertBadge(advert, widgetOptions);
        if (callback.isPresent()) {
            response.setHeader("content-type", "text/javascript");
            badge = new Gson().toJson(Collections.singletonMap("html", badge));
            return callback.get() + "(" + badge + ")";
        } else {
            response.setHeader("content-type", "text/html");
            return badge;
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "filterValues/industries")
    public List<AdvertIndustrySummaryRepresentation> getAdvertIndustrySummaryRepresentations(OpportunityQueryDTO query, @RequestParam(required = false) String q) {
        return advertMapper.getAdvertIndustrySummaryRepresentations(query, q);
    }

    @RequestMapping(method = RequestMethod.GET, value = "filterValues/functions")
    public List<AdvertFunctionSummaryRepresentation> getAdvertFunctionSummaryRepresentations(OpportunityQueryDTO query, @RequestParam(required = false) String q) {
        return advertMapper.getAdvertFunctionSummaryRepresentations(query, q);
    }

    @RequestMapping(method = RequestMethod.GET, value = "filterValues/themes")
    public List<AdvertThemeSummaryRepresentation> getAdvertThemeSummaryRepresentations(OpportunityQueryDTO query, @RequestParam(required = false) String q) {
        return advertMapper.getAdvertThemeSummaryRepresentations(query, q);
    }

    @RequestMapping(method = RequestMethod.GET, value = "filterValues/locations")
    public List<AdvertLocationSummaryRepresentation> getAdvertLocationSummaryRepresentations(OpportunityQueryDTO query, @RequestParam(required = false) String q) {
        return advertMapper.getAdvertLocationSummaryRepresentations(query, q);
    }

    @RequestMapping(method = RequestMethod.GET, value = "filterValues/locations", params = "ids")
    public List<AdvertLocationSummaryRepresentation> getAdvertLocationSummaryRepresentations(String ids) {
        return advertMapper.getAdvertLocationSummaryRepresentations(Stream.of(ids.split(",")).map(Integer::parseInt).collect(toList()));
    }

    @RequestMapping(method = RequestMethod.GET, value = "filterValues/institutions")
    public List<AdvertInstitutionSummaryRepresentation> getInstitutionSummaryRepresentations(OpportunityQueryDTO query, @RequestParam(required = false) String q) {
        return advertMapper.getAdvertInstitutionSummaryRepresentations(query, q);
    }

}
