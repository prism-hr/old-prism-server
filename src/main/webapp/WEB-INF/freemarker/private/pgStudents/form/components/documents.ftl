<#import "/spring.ftl" as spring />
<#assign errorCode = RequestParameters.errorCode! />
<#assign htmlEscape = true in spring>

<a name="documents-details"></a>
<h2 id="documents-H2" class="empty open">
	<span class="left"></span><span class="right"></span><span class="status"></span>
    Documents<em>*</em>
</h2>

<div>      
	<form> 
	
		<#if errorCode?? && errorCode=="true">
		<div class="section-error-bar">
			<div class="row">
				<span class="error-hint" data-desc="Please provide all mandatory fields in this section."></span>             	
				<span class="invalid-info-text">
					<@spring.message 'documentsDetails.sectionInfo'/>
				</span>
			</div>
		</div>
		<#else>
		<div id="doc-info-bar-div" class="section-info-bar">
			<div class="row">
				<span id="doc-info-bar-span" class="info-text">
					<@spring.message 'documentsDetails.sectionInfo'/> 
				</span>
			</div>
		</div>	
		</#if>
		
		<div class="row-group">
			<div class="row">
				<span class="plain-label">Personal Statement (PDF)<em>*</em></span>
				<span class="hint" data-desc="<@spring.message 'supportingDocuments.personalStatement'/>"></span>	 
				<div class="field<#if applicationForm.personalStatement.fileName??> uploaded</#if>" id="psUploadFields">        	
					<input id="psDocument" class="full" data-type="PERSONAL_STATEMENT" data-reference="Personal Statement" type="file" name="file" value="" <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>disabled="disabled"</#if>/>					
					<span id="psUploadedDocument"><input type="hidden" id="document_PERSONAL_STATEMENT" value = "${(encrypter.encrypt(applicationForm.personalStatement.id))!}"/>
					<input type="hidden" name="MAX_FILE_SIZE" value="500" />
					<a target="_blank" href="<@spring.url '/download?documentId=${(encrypter.encrypt(applicationForm.personalStatement.id))!}'/>">${(applicationForm.personalStatement.fileName?html)!}</a></span>
					<span id="psDocumentProgress" class="progress" style="display: none;"></span>					
				</div>  
			</div>
			<@spring.bind "applicationForm.personalStatement" /> 
			<#list spring.status.errorMessages as error>
			<div class="row">
				<div class="field">
					<span class="invalid">${error}</span>
				</div>
			</div>
			</#list>
						
			<div class="row">
				<span class="plain-label">CV / Resume (PDF)</span>
				<span class="hint" data-desc="<@spring.message 'supportingDocuments.cv'/>"></span>
				<div class="field<#if applicationForm.cv.fileName??> uploaded</#if>" id="cvUploadFields">        	
					<input id="cvDocument" class="full" type="file" data-type="CV" data-reference="CV" name="file" value="" <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>disabled="disabled"</#if>/>					
					<span id="cvUploadedDocument"><input type="hidden" id="document_CV" value="${(encrypter.encrypt(applicationForm.cv.id))!}"/>
					<a target="_blank" href="<@spring.url '/download?documentId=${(encrypter.encrypt(applicationForm.cv.id))!}'/>">${(applicationForm.cv.fileName)!}</a></span>
					<span id="cvDocumentProgress" class="progress" style="display: none;"></span>					
				</div>  
			</div>
	
			<@spring.bind "applicationForm.cv" /> 
			<#list spring.status.errorMessages as error>
			<div class="row">
				<div class="field">
					<span class="invalid">${error}</span>
				</div>
			</div>
			</#list>
			
		</div>
			
		<#if applicationForm.isModifiable() && !applicationForm.isInState('UNSUBMITTED')>
		<div class="row-group terms-box">
			<div class="row">
				<span class="terms-label">
					I understand that in accepting this declaration I am confirming
					that the information contained in this section is true and accurate. 
					I am aware that any subsequent offer of study may be retracted at any time
					if any of the information contained is found to be misleading or false.
				</span>
				<div class="terms-field">
					<input type="checkbox" name="acceptTermsDDCB" id="acceptTermsDDCB"/>
				</div>
				<input type="hidden" name="acceptTermsDDValue" id="acceptTermsDDValue"/>
			</div>	        
		</div>
		</#if>  
	
		<div class="buttons">
			<#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
			<button type="reset" id="documentsCancelButton" value="cancel">Clear</button>
			</#if>             
			<button class="blue" id="documentsCloseButton" value="close">Close</button>
			<#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()> 
			<button type="button" class="blue" id="documentsSaveButton" value="close">Save</button>
			</#if>      
		</div>

	</form>
</div>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/ajaxfileupload.js'/>"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/documents.js'/>"></script>
	
<@spring.bind "applicationForm.*" /> 
 
<#if (errorCode?? && errorCode=='false') || (message?? && message='close' && !spring.status.errorMessages?has_content)>	
<script type="text/javascript">
	$(document).ready(function(){
		$('#documents-H2').trigger('click');
	});
</script>
</#if>