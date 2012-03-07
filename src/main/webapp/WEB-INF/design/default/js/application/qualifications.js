$(document).ready(function(){
	$('#qualificationsSaveButton').click(function(){
		$.post("/pgadmissions/update/editQualification", { country: $("#q_country").val(), 
			name_of_programme: $("#q_name").val(), 
			institution: $("#q_provider").val(), 
			termination_reason: $("#q_term_reason").val(), 
			termination_date: $("#q_term_date").val(), 
			level: $("#q_level").val(),
			type: $("#q_type").val(),
			grade: $("#q_grade").val(),
			score: $("#q_score").val(),
			start_date: $("#q_start_date").val(),
			language_of_study: $("#q_language").val(),
			appId: $("#appId").val(),
			id: $("#id").val()
		},
		function(data) {
			$('#qualificationsSection').html(data);
		});
	});
	
	$('a[name="editQualificationLink"]').click(function(){
		var id = this.id;
		id = id.replace('qualification_', '');
		var value = $('#'+id+'_q_provider').val();
		$('#q_provider').val(value);
	});
});