<form id="applyForm" action="${host}/pgadmissions/apply/new" method="post" target="_blank">
	<input type="hidden" id="program" name="program" value="${(programCode?html)!}" />
	<#if projectId??>
		<input type="hidden" id="project" name="project" value="${projectId}" />
	</#if>
	<#if advertId??>
		<input type="hidden" id="advert" name="advert" value="${advertId}" />
	</#if>
	<button style="border: none; cursor: pointer; padding: 0" type="submit"><img border="0" src="${host}/pgadmissions/design/default/images/prism_apply_now.png" /></button>
</form>
