<#if model?? && model.user??>
	<input type="hidden" id="userRolesDP" name="userRolesDP" value="${model.userRoles}"/>
	<input type="hidden" id="userFirstNameDP" name="userFirstNameDP" value="${model.user.firstName}"/>
	<input type="hidden" id="userLastNameDP" name="userLastNameDP" value="${model.user.lastName}"/>
	<input type="hidden" id="userEmailDP" name="userEmailDP" value="${model.user.email}"/>

	<!-- Feedback button. -->
	<a id="feedbackButton">Send Feedback</a>
  
  <!-- Insert JS -->
  <script type="text/javascript">
    var body = document.getElementsByTagName("body")[0];         
    var newScript = document.createElement('script');
    newScript.type = 'text/javascript';
    newScript.src = '<@spring.url '/design/default/js/feedback.js' />';
    body.appendChild(newScript);
  </script>
<#elseif user??>
	<input type="hidden" id="userFirstNameDP" name="userFirstNameDP" value="${user.firstName}"/>
	<input type="hidden" id="userLastNameDP" name="userLastNameDP" value="${user.lastName}"/>
	<input type="hidden" id="userEmailDP" name="userEmailDP" value="${user.email}"/>
	
	<!-- Feedback button. -->
	<a id="feedbackButton">Send Feedback</a>

  <!-- Insert JS -->
  <script type="text/javascript">
    var body = document.getElementsByTagName("body")[0];         
    var newScript = document.createElement('script');
    newScript.type = 'text/javascript';
    newScript.src = '<@spring.url '/design/default/js/feedback.js' />';
    body.appendChild(newScript);
  </script>
</#if>
                                           