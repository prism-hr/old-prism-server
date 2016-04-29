package uk.co.alumeni.prism.rest.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.exceptions.ResourceNotFoundException;
import uk.co.alumeni.prism.mapping.AdvertMapper;
import uk.co.alumeni.prism.rest.dto.OpportunitiesQueryDTO;
import uk.co.alumeni.prism.rest.representation.advert.AdvertListRepresentation;
import uk.co.alumeni.prism.rest.representation.advert.AdvertLocationAddressPartRepresentation;
import uk.co.alumeni.prism.rest.representation.advert.AdvertRepresentationExtended;
import uk.co.alumeni.prism.services.AdvertService;
import uk.co.alumeni.prism.services.WidgetService;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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
    public AdvertListRepresentation getAdverts(OpportunitiesQueryDTO query) {
        AdvertListRepresentation representation = advertMapper.getAdvertExtendedRepresentations(query);
        return representation;
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
    public String getAdvertBadge(@PathVariable String resourceScope, @PathVariable Integer resourceId,
                                 @RequestParam Optional<String> callback,
                                 @RequestParam String options,
                                 HttpServletResponse response) {
        response.setHeader("X-Frame-Options", null);
        Advert advert = advertService.getAdvert(PrismScope.valueOf(removeEnd(resourceScope, "s").toUpperCase()), resourceId);
        Type mapType = new TypeToken<HashMap<String, String>>() {
        }.getType();
        HashMap<String, String> widgetOptions = new Gson().fromJson(options, mapType);
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

    @RequestMapping(method = RequestMethod.GET, value = "locations")
    public List<AdvertLocationAddressPartRepresentation> getAdvertLocationAddressPartRepresentations(@RequestParam String q) {
        return advertMapper.getAdvertLocationAddressPartRepresentations(q);
    }

}
