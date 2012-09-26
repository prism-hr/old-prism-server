<div class="row">
	<label class="plain-label" for="file">Attach Document</label>
	<span class="hint" data-desc="<@spring.message 'validateApp.document'/>"></span>
	<div class="field" id="uploadFields">
        <input id="commentDocument" class="full" data-type="COMMENT" data-reference="Comment" type="file" name="file" value="" />                   
        <span id="commentUploadedDocument">
            <input type="hidden" class="file" id="document_COMMENT" value=""/>
            <input type="hidden" name="MAX_FILE_SIZE" value="2097152" />
        </span>
        <span id="commentDocumentProgress" class="progress" style="display: none;"></span>
        
        
		<span id="commentUploadedDocument" class="uploaded-files">
			<#if comment??>
				<#list comment.documents as document>
				<span class="uploaded-file" name="supportingDocumentSpan">
					<input type="text" value="${encrypter.encrypt(document.id)}" name="documents"/>	
					<a class="uploaded-filename" href="<@spring.url '/download?documentId=${encrypter.encrypt(document.id)}'/>" target="_blank">${document.fileName?html}</a>
					<a name="delete" data-desc="Delete" class="button-delete button-hint" id="${encrypter.encrypt(document.id)}">delete</a>
				</span>
				</#list>
			</#if>
		</span>
		
        
	</div>
</div>