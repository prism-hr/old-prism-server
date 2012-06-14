<#import "/spring.ftl" as spring />
 <form id="applyForm" action="${host}/<@spring.url '/apply/new'/>" method="POST">
	<p>Programme: ${(program.title?html)!}</p>
	<p>Programme home page: ${RequestParameters.programhome!}</p>
	<p>Project: ${RequestParameters.project!}</p>	
	<p>Batch deadline: ${RequestParameters.batchdeadline!}</p>
	<input type="hidden" id="program" name="program" value="${(program.code?html)!}"/>
	<input type="hidden" id="programhome" name="programhome" value="${RequestParameters.programhome!}"/>
	<input type="hidden" id="projectTitle" name="projectTitle" value="${RequestParameters.project!}"/>
  	<input type="hidden" id="programDeadline" name="programDeadline" value="${RequestParameters.batchdeadline!}"/>
	<input type="Submit" value="Apply"/>
</form>
