package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;

public class ConcreteAdvert extends Advert {

    private static final long serialVersionUID = -4660798281252513600L;

    @Override
    public Program getProgram() {
        return null;
    }

    @Override
    public Project getProject() {
        return null;
    }

}
