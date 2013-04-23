<#import "/spring.ftl" as spring />
<span class="uploaded-file" name="supportingDocumentSpan">
	<#if document?? && document.id??>
		<#if document.type != 'COMMENT'>
			<input type="hidden" class="file" id="document_${document.type}" value="${encrypter.encrypt(document.id)}" name="document"/>
			<#if document.type != 'REFERENCE' >
				<a class="uploaded-filename" href="<@spring.url '/download?documentId=${encrypter.encrypt(document.id)}'/>" target="_blank">${document.fileName?html}</a>
				<a data-desc="Delete" class="btn btn-danger delete"><i class="icon-trash icon-large"></i> Delete</a>
			<#else>
				${document.fileName?html}
			</#if>
		<#else>
			<input type="hidden" class="file" value="${encrypter.encrypt(document.id)}" name="documents" />	
			<a class="uploaded-filename" href="<@spring.url '/download?documentId=${encrypter.encrypt(document.id)}'/>" target="_blank">${document.fileName?html}</a>
			<a name="delete" data-desc="Delete" class="btn btn-danger delete" id="${encrypter.encrypt(document.id)}"><i class="icon-trash icon-large"></i> Delete</a>
		</#if>
	</#if>
	<#if message??>
		${message?html}
	</#if>
</span>

<@spring.bind "document.*" /> 
<#list spring.status.errorMessages as error>
<div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
</#list> 	 
