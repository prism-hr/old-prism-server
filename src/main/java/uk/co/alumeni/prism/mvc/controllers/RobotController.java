package uk.co.alumeni.prism.mvc.controllers;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.exceptions.ResourceNotFoundException;
import uk.co.alumeni.prism.mapping.ResourceMapper;
import uk.co.alumeni.prism.mapping.SystemMapper;
import uk.co.alumeni.prism.services.ResourceService;
import uk.co.alumeni.prism.services.SystemService;
import uk.co.alumeni.prism.utils.PrismTemplateUtils;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.*;

@Controller
@RequestMapping("api/robots")
public class RobotController {

    @Value("${application.url}")
    private String applicationUrl;

    @Inject
    private ResourceService resourceService;

    @Inject
    private SystemService systemService;

    @Inject
    private ResourceMapper resourceMapper;

    @Inject
    private SystemMapper systemMapper;

    @Inject
    private PrismTemplateUtils prismTemplateUtils;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = "text/html")
    public String serve(@RequestParam String escapedFragment) throws Exception {
        Map<String, String> queryMap = getQueryMap(escapedFragment);

        PrismScope resourceScope = getQueryResourceScope(queryMap);
        Integer resourceId = getQueryResourceId(resourceScope, queryMap);

        if (resourceScope == null || resourceId == null) {
            throw new ResourceNotFoundException("Missing resource information");
        }

        Map<String, Object> model = Maps.newHashMap();
        model.put("metadata", resourceScope.equals(SYSTEM) ? systemMapper.getRobotsRepresentation()
                : resourceMapper.getResourceRepresentationRobot((ResourceParent) resourceService.getById(resourceScope, resourceId)));
        return prismTemplateUtils.getContentFromLocation("robot_representation.ftl", model);
    }

    private Map<String, String> getQueryMap(String escapedFragment) throws UnsupportedEncodingException {
        String fragment = URLDecoder.decode(escapedFragment, Charsets.UTF_8.name());
        int questionMarkIndex = fragment.lastIndexOf("?");
        String query = questionMarkIndex > -1 ? fragment.substring(questionMarkIndex + 1) : "";

        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params) {
            String[] split = param.split("=");
            if (split.length == 2) {
                String name = split[0];
                String value = split[1];
                map.put(name, value);
            }
        }
        return map;
    }

    private PrismScope getQueryResourceScope(Map<String, String> queryMap) {
        if (queryMap.containsKey("institution")) {
            return INSTITUTION;
        } else if (queryMap.containsKey("department")) {
            return DEPARTMENT;
        } else if (queryMap.containsKey("program")) {
            return PROGRAM;
        } else if (queryMap.containsKey("project")) {
            return PROJECT;
        }
        return SYSTEM;
    }

    private Integer getQueryResourceId(PrismScope resourceScope, Map<String, String> queryMap) {
        switch (resourceScope) {
        case INSTITUTION:
        case DEPARTMENT:
        case PROGRAM:
        case PROJECT:
            return Integer.parseInt(queryMap.get(resourceScope.getLowerCamelName()));
        case SYSTEM:
            return systemService.getSystem().getId();
        default:
            throw new UnsupportedOperationException();
        }
    }

}
