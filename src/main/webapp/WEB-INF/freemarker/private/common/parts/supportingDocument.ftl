<#import "/spring.ftl" as spring />
<span class="uploaded-file" name="supportingDocumentSpan">
	<#if document?? && document.id??>
		<#if document.type != 'COMMENT'>
			<input type="text" class="file" id="document_${document.type}" value="${encrypter.encrypt(document.id)}" style="display:none" name="document"/>
			<#if document.type != 'REFERENCE' >
				<a class="uploaded-filename" href="<@spring.url '/download?documentId=${encrypter.encrypt(document.id)}'/>" target="_blank">${document.fileName?html}</a>
				<a data-desc="Delete" class="button-delete button-hint">delete</a>
			<#else>
				${document.fileName?html}
			</#if>
		<#else>
			<input type="text" class="file" value="${encrypter.encrypt(document.id)}" style="display:none" name="documents" />	
			<a class="uploaded-filename" href="<@spring.url '/download?documentId=${encrypter.encrypt(document.id)}'/>" target="_blank">${document.fileName?html}</a>
			<a name="delete" data-desc="Delete" class="button-delete button-hint" id="${encrypter.encrypt(document.id)}">delete</a>
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
