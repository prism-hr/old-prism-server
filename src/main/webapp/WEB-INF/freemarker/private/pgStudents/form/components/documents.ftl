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
			<span class="error-hint" data-desc="Please provide all mandatory fields in this section."></span>             	
			<@spring.message 'documentsDetails.sectionInfo'/>
		</div>
		<#else>
		<div id="doc-info-bar-div" class="section-info-bar">
			<@spring.message 'documentsDetails.sectionInfo'/> 
		</div>	
		</#if>
		
		<div class="row-group">
			<div class="row">
				<span class="plain-label">Personal Statement (PDF)<em>*</em></span>
				<span class="hint" data-desc="<@spring.message 'supportingDocuments.personalStatement'/>"></span>	 
				<div class="field<#if applicationForm.personalStatement?? && applicationForm.personalStatement.fileName??> uploaded</#if>" id="psUploadFields">        	
					<input id="psDocument" class="full" data-type="PERSONAL_STATEMENT" data-reference="Personal Statement" type="file" name="file" value="" <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>disabled="disabled"</#if>/>					
					<span id="psUploadedDocument">
						<input type="hidden" class="file" id="document_PERSONAL_STATEMENT" value="${(encrypter.encrypt(applicationForm.personalStatement.id))!}"/>
						<input type="hidden" name="MAX_FILE_SIZE" value="500" />
						<a id="psLink" class="uploaded-filename" target="_blank" href="<@spring.url '/download?documentId=${(encrypter.encrypt(applicationForm.personalStatement.id))!}'/>">${(applicationForm.personalStatement.fileName?html)!}</a>
						<#if applicationForm.personalStatement?? && !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
						<a id="deletePs" data-desc="Change Personal Statement" class="button-delete button-hint">delete</a>
						</#if>
					</span>
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
				<div class="field<#if applicationForm.cv??> uploaded</#if>" id="cvUploadFields">        	
					<input id="cvDocument" class="full" type="file" data-type="CV" data-reference="CV" name="file" value="" <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>disabled="disabled"</#if>/>					
					<span id="cvUploadedDocument">
						<input type="hidden" class="file" id="document_CV" value="${(encrypter.encrypt(applicationForm.cv.id))!}"/>
						<a id="cvLink" class="uploaded-filename" target="_blank" href="<@spring.url '/download?documentId=${(encrypter.encrypt(applicationForm.cv.id))!}'/>">${(applicationForm.cv.fileName)!}</a>
						<#if  applicationForm.cv??  !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
						<a id="deleteCv" data-desc="Change CV" class="button-delete button-hint">delete</a>
						</#if>
					</span>
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
				<@spring.bind "applicationForm.acceptedTerms" />
		       	<#if spring.status.errorMessages?size &gt; 0>        
				    <div class="row-group terms-box invalid" >
		
		      	<#else>
		    		<div class="row-group terms-box" >
		     	 </#if>
			<div class="row">
				<span class="terms-label">
					Confirm that the information that you have provided in this section is true 
					and correct. Failure to provide true and correct information may result in a 
					subsequent offer of study being withdrawn.				
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
			<button type="button" class="clear" id="documentsClearButton">Clear</button>
			</#if>             
			<button type="button" class="blue" id="documentsCloseButton" value="close">Close</button>
			<#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()> 
			<button type="button" class="blue" id="documentsSaveButton" value="close">Save</button>
			</#if>      
		</div>

	</form>
</div>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/documents.js'/>"></script>
