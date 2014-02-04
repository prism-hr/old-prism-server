package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.DomicileDAO;
import com.zuehlke.pgadmissions.domain.Domicile;

@Service
@Transactional
public class DomicileService {

    private final DomicileDAO domicileDAO;
    
    public DomicileService() {
        this(null);
    }
    
    @Autowired
    public DomicileService(DomicileDAO dao) {
        this.domicileDAO = dao;
    }

    public List<Domicile> getAllEnabledDomiciles() {
        return domicileDAO.getAllEnabledDomiciles();
    }

    public List<Domicile> getAllEnabledDomicilesExceptAlternateValues() {
    	return domicileDAO.getAllEnabledDomicilesExceptAlternateValues();
    }

    public Domicile getDomicileById(Integer id) {
        return domicileDAO.getDomicileById(id);
    }
    
    public Domicile getEnabledDomicileByCode(String code) {
        return domicileDAO.getEnabledDomicileByCode(code);
    }
}
