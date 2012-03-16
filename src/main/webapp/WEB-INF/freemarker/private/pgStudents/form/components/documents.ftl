<#import "/spring.ftl" as spring />
	<h2 id="documents-H2" class="empty">
    	<span class="left"></span><span class="right"></span><span class="status"></span>
        Documents
	</h2>
    
    <div>
    	
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
		                <td ><a href="<@spring.url '/download'/>?documentId=${document.id}">
		                	<#if document.fileName?length <40 >${document.fileName}<#else>${document.fileName?substring(0,37)}...</#if></a></td>
		                <td>${(document.dateUploaded?string('dd-MMM-yyyy'))!}</td>
		                <td>
		                	<#if !model.applicationForm.submitted>
			                	<form method="Post" action="<@spring.url '/delete'/>" style="padding:0">
			                		<input type="hidden" name="documentId" value="${document.id}"/>		                		
			                		<a name="deleteButton" class="button-delete">delete</a>
			                	</form>
		                	</#if>
		               </td>
	                </tr>
					</#list>
                </tbody>
		</table>
        
		<form id="documentUploadForm" method="POST" action="<@spring.url '/documents'/>" enctype="multipart/form-data">
             <input type="hidden" name="id" value="${model.applicationForm.id}"/>
             <div>
                
             	<!-- Document type -->
                <div class="row">
                    <span class="label">Type</span>
                    <span class="hint"></span>
                    <div class="field">
                    	<select class="full" name="documentType" <#if model.applicationForm.submitted>disabled="disabled"</#if>>
                    		<#list model.documentTypes as documentType>                    			
                    			<option value="${documentType}">${documentType.displayValue}</option>
                    		</#list>	              
                      	</select>
                    </div>  
				</div>

                <!-- Document upload -->
                <div class="row">
                    <span class="label">Document</span>
                    <span class="hint"></span>
                    <div class="field">
                		<input class="full" type="file" name="file" value=""  <#if model.applicationForm.submitted>disabled="disabled"</#if>/>                      	
                        <button style="margin-left:30px" class="blue" type="submit" value="close"  <#if model.applicationForm.submitted>disabled="disabled"</#if>>Upload</button>          
                    </div>  
				</div>
				<#if model.uploadErrorCode?? >
					<span class="invalid"><@spring.message  model.uploadErrorCode /></span>         
				</#if>				
			</div>

			<div class="buttons">
                <button class="blue" id="documentsCloseButton" value="close">Close</button>              
			</div>

		</form>
	</div>
	<script type="text/javascript" src="<@spring.url '/design/default/js/application/documents.js'/>"></script>