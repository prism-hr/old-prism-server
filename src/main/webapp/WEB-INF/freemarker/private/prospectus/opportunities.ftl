<#import "/spring.ftl" as spring/>

<div class="row-fluid">
	<div class="span6" id="yourApplication">
		<#assign feedTitle = "Your Application">
		<#assign feedKey = "CURRENTOPPORTUNITYBYADVERTID">
		<#if applicationForm.project??>
			<#assign feedKeyValue = applicationForm.project.id?string.computer>
		<#else>
			<#assign feedKeyValue = applicationForm.program.id?string.computer>
		</#if>
 	    <#include "/private/prospectus/adverts.ftl"/>
	</div>
	<div class="span6" id="opportunitiList">
		<#assign feedTitle="Related Opportunities">
		<#assign feedKey = "RELATEDOPPORTUNITIES">
		<#assign feedKeyValue = applicationForm.applicant.id?string.computer>
 	    <#include "/private/prospectus/adverts.ftl"/>
	</div>
</div>
 