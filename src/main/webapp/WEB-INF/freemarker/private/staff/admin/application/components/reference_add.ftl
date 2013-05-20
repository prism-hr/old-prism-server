<script type="text/javascript" src="<@spring.url '/design/default/js/scores.js' />"></script>

<div class="row-group">
    <div class="row">
        <label for="refereeComment_${encRefereeId}" class="plain-label">Comment<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'interviewOutcome.comment'/>"></span>
        <div class="field">
            <textarea name="refereeComment" id="refereeComment_${encRefereeId}" class="max" rows="6" cols="80" >${(refereesAdminEditDTO.comment?html)!}</textarea>
             <@spring.bind "refereesAdminEditDTO.comment" />
                <#list spring.status.errorMessages as error>
                    <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error}</div>
                </#list>
        </div>
    </div>
   
</div>

<div class="row-group">
    <div class="row">
        <label for="referenceDocument_${encRefereeId}" class="plain-label">Attach Document (PDF)</label>
        <span class="hint" data-desc="<@spring.message 'validateApp.document'/>"></span>
        <div class="field <#if refereesAdminEditDTO.referenceDocument??>uploaded</#if>" id="psUploadFields">
        
        	<div class="fileupload fileupload-new" data-provides="fileupload">
                <div class="input-append">
                  <div class="uneditable-input span4" > <i class="icon-file fileupload-exists"></i> <span class="fileupload-preview"></span> </div>
                  <span class="btn btn-file"><span class="fileupload-new">Select file</span><span class="fileupload-exists">Change</span>
                  <input id="referenceDocument_${encRefereeId}" type="file" value="" name="file" data-reference="" data-type="COMMENT" class="full">
                  <input type="hidden" class="file" id="document_COMMENT" value="${(encrypter.encrypt(refereesAdminEditDTO.referenceDocument.id))!}"/>
                </span> </div>
              </div>
            
            <ul id="psUploadedDocument" class="uploaded-files">
            	
                <#if refereesAdminEditDTO.referenceDocument??> 
                <li class="done">
                    <span class="uploaded-file" name="supportingDocumentSpan">
                        <a id="lqLink" class="uploaded-filename" href="<@spring.url '/download?documentId=${(encrypter.encrypt(refereesAdminEditDTO.referenceDocument.id))!}'/>" target="_blank">
                        ${(refereesAdminEditDTO.referenceDocument.fileName?html)!}</a>
                        <a id="deleteLq" class="btn btn-danger delete" data-desc="Change reference document"><i class="icon-trash icon-large"></i> Delete</a>
                    </span>
                </li>
                </#if>
            </ul>
            <@spring.bind "refereesAdminEditDTO.referenceDocument" /> 
            <#list spring.status.errorMessages as error>
                <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error}</div>
            </#list>
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
            <#if refereesAdminEditDTO.isSuitableForUCLSet() && refereesAdminEditDTO.suitableForUCL> checked="checked"</#if>
            /> Yes</label> 
            <label><input type="radio" name="suitableForUCL_${encRefereeId}" value="false" id="suitableRB_false"
            <#if refereesAdminEditDTO.isSuitableForUCLSet() && !refereesAdminEditDTO.suitableForUCL> checked="checked"</#if>
            /> No</label>
            <@spring.bind "refereesAdminEditDTO.suitableForUCL" /> 
                <#list spring.status.errorMessages as error> <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error}</div></#list>
            </div>
    </div>
    <div class="row multi-line" id="field-issuitableprog">
        <span id="supervise-lbl" class="plain-label">Is the applicant suitable for their chosen postgraduate study programme?<em>*</em></span>
        <span class="hint" data-desc="<@spring.message 'interviewOutcome.suitsPGP'/>"></span>
        <div class="field">
            <label><input type="radio" name="suitableForProgramme_${encRefereeId}" value="true" id="willingRB_true"
            <#if refereesAdminEditDTO.isSuitableForProgrammeSet() && refereesAdminEditDTO.suitableForProgramme> checked="checked"</#if> 
            /> Yes</label> 
            <label><input type="radio" name="suitableForProgramme_${encRefereeId}" value="false" id="willingRB_false"
            <#if refereesAdminEditDTO.isSuitableForProgrammeSet() && !refereesAdminEditDTO.suitableForProgramme> checked="checked"</#if>
            /> No</label> 
            <@spring.bind "refereesAdminEditDTO.suitableForProgramme" /> 
            <#list spring.status.errorMessages as error> 
            <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
            </#list>
        </div>
        <#if scores!?size == 0>
        <!-- Add reference add button -->
        <div class="row">
            <div class="field">
                <button id="addReferenceButton" type="button" class="btn btn-primary">Add Reference</button>
            </div>
        </div>
        </#if>
    </div>
    	<@spring.bind "refereesAdminEditDTO.*" />
     
    	<#assign anyReferenceErrors = spring.status.errorMessages?size &gt; 0>
    	<input type="hidden" name="anyReferenceErrors" id="anyReferenceErrors" value="${anyReferenceErrors?string}" />
    
</div>

	
<#assign scores = refereesAdminEditDTO.scores>
<#if refereesAdminEditDTO.alert??>
	<#assign alertForScoringQuestions=refereesAdminEditDTO.alert>
</#if>
<#if (scores)?has_content>
<div class="row-group">
    <div id="scoring-questions_${encRefereeId}">
      <#assign errorsContainerName = "refereesAdminEditDTO">
      <h3>Programme Specific Questions</h3>
      <#include "/private/staff/scores.ftl"/>
    </div>
    <!-- Add reference add button -->
    <div class="row">
        <div class="field">
            <button id="addReferenceButton" type="button" class="btn btn-primary">Add Reference</button>
        </div>
    </div>
</div>
</#if>
    