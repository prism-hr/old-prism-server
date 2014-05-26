package com.zuehlke.pgadmissions.domain;

import java.util.HashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.search.annotations.Indexed;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Entity
@Table(name = "SYSTEM")
@Indexed
public class System extends PrismResource {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void setState(State state) {
        this.state = state;
    }
    
    @Override
    public System getSystem() {
        return this;
    }
    
    @Override
    public void setSystem(System system) {
    }

    @Override
    public Institution getInstitution() {
        return null;
    }
    
    @Override
    public void setInstitution(Institution institution) {
    }

    @Override
    public Program getProgram() {
        return null;
    }
    
    @Override
    public void setProgram(Program program) {  
    }
    
    @Override
    public Project getProject() {
        return null;
    }

    @Override
    public void setProject(Project project) {
    }

    @Override
    public Application getApplication() {
        return null;
    }
    
    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }
    
    @Override
    public UniqueResourceSignature getUniqueResourceSignature() {
        List<HashMap<String, Object>> propertiesWrapper = Lists.newArrayList();
        HashMap<String, Object> properties = Maps.newHashMap();
        properties.put("name", name);
        propertiesWrapper.add(properties);
        return new UniqueResourceSignature(propertiesWrapper, null);
    }
    
}
