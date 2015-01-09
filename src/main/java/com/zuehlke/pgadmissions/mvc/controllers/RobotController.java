package com.zuehlke.pgadmissions.mvc.controllers;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.services.ResourceService;

import freemarker.template.Template;

@Controller
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

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = "text/html")
    public String serve(@RequestParam String escapedFragment) throws Exception {
        String templateContent = Resources.toString(Resources.getResource("template/robot_representation.ftl"), Charsets.UTF_8);
        Template template = new Template("robot_representation", new StringReader(templateContent), freemarkerConfig.getConfiguration());
        Map<String, Object> model = Maps.newHashMap();
        String title = "PRiSM";
        String description = "The Opportunity Portal";
        String imageUrl = applicationUrl + "/images/fbimg.jpg";
        String ogUrl = applicationUrl;

        String fragment = URLDecoder.decode(escapedFragment, Charsets.UTF_8.name());
        int questionMarkIndex = fragment.lastIndexOf("?");
        String query = questionMarkIndex > -1 ? fragment.substring(questionMarkIndex + 1) : "";
        Map<String, String> queryMap = getQueryMap(query);

        if (queryMap.containsKey("institution")) {
            int resourceId = Integer.parseInt(queryMap.get("institution"));
            Institution institution = resourceService.getById(Institution.class, resourceId);
            if (institution != null) {
                title = institution.getTitle();
                description = institution.getDescription();
                ogUrl = applicationUrl + "/#!/?institution=" + resourceId;
                if (institution.getLogoDocument() != null) {
                    imageUrl = applicationApiUrl + "/images/" + institution.getLogoDocument().getId();
                }
            }
        } else if (queryMap.containsKey("program")) {
            int resourceId = Integer.parseInt(queryMap.get("program"));
            Program program = resourceService.getById(Program.class, resourceId);
            if (program != null) {
                title = program.getTitle();
                description = program.getAdvert().getDescription();
                ogUrl = applicationUrl + "/#!/?program=" + resourceId;
                if (program.getInstitution().getLogoDocument() != null) {
                    imageUrl = applicationApiUrl + "/images/" + program.getInstitution().getLogoDocument().getId();
                }
            }
        } else if (queryMap.containsKey("project")) {
            int resourceId = Integer.parseInt(queryMap.get("project"));
            Project project = resourceService.getById(Project.class, resourceId);
            if (project != null) {
                title = project.getTitle();
                description = project.getAdvert().getDescription();
                ogUrl = applicationUrl + "/#!/?project=" + resourceId;
                if (project.getInstitution().getLogoDocument() != null) {
                    imageUrl = applicationApiUrl + "/images/" + project.getInstitution().getLogoDocument().getId();
                }
            }
        }

        model.put("title", title);
        model.put("description", description);
        model.put("imageUrl", imageUrl);
        model.put("ogUrl", ogUrl);
        model.put("hostname", applicationUrl);
        model.put("body", getPageBody(ogUrl));

        return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
    }

    private String getPageBody(String url) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
        WebClient client = new WebClient();
        HtmlPage content = client.getPage(url);
        client.waitForBackgroundJavaScript(2000);
        return content.getElementByName("body").asXml();
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
