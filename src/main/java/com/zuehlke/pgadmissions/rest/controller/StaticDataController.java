package com.zuehlke.pgadmissions.rest.controller;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zuehlke.pgadmissions.services.StaticDataService;

@RestController
@RequestMapping("/api/static")
public class StaticDataController {

    @Inject
    private StaticDataService staticDataService;

    public Map<String, Object> getData() {
        return staticDataService.getData();
    }

}
