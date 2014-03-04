<#import "/spring.ftl" as spring />
<#setting locale = "en_US">
<!-- Styles for Application List Page -->
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/staff/timeline.css' />"/>
<script type="text/javascript" src="<@spring.url '/design/default/js/admin/comment/timelineApplication.js'/>"></script>

<input type="hidden" id="isReferee" value="
	<#if user?? && applicationForm??>
		${user.isRefereeOfApplicationForm(applicationForm)?string}
	</#if>"/>

<div id="timelineview">
	<ul class="tabs">
		<li><a href="#timeline" id="timelineBtn">Timeline</a></li>
		<li><a href="#application" id="applicationBtn">Application</a></li>
		<li><a href="#opportunity" id="opportunityBtn">Opportunity</a></li>
	</ul>
	<div class="tab-page" id="timelineTab"></div>
	<div class="tab-page" id="applicationTab"></div>
	<div class="tab-page" id="opportunityTab"></div>
</div>
