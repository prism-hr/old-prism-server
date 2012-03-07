$(document).ready(function(){
	$('#qualificationsSaveButton').click(function(){
		$.post("/pgadmissions/update/editQualification", {  
			name_of_programme: $("#q_name").val(), 
			institution: $("#q_provider").val(), 
			level: $("#q_level").val(),
			qualification_type: $("#q_Type").val(),
			grade: $("#q_grade").val(),
			score: $("#q_score").val(),
			start_date: $("#q_start_date").val(),
			language_of_study: $("#q_language").val(),
			award_date: $("#q_award_date").val(),
			appId: $("#appId").val(),
			qualId: $("#qualId").val(),
			id: $("#id").val()
		},
		function(data) {
			$('#qualificationsSection').html(data);
		});
	});
	
	$('a[name="editQualificationLink"]').click(function(){
		var id = this.id;
		id = id.replace('qualification_', '');
		$('#q_provider').val($('#'+id+'_q_provider').val());
		$('#q_name').val($('#'+id+'_q_name').val());
		$('#q_start_date').val($('#'+id+'_q_start_date').val());
		$('#q_language').val($('#'+id+'_q_language').val());
		$('#q_level').val($('#'+id+'_q_level').val());
		$('#q_Type').val($('#'+id+'_q_Type').val());
		$('#q_grade').val($('#'+id+'_q_grade').val());
		$('#q_score').val($('#'+id+'_q_score').val());
		$('#q_provider').val($('#'+id+'_q_provider').val());
		$('#q_award_date').val($('#'+id+'_q_award_date').val());
		$('#qualId').val($('#'+id+'_qualId').val());
	});
});