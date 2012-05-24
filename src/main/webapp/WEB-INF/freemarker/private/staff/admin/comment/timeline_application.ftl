<#import "/spring.ftl" as spring />
<!-- Styles for Application List Page -->
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/global_private.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/staff/timeline.css' />"/>
<script type="text/javascript" src="<@spring.url '/design/default/js/admin/comment/timelineApplication.js'/>"></script>


<!--
<div class="buttons">
		<button type="button" id="timelineBtn" class="blue" >Timeline</button>
		<button class="blue" id="applicationBtn" type="button" >Application</button>						        
</div>
<br/>
-->
<div id="timelineview">
	<ul class="tabs">
		<li><a href="#timeline">Timeline</a></li>
		<li><a href="#application">Application</a></li>
	</ul>
	<div id="timeline"></div>
	<div id="application"></div>
</div>
