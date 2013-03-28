<div class="row">
	<label class="plain-label" for="commentDocument">Attach Document</label>
	<span class="hint" data-desc="<@spring.message 'validateApp.document'/>"></span>
	<div class="field" id="uploadFields">
        <input id="commentDocument" class="full" data-type="COMMENT" data-reference="Comment" type="file" name="file" value="" />                   
        <input type="hidden" class="file" id="document_COMMENT" value=""/>
        <input type="hidden" name="MAX_FILE_SIZE" value="2097152" />
        <span id="commentDocumentProgress" class="progress" style="display: none;"></span>
	</div>
</div>
	
<span id="commentUploadedDocument" class="uploaded-files">
    <#if comment??>
        <#list comment.documents as document>
            <div class="row">
                <div class="field">
                    <span class="uploaded-file" name="supportingDocumentSpan" style="display:inline">
                        <input class="file" style="display:none" type="text" value="${encrypter.encrypt(document.id)}" name="documents"/>	
                        <a class="uploaded-filename" href="<@spring.url '/download?documentId=${encrypter.encrypt(document.id)}'/>" target="_blank">${document.fileName?html}</a>
                        <a name="delete" data-desc="Delete" class="button-delete button-hint" id="${encrypter.encrypt(document.id)}">delete</a>
                    </span>
                </div>
            </div>
        </#list>
    </#if>
</span>