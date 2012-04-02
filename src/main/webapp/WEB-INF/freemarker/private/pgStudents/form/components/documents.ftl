<#if model.applicationForm.supportingDocuments?has_content>
    <#assign hasDocs = true>
<#else>
    <#assign hasDocs = false>
</#if> 
<#import "/spring.ftl" as spring />
	<h2 id="documents-H2" class="empty">
    	<span class="left"></span><span class="right"></span><span class="status"></span>
        Documents
	</h2>
    
    <div>

        <#if hasDocs>                    
    	<table class="existing">
        	<colgroup>
            	<col style="width: 20px" />
                <col style="width: 300px" />
                <col />
                <col style="width: 120px" />
                <col style="width: 30px" />
			</colgroup>
            
            <thead>
            	<tr>
	                <th colspan="2">Document Type</th>
	                <th>Document Name</th>
	                <th>Date Uploaded</th>
	                <th>&nbsp;</th>
                </tr>
                
                </thead>
                <tbody>
					<#list model.applicationForm.supportingDocuments as document>
					<tr>
						<td>-</td>
		                <td  nowrap>${document.type.displayValue}</td>
		                <td ><a href="<@spring.url '/download'/>?documentId=${document.id?string('#######')}">
		                	<#if document.fileName?length <40 >${document.fileName}<#else>${document.fileName?substring(0,37)}...</#if></a></td>
		                <td>${(document.dateUploaded?string('dd-MMM-yyyy'))!}</td>
		                <td>
		                	<#if !model.applicationForm.submitted>
			                	<form method="Post" action="<@spring.url '/delete'/>" style="padding:0">
			                		<input type="hidden" name="documentId" value="${document.id?string('#######')}"/>		                		
			                		<a name="deleteButton" class="button-delete">delete</a>
			                	</form>
		                	</#if>
		               </td>
	                </tr>
					</#list>
                </tbody>
		</table>
		</#if>
        
		<form id="documentUploadForm" method="POST" action="<@spring.url '/documents'/>" enctype="multipart/form-data">
             <input type="hidden" name="id" value="${model.applicationForm.id?string('#######')}"/>
             <div>
             
             <div class="row">
             <#if model.hasError('supportingDocuments')>                           
                     <span class="invalid"><@spring.message  model.result.getFieldError('supportingDocuments').code /></span>                           
             </#if>
             </div>
                
                <!-- Document upload -->
                <div class="row">
                    <span class="label">CV / resume (PDF)</span>
                    <span class="hint"></span>
                    <div class="field">
                		<input class="full" type="file" name="resume" value=""  <#if model.applicationForm.submitted>disabled="disabled"</#if>/>                      	
                    <#if model.uploadErrorCode?? >
                    <div class="row">
                       <span class="invalid"><@spring.message  model.uploadErrorCode /></span>
                    </div>            
                </#if>  
                    </div>
				</div>
				
				<div class="row">
				    <span class="label">Personal Statement (PDF)</span>
                    <span class="hint"></span>
                    <div class="field">
                        <input class="full" type="file" name="personalStatement" value=""  <#if model.applicationForm.submitted>disabled="disabled"</#if>/>
                   <#if model.uploadTwoErrorCode?? >
                    <div class="row">
                       <span class="invalid"><@spring.message  model.uploadTwoErrorCode /></span>
                    </div> 
                   </#if>                                       
                    </div> 
                     
                </div>
				<div class="row">
                    <span class="label">Max file size is 10Mb.</span>
                  
				</div>
			</div>

			<div class="buttons">
				<button type="reset" id="documentsCancelButton" value="cancel">Cancel</button>
                <button class="blue" id="documentsCloseButton" value="close">Close</button> 
                <#if !model.applicationForm.submitted>
                    <button class="blue" type="submit" id="documentsSaveButton" value="close">Save</button> 
                </#if>             
			</div>

		</form>
	</div>
	<script type="text/javascript" src="<@spring.url '/design/default/js/application/documents.js'/>"></script>
	
<#if model.uploadErrorCode?? || model.uploadTwoErrorCode?? || model.hasError('supportingDocuments') >

<#else >
<script type="text/javascript">
	$(document).ready(function(){
		$('#documents-H2').trigger('click');
	});
</script>
</#if>