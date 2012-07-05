<#import "/spring.ftl" as spring />
<form id="applyForm" action="${host}<@spring.url '/apply/new'/>" method="POST" target="blank">
	<input type="hidden" id="program" name="program" value="${(program.code?html)!}" />
	<input type="hidden" id="programhome" name="programhome" value="${RequestParameters.programhome!}" />
	<input type="hidden" id="projectTitle" name="projectTitle" value="${RequestParameters.project!}" />
	<input type="hidden" id="programDeadline" name="programDeadline" value="${RequestParameters.batchdeadline!}" />
	<button type="submit"<#if RequestParameters.disable??> disabled="disabled"</#if>><img src="${host}<@spring.url '/design/default/images/prism_apply_now.jpg'/>" /></button>
</form>
