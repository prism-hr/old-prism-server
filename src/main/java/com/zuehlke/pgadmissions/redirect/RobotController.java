package com.zuehlke.pgadmissions.redirect;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

@RestController
@RequestMapping("api/robots")
public class RobotController {

    @Autowired
    private FreeMarkerConfig freemarkerConfig;

    @Value("${application.url}")
    private String applicationUrl;

    @RequestMapping(method = RequestMethod.GET)
    public String serve(@RequestParam) throws IOException, TemplateException {
        String templateContent = Resources.toString(Resources.getResource("template/robot_representation.ftl"), Charsets.UTF_8);
        Template template = new Template("robot_representation", new StringReader(templateContent), freemarkerConfig.getConfiguration());

        Map<String, Object> model = Maps.newHashMap();
        model.put("title", "ttt");
        model.put("description", "ddd");
        model.put("hostname", "applicationUrl");

        return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
    }

}
