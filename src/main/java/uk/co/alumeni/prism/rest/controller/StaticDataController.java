package uk.co.alumeni.prism.rest.controller;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uk.co.alumeni.prism.mapping.StaticDataMapper;

@RestController
@RequestMapping("/api/static")
public class StaticDataController {

    @Inject
    private StaticDataMapper staticDataMapper;

    @RequestMapping
    public Map<String, Object> getData() {
        return staticDataMapper.getData();
    }

}
