

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
			qualificationSubject: $("#qualificationSubject").val(), 
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
			add:"add"
		},
		function(data) {
			$('#qualificationsSection').html(data);
		});
	});
	
	$('#qualificationSaveCloseButton').click(function(){
		$.post("/pgadmissions/update/editQualification", {  
			qualificationSubject: $("#qualificationSubject").val(), 
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
			qualificationId: $("#qualificationId").val()
		},
		function(data) {
			$('#qualificationsSection').html(data);
		});
	});
	
	$('a[name="editQualificationLink"]').click(function(){
		var id = this.id;
		id = id.replace('qualification_', '');
		$('#qualificationId').val($('#'+id+'_qualificationIdDP').val());
		$('#qualificationSubject').val($('#'+id+'_qualificationSubjectDP').val());
		$('#qualificationInstitution').val($('#'+id+'_qualificationInstitutionDP').val());
		$('#qualificationLevel').val($('#'+id+'_qualificationLevelDP').val());
		$('#qualificationType').val($('#'+id+'_qualificationTypeDP').val());
		$('#qualificationGrade').val($('#'+id+'_qualificationGradeDP').val());
		$('#qualificationScore').val($('#'+id+'_qualificationScoreDP').val());
		$('#qualificationStartDate').val($('#'+id+'_qualificationStartDateDP').val());
		$('#qualificationLanguage').val($('#'+id+'_qualificationLanguageDP').val());
		if ($('#'+id+'_qualificationCompleted').val() =='YES'){
			$("#currentQualificationCB").attr('checked', true);
			$("#awardDateField").html("<input type=\"text\" class=\"half date\" id=\"qualificationAwardDate\" name=\"qualificationAwardDate\" value=\"\"> </input>");
			$("#currentQualification").val("YES");
			bindDatePickers();
		} else {
			$("#currentQualificationCB").attr('checked', false);
		}
		$('#qualificationAwardDate').val($('#'+id+'_qualificationAwardDateDP').val());
	});
	
	$('a[name="qualificationCancelButton"]').click(function(){
		$("#qualificationId").val("");
		$("#qualificationSubject").val("");
		$("#qualificationInstitution").val("");
		$("#qualificationLevel").val("");
		$("#qualificationGrade").val("");
		$("#qualificationType").val("");
		$("#qualificationScore").val("");
		$("#qualificationStartDate").val("");
		$("#qualificationLanguage").val("");
		$("#qualificationAwardDate").val("");
		$("#currentQualificationCB").attr('checked', false);
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