<div class="row">
	<label class="plain-label" for="file">Attach Document</label>
	<span class="hint" data-desc="<@spring.message 'validateApp.document'/>"></span>
	<div class="field" id="uploadFields">
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
		<input id="commentDocument" class="full" type="file" name="file" value="" />
		<span id="commentDocumentProgress" style="display: none;" ></span>
	</div>
</div>