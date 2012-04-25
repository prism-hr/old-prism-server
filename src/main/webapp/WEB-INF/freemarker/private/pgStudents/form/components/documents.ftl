<#import "/spring.ftl" as spring />
<#assign errorCode = RequestParameters.errorCode! />
<#assign htmlEscape = true in spring>
<h2 id="documents-H2" class="empty open">
	<span class="left"></span><span class="right"></span><span class="status"></span>
    Documents<em>*</em>
</h2>

<div>      
	<form> 
		<div class="section-info-bar">
			<div class="row">
				<span class="info-text"> &nbsp Coming Soon.
					<!--@spring.message 'programmeDetails.programme'/--> 
				</span>
			</div>
		</div>
	   
	  <div>
 			<#if errorCode?? && errorCode=="true">
				<div class="row">              	
					<span class="invalid">Please provide all mandatory fields in this section.<p></p></span>
			     </div>            	
			</#if>
			<div class="row">
        		<span class="plain-label">Personal Statement (PDF)<em>*</em></span>
        		<span class="hint" data-desc="<@spring.message 'supportingDocuments.personalStatement'/>"></span>	 
        		<div class="field" id="psUploadFields">        	
            			<input id="psDocument" class="full" type="file" name="file" value="" <#if applicationForm.isDecided()>disabled="disabled"</#if>/>					
					<span id="psUploadedDocument" ><input type="hidden" id="document_PERSONAL_STATEMENT" value = "${(applicationForm.personalStatement.id?string('######'))!}"/>
					<@spring.bind "applicationForm.personalStatement" /> 
                	<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>  
					<a href="<@spring.url '/download?documentId=${(applicationForm.personalStatement.id?string("#######"))!}'/>">${(applicationForm.personalStatement.fileName?html)!}</a></span>
					<span id="psDocumentProgress" style="display: none;" ></span>					
        		</div>  
        		
      		</div>
      		
      		 <div class="row">
        		<span class="plain-label">CV / resume (PDF)</span>
        		<span class="hint" data-desc="<@spring.message 'supportingDocuments.cv'/>"></span>
        		<div class="field" id="cvUploadFields">        	
          			<input id="cvDocument" class="full" type="file" name="file" value="" <#if applicationForm.isDecided()>disabled="disabled"</#if>/>					
					<span id="cvUploadedDocument" ><input type="hidden" id="document_CV" value = "${(applicationForm.cv.id?string('######'))!}"/>
					<@spring.bind "applicationForm.cv" /> 
                	<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>  
					<a href="<@spring.url '/download?documentId=${(applicationForm.cv.id?string("#######"))!}'/>">${(applicationForm.cv.fileName)!}</a></span>
					<span id="cvDocumentProgress" style="display: none;" ></span>					
        		</div>  
        		
      		</div>
			
		</div>

		<div class="buttons">
			
            <#if !applicationForm.isDecided()>
            	<button type="reset" id="documentsCancelButton" value="cancel">Cancel</button>
            </#if>             
            <button class="blue" id="documentsCloseButton" value="close">Close</button>
            <#if !applicationForm.isDecided()> 
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