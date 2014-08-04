$(document).ready(function()
{
	var pathname = window.location.pathname;
	var linkToFeedback = "https://docs.google.com/spreadsheet/viewform?formkey=dDNPWWt4MTJ2TzBTTzQzdUx6MlpvWVE6MQ"
		+"&entry_2="+pathname+"&entry_3="+$("#userRolesDP").val()+"&entry_4="+$("#userFirstNameDP").val()+"&entry_5="+$("#userLastNameDP").val()
		+"&entry_6="+$("#userEmailDP").val();
		
	$("#feedbackButton").attr('href', linkToFeedback);	
});
