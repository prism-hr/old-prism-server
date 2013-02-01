package com.zuehlke.pgadmissions.dto;

import java.util.List;

public class SendToPorticoDataDTO {

    List<Integer> referencesSendToPortico;
    
    List<Integer> qualificationsSendToPortico;

    public List<Integer> getReferencesSendToPortico() {
        return referencesSendToPortico;
    }

    public void setReferencesSendToPortico(List<Integer> referencesSendToPortico) {
        this.referencesSendToPortico = referencesSendToPortico;
    }

    public List<Integer> getQualificationsSendToPortico() {
        return qualificationsSendToPortico;
    }

    public void setQualificationsSendToPortico(List<Integer> qualificationsSendToPortico) {
        this.qualificationsSendToPortico = qualificationsSendToPortico;
    }
    
    

}