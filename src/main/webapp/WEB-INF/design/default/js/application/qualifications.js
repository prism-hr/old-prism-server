

$(document).ready(function(){
	
	$("#currentQualificationCB").attr('checked', false);
	$("#currentQualification").val("NO");
	
	$('#qualificationsCloseButton').click(function(){
		$('#qualifications-H2').trigger('click');
		return false;
	});
	
	$("input[name*='currentQualificationCB']").click(function() {
		if ($("#currentQualification").val() =='YES'){
			$("#currentQualification").val("NO");
			$("#awardDateField").html("<input type=\"text\" class=\"half date\" id=\"qualificationAwardDate\" name=\"qualificationAwardDate\" value=\"\" disabled=\"disabled\"> </input>");
		} else {		
			$("#currentQualification").val("YES");
			$("#awardDateField").html("<input type=\"text\" class=\"half date\" id=\"qualificationAwardDate\" name=\"qualificationAwardDate\" value=\"\"> </input>");
			bindDatePickers();
		}
	});
	
	$('a[name="deleteButton"]').click( function(){	
		$(this).parent("form").submit();
	});
	
	
	$('#qualificationsSaveButton').click(function(){
		$.post("/pgadmissions/update/editQualification", {  
			qualificationProgramName: $("#qualificationProgramName").val(), 
			qualificationInstitution: $("#qualificationInstitution").val(), 
			qualificationLevel: $("#qualificationLevel").val(),
			qualificationType: $("#qualificationType").val(),
			qualificationGrade: $("#qualificationGrade").val(),
			qualificationScore: $("#qualificationScore").val(),
			qualificationStartDate: $("#qualificationStartDate").val(),
			qualificationLanguage: $("#qualificationLanguage").val(),
			qualificationAwardDate: $("#qualificationAwardDate").val(),
			completed: $("#currentQualification").val(),
			appId: $("#appId").val(),
			qualificationId: $("#qualificationId").val(),
			id: $("#id").val(),
			add:"add"
		},
		function(data) {
			$('#qualificationsSection').html(data);
		});
	});
	
	$('#qualificationSaveCloseButton').click(function(){
		$.post("/pgadmissions/update/editQualification", {  
			qualificationProgramName: $("#qualificationProgramName").val(), 
			qualificationInstitution: $("#qualificationInstitution").val(), 
			qualificationLevel: $("#qualificationLevel").val(),
			qualificationType: $("#qualificationType").val(),
			qualificationGrade: $("#qualificationGrade").val(),
			qualificationScore: $("#qualificationScore").val(),
			qualificationStartDate: $("#qualificationStartDate").val(),
			qualificationLanguage: $("#qualificationLanguage").val(),
			qualificationAwardDate: $("#qualificationAwardDate").val(),
			appId: $("#appId").val(),
			completed: $("#currentQualification").val(),
			qualificationId: $("#qualificationId").val(),
			id: $("#id").val()
		},
		function(data) {
			$('#qualificationsSection').html(data);
		});
	});
	
	$('a[name="editQualificationLink"]').click(function(){
		var id = this.id;
		id = id.replace('qualification_', '');
		$('#qualificationId').val($('#'+id+'_qualificationIdDP').val());
		$('#qualificationProgramName').val($('#'+id+'_qualificationProgramNameDP').val());
		$('#qualificationInstitution').val($('#'+id+'_qualificationInstitutionDP').val());
		$('#qualificationLevel').val($('#'+id+'_qualificationLevelDP').val());
		$('#qualificationType').val($('#'+id+'_qualificationTypeDP').val());
		$('#qualificationGrade').val($('#'+id+'_qualificationGradeDP').val());
		$('#qualificationScore').val($('#'+id+'_qualificationScoreDP').val());
		$('#qualificationStartDate').val($('#'+id+'_qualificationStartDateDP').val());
		$('#qualificationLanguage').val($('#'+id+'_qualificationLanguageDP').val());
		$('#qualificationAwardDate').val($('#'+id+'_qualificationAwardDateDP').val());
		if ($('#'+id+'_qualificationCompleted').val() =='YES'){
			$("#currentQualificationCB").attr('checked', true);
		} else {
			$("#currentQualificationCB").attr('checked', false);
		}
	});
	
	$('a[name="qualificationCancelButton"]').click(function(){
		$("#qualificationId").val("");
		$("#qualificationProgramName").val("");
		$("#qualificationInstitution").val("");
		$("#qualificationLevel").val("");
		$("#qualificationGrade").val("");
		$("#qualificationType").val("");
		$("#qualificationScore").val("");
		$("#qualificationStartDate").val("");
		$("#qualificationLanguage").val("");
		$("#qualificationAwardDate").val("");
		$("span[class='invalid']").each(function(){
			$(this).html("");
		});
	});
	
	  bindDatePickers();

		//open/close
		var $header  =$('#qualifications-H2');
		var $content = $header.next('div');
		$header.bind('click', function()
		{
		  $content.toggle();
		  $(this).toggleClass('open', $content.is(':visible'));
		  return false;
		});
		
});