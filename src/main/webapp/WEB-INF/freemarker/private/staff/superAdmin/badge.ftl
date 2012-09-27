<#import "/spring.ftl" as spring />
<#setting locale = "en_US">
<form id="applyForm" action="${host}<@spring.url '/apply/new'/>" method="post" target="_blank">
	<input type="hidden" id="program" name="program" value="${(program.code?html)!}" />
	<input type="hidden" id="programhome" name="programhome" value="${badge.programmeHomepage!}" />
	<input type="hidden" id="projectTitle" name="projectTitle" value="${badge.projectTitle!}" />
	<input type="hidden" id="programDeadline" name="programDeadline" value="${(badge.closingDate?string('dd MMM yyyy'))!}" />
	<button style="border: none; cursor: pointer; padding: 0" type="submit"<#if RequestParameters.disable??> disabled="disabled"</#if>><img border="0" src="${host}<@spring.url '/design/default/images/prism_apply_now.png'/>" /></button>
</form>
