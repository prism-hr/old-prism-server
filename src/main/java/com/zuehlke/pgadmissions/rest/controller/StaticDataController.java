package com.zuehlke.pgadmissions.rest.controller;

import com.zuehlke.pgadmissions.services.StaticDataService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Map;

@RestController
@RequestMapping("/api/static")
public class StaticDataController {

    @Inject
    private StaticDataService staticDataService;

    @RequestMapping
    public Map<String, Object> getData() {
        return staticDataService.getData();
    }

}
