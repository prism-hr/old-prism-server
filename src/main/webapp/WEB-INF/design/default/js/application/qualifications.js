$(document).ready(function(){
	
	if($("#qualificationInstitution").val() == ""){
		$("#currentQualificationCB").attr('checked', false);
		$("#currentQualification").val("NO");
	}
	
	if($("#currentQualificationCB").is(":checked")){
		$("#currentQualification").val("YES");
	}
	else{	
		$("#currentQualification").val("NO");
		$("#qualificationAwardDate").val("");
		$("#qualificationAwardDate").attr("disabled", "disabled");
		$("#proofOfAward").val("");
		$("#proofOfAward").attr("disabled", "disabled");	
	}
	
	$('#qualificationsCloseButton').click(function(){
		$('#qualifications-H2').trigger('click');
		return false;
	});
	
	$("input[name*='currentQualificationCB']").click(function() {
		if ($("#currentQualification").val() =='YES'){
			$("#currentQualification").val("NO");
			$("#qualificationAwardDate").val("");
			$("#qualificationAwardDate").attr("disabled", "disabled");
			$("#proofOfAward").val("");
			$("#proofOfAward").attr("disabled", "disabled");	

		} else {		
		
			$("#currentQualification").val("YES");
			$("#qualificationAwardDate").removeAttr("disabled", "disabled");	
			$("#proofOfAward").removeAttr("disabled", "disabled");

			bindDatePickers();
		}
	
	});
	
	$('a[name="deleteQualificationButton"]').click( function(){	
		var id = $(this).attr("id").replace("qualification_", "");
		$.post("/pgadmissions/deleteentity/qualification",
				{
					id: id	
				}, 
				
				function(data) {
					$('#qualificationsSection').html(data);
				}	
				
			);
	});
	
	
	$('#qualificationsSaveButton').click(function(){
		postQualificationData('add');
	});
	
	$('#qualificationSaveCloseButton').click(function(){
		postQualificationData('close');
	});
	
	$('a[name="editQualificationLink"]').click(function(){
		var id = this.id;
		id = id.replace('qualification_', '');	
		$.get("/pgadmissions/update/getQualification",
				{
					applicationId:  $('#applicationId').val(),
					qualificationId: id,
					message: 'edit'
				},
				function(data) {
					$('#qualificationsSection').html(data);
				}
		);
	});
	
	$('a[name="qualificationCancelButton"]').click(function(){
		$.get("/pgadmissions/update/getQualification",
				{
					applicationId:  $('#applicationId').val(),
					message: 'cancel'
				},
				function(data) {
					$('#qualificationsSection').html(data);
				}
		);
	});
	
	bindDatePicker('#qualificationStartDate');
	bindDatePicker('#qualificationAwardDate');
	addToolTips();
	// open/close
	var $header  =$('#qualifications-H2');
	var $content = $header.next('div');
	$header.bind('click', function()
	{
	  $content.toggle();
	  $(this).toggleClass('open', $content.is(':visible'));
	  return false;
	});
		
	$('#uploadFields').on('change','#proofOfAward', function(event){	
		ajaxProofOfAwardDelete();
		$('#progress').html("uploading file...");
		$('#proofOfAward').attr("readonly", "readonly");
		ajaxProofOfAwardUpload();
		$('#proofOfAward').removeAttr("readonly");
	});
	
	
});

function postQualificationData(message){

	$.post("/pgadmissions/update/editQualification", {  
		qualificationSubject: $("#qualificationSubject").val(), 
		qualificationInstitution: $("#qualificationInstitution").val(), 
		qualificationType: $("#qualificationType").val(),
		qualificationGrade: $("#qualificationGrade").val(),
		qualificationScore: $("#qualificationScore").val(),
		qualificationStartDate: $("#qualificationStartDate").val(),
		qualificationLanguage: $("#qualificationLanguage").val(),
		qualificationAwardDate: $("#qualificationAwardDate").val(),
		completed: $("#currentQualification").val(),			
		qualificationId: $("#qualificationId").val(),
		applicationId:  $('#applicationId').val(),
		application:  $('#applicationId').val(),
		institutionCountry: $('#institutionCountry').val(),
		proofOfAward: $('#document_PROOF_OF_AWARD').val(),
		message:message
	},
	function(data) {
		$('#qualificationsSection').html(data);
	});
}
function ajaxProofOfAwardDelete(){
	
	if($('#profOfAwardId') && $('#profOfAwardId').val() && $('#profOfAwardId').val() != ''){
		$.post("/pgadmissions/delete/asyncdelete",
			{
				documentId: $('#profOfAwardId').val()
				
			}				
		);

	}
}
function ajaxProofOfAwardUpload()
{	
	
	$("#progress").ajaxStart(function(){
		$(this).show();
	})
	.ajaxComplete(function(){
		$(this).hide();
		$('#progress').html("");
		
	});

	$.ajaxFileUpload
	(
		{
			url:'/pgadmissions/documents/async',
			secureuri:false,
			
			fileElementId:'proofOfAward',	
			dataType:'text',
			data:{type:'PROOF_OF_AWARD'},
			success: function (data)
			{		
				$('#uploadedDocument').html(data);
				$('#uploadedDocument').show();
				
			}
		}
	)

}