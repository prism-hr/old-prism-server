<#import "/spring.ftl" as spring/>

<div class="row-fluid">
	<div class="span6" id="yourApplication">
		<#assign feedId = "current-opportunity-list">
		<#assign feedTitle = "Your Application">
		<#assign feedKey = "CURRENTOPPORTUNITYBYAPPLICATIONFORMID">
		<#assign feedKeyValue = applicationForm.id?string.computer>
 	    <#include "/private/prospectus/adverts_abstract.ftl"/>
	</div>
	<div class="span6" id="opportunitiList">
		<#assign feedId = "related-opportunity-list">
		<#assign feedTitle="Other Recommended Opportunities">
		<#assign feedKey = "RECOMMENDEDOPPORTUNTIIESBYAPPLICANTID">
		<#assign feedKeyValue = applicationForm.applicant.id?string.computer>
 	    <#include "/private/prospectus/adverts_abstract.ftl"/>
	</div>
</div>
<script type="text/javascript" src="<@spring.url '/design/default/js/prospectus/opportunities.js' />"></script>