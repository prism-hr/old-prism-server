$(document).ready(function(){
	
	$('#addSupervisorBtn').click(function() {
		var selectedSupervisors = $('#programSupervisors').val();
		selectedSupervisors.forEach(function(id) {			
			var selText = $("#programSupervisors option[value='" + id + "']").text();
			var category = $("#programSupervisors option[value='" + id + "']").attr("category");
			$("#programSupervisors option[value='" + id + "']").remove();
			$("#applicationSupervisors").append('<option value="'+ id +'" category="' + category + '">'+ selText + ' (*)</option>');
		});
		$('#programSupervisors').attr("size", $('#programSupervisors option').size() + 1);
		$('#applicationSupervisors').attr("size", $('#applicationSupervisors option').size() + 1);
		
		
	});
	
	
	$('#createSupervisor').click(function() {

		$('#applicationSupervisors option').each(function(){
			var ids = $(this).val();
		 	var user = ids.substring(ids.indexOf("|") + 1);
			$('#postSupervisorForm').append("<input name='pendingSupervisor' type='hidden' value='" + user + "'/>");	
		});
		$('#postSupervisorForm').append("<input name='applicationId' type='hidden' value='" +  $('#applicationId').val() + "'/>");
		$('#postSupervisorForm').append("<input name='approvalRoundId' type='hidden' value='" +  $('#approvalRoundId').val() + "'/>");
		$('#postSupervisorForm').append("<input name='firstName' type='hidden' value='" +  $('#newSupervisorFirstName').val() + "'/>");
		$('#postSupervisorForm').append("<input name='lastName' type='hidden' value='" +  $('#newSupervisorLastName').val() + "'/>");
		$('#postSupervisorForm').append("<input name='email' type='hidden' value='" +  $('#newSupervisorEmail').val() + "'/>");		
		
		$('#postSupervisorForm').submit();
		
	});
	
	$('#removeSupervisorBtn').click(function() {
		var selectedSupervisors = $('#applicationSupervisors').val();
		selectedSupervisors.forEach(function(id) {
			var selText = $("#applicationSupervisors option[value='" + id + "']").text().replace(' (*)', '');
			$("#applicationSupervisors option[value='" + id + "']").remove();
			$("#programSupervisors").append('<option value="'+ id +'">'+ selText +'</option>');
		});
		$('#programSupervisors').attr("size", $('#programSupervisors option').size() + 1);
		$('#applicationSupervisors').attr("size", $('#applicationSupervisors option').size() + 1);
	});

	
	
	$('#moveToApprovalBtn').click(function() {
		
		$('#applicationSupervisors option').each(function(){
			 	var ids = $(this).val();
			 	var user = ids.substring(ids.indexOf("|") + 1);
				$('#postApprovalForm').append("<input name='pendingSupervisors' type='hidden' value='" + user + "'/>");	
				$('#postApprovalForm').append("<input name='supervisors' type='hidden' value='" +  $(this).val() + "'/>");
		});
		$('#postApprovalForm').append("<input name='applicationId' type='hidden' value='" +  $('#applicationId').val() + "'/>");				
		$('#postApprovalForm').submit();
	});
		
		
	
});


