$(document).ready(function(){	
	$("#acceptTermsValue").val("NO");
$("input[name*='acceptTermsCB']").click(function() {
	if ($("#acceptTermsValue").val() =='YES'){
		$("#acceptTermsValue").val("NO");
	} else {	
		$("#acceptTermsValue").val("YES");
		$.post("/pgadmissions/acceptTerms", {  
			applicationId: $("#ATapplicationFormId").val(), 
			acceptedTerms: $("#acceptTermsValue").val()
		},
		function(data) {
		});
	}
	});
});