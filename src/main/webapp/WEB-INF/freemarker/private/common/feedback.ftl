<#if model?? && model.user??>
	<script type="text/javascript" src="<@spring.url '/design/default/js/feedback.js' />"></script>
	
	
	<input type="hidden" id="userRolesDP" name="userRolesDP" value="${model.userRoles}"/>
	<input type="hidden" id="userFirstNameDP" name="userFirstNameDP" value="${model.user.firstName}"/>
	<input type="hidden" id="userLastNameDP" name="userLastNameDP" value="${model.user.lastName}"/>
	<input type="hidden" id="userEmailDP" name="userEmailDP" value="${model.user.email}"/>

	<a class="blue button" type="button" id="feedbackButton" name="feedbackButton">Send Feedback</a>
<#elseif user??>
	<script type="text/javascript" src="<@spring.url '/design/default/js/feedback.js' />"></script>
	
	
	
	<input type="hidden" id="userFirstNameDP" name="userFirstNameDP" value="${user.firstName}"/>
	<input type="hidden" id="userLastNameDP" name="userLastNameDP" value="${user.lastName}"/>
	<input type="hidden" id="userEmailDP" name="userEmailDP" value="${user.email}"/>
	
	<a class="blue button" type="button" id="feedbackButton" name="feedbackButton">Send Feedback</a>
</#if>
                                           