package com.zuehlke.pgadmissions.controllers.export;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zuehlke.pgadmissions.exceptions.XMLDataImportException;
import com.zuehlke.pgadmissions.services.importers.Importer;

@Controller
@RequestMapping("/importujProgramy")
public class ImportProgrammesTestController {

    @Autowired
    private List<Importer> importers;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public String sendMails() throws XMLDataImportException {
        importers.get(6).importData();
        return "Done!";
    }
}
