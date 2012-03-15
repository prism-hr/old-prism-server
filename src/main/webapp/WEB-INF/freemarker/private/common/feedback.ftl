<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/feedback.js' />"></script>

<input type="hidden" id="userRolesDP" name="userRolesDP" value="${model.userRoles}"/>
<input type="hidden" id="userFirstNameDP" name="userFirstNameDP" value="${model.user.firstName}"/>
<input type="hidden" id="userLastNameDP" name="userLastNameDP" value="${model.user.lastName}"/>
<input type="hidden" id="userEmailDP" name="userEmailDP" value="${model.user.email}"/>

<a class="blue button" type="button" id="feedbackButton" name="feedbackButton">Send Feedback</a>
                                           