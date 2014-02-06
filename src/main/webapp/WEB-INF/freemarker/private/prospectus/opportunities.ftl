<div class="row-fluid">
	<div class="span6" id="yourApplication">
		<#assign feedTitle = "Your Application">
		<#assign feedKey = "CURRENTOPPORTUNITY">
		<#assign feedKeyValue = "${(applicationForm.id)!}">
 	    <#include "/private/prospectus/adverts.ftl"/>
	</div>
	<div class="span6" id="opportunitiList">
		<#assign feedTitle="Related Opportunities">
		<#assign feedKey = "RELATEDOPPORTUNITIES">
		<#assign feedKeyValue = "${(applicationForm.applicant.id)!}>
 	    <#include "/private/prospectus/adverts.ftl"/>
	</div>
</div>
 