package com.zuehlke.pgadmissions.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zuehlke.pgadmissions.domain.PrismResourceDynamic;
import com.zuehlke.pgadmissions.domain.enums.PrismScope;
import com.zuehlke.pgadmissions.dto.PrismResourceDynamicDTO;
import com.zuehlke.pgadmissions.services.ResourceService;

@Controller
@RequestMapping("/resource")
public class ResourceController {
    
    @Autowired
    private ResourceService resourceService;

    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public <T extends PrismResourceDynamic> String getConsoleListBlock(@RequestParam PrismScope resourceTypeId, Integer page, Integer rowsPerPage) {
        try {
            Class<T> resourceType = (Class<T>) Class.forName("com.zuehlke.pgadmissions.domain." + resourceTypeId.getSimpleName());
            List<PrismResourceDynamicDTO> resourceListBlock = resourceService.getConsoleListBlock(resourceType, page, rowsPerPage);
            return "Parse to JSON ..." + resourceListBlock.toString();
        } catch (ClassNotFoundException e) {
            throw new Error("Requested resource list for invalid prism resource type", e);
        }
    }
    
}
