<#import "/spring.ftl" as spring />
<#assign errorCode = RequestParameters.errorCode! />
<#assign htmlEscape = true in spring>
<#setting locale = "en_US"> <a name="documents-details"></a>
<h2 id="documents-H2" class="empty"> <span class="left"></span><span class="right"></span><span class="status"></span> Documents<em>*</em> </h2>
<div style="display:none;">
  <form>
  <#if errorCode?? && errorCode=="true">
    <div class="alert alert-error"> <i class="icon-warning-sign" data-desc="Please complete all of the mandatory fields in this section."></i> 
      <@spring.message 'documentsDetails.sectionInfo'/>
    </div>
    <#else>
    <div class="alert alert-info"> <i class="icon-info-sign"></i>
      <@spring.message 'documentsDetails.sectionInfo'/>
    </div>
    </#if>
 	
    <!--Personal Statement 1-->
    <div class="row-group">
    	<div class="row"> 
      <label class="plain-label" for="psDocument">Personal Statement (PDF)<em>*</em></label> 
      <span class="hint" data-desc="<@spring.message 'supportingDocuments.personalStatement'/>"></span>
        <div class="field<#if documentsSectionDTO.personalStatement??> uploaded</#if>" id="psUploadFields"> 
       		
            <div class="fileupload fileupload-new" data-provides="fileupload">
                <div class="input-append">
                  <div class="uneditable-input span4" > <i class="icon-file fileupload-exists"></i> <span class="fileupload-preview"></span> </div>
                  <span class="btn btn-file"><span class="fileupload-new">Select file</span><span class="fileupload-exists">Change</span>
                  <input id="psDocument" class="full" data-type="PERSONAL_STATEMENT" data-reference="Personal Statement" type="file" name="file" value="" /> 
                </span> </div>
              </div>
        
        
        <ul id="psUploadedDocument" class="uploaded-files">
          <#if documentsSectionDTO.personalStatement??>
            <#assign ps = documentsSectionDTO.personalStatement>
            <li class="done">
            	<span class="uploaded-file" name="supportingDocumentSpan">
                <input type="hidden" class="file" id="document_PERSONAL_STATEMENT" value="${(encrypter.encrypt(ps.id))!}"/>
                <input type="hidden" name="MAX_FILE_SIZE" value="2097152" />
                <a id="psLink" class="uploaded-filename" target="_blank" href="<@spring.url '/download?documentId=${(encrypter.encrypt(ps.id))!}'/>">
                ${(ps.fileName?html)!}
                </a> 
                <a id="deletePs" data-desc="Change Personal Statement" class="btn btn-danger delete"><i class="icon-trash icon-large"></i> Delete</a> 
                </span>
            </li>
          </#if>
      </ul>

      
          <@spring.bind "documentsSectionDTO.personalStatement" />
          <#list spring.status.errorMessages as error>
              <div class="alert alert-error"> 
                <i class="icon-warning-sign"></i>
                    ${error}
              </div>
          </#list>
          
          </div>
      </div>

        <div class="row">
          <label class="plain-label" for="cvDocument">CV / Resume (PDF)</label>
          <span class="hint" data-desc="<@spring.message 'supportingDocuments.cv'/>"></span>
          <div class="field<#if documentsSectionDTO.cv??> uploaded</#if>" id="cvUploadFields">
          	<div class="fileupload fileupload-new" data-provides="fileupload">
              <div class="input-append">
                <div class="uneditable-input span4" > <i class="icon-file fileupload-exists"></i> <span class="fileupload-preview"></span> </div>
                <span class="btn btn-file"><span class="fileupload-new">Select file</span><span class="fileupload-exists">Change</span>
                  <input id="cvDocument" class="full" type="file" data-type="CV" data-reference="CV" name="file" value=""/>
                </span>
              </div>
            </div>
            
            <ul id="cvUploadedDocument" class="uploaded-files">
              <#if documentsSectionDTO.cv??>
                <#assign cv = documentsSectionDTO.cv>
                <li class="done">
                  <span class="uploaded-file" name="supportingDocumentSpan">
                    <input type="hidden" class="file" id="document_CV" value="${(encrypter.encrypt(cv.id))!}"/>
                    <a id="cvLink" class="uploaded-filename" target="_blank" href="<@spring.url '/download?documentId=${(encrypter.encrypt(cv.id))!}'/>">
                    ${(cv.fileName)!}
                    </a>
                    <a id="deleteCv" data-desc="Change CV" class="btn btn-danger delete"><i class="icon-trash icon-large"></i> Delete</a>
                  </span>
                </li>
              </#if>
            </ul>
            <@spring.bind "documentsSectionDTO.cv" />
            <#list spring.status.errorMessages as error>
              <div class="alert alert-error"> <i class="icon-warning-sign"></i>
                ${error}
              </div>
            </#list>
          </div>
            
        </div>
    </div>
    <#if applicationForm.isModifiable() && !applicationForm.isInState('UNSUBMITTED')>
    <@spring.bind "applicationForm.acceptedTerms" />
    <#if spring.status.errorMessages?size &gt; 0>
     <div class="alert alert-error tac" >
    <#else>
        <div class="alert tac" >
    </#if>
      <div class="row"> 
      <label for="acceptTermsDDCB" class="terms-label"> Confirm that the information that you have provided in this section is true 
        and correct. Failure to provide true and correct information may result in a 
        subsequent offer of study being withdrawn. </label>
        
        <div class="terms-field">
          <input type="checkbox" name="acceptTermsDDCB" id="acceptTermsDDCB"/>
        </div>
        <input type="hidden" name="acceptTermsDDValue" id="acceptTermsDDValue"/>
      </div>
    </div>
    </#if>
    
    <!--Buttons-->
    <div class="buttons"> <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
      <button type="button" class="btn" id="documentsClearButton">Clear</button>
      </#if>
      <button type="button" class="btn" id="documentsCloseButton" value="close">Close</button>
      <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
      <button type="button" class="btn btn-primary" id="documentsSaveButton" value="close">Save</button>
      </#if> </div>
  </form>
</div>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/documents.js'/>"></script> 
