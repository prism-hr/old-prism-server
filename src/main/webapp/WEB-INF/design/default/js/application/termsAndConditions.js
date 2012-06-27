$(document).ready(function(){	
	$("#acceptTermsValue").val("NO");
$("input[name*='acceptTermsCB']").click(function() {
	if ($("#acceptTermsValue").val() =='YES'){
		$("#acceptTermsValue").val("NO");
	} else {	
		$("#acceptTermsValue").val("YES");
		$.ajax({
			type: 'POST',
			 statusCode: {
				  401: function() {
					  window.location.reload();
				  }
			  },
			url:"/pgadmissions/acceptTerms", 
			data:{  
				applicationId: $("#ATapplicationFormId").val(), 
				acceptedTerms: $("#acceptTermsValue").val()
			},
			success: function(data) {}
		});
	}
	});
});