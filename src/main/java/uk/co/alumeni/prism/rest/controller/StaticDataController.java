package uk.co.alumeni.prism.rest.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.alumeni.prism.mapping.StaticDataMapper;

import javax.inject.Inject;
import java.util.Map;

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
