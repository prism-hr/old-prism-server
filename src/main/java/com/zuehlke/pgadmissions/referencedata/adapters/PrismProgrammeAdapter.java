package com.zuehlke.pgadmissions.referencedata.adapters;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.time.DateUtils;

import com.zuehlke.pgadmissions.domain.CodeObject;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgramInstanceInterface;
import com.zuehlke.pgadmissions.referencedata.v2.jaxb.ProgrammeOccurrences.ProgrammeOccurrence;
import com.zuehlke.pgadmissions.services.importers.ImportService;

public class PrismProgrammeAdapter implements ProgramInstanceInterface, ImportData {

    private ProgrammeOccurrence programme;

    public String getName() {
        return programme.getProgramme().getName();
    }

    public PrismProgrammeAdapter(ProgrammeOccurrence programme) {
        this.programme = programme;
    }

    @Override
    public String getCode() {
        return programme.getProgramme().getCode();
    }

    @Override
    public String getStudyOptionCode() {
        return programme.getModeOfAttendance().getCode();
    }

    @Override
    public String getStudyOption() {
        return programme.getModeOfAttendance().getName();
    }

    @Override
    public String getAcademic_year() {
        return programme.getAcademicYear();
    }

    @Override
    public String getIdentifier() {
        return programme.getIdentifier();
    }
    
    @Override
    public Boolean isAtasRequired() {
        return BooleanUtils.isTrue(programme.getProgramme().isAtasRegistered());
    }

    @Override
    public Date getApplicationStartDate() {
        try {
            return DateUtils.parseDate(programme.getStartDate(), new String[] { "yyyy-MM-dd", "dd-MMM-yy" });
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Date getApplicationDeadline() {
        try {
            return DateUtils.parseDate(programme.getEndDate(), new String[] { "yyyy-MM-dd", "dd-MMM-yy" });
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ProgramInstance createDomainObject(List<? extends CodeObject> currentData, List<? extends CodeObject> changes) {
        ProgramInstance result = new ProgramInstance();
        result.setAcademicYear(getAcademic_year());
        result.setApplicationDeadline(getApplicationDeadline());
        result.setApplicationStartDate(getApplicationStartDate());
        result.setStudyOption(getStudyOption());
        result.setStudyOptionCode(getStudyOptionCode());
        result.setIdentifier(getIdentifier());
        result.setEnabled(true);
        Program program = getProgramme(currentData, changes);
        program.setAtasRequired(isAtasRequired());
        result.setProgram(program);
        return result;
    }

    private Program getProgramme(List<? extends CodeObject> currentData, List<? extends CodeObject> changes) {
        Program program = getProgrammeBinary(currentData);
        if (program != null) {
            return program;
        }
        program = getProgrammeLineary(changes);
        if (program != null) {
            return program;
        }
        program = new Program();
        program.setCode(getCode());
        program.setTitle(getName());
        program.setEnabled(true);
        return program;
    }

    private Program getProgrammeLineary(List<? extends CodeObject> exsistingData) {
        ProgramInstance programInstance = (ProgramInstance) CollectionUtils.find(exsistingData, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                ProgramInstance instance = (ProgramInstance) object;
                return instance.getCode().equals(getCode()) && instance.getName().equals(getName());
            }
        });

        if (programInstance != null) {
            return programInstance.getProgram();
        } else {
            return null;
        }
    }

    private Program getProgrammeBinary(List<? extends CodeObject> exsistingData) {
        Program program;
        int i = Collections.binarySearch(exsistingData, this, ImportService.codeComparator);

        if (i >= 0) {
            program = ((ProgramInstance) exsistingData.get(i)).getProgram();
        } else {
            program = null;
        }
        return program;
    }
}
