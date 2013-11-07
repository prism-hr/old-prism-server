<#import "/spring.ftl" as spring />


<@spring.bind "document.*" /> 

<#if spring.status.error >

  <#list spring.status.errorMessages as error>
  <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
  </#list> 	 

<#else>

  
  <span class="uploaded-file" name="supportingDocumentSpan">
  	<#if document?? && document.id??>
  		<#if document.type != 'COMMENT'>
  			<input type="text" class="file" id="document_${document.type}" value="${encrypter.encrypt(document.id)}" name="document" style="display:none;"/>
  			<#if document.type != 'REFERENCE' >
  				<a class="uploaded-filename" href="<@spring.url '/download?documentId=${encrypter.encrypt(document.id)}'/>" target="_blank">${document.fileName?html}</a>
  				<a data-desc="Delete" class="btn btn-danger delete"><i class="icon-trash icon-large"></i> Delete</a>
  			<#else>
  				${document.fileName?html}
  			</#if>
  		<#else>
  			<input type="text" class="file" value="${encrypter.encrypt(document.id)}" name="documents" style="display:none" />	
  			<a class="uploaded-filename" href="<@spring.url '/download?documentId=${encrypter.encrypt(document.id)}'/>" target="_blank">${document.fileName?html}</a>
  			<a name="delete" data-desc="Delete" class="btn btn-danger delete" id="${encrypter.encrypt(document.id)}"><i class="icon-trash icon-large"></i> Delete</a>
  		</#if>
  	</#if>
  	<#if message??>
  		${message?html}
  	</#if>
  </span>

</#if>

