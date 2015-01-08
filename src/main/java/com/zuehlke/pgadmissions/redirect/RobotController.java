package com.zuehlke.pgadmissions.redirect;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.services.ResourceService;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.StringReader;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/robots")
public class RobotController {

    @Autowired
    private FreeMarkerConfig freemarkerConfig;

    @Autowired
    private ResourceService resourceService;

    @Value("${application.url}")
    private String applicationUrl;

    @Value("${application.api.url}")
    private String applicationApiUrl;

    @RequestMapping(method = RequestMethod.GET)
    public String serve(@RequestParam String escapedFragment) throws Exception {
        String templateContent = Resources.toString(Resources.getResource("template/robot_representation.ftl"), Charsets.UTF_8);
        Template template = new Template("robot_representation", new StringReader(templateContent), freemarkerConfig.getConfiguration());
        Map<String, Object> model = Maps.newHashMap();
        String title = "PRiSM";
        String description = "The Opportunity Portal";
        String imageUrl = "http://www.prism.ucl.ac.uk/images/fbimg.jpg";

        String fragment = URLDecoder.decode(escapedFragment, Charsets.UTF_8.name());
        int questionMarkIndex = fragment.lastIndexOf("?");
        String query = questionMarkIndex > -1 ? fragment.substring(questionMarkIndex + 1) : "";
        Map<String, String> queryMap = getQueryMap(query);

        PrismScope resourceScope = null;
        Integer resourceId = null;
        if (queryMap.containsKey("institution")) {
            resourceId = Integer.parseInt(queryMap.get("institution"));
            Institution institution = resourceService.getById(Institution.class, resourceId);
            title = institution.getTitle();
            description = institution.getDescription();
            if(institution.getLogoDocument() != null) {
                imageUrl = applicationApiUrl + "/images/" + institution.getLogoDocument().getId();
            }
        } else if (queryMap.containsKey("program")) {
            resourceId = Integer.parseInt(queryMap.get("program"));
            Program program = resourceService.getById(Program.class, resourceId);
            title = program.getTitle();
            description = program.getAdvert().getDescription();
            if(program.getInstitution().getLogoDocument() != null) {
                imageUrl = applicationApiUrl + "/images/" + program.getInstitution().getLogoDocument().getId();
            }
        } else if (queryMap.containsKey("project")) {
            resourceId = Integer.parseInt(queryMap.get("project"));
            Project project = resourceService.getById(Project.class, resourceId);
            title = project.getTitle();
            description = project.getAdvert().getDescription();
            if(project.getInstitution().getLogoDocument() != null) {
                imageUrl = applicationApiUrl + "/images/" + project.getInstitution().getLogoDocument().getId();
            }
        }

        System.out.println("Escaped query: " + query);
        model.put("title", title);
        model.put("description", description);
        model.put("imageUrl", imageUrl);
        model.put("hostname", applicationUrl);

        return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
    }

    private Map<String, String> getQueryMap(String query) {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params) {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }
        return map;
    }

}
