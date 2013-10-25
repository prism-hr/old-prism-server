<script type="text/javascript" src="<@spring.url '/design/default/js/scores.js' />"></script>

<@spring.bind "refereesAdminEditDTO.*"/>

<#assign isEditedReferee = 0>
<#if editedRefereeId?? &&
	encRefereeId == editedRefereeId>
	<#assign isEditedReferee = 1>
</#if>

<div class="row-group">
    <div class="row">
        <label for="refereeComment_${encRefereeId}" class="plain-label">Comment<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'interviewOutcome.comment'/>"></span>
        <div class="field">
            <textarea name="refereeComment" id="refereeComment_${encRefereeId}" class="max" rows="6" cols="80"><#if isEditedReferee == 1>${(refereesAdminEditDTO.comment?html)!}</#if></textarea>
             <@spring.bind "refereesAdminEditDTO.comment" />
                <#list spring.status.errorMessages as error>
                    <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error}</div>
                </#list>
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
              <input id="applicantRating_${encRefereeId}" name="applicantRating_${encRefereeId}" class="rating-input" type="number" value="<#if isEditedReferee == 1>${refereesAdminEditDTO.applicantRating!}</#if>" min="0" max="5" />
              
             <@spring.bind "refereesAdminEditDTO.applicantRating" />
              <#list spring.status.errorMessages as error>
              	<div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
              </#list>
            </div>
          
        </div>

        
    </div>
   
</div>

<div class="row-group">
    <div class="row">
        <label for="referenceDocument_${encRefereeId}" class="plain-label">Attach Document (PDF)</label>
        <span class="hint" data-desc="<@spring.message 'validateApp.document'/>"></span>
        <div class="field <#if isEditedReferee == 1 && refereesAdminEditDTO.referenceDocument??>uploaded</#if>" id="psUploadFields">
        
        	<div class="fileupload fileupload-new" data-provides="fileupload" <#if isEditedReferee == 1 && refereesAdminEditDTO.referenceDocument??>style="display:none"</#if>>
                <div class="input-append">
                  <div class="uneditable-input span4" > <i class="icon-file fileupload-exists"></i> <span class="fileupload-preview"></span> </div>
                  <span class="btn btn-file"><span class="fileupload-new">Select file</span><span class="fileupload-exists">Change</span>
                  <input id="referenceDocument_${encRefereeId}" type="file" value="" name="file" data-reference="" data-type="COMMENT" class="full">
                  <input type="hidden" class="file" id="document_COMMENT" value="<#if isEditedReferee == 1>${(encrypter.encrypt(refereesAdminEditDTO.referenceDocument.id))!}</#if>"/>
                </span> </div>
              </div>
              
          	<input type="hidden" name="MAX_FILE_SIZE" value="2097152" />
			<div id="commentDocumentProgress" class="progress" style="display: none;"></span> </div>
			<ul id="commentUploadedDocument"  class="uploaded-files">
            	<#if isEditedReferee == 1 && refereesAdminEditDTO.referenceDocument??> 
	            	<li class="done">
	                	<span class="uploaded-file" name="supportingDocumentSpan">
	                		<input type="text" style="display:none" name="documents" value="${(encrypter.encrypt(refereesAdminEditDTO.referenceDocument.id))!}" class="file">	
	                    	<a id="reference_document_link_${encRefereeId}" class="uploaded-filename" href="<@spring.url '/download?documentId=${(encrypter.encrypt(refereesAdminEditDTO.referenceDocument.id))!}'/>" target="_blank">
	                    	${(refereesAdminEditDTO.referenceDocument.fileName?html)!}</a>
	                    	<a id="reference_document_delete_${encRefereeId}" class="btn btn-danger delete" data-desc="Change reference document"><i class="icon-trash icon-large"></i> Delete</a>
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
            <label><input type="radio" name="suitableForUCL_${encRefereeId}" value="true" id="suitableUCL_true"
            <#if isEditedReferee == 1 && refereesAdminEditDTO.isSuitableForUCLSet() && refereesAdminEditDTO.suitableForUCL> checked="checked"</#if>
            /> Yes</label> 
            <label><input type="radio" name="suitableForUCL_${encRefereeId}" value="false" id="suitableUCL_false"
            <#if isEditedReferee == 1 && refereesAdminEditDTO.isSuitableForUCLSet() && !refereesAdminEditDTO.suitableForUCL> checked="checked"</#if>
            /> No</label>
            <@spring.bind "refereesAdminEditDTO.suitableForUCL" /> 
            <#list spring.status.errorMessages as error>
                <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error}</div>
            </#list>
    	</div>
    </div>
    <div class="row multi-line" id="field-issuitableprog">
        <span id="supervise-lbl" class="plain-label">Is the applicant suitable for their chosen postgraduate study programme?<em>*</em></span>
        <span class="hint" data-desc="<@spring.message 'interviewOutcome.suitsPGP'/>"></span>
        <div class="field">
            <label><input type="radio" name="suitableForProgramme_${encRefereeId}" id="suitableProgramme_true" value="true"
            <#if isEditedReferee == 1 && refereesAdminEditDTO.isSuitableForProgrammeSet() && refereesAdminEditDTO.suitableForProgramme> checked="checked"</#if> 
            /> Yes</label> 
            <label><input type="radio" name="suitableForProgramme_${encRefereeId}" id="suitableProgramme_false" value="false"
            <#if isEditedReferee == 1 && refereesAdminEditDTO.isSuitableForProgrammeSet() && !refereesAdminEditDTO.suitableForProgramme> checked="checked"</#if>
            /> No</label> 
            <@spring.bind "refereesAdminEditDTO.suitableForProgramme" /> 
            <#list spring.status.errorMessages as error> 
            	<div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
            </#list>
        </div>
        
        <#if isEditedReferee == 1>
			<#assign scores = refereesAdminEditDTO.scores>
			<#if refereesAdminEditDTO.alert??>
				<#assign alertForScoringQuestions=refereesAdminEditDTO.alert>
			</#if>
		</#if>
        
        <#if !(scores)?has_content>
	        <!-- Add reference add button -->
	        <div class="row">
	            <div class="field">
	                <button id="addReferenceButton_${encRefereeId}" type="button" class="btn btn-primary addReferenceButton">Add Reference</button>
	            </div>
	        </div>
        </#if>      

    </div>
   
    <#if isEditedReferee == 1>
	    <#assign anyReferenceErrors = spring.status.errorMessages?size &gt; 0>
	    <input type="hidden" name="anyReferenceErrors" id="anyReferenceErrors" value="${anyReferenceErrors?string}"/>
	</#if>
    
</div>

<#if isEditedReferee == 1>
	<#assign scores = refereesAdminEditDTO.scores>
	<#if refereesAdminEditDTO.alert??>
		<#assign alertForScoringQuestions=refereesAdminEditDTO.alert>
	</#if>
</#if>

<#if (scores)?has_content>
	<div class="row-group">
	    <div id="scoring-questions_${encRefereeId}">
	      <#assign errorsContainerName = "refereesAdminEditDTO">
	      <h3>Programme Specific Questions</h3>
	      <#include "/private/staff/scores.ftl"/>
	    </div>
	    <div class="row">
	    	<div class="field">
	        	<button id="addReferenceButton_${encRefereeId}" type="button" class="btn btn-primary addReferenceButton">Add Reference</button>
	        </div>
	    </div>
	</div>
</#if>