<div class="row-group">
    <div class="row">
        <label class="plain-label" for="refereeComment_${encRefereeId}">Comment<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'interviewOutcome.comment'/>"></span>
        <div class="field">
            <textarea name="refereeComment" id="refereeComment_${encRefereeId}" class="max" rows="6" cols="80" >${(referee.reference.comment?html)!}</textarea>
            
        </div>
        
        <div class="row multi-line"> 
            <label id="supervise-work-lbl" class="plain-label"><@spring.message 'feedbackComment.applicantRating'/><em>*</em></label> 
            <span class="hint" data-desc="<@spring.message 'interviewOutcome.applicantRating'/>"></span>
            <div class="field" id="field-applicantRating">
              <ul class="rating-list clearfix">
                <li><i class="icon-thumbs-down"></i></li>
                <li><i class="icon-star-empty"></i></li>
                <li><i class="icon-star-empty"></i></li>
                <li><i class="icon-star-empty"></i></li>
                <li><i class="icon-star-empty"></i></li>
                <li><i class="icon-star-empty"></i></li>
              </ul>
              <input id="applicantRating_${encRefereeId}" name="applicantRating_${encRefereeId}" class="rating-input" type="number" value="${referee.reference.applicantRating!}" min="0" max="5" />
            </div>
          
        </div>

    </div>
    
</div>

<div class="row-group">
    <div class="row">
        <label class="plain-label" for="referenceDocument_${encRefereeId}">Attach Document (PDF)</label>
        <span class="hint" data-desc="<@spring.message 'validateApp.document'/>"></span>
        <div class="field <#if referee.reference.documents[0]??>uploaded</#if>" id="psUploadFields">
        
        	<div class="fileupload fileupload-new" data-provides="fileupload">
                <div class="input-append">
                  <div class="uneditable-input span4" > <i class="icon-file fileupload-exists"></i> <span class="fileupload-preview"></span> </div>
                  <span class="btn btn-file"><span class="fileupload-new">Select file</span><span class="fileupload-exists">Change</span>
                  <input id="referenceDocument_${encRefereeId}" type="file" value="" name="file" data-reference="" data-type="COMMENT" class="full">
                  <input type="hidden" class="file" id="document_COMMENT" value="${(encrypter.encrypt(referee.reference.documents[0].id))!}"/>
                </span> </div>
              </div>
        
            
            <ul id="psUploadedDocument" class="uploaded-files">
                <#if referee.reference.documents[0]??> 
                <li class="done">
                    <span class="uploaded-file" name="supportingDocumentSpan">
                    <a id="lqLink" class="uploaded-filename" href="<@spring.url '/download?documentId=${(encrypter.encrypt(referee.reference.documents[0].id))!}'/>" target="_blank">
                    ${(referee.reference.documents[0].fileName?html)!}</a>
                    <a id="deleteLq" class="btn btn-danger delete" data-desc="Change reference document"><i class="icon-trash icon-large"></i> Delete</a> 
                    </span>
                </li>
                </#if>
            </ul>

        </div>
    </div>
        
</div>

<div class="row-group">
    <h3>Applicant Suitability</h3>

    <div class="row">
        <span id="suitable-lbl" class="plain-label">Is the applicant suitable for postgraduate study at UCL?<em>*</em></span>
        <span class="hint" data-desc="<@spring.message 'interviewOutcome.suitsPG'/>"></span>
        <div class="field" id="field-issuitableucl">
            <label><input type="radio" name="suitableForUCL_${encRefereeId}" value="true" id="suitableRB_true"
            <#if referee.reference.isSuitableForUCLSet() && referee.reference.suitableForUCL> checked="checked"</#if>
            /> Yes</label> 
            <label><input type="radio" name="suitableForUCL_${encRefereeId}" value="false" id="suitableRB_false"
            <#if referee.reference.isSuitableForUCLSet() && !referee.reference.suitableForUCL> checked="checked"</#if>
            /> No</label>
        </div>
    </div>
    <div class="row multi-line" id="field-issuitableprog">
        <span id="supervise-lbl" class="plain-label">Is the applicant suitable for their chosen postgraduate study programme?<em>*</em></span>
        <span class="hint" data-desc="<@spring.message 'interviewOutcome.suitsPGP'/>"></span>
        <div class="field">
            <label><input type="radio" name="suitableForProgramme_${encRefereeId}" value="true" id="willingRB_true"
            <#if referee.reference.isSuitableForProgrammeSet() && referee.reference.suitableForProgramme> checked="checked"</#if> 
            /> Yes</label> 
            <label><input type="radio" name="suitableForProgramme_${encRefereeId}" value="false" id="willingRB_false"
            <#if referee.reference.isSuitableForProgrammeSet() && !referee.reference.suitableForProgramme> checked="checked"</#if>
            /> No</label> 
        </div>
    </div>
    
    <@spring.bind "refereesAdminEditDTO.*" />
     
    <#assign anyReferenceErrors = spring.status.errorMessages?size &gt; 0>
    <input type="hidden" name="anyReferenceErrors" id="anyReferenceErrors" value="${anyReferenceErrors?string}" />
    
    <!-- Add reference add button -->
    <div class="row">
        <div class="field">
            <button id="editReferenceButton" type="button" class="btn btn-primary">Update Reference</button>
        </div>
    </div>
    
   
    
</div>

